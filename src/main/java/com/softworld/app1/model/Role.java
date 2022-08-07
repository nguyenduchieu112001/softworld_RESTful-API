package com.softworld.app1.model;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long roleId;
	
	@Column(name = "rolename", nullable = false, unique = true, length = 50)
	private String roleName;

	public Role() {
		
	}
	
	public Role(String roleName) {
		this.roleName = roleName;
	}
	
	
}
