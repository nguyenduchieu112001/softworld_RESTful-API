package com.softworld.app1.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.softworld.app1.controller.form.PostOut;
import com.softworld.app1.exception.ResourceNotFoundException;
import com.softworld.app1.controller.form.Category_id_name;
import com.softworld.app1.controller.form.DateToSet;
import com.softworld.app1.controller.form.ErrorMessage;
import com.softworld.app1.controller.form.ImageProcessing;
import com.softworld.app1.controller.form.PostInput;
import com.softworld.app1.model.Post;
import com.softworld.app1.service.CategoryPostServiceImpl;
import com.softworld.app1.service.CategoryServiceImpl;
import com.softworld.app1.service.PostServiceImpl;

@RestController
@RequestMapping("/api")
public class PostController {

	@Autowired(required = false)
	private PostServiceImpl postService;

	@Autowired(required = false)
	private CategoryPostServiceImpl cpService;

	@Autowired(required = false)
	private CategoryServiceImpl categoryService;

	// get all Post's data
	@RequestMapping(value = "posts", method = RequestMethod.GET)
	@PreAuthorize("permitAll()")
	public Object getAllPosts() {
		return new ResponseEntity<Iterable<Post>>(postService.findAll(), HttpStatus.OK);
	}

	// find Post's data by ID
	@GetMapping("/post/{id}")
	@PreAuthorize("permitAll()")
	public Object getByPostId(@PathVariable("id") long id) {
		return new ResponseEntity<Post>(postService.getById(id), HttpStatus.OK);
	}

	// create 1 category's data into database categories (schema app1)
	@RequestMapping(value = "post/create", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority(\"ROLE_EDITOR\", \"ROLE_ADMIN\")")
	public Object addPost(@Valid @RequestBody Post postForm) {

		Post post = new Post();
		// if create_at and update_at have null value then assign value equal to current time
		DateToSet.setDateOfCreatePost(postForm);
		post = postService.save(postForm);
		return new ResponseEntity<Post>(post, HttpStatus.OK);

	}

	// update 1 category's data into database categories(schema app1)
	@RequestMapping(value = "/post/edit/{id}", method = RequestMethod.PUT)
	@PreAuthorize("hasAnyAuthority(\"ROLE_EDITOR\", \"ROLE_ADMIN\")")
	public Object updateCategory(@RequestBody Post postForm, @PathVariable("id") long id) {

		Post post = postService.getById(id);
		if (post == null)
			return ResponseEntity.notFound().build();
		else {
			post.setTitle(postForm.getTitle());

			post.setContent(postForm.getContent());

			DateToSet.setDateOfUpdatePost(post, postForm);

			Post updatePost = postService.save(post);
			return new ResponseEntity<Post>(updatePost, HttpStatus.OK);
		}

	}

	// delete Post's data by Id
	@DeleteMapping("/post/delete/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public Object deletePostById(@PathVariable("id") long id) throws Exception {
//		return new ResponseEntity<Optional<Post>>(postService.delete(id), HttpStatus.OK);
		if (postService.getById(id) == null)
			return ErrorMessage.notFount("Post not available!");
		else {
			postService.delete(id);
			return ErrorMessage.OK("Delete completed");
		}
	}

	// get Post by id with categoryID and categoryName
	@GetMapping("/get/post/{id}")
	public PostOut getPostById(@PathVariable(value = "id") long id) {

		Post post = postService.getPostById(id);
		if (post != null) {
			PostOut postOut = new PostOut();
			List<Category_id_name> categories = new ArrayList<>();
			// category_ids
			List<Long> listCategoryID = cpService.getCategoryIDFromCategoryPost(id);

			postOut.setTitle(post.getTitle());
			postOut.setContent(post.getContent());
			for (Long list : listCategoryID) {
				String categoryName = categoryService.getNameById(list);
				categories.add(new Category_id_name(list, categoryName));
			}
			postOut.setCategories(categories);
			return postOut;
		} else
			throw new ResourceNotFoundException("Post not exist with id: " + id);

	}

	// create Post with categoryID using PostInput
	@PostMapping("create/post")
	public Object createPost(@RequestBody(required = false) PostInput postInputForm) {
		Post newPost = new Post(postInputForm.getTitle(), postInputForm.getContent(), null, null);
		DateToSet.setDateOfCreatePost(newPost);
		postService.save(newPost);

		for (Long list : postInputForm.getCategoryIDs()) {
			cpService.insertCategoryPost(list, newPost.getPostId());
		}

		return ErrorMessage.Created("Create completed");
	}

	// update Post by id with categoryID and categoryName using PostInput
	@PutMapping("/update/post/{id}")
	public PostOut updatePost(@PathVariable("id") long id, @Valid @RequestBody PostInput json) {

		// check Post exist or not
		if (postService.getPostById(id) != null) {
			List<Category_id_name> categories = new ArrayList<>();
			Post post = postService.getPostById(id);
			PostOut postOut = new PostOut();

			// update Post
			if (json.getTitle() != post.getTitle())
				post.setTitle(json.getTitle());
			if (json.getContent() != post.getContent())
				post.setContent(json.getContent());

			postOut.setTitle(post.getTitle());
			postOut.setContent(post.getContent());
			List<Long> listCategoryID = cpService.getCategoryIDFromCategoryPost(id);

			for (long list : listCategoryID) {
				cpService.delCategoryPost(list, id);
			}

			List<Long> item = new ArrayList<>();
			for (Long listCategoryIDs : json.getCategoryIDs()) {
				cpService.insertCategoryPost(listCategoryIDs, id);
				item.add(listCategoryIDs);
			}

			for (Long lists : item) {
				String cate = categoryService.getNameById(lists);
				categories.add(new Category_id_name(lists, cate));
			}
			postOut.setCategories(categories);

			return postOut;
		} else {
			throw new ResourceNotFoundException("Post not exist with id: " + id);
		}

	}

	// delete Post by id with categoryID and categoryName
	@DeleteMapping("/delete/post/{id}")
	public Object delPost(@PathVariable("id") long postID) throws Exception {
		List<Long> listCategoryID = cpService.getCategoryIDFromCategoryPost(postID);
		if (listCategoryID.size() == 0)
			return ErrorMessage.notFount("Post not exist have list categoris with id: " + postID);
		for (Long list : listCategoryID) {
			cpService.delCategoryPost(list, postID);
		}
		postService.delete(postID);
		return ErrorMessage.OK("Delete completed");
	}

	// get Post with Pagination and search
	@GetMapping("/posts/search")
	public ResponseEntity<Page<Post>> getPostsWithSearchAndPagination(@RequestParam String query,
			@RequestParam Optional<Integer> page, @RequestParam Optional<Integer> pageSize, Pageable pageable) {

		List<Post> listPost = postService.getPostsWithSearchAndPagination(query, page.orElse(1), pageSize.orElse(10));
		Page<Post> pPage = new PageImpl<Post>(listPost, pageable, listPost.size());
		return new ResponseEntity<Page<Post>>(pPage, HttpStatus.OK);
	}

	// get Categories and Posts with pagination and search
	@GetMapping("/search")
	public ResponseEntity<Page<PostOut>> getCategoriesAndPostWithSearchAndPagination(@RequestParam String title,
			@RequestParam long categoryId, @RequestParam String name, @RequestParam Optional<Integer> page,
			@RequestParam Optional<Integer> pageSize, Pageable pageable) {
		List<Post> post = postService.getCategoriesAndPostWithSearchAndPagination(title, categoryId, name,
				page.orElse(1), pageSize.orElse(5));
		List<PostOut> postOut = new ArrayList<>();
		for (Post post1 : post) {
			Post post2 = postService.getById(post1.getPostId());
			PostOut pOut = new PostOut();
			List<Category_id_name> categories = new ArrayList<>();
			// category_ids
			List<Long> listCategoryID = cpService.getCategoryIDFromCategoryPost(post1.getPostId());

			pOut.setTitle(post2.getTitle());
			pOut.setContent(post2.getContent());
			for (Long long1 : listCategoryID) {
				String cate = categoryService.getNameById(long1);
				categories.add(new Category_id_name(long1, cate));
			}
			pOut.setCategories(categories);
			postOut.add(pOut);
		}
		Page<PostOut> pPage = new PageImpl<PostOut>(postOut, pageable, postOut.size());
		return new ResponseEntity<Page<PostOut>>(pPage, HttpStatus.OK);
	}

	// upload file image
	@PostMapping("/upload/image")
	public Object uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

		return ImageProcessing.uploadImage(file);
	}

	// download image
	@GetMapping("/downloadFile/{file:.+}")
	public Object download(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("file") String fileName) throws IOException {

		return ImageProcessing.downloadImage(fileName);
	}
}
