package com.dockerdemoapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot running in Kubernetes cluster!";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to my KubernetesDemoApp!";
    }
}