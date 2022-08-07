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
	public ResponseEntity<Category_Post> addCategoryPost(@RequestBody Category_Post cp) {
		Category_Post cate_p = null;
		try {
			cate_p = cpService.save(cp);
		} catch (Exception e) {
			e.getMessage();
		}
		return new ResponseEntity<Category_Post>(cate_p, HttpStatus.OK);
	}

	//insert value into category_posts
	@PostMapping("/categorypost/insert")
	public PostInput insert(@RequestBody PostInput json) {
		long post_id = 0;
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Post p = new Post(json.getTitle(), json.getContent(), formatter.format(date), formatter.format(date));
		postService.save(p);
		post_id = p.getPostId();

		Category_Post cp = null;
		for (int long1 : json.getCategoryIDs()) {
			cp = new Category_Post(long1, post_id);
			cpService.save(cp);
		}
		return json;
	}

}
