package com.example.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.accountservice.entity.SavingsAccount;

public record SavingsAccountResponse(
        Long accountId,
        String accountNumber,
        Long customerId,
        BigDecimal balance,
        BigDecimal interestRate,
        LocalDate openedOn) {

    public static SavingsAccountResponse from(SavingsAccount account) {
        return new SavingsAccountResponse(
                account.getAccountId(),
                account.getAccountNumber(),
                account.getCustomerId(),
                account.getBalance(),
                account.getInterestRate(),
                account.getOpenedOn());
    }
}
