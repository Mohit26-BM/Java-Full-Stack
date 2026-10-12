package com.example.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/user/hello")
    public String userHello() {
        return "Hello USER! You have access to user endpoints.";
    }

    @GetMapping("/admin/hello")
    public String adminHello() {
        return "Hello ADMIN! You have access to admin endpoints.";
    }

    @GetMapping("/common/hello")
    public String commonHello() {
        return "Hello! This endpoint is accessible to all authenticated users.";
    }
}
