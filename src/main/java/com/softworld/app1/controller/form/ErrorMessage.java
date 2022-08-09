package com.softworld.app1.controller.form;

import java.util.Date;

public class ErrorMessage {

	public static Code_Message OK(String message) {
		Code_Message codeMessage = new Code_Message();
		codeMessage.setTimestamp(new Date());
		codeMessage.setStatus(200);
		codeMessage.setError("OK");
		codeMessage.setMessage(message);

		return codeMessage;
	}

	public static Code_Message methodNotAllowed(String message) {
		Code_Message codeMessage = new Code_Message();
		codeMessage.setTimestamp(new Date());
		codeMessage.setStatus(405);
		codeMessage.setError("Method Not Allowed");
		codeMessage.setMessage(message);

		return codeMessage;
	}
	
	public static Code_Message unAuthorized(String message) {
		Code_Message codeMessage = new Code_Message();
		codeMessage.setTimestamp(new Date());
		codeMessage.setStatus(401);
		codeMessage.setError("Unauthorized");
		codeMessage.setMessage(message);

		return codeMessage;
	}
	
	public static Code_Message notAcceptable(String message) {
		Code_Message codeMessage = new Code_Message();
		codeMessage.setTimestamp(new Date());
		codeMessage.setStatus(406);
		codeMessage.setError("Not Acceptable");
		codeMessage.setMessage(message);

		return codeMessage;
	}
	
	public static Code_Message notFount(String message) {
		Code_Message codeMessage = new Code_Message();
		codeMessage.setTimestamp(new Date());
		codeMessage.setStatus(404);
		codeMessage.setError("Not Found");
		codeMessage.setMessage(message);

		return codeMessage;
	}
}
