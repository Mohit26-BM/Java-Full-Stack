package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
	
	@GetMapping
	public String welcome() {
		return "welcome user ";
	}
	@GetMapping("/ignore1")
	public String welcome1() {
		return "ignore1 is completely bypassed of secuity";
	}
	
	@GetMapping("/ignore2")
	public String welcome2() {
		return "ignore2 is also completely bypassed of security";
	}

}
