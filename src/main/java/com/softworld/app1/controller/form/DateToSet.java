package com.softworld.app1.controller.form;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.softworld.app1.model.Category;
import com.softworld.app1.model.Post;

public class DateToSet {

	static Date date = new Date();
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public static void setDateOfCreateCategory(Category category) {
		if (category.getUpdatedAt() == null)
			category.setUpdatedAt(formatter.format(date));
		if (category.getCreatedAt() == null)
			category.setCreatedAt(formatter.format(date));
	}

	public static void setDateOfUpdateCategory(Category category, Category categoryForm) {
		if (categoryForm.getCreatedAt() == null)
			category.setUpdatedAt(formatter.format(date));
		else category.setCreatedAt(categoryForm.getCreatedAt());
		if (categoryForm.getCreatedAt() == null)
			category.setCreatedAt(formatter.format(date));
		else category.setUpdatedAt(categoryForm.getUpdatedAt());
	}

	public static void setDateOfCreatePost(Post post) {
		if (post.getUpdatedAt() == null)
			post.setUpdatedAt(formatter.format(date));
		if (post.getCreatedAt() == null)
			post.setCreatedAt(formatter.format(date));
	}
	
	public static void setDateOfUpdatePost(Post post, Post postForm) {
		if (postForm.getCreatedAt() == null)
			post.setUpdatedAt(formatter.format(date));
		else post.setCreatedAt(postForm.getCreatedAt());
		if (postForm.getCreatedAt() == null)
			post.setCreatedAt(formatter.format(date));
		else post.setUpdatedAt(postForm.getUpdatedAt());
	}
}
