package com.example.bank.account;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Account endpoints. The business and access rules live in {@link AccountService}; this class only handles HTTP.
 * The caller (username, role, linked customer) comes from the verified token via {@link Caller}.
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    private final AccountService service;
    private final CustomerClient customers;

    public AccountController(AccountService service, CustomerClient customers) {
        this.service = service;
        this.customers = customers;
    }

    @GetMapping
    public List<AccountResponse> list(HttpServletRequest http) {
        return service.list(Caller.from(http));
    }

    @GetMapping("/{accountno}")
    public AccountResponse get(@PathVariable Long accountno, HttpServletRequest http) {
        return service.get(accountno, Caller.from(http));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody AccountRequest request,
                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                  HttpServletRequest http) {
        Caller caller = Caller.from(http);
        if (!caller.isStaff()) { // refuse before the remote call, so a customer cannot probe which customers exist
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only bank staff can open accounts");
        }
        // remote call first, outside any DB transaction; the caller's token is passed on because the customer service checks it too
        customers.requireCustomer(request.customerId(), authorization);
        return service.open(request, caller);
    }

    @PostMapping("/{accountno}/deposit")
    public TransactionResponse deposit(@PathVariable Long accountno, @Valid @RequestBody AmountRequest request,
                                       @RequestHeader(name = IDEMPOTENCY_KEY, required = false) String key,
                                       HttpServletRequest http) {
        return service.deposit(accountno, request, key(key), Caller.from(http));
    }

    @PostMapping("/{accountno}/withdraw")
    public TransactionResponse withdraw(@PathVariable Long accountno, @Valid @RequestBody AmountRequest request,
                                        @RequestHeader(name = IDEMPOTENCY_KEY, required = false) String key,
                                        HttpServletRequest http) {
        return service.withdraw(accountno, request, key(key), Caller.from(http));
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request,
                                        @RequestHeader(name = IDEMPOTENCY_KEY, required = false) String key,
                                        HttpServletRequest http) {
        return service.transfer(request, key(key), Caller.from(http));
    }

    /** Newest first; limit is capped at 100; from / to are optional dates (yyyy-MM-dd, both inclusive). */
    @GetMapping("/{accountno}/transactions")
    public List<TransactionResponse> transactions(@PathVariable Long accountno,
                                                  @RequestParam(name = "limit", defaultValue = "20") int limit,
                                                  @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                  @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                                  HttpServletRequest http) {
        return service.statement(accountno, from, to, limit, Caller.from(http));
    }

    @PatchMapping("/{accountno}/status")
    public AccountResponse changeStatus(@PathVariable Long accountno, @Valid @RequestBody StatusRequest request,
                                        HttpServletRequest http) {
        return service.changeStatus(accountno, AccountStatus.valueOf(request.status()), Caller.from(http));
    }

    /** Admin only. {@code month} is yyyy-MM and defaults to the current month. */
    @PostMapping("/interest")
    public InterestRunResponse runInterest(@RequestParam(name = "month", required = false) String month,
                                           HttpServletRequest http) {
        YearMonth target;
        try {
            target = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month.trim());
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "month must look like 2026-10");
        }
        return service.accrueInterest(target, Caller.from(http));
    }

    /** Admin only, newest first. */
    @GetMapping("/audit")
    public List<AuditResponse> audit(@RequestParam(name = "limit", defaultValue = "50") int limit, HttpServletRequest http) {
        return service.auditTrail(limit, Caller.from(http));
    }

    private String key(String key) {
        return key == null || key.isBlank() ? null : key.trim();
    }
}
