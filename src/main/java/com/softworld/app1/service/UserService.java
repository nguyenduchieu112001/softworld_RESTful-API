package com.softworld.app1.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softworld.app1.exception.ResourceNotFoundException;
import com.softworld.app1.model.User;
import com.softworld.app1.repository.IUserRepository;

//import lombok.extern.slf4j.Slf4j;

@Service 
//@Slf4j
public class UserService implements ICommonService<User>{

	@Autowired
	private IUserRepository userRepository;

	@Override
	public Iterable<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User save(User t) {
		return userRepository.save(t);
	}

	@Override
	public User update(User t, long id) {
		return userRepository.save(t);
	}

	@Override
	public User getById(long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found by id:" + id));
	}

	@Override
	public void delete(long id){
		userRepository.deleteById(id);
	}

	public User getUserName(String userName) {
		return userRepository.getUserByUsername(userName);
	}
}
