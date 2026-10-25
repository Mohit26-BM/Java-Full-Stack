package com.company.dbauth.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.dbauth.dto.UserResponses.DirectoryEntry;
import com.company.dbauth.service.AccountService;

@RestController
public class DirectoryController {
    private final AccountService accountService;

    public DirectoryController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/api/directory")
    public List<DirectoryEntry> directory() {
        return accountService.directory();
    }

    @GetMapping("/api/public/hello")
    public Map<String, String> hello() {
        return Map.of("message", "HR portal is running", "status", "UP");
    }
}
