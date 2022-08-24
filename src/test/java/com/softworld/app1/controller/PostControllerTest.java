package com.softworld.app1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.softworld.app1.controller.form.PostInput;
import com.softworld.app1.model.Category;
import com.softworld.app1.model.Category_Post;
import com.softworld.app1.model.Post;
import com.softworld.app1.service.CategoryPostServiceImpl;
import com.softworld.app1.service.CategoryServiceImpl;
import com.softworld.app1.service.PostServiceImpl;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = PostController.class)
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SuppressWarnings("deprecation")
public class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@MockBean
	private PostServiceImpl postService;
	@MockBean
	private CategoryServiceImpl categoryService;
	@MockBean
	private CategoryPostServiceImpl cpService;

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
	@DisplayName("Test findAll")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_find_all_posts() throws Exception {
		Post post_1 = new Post("ab", "ab", "2020-04-04", "2020-04-04");
		post_1.setPostId(1L);

		Post post_2 = new Post("ba", "ba", "2020-04-04", "2020-04-04");
		post_2.setPostId(2L);

		List<Post> listPost = new ArrayList<>();
		listPost.add(post_1);
		listPost.add(post_2);

		when(postService.findAll()).thenReturn(listPost);

		MvcResult newPost = mockMvc.perform(get("/api/posts")).andDo(MockMvcResultHandlers.print()).andReturn();

		// convert string Body in MockHttpServletResponse to json Array
		JsonArray jsonArray = new JsonParser().parse(newPost.getResponse().getContentAsString()).getAsJsonArray();

		// Creating an empty ArrayList of type Object
		ArrayList<Object> listJson = new ArrayList<Object>();
		if (jsonArray != null)
			for (int i = 0; i < jsonArray.size(); i++)
				listJson.add(jsonArray.get(i));

		// convert json ArrayList(1) to json Object
		JsonObject json = new JsonParser().parse(listJson.get(1).toString()).getAsJsonObject();
		Assertions.assertTrue(json.get("title").getAsString().equals("ba"));

	}

	@Test
	@DisplayName("Test findById success")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_get_post_by_id() throws Exception {
		Post post = new Post("ab", "ab", "2020-04-04", "2020-04-04");
		post.setPostId(1L);

		when(postService.getById(any(Long.class))).thenReturn(post);

		MvcResult newPost = mockMvc.perform(get("/api/post/{id}", post.getPostId()))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		// convert string Body in MockHttpServletResponse to json Object
		JsonObject json = new JsonParser().parse(newPost.getResponse().getContentAsString()).getAsJsonObject();

		Assertions.assertTrue(json.get("title").getAsString().equals("ab"));
	}

	@Test
	@DisplayName("Test create new Post")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_post_is_create_successfully() throws Exception {
		Post post = new Post("abc", "abc", "2020-04-04", "2020-04-04");
		post.setPostId(1L);

		when(postService.save(any(Post.class))).thenReturn(post);

		MvcResult newPost = this.mockMvc
				.perform(MockMvcRequestBuilders.post("/api/post/create").content(asJsonString(post))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(newPost.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("postId").getAsString().equals("1"));
		Assertions.assertTrue(json.get("title").getAsString().equals("abc"));
	}

	@Test
	@DisplayName("Test update Post by id")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_post_is_update_successfully() throws Exception {
		Post post = new Post("abcd", "abcd", "2020-12-12", "2020-12-12");
		post.setPostId(1L);

		Post newPost = new Post("ab", "abc", null, null);
		newPost.setPostId(1L);

		when(postService.getById(any(Long.class))).thenReturn(post);
		when(postService.save(any(Post.class))).thenReturn(newPost);

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.put("/api/post/edit/{id}", post.getPostId()).content(asJsonString(newPost))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("title").getAsString().equals("ab"));

	}

	@Test
	@DisplayName("Test delete Post")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_post_id_delete_successfull() throws Exception {
		Post post = new Post("ab", "ab", "2020-04-04", "2020-04-04");
		post.setPostId(1L);

		when(postService.getById(any(Long.class))).thenReturn(post);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/post/delete/{id}", post.getPostId()))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("message").getAsString().equals("Delete completed"));
	}

	@Test
	@DisplayName("Test get Post by id with categoryName and categoryID")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_get_post_by_id_with_categoryName_categoryID() throws Exception {
		Post post = new Post("abc", "abc", "2020-09-09", "2020-09-09");
		post.setPostId(1L);

		Category category_1 = new Category("Ashe", null, null);
		category_1.setCategoryID(1L);
		Category category_2 = new Category("Udyr", null, null);
		category_2.setCategoryID(2L);

		List<Long> listCategoryID = new ArrayList<Long>();
		listCategoryID.add(category_1.getCategoryID());
		listCategoryID.add(category_2.getCategoryID());

		when(postService.getPostById(anyLong())).thenReturn(post);
		when(cpService.getCategoryIDFromCategoryPost(any(Long.class))).thenReturn(listCategoryID);
		when(categoryService.getNameById(anyLong())).thenReturn(category_1.getCategoryName())
				.thenReturn(category_2.getCategoryName());

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/get/post/{id}", post.getPostId()))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("title").getAsString().equals("abc"));

		JsonArray jsonArray = json.getAsJsonArray("categories");
		ArrayList<Object> listdata = new ArrayList<Object>();
		if (jsonArray != null)
			for (int i = 0; i < jsonArray.size(); i++)
				listdata.add(jsonArray.get(i));
		JsonObject jsonCategory = new JsonParser().parse(listdata.get(0).toString()).getAsJsonObject();
		Assertions.assertTrue(jsonCategory.get("categoryName").getAsString().equals("Ashe"));
	}

	@Test
	@DisplayName("Test create Post with CategoryId")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_create_post_with_categoryIDs() throws Exception {

		List<Long> listCategoryIDs = new ArrayList<>();
		listCategoryIDs.add(1L);
		listCategoryIDs.add(2L);

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/api/create/post")
						.content(asJsonString(new PostInput("ab", "ab", listCategoryIDs)))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("message").getAsString().equals("Create completed"));
	}

	@Test
	@DisplayName("Test update Post by id with categoryID and categoryName using PostInput")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_update_post_by_id_witn_categoryID_and_categoryName() throws Exception {

		Post post = new Post("ab", "ab", null, null);
		post.setPostId(1L);

		Category category_1 = new Category("Ashe", null, null);
		category_1.setCategoryID(1L);
		Category category_2 = new Category("Udyr", null, null);
		category_2.setCategoryID(2L);
		Category_Post category_post_1 = new Category_Post(category_1.getCategoryID(), post.getPostId());
		Category_Post category_post_2 = new Category_Post(category_2.getCategoryID(), post.getPostId());

		Category category_3 = new Category("Luffy", null, null);
		category_3.setCategoryID(3L);
		Category category_4 = new Category("Ulta", null, null);
		category_4.setCategoryID(4L);
		List<Long> listCategoryIDs = new ArrayList<>();
		listCategoryIDs.add(3L);
		listCategoryIDs.add(4L);

		when(postService.getPostById(any(Long.class))).thenReturn(post);
		when(cpService.getCategoryIDFromCategoryPost(any(Long.class))).thenReturn(listCategoryIDs);
		when(categoryService.getNameById(anyLong())).thenReturn(category_3.getCategoryName())
				.thenReturn(category_4.getCategoryName());

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.put("/api/update/post/{id}", post.getPostId())
						.content(asJsonString(new PostInput("ba", "ba", listCategoryIDs)))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("title").getAsString().equals("ba"));

		JsonArray jsonArray = json.getAsJsonArray("categories");
		ArrayList<Object> listdata = new ArrayList<Object>();
		if (jsonArray != null)
			for (int i = 0; i < jsonArray.size(); i++)
				listdata.add(jsonArray.get(i));
		JsonObject jsonCategory_1 = new JsonParser().parse(listdata.get(0).toString()).getAsJsonObject();
		Assertions.assertTrue(jsonCategory_1.get("categoryName").getAsString().equals("Luffy"));

		JsonObject jsonCategory_2 = new JsonParser().parse(listdata.get(1).toString()).getAsJsonObject();
		Assertions.assertTrue(jsonCategory_2.get("categoryName").getAsString().equals("Ulta"));
	}

	@Test
	@DisplayName("Test delete Post by id with categoryID and categoryName")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_delete_post_by_id_with_categoryId_and_categoryName() throws Exception {
		Post post = new Post();
		post.setPostId(1L);

		Category category_1 = new Category();
		category_1.setCategoryID(1L);
		Category category_2 = new Category();
		category_2.setCategoryID(2L);
//		Category_Post category_post_1 = new Category_Post(category_1.getCategoryID(), post.getPostId());
//		Category_Post category_post_2 = new Category_Post(category_2.getCategoryID(), post.getPostId());

		List<Long> listCategoryIDs = new ArrayList<>();
		listCategoryIDs.add(category_1.getCategoryID());
		listCategoryIDs.add(category_2.getCategoryID());

		when(cpService.getCategoryIDFromCategoryPost(any(Long.class))).thenReturn(listCategoryIDs);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete/post/{id}", 1L))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("message").getAsString().equals("Delete completed"));

	}

	@Test
	@DisplayName("Test upload image")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_upload_image() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "zoro.png", "image/png", "image".getBytes());

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload/image").file(file))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("message").getAsString().equals("http://localhost/api/downloadFile/zoro.png"));
	}

	@Test
	@DisplayName("Test download image")
	@WithMockUser(username = "sa", authorities = "ROLE_ADMIN")
	public void assert_that_download_image_not_exist() throws Exception {
		String fileName = "zoro.jpg";
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/downloadFile/{file:.+}", fileName))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		JsonObject json = new JsonParser().parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		Assertions.assertTrue(json.get("message").getAsString().equals("Your image file does not exist"));

	}
}
