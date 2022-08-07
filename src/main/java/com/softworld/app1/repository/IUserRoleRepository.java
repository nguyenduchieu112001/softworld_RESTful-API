package com.softworld.app1.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softworld.app1.model.User_Role;

@Repository
public interface IUserRoleRepository extends CrudRepository<User_Role, Long> {

	@Transactional
	@Modifying
	@Query(value="delete form user_roles where user_id = :u and role_id = :r", nativeQuery = true)
	public void delUserRole(@Param("u") long userID, @Param("r") long roleID);
	
	@Transactional
	@Modifying
	@Query(value="insert into user_roles(user_id, role_id) values (:u, :r)", nativeQuery = true)
	public void insertUserRole(@Param("u") long userID, @Param("r") long roleID);
}
