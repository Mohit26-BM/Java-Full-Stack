package com.company.boot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.dto.AccountsView;
import com.company.boot.dto.TransferMode;
import com.company.boot.dto.TransferRequest;
import com.company.boot.exception.TransferException;
import com.company.boot.service.AccountService;
import com.company.boot.service.TransferService;

/**
 * POST /api/transfers        run one experiment (mode + simulateFailure in the body)
 * GET  /api/accounts         balances + total
 * POST /api/accounts/reset   back to 10000 / 5000
 *
 * The controller is NOT transactional: transactions are placed on the service methods.
 */
@RestController
@RequestMapping("/api")
public class TransferController {

    private final TransferService transferService;
    private final AccountService accountService;

    public TransferController(TransferService transferService, AccountService accountService) {
        this.transferService = transferService;
        this.accountService = accountService;
    }

    @PostMapping("/transfers")
    public AccountsView transfer(@RequestBody TransferRequest request) throws TransferException {
        TransferMode mode = request.mode() == null ? TransferMode.WITH_TRANSACTION : request.mode();
        switch (mode) {
            case WITH_TRANSACTION -> transferService.transferWithTransaction(
                    request.fromAccountId(), request.toAccountId(), request.amount(), request.simulateFailure());
            case WITHOUT_TRANSACTION -> transferService.transferWithoutTransaction(
                    request.fromAccountId(), request.toAccountId(), request.amount(), request.simulateFailure());
            case CHECKED_DEFAULT -> transferService.transferCheckedDefault(
                    request.fromAccountId(), request.toAccountId(), request.amount(), request.simulateFailure());
            case CHECKED_ROLLBACK_FOR -> transferService.transferCheckedRollbackFor(
                    request.fromAccountId(), request.toAccountId(), request.amount(), request.simulateFailure());
            case SELF_INVOCATION -> transferService.transferSelfInvocation(
                    request.fromAccountId(), request.toAccountId(), request.amount(), request.simulateFailure());
            case SWALLOWED_EXCEPTION -> transferService.transferSwallowingException(
                    request.fromAccountId(), request.toAccountId(), request.amount(), request.simulateFailure());
        }
        return accountService.findAll();        // balances after a successful transfer
    }

    @GetMapping("/accounts")
    public AccountsView accounts() {
        return accountService.findAll();
    }

    @PostMapping("/accounts/reset")
    public AccountsView reset() {
        return accountService.reset();
    }
}
