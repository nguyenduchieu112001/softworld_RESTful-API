package com.softworld.app1.controller;

import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.softworld.app1.controller.form.UserInput;
import com.softworld.app1.controller.form.UserRole;
import com.softworld.app1.model.User;
import com.softworld.app1.service.EmailSenderService;
import com.softworld.app1.service.RoleServiceImpl;
import com.softworld.app1.service.UserService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = UserController.class)
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SuppressWarnings("deprecation")
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@MockBean
	private UserService userService;
	@MockBean
	private EmailSenderService mailSenderService;
	@MockBean
	private RoleServiceImpl roleService;
	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	// truyen du lieu cho mockMvc
	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	@DisplayName("Test create new User")
	public void assert_that_user_is_created_successfully() throws Exception {
		User user = new User("user", "nguyen van a", DigestUtils.sha1Hex("12345678"), null, "nguyenvana@gmail.com");
		user.setUserID(1L);

		when(userService.save(any(User.class))).thenReturn(user);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create")
				.content(asJsonString(user)).contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("userName").getAsString().equals("user"));
	}

	@Test
	@DisplayName("Test update user by id")
	public void assert_that_user_id_updated_successfully() throws Exception {
		User oldUser = new User("user", "nguyen van a", DigestUtils.sha1Hex("12345678"), null, "nguyenvana@gmail.com");
		oldUser.setUserID(1L);

		User newUser = new User("admin", "tran van b", DigestUtils.sha1Hex("12345678"), null, "tranvanb@gmail.com");
		newUser.setUserID(1L);

		when(userService.getById(any(Long.class))).thenReturn(oldUser);
		when(userService.save(any(User.class))).thenReturn(newUser);

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.put("/api/user/edit/{id}", oldUser.getUserID())
						.content(asJsonString(newUser)).contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("userName").getAsString().equals("admin"));
	}

	@Test
	@DisplayName("Test delete user by id")
	public void assert_that_user_is_deleted_successfully() throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();

		User user = new User("user", "nguyen van a", DigestUtils.sha1Hex("12345678"), null, "nguyenvana@gmail.com");
		user.setUserID(1L);

		when(userService.getById(any(Long.class))).thenReturn(user);
		when(userService.save(any(User.class))).thenReturn(user);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/user/delete/{id}", user.getUserID())
				.contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("deleteAt").getAsString().equals(formatter.format(now)));
	}

	@Test
	@DisplayName("Test login")
	public String asssert_that_login_successful() throws Exception {
		User user = new User("user", "nguyen van a", DigestUtils.sha1Hex("12345678"), null, "nguyenvana@gmail.com");
		user.setUserID(1L);
		UserRole userRole = new UserRole("user", "user");

		when(userService.getUserName(any(String.class))).thenReturn(user);
		when(roleService.getRoleName(any(String.class))).thenReturn(userRole.getUsername());

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/api/login")
						.content(asJsonString(new UserInput("user", "12345678")))
						.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("error").getAsString().equals("OK"));
		String token = json.get("message").getAsString();
		return token;
	}

	@Test
	@DisplayName("Test convert JWT to json")
	public void assert_that_convert_jwt_to_json() throws Exception {
		String token = asssert_that_login_successful();
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/convert")
				.param("token", token)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		
		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("fullName").getAsString().equals("nguyen van a"));
	}
	
	
}
