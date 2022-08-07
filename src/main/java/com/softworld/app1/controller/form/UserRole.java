package com.softworld.app1.controller.form;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRole {
	
	private String username;
	private String rolename;
	
	public UserRole() {
		
	}
	
	public UserRole(String username, String rolename) {
		this.username = username;
		this.rolename = rolename;
	}
	
	
}
