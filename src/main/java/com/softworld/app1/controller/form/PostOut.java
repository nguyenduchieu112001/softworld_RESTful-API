package com.softworld.app1.controller.form;

import java.util.List;

public class PostOut {

	String title;
	String content;
	List<Category_id_name> categories;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<Category_id_name> getCategories() {
		return categories;
	}
	public void setCategories(List<Category_id_name> categories) {
		this.categories = categories;
	}
	
	public PostOut() {
		
	}
	
	public PostOut(String title, String content, List<Category_id_name> categories) {
		this.title = title;
		this.content = content;
		this.categories = categories;
	}

	
	
	
	
}
