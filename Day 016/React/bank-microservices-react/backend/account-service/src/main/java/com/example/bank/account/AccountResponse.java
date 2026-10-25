package com.example.bank.account;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponse(Long accountno, Long customerId, String accountType, LocalDate openingDate,
                              BigDecimal balance) {

    static AccountResponse from(Account a) {
        return new AccountResponse(a.getAccountno(), a.getCustomerId(), a.getAccountType(), a.getOpeningDate(),
                a.getBalance());
    }
}
