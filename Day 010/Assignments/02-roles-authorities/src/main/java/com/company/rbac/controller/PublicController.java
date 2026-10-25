package com.company.rbac.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {
    @GetMapping("/api/public/hello")
    public Map<String, String> hello() {
        return Map.of("message", "HR portal is running", "status", "UP");
    }
}
