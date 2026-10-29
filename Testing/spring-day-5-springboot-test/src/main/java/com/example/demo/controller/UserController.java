package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/save")
	public ResponseEntity<User> insertUser(@RequestBody User user){
		User usr = userService.insertUser(user);
		return new ResponseEntity<>(usr,HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<User>> getAllusers(){
		List<User> users = userService.getAllUsers();
		return new ResponseEntity<>(users,HttpStatus.OK);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id){
		User user = userService.getUserById(id);		
		return ResponseEntity.ok(user);
				//return null;
	}

}
