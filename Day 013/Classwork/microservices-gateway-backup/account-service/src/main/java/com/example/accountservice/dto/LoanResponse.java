package com.example.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.accountservice.entity.Loan;
import com.example.accountservice.entity.LoanType;

public record LoanResponse(
        Long loanId,
        Long customerId,
        LoanType loanType,
        BigDecimal principalAmount,
        BigDecimal interestRate,
        Integer tenureMonths,
        LocalDate startDate) {

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getLoanId(),
                loan.getCustomerId(),
                loan.getLoanType(),
                loan.getPrincipalAmount(),
                loan.getInterestRate(),
                loan.getTenureMonths(),
                loan.getStartDate());
    }
}
