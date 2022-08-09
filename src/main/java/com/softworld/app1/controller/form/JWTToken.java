package com.softworld.app1.controller.form;

import java.util.Base64;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTToken {

	String token;
	private static final String SECRET_KEY = "123456789";
	private static final long EXPIRE_TIME = 86400000000L;
	
	public static String token(String fullName, String roleName, String userName) {
		return Jwts.builder().claim("fullName", fullName)
				.claim("role", roleName.toString())
				.setSubject((userName))
				.setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + EXPIRE_TIME * 1000))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
	}
	
	public static String payload(String token) {
		String[] chunks = token.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();

		String payload = new String(decoder.decode(chunks[1]));

		return payload;
	}
}
