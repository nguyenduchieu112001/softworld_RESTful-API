package com.softworld.app1.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = { "username", "email" }))
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long userID;

	@NotBlank
	@Size(min = 2, max = 20, message = "username should have atleast 2 characters and no more than 20 characters")
	@Column(name = "username")
	@JsonFormat(shape = Shape.STRING)
	private String userName;

	@NotBlank
	@Size(min = 7, max = 50, message = "fullname should have atleast 8 characters and no more than 50 characters")
	@Column(name = "fullname")
	@JsonFormat(shape = Shape.STRING)
	private String fullName;

	@NotBlank
	@Size(min = 8, message = "password should have atleast 8 characters")
	@Column(name = "password")
	@JsonFormat(shape = Shape.STRING)
	private String password;

	@Column(name = "delete_at")
	@JsonFormat(pattern = "yyyy-MM-dd", shape = Shape.STRING)
	private String deleteAt;

	@NotBlank
	@Email
	@Column(name = "email")
	private String email;

	public User() {

	}

	public User(String userName, String fullName, String password, String deleteAt, @NotBlank @Email String email) {
		this.userName = userName;
		this.fullName = fullName;
		this.password = password;
		this.deleteAt = deleteAt;
		this.email = email;
	}

}
