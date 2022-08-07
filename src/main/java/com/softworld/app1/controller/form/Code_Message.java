package com.softworld.app1.controller.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Code_Message {

	private int status;
	private String error;
	private String message;
	
	public Code_Message() {
		
	}

	public Code_Message(int status, String error, String message) {
		super();
		this.status = status;
		this.error = error;
		this.message = message;
	}

}
