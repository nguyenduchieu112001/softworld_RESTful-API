package com.softworld.app1.controller.form;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldsToSearch {

	private Optional<Integer> page;
	private Optional<Integer> pageSize;
	
}
