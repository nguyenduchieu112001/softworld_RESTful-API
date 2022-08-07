package com.softworld.app1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softworld.app1.model.User_Role;
import com.softworld.app1.service.UserRoleServiceImpl;

@RestController
@RequestMapping("/api")
public class UserRoleController {

	@Autowired
	private UserRoleServiceImpl urService;
	
	//create value for user_roles
	@PostMapping("userrole/create")
	public ResponseEntity<User_Role> addUserRole(@RequestBody User_Role ur){
		return new ResponseEntity<User_Role>(urService.save(ur), HttpStatus.OK);
	}
}
