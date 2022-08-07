package com.softworld.app1.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softworld.app1.exception.ResourceNotFoundException;
import com.softworld.app1.model.Role;
import com.softworld.app1.repository.IRoleRepository;

@Service
public class RoleServiceImpl {

	@Autowired
	private IRoleRepository roleRepository;
	
	public Role getById(long id) {
		return roleRepository.findById(id).orElseThrow(() ->
										new ResourceNotFoundException("Role not found by id:" +id));
	}

	public String getRoleName(String username) {
		return roleRepository.getRoleName(username);
	}
}
