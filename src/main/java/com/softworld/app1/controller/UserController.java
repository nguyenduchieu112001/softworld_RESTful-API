package com.softworld.app1.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softworld.app1.service.EmailSenderService;
import com.softworld.app1.service.RoleServiceImpl;
import com.softworld.app1.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.softworld.app1.controller.form.Code_Message;
import com.softworld.app1.controller.form.UserCode;
import com.softworld.app1.controller.form.UserInput;
import com.softworld.app1.controller.form.UserRole;
import com.softworld.app1.controller.form.VerificationCodeUser;
import com.softworld.app1.model.User;

import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private EmailSenderService mailSenderService;

	@Autowired
	private RoleServiceImpl roleService;

	private static final String SECRET_KEY = "123456789";
	private static final long EXPIRE_TIME = 86400000000L;

	// create User
	@PostMapping("/user/create")
	public ResponseEntity<User> addUser(@RequestBody User user) throws NoSuchAlgorithmException {
		// convert password to string SHA1
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(user.getPassword().getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++)
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));

		User u = new User(user.getUserName(), user.getFullName(), sb.toString(), user.getDeleteAt(), user.getEmail());
		return new ResponseEntity<User>(userService.save(u), HttpStatus.OK);

	}

	//update users with id
	@PutMapping("/user/edit/{id}")
	public ResponseEntity<User> updateUser(@PathVariable("id") long userID, @RequestBody User user) {
		User u = userService.getById(userID);
		u.setUserName(user.getUserName());
		u.setFullName(user.getFullName());
		u.setPassword(DigestUtils.sha1Hex(user.getPassword().toString()));
		u.setDeleteAt(user.getDeleteAt());
		User userUpdate = userService.save(u);
		return new ResponseEntity<User>(userUpdate, HttpStatus.OK);
	}

	//delete users with id
	@PutMapping("/user/delete/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable("id") long userID) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		User user = userService.getById(userID);
		user.setDeleteAt(formatter.format(date));
		userService.save(user);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	// check user and password (login)
	@PostMapping("/login")
	public Object Login(@RequestBody UserInput uInput, HttpServletRequest request) throws Exception {
		// convert password to SHA1
		// DigestUtils.sha1Hex(uInput.getPassword().toString())
		UserRole ur = new UserRole();
		
		ur.setUsername(uInput.getUsername());
		ur.setRolename(roleService.getRoleName(ur.getUsername()));
		
		User user = userService.getUserName(uInput.getUsername());
		if (user != null && DigestUtils.sha1Hex(uInput.getPassword().toString()).equals(user.getPassword())) {
			if (user.getDeleteAt() == null) {

				request.getSession().setAttribute("userrole", ur);

				String token = Jwts.builder().claim("fullName", user.getFullName())
						.claim("role", ur.getRolename().toString())
						.setSubject((uInput.getUsername()))
						.setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + EXPIRE_TIME * 1000))
						.signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
				return new Code_Message(200, "OK", token);
			} else {
				return new Code_Message(405, "Method Not Allowed", "Account disabled");

			}
		} else {
			return new Code_Message(401, "Unauthorized", "Login information is incorrect");
		}

	}

	// convert JWT to json
	@GetMapping("/convert")
	public Object convertJwtToJson(@RequestParam String token) {
		String[] chunks = token.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();

		String payload = new String(decoder.decode(chunks[1]));

		return payload;
	}

	// forgot password, check user name
	@PostMapping("/forgotpassword")
	public Object forgotPassword(@RequestParam String username, HttpSession session) {
		User user = userService.getUserName(username);
		VerificationCodeUser verUser = new VerificationCodeUser();

		// save user name into session
		verUser.setUserName(username);
		session.setAttribute("user", verUser);

		if (user != null) {
			if (user.getDeleteAt() != null) {
				return new Code_Message(405, "Method Not Allowed", "Account disabled");
			}
			// random verification code
			String verificationCode = "";
			for (int i = 0; i < 6; i++) {
				Random x = new Random();
				verificationCode += x.nextInt(9);
				verUser.setVerificationCode(verificationCode);
				session.setAttribute("user", verUser);

				Date oldDate = new Date(); // oldDate == current time
				SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				Date newDate = new Date(oldDate.getTime() + TimeUnit.MINUTES.toMillis(2)); // Add 2 minutes
				verUser.setExpirationCodeTime(formatter.format(newDate));
				session.setAttribute("user", verUser);

				userService.save(user);

			}
			// send verification code into email "nguyenduchieu112001@gmail.com" with title
			// "Mã xác thực"
			mailSenderService.sendEmail("nguyenduchieu112001@gmail.com", "Mã xác thực", verificationCode.toString());

			return new Code_Message(200, "OK", "UserName correct");
		}

		else
			return new Code_Message(401, "Unauthorized", "Login information is incorrect");
	}

	// reset Password when enter verificationCode true
	@PostMapping("/resetpassword")
	public Object resetPassword(@RequestBody UserCode userCode, HttpServletRequest request) {

		// get user name from session
		VerificationCodeUser verUser = null;
		HttpSession session = request.getSession();
		if (session.getAttributeNames() != null)
			verUser = (VerificationCodeUser) session.getAttribute("user");

		User u = userService.getUserName(verUser.getUserName());

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date(date.getTime());
		String time = formatter.format(currentTime);

		while (userCode.getCode().compareTo(verUser.getVerificationCode()) == 0) {
			if (time.compareTo(verUser.getExpirationCodeTime()) == 1) {
				return new Code_Message(405, "Method Not Allowed", "Verification Code is expired");
			} else {
				u.setPassword(DigestUtils.sha1Hex(userCode.getPassword().toString()));
				userService.save(u);
				return u;
			}
		}
		return new Code_Message(401, "Unauthorized", "Verification Code is not correct");
	}

}
