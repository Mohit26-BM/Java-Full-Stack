package com.example.accountservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.accountservice.dto.LoanRequest;
import com.example.accountservice.dto.LoanResponse;
import com.example.accountservice.dto.SavingsAccountRequest;
import com.example.accountservice.dto.SavingsAccountResponse;
import com.example.accountservice.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService service;

    @Value("${server.port}")
    private String port;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/savings")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountResponse openSavingsAccount(@Valid @RequestBody SavingsAccountRequest request) {
        return service.openSavingsAccount(request);
    }

    @GetMapping("/savings/{customerId}")
    public List<SavingsAccountResponse> getSavingsAccounts(@PathVariable Long customerId) {
        return service.getSavingsAccounts(customerId);
    }

    @PostMapping("/loans")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse createLoan(@Valid @RequestBody LoanRequest request) {
        return service.createLoan(request);
    }

    @GetMapping("/loans/{customerId}")
    public List<LoanResponse> getLoans(@PathVariable Long customerId) {
        return service.getLoans(customerId);
    }

    @GetMapping("/instance")
    public Map<String, String> instance() {
        return Map.of("port", port);
    }
}