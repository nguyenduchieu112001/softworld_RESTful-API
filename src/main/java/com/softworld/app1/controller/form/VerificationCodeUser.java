package com.softworld.app1.controller.form;


import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class VerificationCodeUser {

	private String verificationCode;
	private String userName;
	private String gmail;
	private String expirationCodeTime;
	
	public VerificationCodeUser() {
		
	}
	
	public VerificationCodeUser(String verificationCode, String userName, String gmail, String expirationCodeTime) {
		super();
		this.verificationCode = verificationCode;
		this.userName = userName;
		this.gmail = gmail;
		this.expirationCodeTime = expirationCodeTime;
	}
	
	
}
