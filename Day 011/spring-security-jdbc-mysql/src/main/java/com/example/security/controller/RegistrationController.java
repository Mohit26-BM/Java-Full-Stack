package com.example.security.controller;

import com.example.security.dto.RegisterRequest;
import com.example.security.service.UserRegistrationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private final UserRegistrationService service;

    public RegistrationController(UserRegistrationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        service.register(request.getUsername(), request.getPassword());
        return "User registered successfully";
    }
}