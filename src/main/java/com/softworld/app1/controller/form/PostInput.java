package com.softworld.app1.controller.form;

import java.util.List;


public class PostInput {

	private String title;
	private String content;
	private List<Integer> categoryIDs;
	
	
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
	public List<Integer> getCategoryIDs() {
		return categoryIDs;
	}
	public void setCategoryIDs(List<Integer> categoryIDs) {
		this.categoryIDs = categoryIDs;
	}
	public PostInput() {
		
	}
	
	public PostInput(String title, String content, List<Integer> categoryIDs) {
		this.title = title;
		this.content = content;
		this.categoryIDs = categoryIDs;
	}
	
	
}
