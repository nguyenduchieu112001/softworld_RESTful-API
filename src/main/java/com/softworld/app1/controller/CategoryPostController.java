package com.softworld.app1.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.softworld.app1.controller.form.PostInput;
import com.softworld.app1.model.Category_Post;
import com.softworld.app1.model.Post;
import com.softworld.app1.service.CategoryPostServiceImpl;
import com.softworld.app1.service.PostServiceImpl;

@RestController
@RequestMapping("/api")
public class CategoryPostController {

	@Autowired
	private CategoryPostServiceImpl cpService;

	@Autowired
	private PostServiceImpl postService;

	// create value for category_posts
	@RequestMapping(value = "categorypost/create", method = RequestMethod.POST)
	public ResponseEntity<Category_Post> addCategoryPost(@RequestBody Category_Post categoryPostForm) {
		Category_Post cate_post = null;
		try {
			cate_post = cpService.save(categoryPostForm);
		} catch (Exception e) {
			e.getMessage();
		}
		return new ResponseEntity<Category_Post>(cate_post, HttpStatus.OK);
	}

	//insert value into category_posts
	@PostMapping("/categorypost/insert")
	public PostInput insert(@RequestBody PostInput json) {
		long post_id = 0;
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Post post = new Post(json.getTitle(), json.getContent(), formatter.format(date), formatter.format(date));
		postService.save(post);
		post_id = post.getPostId();

		Category_Post category_post = null;
		for (Long long1 : json.getCategoryIDs()) {
			category_post = new Category_Post(long1, post_id);
			cpService.save(category_post);
		}
		return json;
	}

}
