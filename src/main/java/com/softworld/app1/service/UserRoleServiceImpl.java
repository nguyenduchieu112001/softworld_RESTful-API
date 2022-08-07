package com.softworld.app1.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softworld.app1.exception.ResourceNotFoundException;
import com.softworld.app1.model.User_Role;
import com.softworld.app1.repository.IUserRoleRepository;

@Service
public class UserRoleServiceImpl{

	@Autowired
	private IUserRoleRepository ulRepository;

	public User_Role getById(long id) {
		return ulRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User_Role not found by id:" + id));
	}

	public Iterable<User_Role> findAll() {
		return ulRepository.findAll();
	}

	public User_Role save(User_Role t) {
		return ulRepository.save(t);
	}

	public void delUserRole(long userId, long roleId) {
		ulRepository.delUserRole(userId, roleId);
	}
	
	public void insertUserRole(long userId, long roleId) {
		ulRepository.insertUserRole(userId, roleId);
	}
	

}
