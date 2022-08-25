package com.softworld.app1.controller;

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


import com.softworld.app1.controller.form.ErrorMessage;
import com.softworld.app1.controller.form.JWTToken;
import com.softworld.app1.controller.form.UserCode;
import com.softworld.app1.controller.form.UserInput;
import com.softworld.app1.controller.form.UserRole;
import com.softworld.app1.controller.form.VerificationCodeUser;
import com.softworld.app1.model.User;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired(required = false)
	private UserService userService;

	@Autowired(required = false)
	private EmailSenderService mailSenderService;

	@Autowired(required = false)
	private RoleServiceImpl roleService;

	// create User
	@PostMapping("/user/create")
	public ResponseEntity<User> addUser(@Valid @RequestBody User user) throws NoSuchAlgorithmException {

		User u = new User(user.getUserName(), user.getFullName(), DigestUtils.sha1Hex(user.getPassword().toString()),
				user.getDeleteAt(), user.getEmail());
		return new ResponseEntity<User>(userService.save(u), HttpStatus.OK);

	}

	// update users with id
	@PutMapping("/user/edit/{id}")
	public ResponseEntity<User> updateUser(@PathVariable("id") long userID, @RequestBody User userForm) {
		User user = userService.getById(userID);
		user.setUserName(userForm.getUserName());
		user.setFullName(userForm.getFullName());
		user.setPassword(DigestUtils.sha1Hex(userForm.getPassword().toString()));
		user.setEmail(userForm.getEmail());
		user.setDeleteAt(userForm.getDeleteAt());
		User userUpdate = userService.save(user);
		return new ResponseEntity<User>(userUpdate, HttpStatus.OK);
	}

	// delete users with id
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
	public Object Login(@Valid @RequestBody UserInput uInput, HttpServletRequest request) throws Exception {
		// convert password to SHA1
		// DigestUtils.sha1Hex(uInput.getPassword().toString())
		UserRole userRole = new UserRole();
		//set user with role
		userRole.setUsername(uInput.getUsername());
		userRole.setRolename(roleService.getRoleName(userRole.getUsername()));

		User user = userService.getUserName(uInput.getUsername());
		if (user != null && DigestUtils.sha1Hex(uInput.getPassword().toString()).equals(user.getPassword())) {
			if (user.getDeleteAt() == null) {
				request.getSession().setAttribute("userrole", userRole);
				String token = JWTToken.token(user.getFullName(), userRole.getRolename(), user.getUserName());
				return ErrorMessage.OK(token);
			} else {
				return ErrorMessage.methodNotAllowed("Account disabled");
			}
		} else {
			return ErrorMessage.unAuthorized("Information login is incorrect");
		}
	}

	// convert JWT to json
	@GetMapping("/convert")
	public Object convertJwtToJson(@RequestParam String token) {
		return JWTToken.payload(token);
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
				return ErrorMessage.methodNotAllowed("Account disabled");
			}

			// random verification code and set Expiration Code Time
			String verifyCode = VerificationCodeUser.randomVerificationCode(session);

			// send verification code into email "email of user" with title
			// "Mã xác thực" and verification code
			mailSenderService.sendEmail(user.getEmail(), "Mã xác thực", verifyCode.toString());
			return ErrorMessage.OK("UserName correct");
		} else
			return ErrorMessage.unAuthorized("Login information is incorrect");
	}

	// reset Password when enter verificationCode true
	@PostMapping("/resetpassword")
	public Object resetPassword(@RequestBody UserCode userCode, HttpServletRequest request) {

		// get user name from session
		VerificationCodeUser verUser = null;
		HttpSession session = request.getSession();
		if (session.getAttributeNames() != null)
			verUser = (VerificationCodeUser) session.getAttribute("user");

		User user = userService.getUserName(verUser.getUserName());

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date(date.getTime());
		String time = formatter.format(currentTime);

		if (userCode.getCode().compareTo(verUser.getVerificationCode()) == 0) {
			if (time.compareTo(verUser.getExpirationCodeTime()) == 1) {
				return ErrorMessage.methodNotAllowed("Verification Code is expired");
			} else {
				user.setPassword(DigestUtils.sha1Hex(userCode.getPassword().toString()));
				userService.save(user);
				return user;
			}
		}
		return ErrorMessage.unAuthorized("Verification Code is not correct");
	}

}
