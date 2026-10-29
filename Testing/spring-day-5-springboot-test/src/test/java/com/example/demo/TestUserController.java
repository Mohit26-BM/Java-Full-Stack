package com.example.demo;


import com.example.demo.model.User;
import com.example.demo.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TestUserController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("Praveen");
        user.setEmail("praveen@example.com");
    }

    // Test 1: Insert a user
    @Test
    void testInsertUser() throws Exception {

        when(userService.insertUser(any(User.class)))
                .thenReturn(user);

        mockMvc.perform(post("/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                			"id": 1001,
                        "username": "Praveen",
                        "email": "praveen@example.com"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Praveen"))
                .andExpect(jsonPath("$.email")
                        .value("praveen@example.com"));

        verify(userService).insertUser(any(User.class));
    }

    // Test 2: Retrieve all users
    @Test
    void testGetAllUsers() throws Exception {

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("Rahul");
        user2.setEmail("rahul@example.com");

        when(userService.getAllUsers())
                .thenReturn(Arrays.asList(user, user2));

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("Praveen"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("Rahul"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(userService).getAllUsers();
    }

    // Test 3: Retrieve a user by ID
    @Test
    void testGetUserById() throws Exception {

        when(userService.getUserById(1L))
                .thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Praveen"))
                .andExpect(jsonPath("$.email")
                        .value("praveen@example.com"));

        verify(userService).getUserById(1L);
    }

    // Test 4: Retrieve all users when none exist
    @Test
    void testGetAllUsersWhenEmpty() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Test 5: Send malformed JSON
    @Test
    void testInsertUserWithMalformedJson() throws Exception {

        mockMvc.perform(post("/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":"))
                .andExpect(status().isBadRequest());
    }
}
