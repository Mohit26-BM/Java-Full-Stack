package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;


@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private UserService service;

	@Test
	void testGetUserById() {
		Optional<User> user1 = Optional.of(new User(100001L,"anil","anil@gmail.com"));
		Mockito.when(userRepository.findById(100001L)).thenReturn(user1);
		User usr = service.getUserById(100001L);
		assertEquals(usr.getUsername(),"anil");
		
	}
	
	@Test
	void testGetAllUsers() {
		User user1 = new User(100001L,"anil","anil@gmail.com");
		User user2 = new User(100002L,"sunil","sunil@gmail.com");
		User user3 = new User(100003L,"mahesh","mahesh@gmail.com");
		List<User> users = new ArrayList<>();
		users.add(user1);users.add(user2);users.add(user3);
		
		Mockito.when(userRepository.findAll()).thenReturn(users);
		List<User> actualUsers = service.getAllUsers();
		assertEquals(actualUsers,users);
	}
	
	@Test
	void testInsertUser() {
		User user1 = new User(100001L,"anil","anil@gmail.com");
		Mockito.when(userRepository.save(user1)).thenReturn(user1);
		User usr = service.insertUser(user1);
		assertEquals(usr.getId(),user1.getId());
		assertEquals(usr.getUsername(),user1.getUsername());
		assertEquals(usr.getEmail(),user1.getEmail());
	}

}
