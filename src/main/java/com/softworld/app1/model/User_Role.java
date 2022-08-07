package com.softworld.app1.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
public class User_Role {

	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private long userId;
	
	@Column(name = "role_id")
	private long roleId;
	
	public User_Role() {
		
	}

	public User_Role(long userId, long roleId) {
		super();
		this.userId = userId;
		this.roleId = roleId;
	}
	
	
}
