package com.example.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	private UserService service;

	@Test
	public void testPostUser() throws Exception{
		User user1 = new User(100009L,"mohan","mohan@gmail.com");
		//define
		Mockito.when(service.insertUser(user1)).thenReturn(user1);
		
		// test
		mockMvc.perform(post("/users/save"))
				.andExpect(jsonPath("$[0].username",is("mohan")));
	}
	
	@Test
	public void testGetAllUsers() throws Exception{
		User user1 = new User(100001L,"anil","anil@gmail.com");
		User user2 = new User(100002L,"sunil","sunil@gmail.com");
		User user3 = new User(100003L,"mahesh","mahesh@gmail.com");
		List<User> users = new ArrayList<>();
		users.add(user1);users.add(user2);users.add(user3);
		Mockito.when(service.getAllUsers()).thenReturn(users);
		
		mockMvc.perform(get("/users/all"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id",is(100001)))
				.andExpect(jsonPath("$[1].username",is("sunil")))
				.andExpect(jsonPath("$[2].email",is("mahesh@gmail.com")));
	}
	
	
	@Test
	public void testGetUserById() throws Exception{
		User user = new User(100000L,"anil","anil@gmail.com");
		Mockito.when(service.getUserById(100000L)).thenReturn(user);
		mockMvc.perform(get("/users/100000"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id",is(100000)))
				.andExpect(jsonPath("$.username",is("anil")))
				.andExpect(jsonPath("$.email",is("anil@gmail.com")));
	
	}

}
