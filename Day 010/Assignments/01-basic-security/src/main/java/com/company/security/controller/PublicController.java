package com.company.security.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of("message", "Hello from the employee portal", "status", "UP", "time", Instant.now());
    }
}
