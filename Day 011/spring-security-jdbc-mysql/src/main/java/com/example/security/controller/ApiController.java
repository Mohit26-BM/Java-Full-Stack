package com.example.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/user")
    public String user() {
        return "USER can access this endpoint";
    }

    @GetMapping("/admin")
    public String admin() {
        return "ADMIN can access this endpoint";
    }
}