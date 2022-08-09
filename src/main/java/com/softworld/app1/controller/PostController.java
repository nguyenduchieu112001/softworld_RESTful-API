package com.softworld.app1.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		// if create_at and update_at have null value then assign value equal to current
		// time
		if (post.getUpdatedAt() == null)
			post.setUpdatedAt(formatter.format(date));
		if (post.getCreatedAt() == null)
			post.setCreatedAt(formatter.format(date));
		p = postService.save(post);
		return new ResponseEntity<Post>(p, HttpStatus.OK);

	}

	// update 1 category's data into database categories(schema app1)
	@RequestMapping(value = "/post/edit/{id}", method = RequestMethod.PUT)
	@PreAuthorize("hasAnyAuthority(\"ROLE_EDITOR\", \"ROLE_ADMIN\")")
	public Object updateCategory(@RequestBody Post postForm, @PathVariable("id") long id, HttpServletRequest request) {

		Post p = postService.getById(id);
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		if (p == null)
			return ResponseEntity.notFound().build();
		else {
			p.setTitle(postForm.getTitle());

			p.setContent(postForm.getContent());

			if (postForm.getCreatedAt() == null)
				p.setCreatedAt(formatter.format(date));

			if (postForm.getUpdatedAt() == null)
				p.setUpdatedAt(formatter.format(date));

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

		Post p = postService.getPostById(id);
		if (p != null) {
			PostOut cOut = new PostOut();
			List<Category_id_name> categories = new ArrayList<>();
			// category_ids
			List<Long> l = cpService.getAll(id);

			cOut.setTitle(p.getTitle());
			cOut.setContent(p.getContent());
			for (Long long1 : l) {
				String cate = categoryService.getNameById(long1);
				categories.add(new Category_id_name(long1, cate));
			}
			cOut.setCategories(categories);
			return cOut;
		} else
			throw new ResourceNotFoundException("Post not exist with id: " + id);

	}

	// update Post by id with categoryID and categoryName using PostInput
	@PutMapping("/update/post/{id}")
	public PostOut updatePost(@PathVariable("id") long id, @Valid @RequestBody PostInput json) {

		if (postService.getPostById(id) != null) {
			List<Category_id_name> categories = new ArrayList<>();
			Post p = postService.getPostById(id);
			PostOut cOut = new PostOut();

			if (json.getTitle() != p.getTitle())
				p.setTitle(json.getTitle());
			if (json.getContent() != p.getContent())
				p.setContent(json.getContent());

			cOut.setTitle(p.getTitle());
			cOut.setContent(p.getContent());
			List<Long> j = cpService.getAll(id);

			for (long l : j) {
				cpService.delCategoryPost(l, id);
			}

			List<Integer> item = new ArrayList<>();
			for (int long1 : json.getCategoryIDs()) {
				cpService.insertCategoryPost(long1, id);
				item.add(long1);
			}

			for (Integer long2 : item) {
				String cate = categoryService.getNameById(long2);
				categories.add(new Category_id_name(long2, cate));
			}
			cOut.setCategories(categories);

			return cOut;
		} else {
			throw new ResourceNotFoundException("Post not exist id:" + id);
		}

	}

	// delete Post by id with categoryID and categoryName
	@DeleteMapping("/delete/post/{id}")
	public void delPost(@PathVariable("id") long postID) throws Exception {
		List<Long> j = cpService.getAll(postID);
		if (j.size() == 0)
			throw new ResourceNotFoundException("Post not exist have list categoris with id:" + postID);
		for (Long long1 : j) {
			cpService.delCategoryPost(long1, postID);
		}

		cpService.delete(postID);
		throw new ResourceNotFoundException("Delete completed!");
	}

	// find Posts with Pagination and sort
	@GetMapping("/posts/{offset}/{pagesize}/{field}")
	public ResponseEntity<Page<Post>> findPostsWithPaginationAndSorting(@PathVariable("offset") int offset,
			@PathVariable("pagesize") int pageSize, @PathVariable String field) {
		Page<Post> p = postService.findPostsWithPaginationAndSorting(offset, pageSize, field);
		return new ResponseEntity<Page<Post>>(p, HttpStatus.OK);
	}

	// find Posts with Pagination
	@GetMapping("/posts/{offset}/{pagesize}")
	public ResponseEntity<Page<Post>> findPostsWithPagination(@PathVariable("offset") int offset,
			@PathVariable("pagesize") int pageSize) {
		Page<Post> p = postService.findPostsWithPagination(offset, pageSize);
		return new ResponseEntity<Page<Post>>(p, HttpStatus.OK);
	}

	// get Posts with Pagination
	@GetMapping("/get/posts/{offset}/{pageSize}")
	public ResponseEntity<List<PostOut>> getPostsWithPagination(@PathVariable int offset, @PathVariable int pageSize) {
		if (offset <= 0)
			offset = 1;
		List<Post> p = postService.getPostsWithPagination(offset, pageSize);
		List<PostOut> postOut = new ArrayList<>();
		for (Post post : p) {
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

		List<Post> p = postService.getPostsWithSearchAndPagination(query, page.orElse(1), pageSize.orElse(10));
		Page<Post> pPage = new PageImpl<Post>(p, pageable, p.size());
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
		for (Post p : post) {
			Post p1 = postService.getById(p.getPostId());
			PostOut pOut = new PostOut();
			List<Category_id_name> categories = new ArrayList<>();
			// category_ids
			List<Long> l = cpService.getAll(p.getPostId());

			pOut.setTitle(p1.getTitle());
			pOut.setContent(p1.getContent());
			for (Long long1 : l) {
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
