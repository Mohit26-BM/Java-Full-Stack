package com.example.springbootmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DemoController {
	
	
	@GetMapping("/")
	public String start(Model model) {
		
		model.addAttribute("name","Anil");
		model.addAttribute("city","Mumbai");
		return "home";
	}
	
	@GetMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("login","username","Alex");
	}

	
	@PostMapping("/")
	public String post() {
		return "index";
	}

}
