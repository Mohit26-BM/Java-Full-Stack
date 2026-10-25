package com.example.demo;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Api {

  @GetMapping("/public")
  public String open() {
    return "anyone can see this";
  }

  @GetMapping("/profile")
  public String me(Authentication a) {
    return a == null ? "nobody is logged in" : "hello " + a.getName();
  }

  @GetMapping("/admin")
  public String admin() {
    return "admin only";
  }
}