package com.softworld.app1.model;

import javax.persistence.*;

@Entity
@Table(name = "category_posts")
public class Category_Post {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="category_id")
	private long category_id;
	
	@Column(name="post_id")
	private long post_id;

	public long getCategory_id() {
		return category_id;
	}

	public void setCategory_id(long category_id) {
		this.category_id = category_id;
	}

	public long getPost_id() {
		return post_id;
	}

	public void setPost_id(long post_id) {
		this.post_id = post_id;
	}

	public Category_Post() {
		
	}
	public Category_Post(long category_id, long post_id) {
		this.category_id = category_id;
		this.post_id = post_id;
	}
	
}
