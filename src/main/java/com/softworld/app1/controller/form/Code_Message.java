package com.softworld.app1.controller.form;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Code_Message {

	private Date timestamp;
	private int status;
	private String error;
	private String message;

	public Code_Message() {

	}

	public Code_Message(Date timestamp, int status, String error, String message) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
	}

}
