package com.softworld.app1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.softworld.app1.model.User;
import com.softworld.app1.repository.IRoleRepository;
import com.softworld.app1.repository.IUserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private IRoleRepository roleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getUserByUsername(username);
		if (user == null)
			throw new UsernameNotFoundException("User '" + username + "' not found.");

		UserDetails u = org.springframework.security.core.userdetails.User.withUsername(user.getUserName())
				.password(user.getPassword()).authorities(roleRepository.getRoleName(username)).build();
		return u;
	}

}
