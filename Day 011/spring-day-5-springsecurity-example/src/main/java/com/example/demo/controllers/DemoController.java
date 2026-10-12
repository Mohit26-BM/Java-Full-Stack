package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.MyService;

@RestController
@RequestMapping("/bank")
public class DemoController {

	@Autowired
	MyService service;

	

	@GetMapping("/register")
	public String registerUser() {
		return "new user is registered";
	}

	@GetMapping("/info")
	public String getInfo() {
		return "General Information is displayed";
	}
	
	@GetMapping("/loan/{name}")
	public String getLoanDetails(@PathVariable String name) {
		return service.getLoanDetails(name);
	}

	@GetMapping("/deleteAccount")
	public String deleteAccount() {
		return "user account is deleted...";
	}

	@GetMapping("/update")
	public String updateAccount() {
		return "Account details are updated";
	}

	@GetMapping("/balance")
	public String getBalance() {
		return "Balance of logged in user is returned";
	}

}
