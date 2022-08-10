package com.softworld.app1.controller.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

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

	public static String randomVerificationCode(HttpSession session) {
		VerificationCodeUser verUser = new VerificationCodeUser();
		String verificationCode = "";
		for (int i = 0; i < 6; i++) {
			Random x = new Random();
			verificationCode += x.nextInt(9);
			verUser.setVerificationCode(verificationCode);
			session.setAttribute("user", verUser);

			Date oldDate = new Date(); // oldDate == current time
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			Date newDate = new Date(oldDate.getTime() + TimeUnit.MINUTES.toMillis(1)); // Add 1 minutes
			verUser.setExpirationCodeTime(formatter.format(newDate));
			session.setAttribute("user", verUser);
		}
		return verificationCode;
	}
}
