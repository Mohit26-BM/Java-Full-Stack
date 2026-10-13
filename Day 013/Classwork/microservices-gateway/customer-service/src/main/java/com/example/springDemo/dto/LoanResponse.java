package com.example.springDemo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanResponse(
        Long loanId,
        Long customerId,
        String loanType,
        BigDecimal principalAmount,
        BigDecimal interestRate,
        Integer tenureMonths,
        LocalDate startDate) {
}
