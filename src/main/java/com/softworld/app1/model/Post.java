package com.softworld.app1.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@Entity
@Table(name = "posts")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long postId;

	@NotNull
	@Size(min = 2, message = "title should have atleast 2 characters")
	@Column(name = "title")
	private String title;

	@NotNull
	@Size(min = 2, message = "content should have atleast 2 characters")
	@Column(name = "content")
	private String content;

	@Column(name = "created_at")
	@JsonFormat(pattern = "yyyy-MM-dd", shape = Shape.STRING)
	private String createdAt;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd", shape = Shape.STRING)
	private String updatedAt;

	public long getPostId() {
		return postId;
	}

	public void setPostId(long postId) {
		this.postId = postId;
	}

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

	public Post() {

	}

	public Post(String title, String content, String createdAt, String updatedAt) {
		super();
		this.title = title;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}


}
