package com.softworld.app1.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softworld.app1.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Long>{

	@Query(value = "select u from User u where userName = :name")
	public User getUserByUsername(@Param("name") String userName);

}
