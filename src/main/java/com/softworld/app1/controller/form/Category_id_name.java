package com.softworld.app1.controller.form;

public class Category_id_name {
	
	private long CategoryID;
	
	private String categoryName;

	public long getCategoryID() {
		return CategoryID;
	}

	public void setCategoryID(long categoryID) {
		CategoryID = categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public Category_id_name() {
		
	}

	public Category_id_name(long categoryID, String categoryName) {
		CategoryID = categoryID;
		this.categoryName = categoryName;
	}

	
}
