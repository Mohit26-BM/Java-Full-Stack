package com.example.springDemo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingsAccountResponse(
        Long accountId,
        String accountNumber,
        Long customerId,
        BigDecimal balance,
        BigDecimal interestRate,
        LocalDate openedOn) {
}
