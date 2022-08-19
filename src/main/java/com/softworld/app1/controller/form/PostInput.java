package com.softworld.app1.controller.form;

import java.util.List;


public class PostInput {

	private String title;
	private String content;
	private List<Long> categoryIDs;
	
	
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
	public List<Long> getCategoryIDs() {
		return categoryIDs;
	}
	public void setCategoryIDs(List<Long> categoryIDs) {
		this.categoryIDs = categoryIDs;
	}
	public PostInput() {
		
	}
	
	public PostInput(String title, String content, List<Long> categoryIDs) {
		this.title = title;
		this.content = content;
		this.categoryIDs = categoryIDs;
	}
	
	
}
