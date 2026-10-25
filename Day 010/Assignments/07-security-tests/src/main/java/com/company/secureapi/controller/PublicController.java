package com.company.secureapi.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {
    @GetMapping("/api/public/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
