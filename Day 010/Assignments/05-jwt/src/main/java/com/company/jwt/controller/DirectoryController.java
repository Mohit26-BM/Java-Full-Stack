package com.company.jwt.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.jwt.dto.AuthDtos.DirectoryEntry;
import com.company.jwt.service.UserAdminService;

@RestController
public class DirectoryController {
    private final UserAdminService userAdminService;

    public DirectoryController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping("/api/directory")
    public List<DirectoryEntry> directory() {
        return userAdminService.directory();
    }

    @GetMapping("/api/public/hello")
    public Map<String, String> hello() {
        return Map.of("message", "HR portal is running", "status", "UP");
    }
}
