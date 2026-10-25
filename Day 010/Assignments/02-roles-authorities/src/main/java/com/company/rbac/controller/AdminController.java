package com.company.rbac.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.rbac.security.PortalUserDetailsService;

@RestController
public class AdminController {
    private final PortalUserDetailsService users;

    public AdminController(PortalUserDetailsService users) {
        this.users = users;
    }

    @GetMapping("/api/admin/users")
    public List<Map<String, Object>> users() {
        return users.allUsers().stream()
                .map(user -> Map.<String, Object>of(
                        "username", user.username(),
                        "roles", user.roles(),
                        "permissions", user.permissions()))
                .toList();
    }
}
