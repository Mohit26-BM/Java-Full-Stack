package com.example.bank.account;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountRepository accounts;
    private final CustomerClient customers;

    public AccountController(AccountRepository accounts, CustomerClient customers) {
        this.accounts = accounts;
        this.customers = customers;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<AccountResponse> list() {
        return accounts.findAll().stream().map(AccountResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/{accountno}")
    @Transactional(readOnly = true)
    public AccountResponse get(@PathVariable Long accountno) {
        return accounts.findById(accountno).map(AccountResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account " + accountno + " does not exist"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        customers.requireCustomer(request.customerId()); // outside any DB transaction: it is a remote call
        Account saved = accounts.save(new Account(request.customerId(), request.accountType(),
                request.openingDate(), request.balance()));
        return AccountResponse.from(saved);
    }
}
