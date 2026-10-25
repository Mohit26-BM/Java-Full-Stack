package com.example.springDemo.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class InstanceController {

    private final RestTemplate restTemplate;

    public InstanceController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/api/v1/demo/account-instance")
    public Map<?, ?> accountInstance() {
        return restTemplate.getForObject("http://account-service/api/v1/accounts/instance", Map.class);
    }
}