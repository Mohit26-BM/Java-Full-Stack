package com.company.boot.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.dto.AccountRequest;
import com.company.boot.model.Account;
import com.company.boot.service.AccountService;

import jakarta.validation.Valid;

/**
 * Only the "happy path" lives here: no try/catch, no error responses.
 * Any exception thrown below is handled by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public Account findById(@PathVariable("id") int id) {
        return accountService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Account> create(@Valid @RequestBody AccountRequest request) {
        Account account = accountService.create(request);
        return ResponseEntity.created(URI.create("/api/accounts/" + account.accountId())).body(account);
    }

    /** POST /api/accounts/{id}/withdraw?amount=500 */
    @PostMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable("id") int id, @RequestParam("amount") BigDecimal amount) {
        return accountService.withdraw(id, amount);
    }

    /** POST /api/accounts/transfer?from=1&to=2&amount=1000 */
    @PostMapping("/transfer")
    public List<Account> transfer(@RequestParam("from") int from,
                                  @RequestParam("to") int to,
                                  @RequestParam("amount") BigDecimal amount) {
        return accountService.transfer(from, to, amount);
    }

    /** GET /api/accounts/{id}/instalment?months=12 (months=0 -> unexpected error demo) */
    @GetMapping("/{id}/instalment")
    public Map<String, Object> instalment(@PathVariable("id") int id, @RequestParam("months") int months) {
        return Map.of("accountId", id, "months", months,
                "monthlyInstalment", accountService.monthlyInstalment(id, months));
    }
}
