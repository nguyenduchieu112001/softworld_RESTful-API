package com.softworld.app1.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softworld.app1.model.Role;

@Repository
public interface IRoleRepository extends CrudRepository<Role, Long>{

	@Query(value = "select roles.rolename\r\n"
			+ "from (roles join user_roles \r\n"
			+ "on roles.id = user_roles.role_id)\r\n"
			+ "join users \r\n"
			+ "on user_roles.user_id = users.id\r\n"
			+ "where users.username = :u", nativeQuery = true)
	public String getRoleName(@Param("u") String username);
}
