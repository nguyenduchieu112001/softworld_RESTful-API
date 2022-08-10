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
import com.softworld.app1.controller.form.ImageProcessing;
import com.softworld.app1.controller.form.PostInput;
import com.softworld.app1.model.Post;
import com.softworld.app1.service.CategoryPostServiceImpl;
import com.softworld.app1.service.CategoryServiceImpl;
import com.softworld.app1.service.PostServiceImpl;

@RestController
@RequestMapping("/api")
public class PostController {

	@Autowired
	private PostServiceImpl postService;

	@Autowired
	private CategoryPostServiceImpl cpService;

	@Autowired
	private CategoryServiceImpl categoryService;

	// get all Post's data
	@RequestMapping(value = "posts", method = RequestMethod.GET)
	@PreAuthorize("permitAll()")
	public Object getAllPosts(HttpServletRequest request) {
		return new ResponseEntity<Iterable<Post>>(postService.findAll(), HttpStatus.OK);
	}

	// find Post's data by ID
	@GetMapping("/post/{id}")
	@PreAuthorize("permitAll()")
	public Object getByPostId(@PathVariable("id") long id, HttpServletRequest request) {
		return new ResponseEntity<Post>(postService.getById(id), HttpStatus.OK);
	}

	// create 1 category's data into database categories (schema app1)
	@RequestMapping(value = "post/create", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority(\"ROLE_EDITOR\", \"ROLE_ADMIN\")")
	public Object addPost(@Valid @RequestBody Post post, HttpServletRequest request) {

		Post p = new Post();
		// if create_at and update_at have null value then assign value equal to current time
		DateToSet.setDateOfCreatePost(post);
		p = postService.save(post);
		return new ResponseEntity<Post>(p, HttpStatus.OK);

	}

	// update 1 category's data into database categories(schema app1)
	@RequestMapping(value = "/post/edit/{id}", method = RequestMethod.PUT)
	@PreAuthorize("hasAnyAuthority(\"ROLE_EDITOR\", \"ROLE_ADMIN\")")
	public Object updateCategory(@RequestBody Post postForm, @PathVariable("id") long id, HttpServletRequest request) {

		Post p = postService.getById(id);
		if (p == null)
			return ResponseEntity.notFound().build();
		else {
			p.setTitle(postForm.getTitle());

			p.setContent(postForm.getContent());

			DateToSet.setDateOfUpdatePost(postForm, postForm);

			Post updatePost = postService.save(p);
			return new ResponseEntity<Post>(updatePost, HttpStatus.OK);
		}

	}

	// delete Post's data by Id
	@DeleteMapping("/post/delete/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public Object deletePostById(@PathVariable("id") long id, HttpServletRequest request) throws Exception {
		return new ResponseEntity<Optional<Post>>(postService.delete(id), HttpStatus.OK);
	}

	// get Post by id with categoryID and categoryName
	@GetMapping("/get/post/{id}")
	public PostOut getPostById(@PathVariable(value = "id") long id, HttpStatus status) {

		Post post = postService.getPostById(id);
		if (post != null) {
			PostOut postOut = new PostOut();
			List<Category_id_name> categories = new ArrayList<>();
			// category_ids
			List<Long> listCategoryID = cpService.getAll(id);

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

	// update Post by id with categoryID and categoryName using PostInput
	@PutMapping("/update/post/{id}")
	public PostOut updatePost(@PathVariable("id") long id, @Valid @RequestBody PostInput json) {

		if (postService.getPostById(id) != null) {
			List<Category_id_name> categories = new ArrayList<>();
			Post post = postService.getPostById(id);
			PostOut postOut = new PostOut();

			if (json.getTitle() != post.getTitle())
				post.setTitle(json.getTitle());
			if (json.getContent() != post.getContent())
				post.setContent(json.getContent());

			postOut.setTitle(post.getTitle());
			postOut.setContent(post.getContent());
			List<Long> listCategoryID = cpService.getAll(id);

			for (long list : listCategoryID) {
				cpService.delCategoryPost(list, id);
			}

			List<Integer> item = new ArrayList<>();
			for (int listCategoryIDs : json.getCategoryIDs()) {
				cpService.insertCategoryPost(listCategoryIDs, id);
				item.add(listCategoryIDs);
			}

			for (Integer lists : item) {
				String cate = categoryService.getNameById(lists);
				categories.add(new Category_id_name(lists, cate));
			}
			postOut.setCategories(categories);

			return postOut;
		} else {
			throw new ResourceNotFoundException("Post not exist id:" + id);
		}

	}

	// delete Post by id with categoryID and categoryName
	@DeleteMapping("/delete/post/{id}")
	public void delPost(@PathVariable("id") long postID) throws Exception {
		List<Long> listCategoryID = cpService.getAll(postID);
		if (listCategoryID.size() == 0)
			throw new ResourceNotFoundException("Post not exist have list categoris with id:" + postID);
		for (Long list : listCategoryID) {
			cpService.delCategoryPost(list, postID);
		}

		cpService.delete(postID);
		throw new ResourceNotFoundException("Delete completed!");
	}

	// find Posts with Pagination and sort
	@GetMapping("/posts/{offset}/{pagesize}/{field}")
	public ResponseEntity<Page<Post>> findPostsWithPaginationAndSorting(@PathVariable("offset") int offset,
			@PathVariable("pagesize") int pageSize, @PathVariable String field) {
		Page<Post> post_page = postService.findPostsWithPaginationAndSorting(offset, pageSize, field);
		return new ResponseEntity<Page<Post>>(post_page, HttpStatus.OK);
	}

	// find Posts with Pagination
	@GetMapping("/posts/{offset}/{pagesize}")
	public ResponseEntity<Page<Post>> findPostsWithPagination(@PathVariable("offset") int offset,
			@PathVariable("pagesize") int pageSize) {
		Page<Post> post_page = postService.findPostsWithPagination(offset, pageSize);
		return new ResponseEntity<Page<Post>>(post_page, HttpStatus.OK);
	}

	// get Posts with Pagination
	@GetMapping("/get/posts/{offset}/{pageSize}")
	public ResponseEntity<List<PostOut>> getPostsWithPagination(@PathVariable int offset, @PathVariable int pageSize) {
		if (offset <= 0)
			offset = 1;
		List<Post> listPost = postService.getPostsWithPagination(offset, pageSize);
		List<PostOut> postOut = new ArrayList<>();
		for (Post post : listPost) {
			Post post1 = postService.getById(post.getPostId());
			PostOut pOut = new PostOut();
			List<Category_id_name> categories = new ArrayList<>();
			// category_ids
			List<Long> l = cpService.getAll(post.getPostId());

			pOut.setTitle(post1.getTitle());
			pOut.setContent(post1.getContent());
			for (Long long1 : l) {
				String cate = categoryService.getNameById(long1);
				categories.add(new Category_id_name(long1, cate));
			}
			pOut.setCategories(categories);
			postOut.add(pOut);
		}
		return new ResponseEntity<List<PostOut>>(postOut, HttpStatus.OK);

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
			List<Long> listCategoryID = cpService.getAll(post1.getPostId());

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
