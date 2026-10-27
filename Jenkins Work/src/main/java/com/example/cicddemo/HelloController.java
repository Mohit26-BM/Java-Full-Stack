package com.example.cicddemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/")
	public String home() {
		return "Welcome to the CI/CD Demo Pipeline!";
	}

	@GetMapping("/hello")
	public String hello() {
		return "Hello, World!";
	}

	@GetMapping("/greet")
	public String greet(@RequestParam(defaultValue = "User") String name) {
		return "Hello, " + name + "!";
	}

}
