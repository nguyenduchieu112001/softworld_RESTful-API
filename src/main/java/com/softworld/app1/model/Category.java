package com.softworld.app1.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@Entity
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long categoryID;

	@NotNull
	@Size(min = 2, message = "category name should have atleast 2 characters")
	@Column(name = "name")
	@JsonFormat(shape = Shape.STRING)
	private String categoryName;

	@Column(name = "created_at")
	@JsonFormat(pattern = "yyyy-MM-dd", shape = Shape.STRING)
	private String createdAt;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd", shape = Shape.STRING)
	private String updatedAt;

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Category() {

	}

	public Category(String categoryName, String createdAt, String updatedAt) {
		this.categoryName = categoryName;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

}
