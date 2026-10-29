package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	public User getUserById(Long id) {
		Optional<User> user = userRepository.findById(id);
		return user.orElse(null);
	}
	
	public User insertUser(User user) {
		return userRepository.save(user);
	}

}
