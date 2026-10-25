package com.example.accountservice.dto;

import java.math.BigDecimal;

import com.example.accountservice.entity.LoanType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LoanRequest(

        @NotNull(message = "Customer id is required")
        @Positive(message = "Customer id must be positive")
        Long customerId,

        @NotNull(message = "Loan type is required")
        LoanType loanType,

        @NotNull(message = "Principal amount is required")
        @Positive(message = "Principal amount must be greater than zero")
        @Digits(integer = 13, fraction = 2, message = "Principal amount can have at most 2 decimal places")
        BigDecimal principalAmount,

        @NotNull(message = "Interest rate is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than zero")
        @DecimalMax(value = "100.0", message = "Interest rate cannot exceed 100")
        @Digits(integer = 3, fraction = 2, message = "Interest rate can have at most 2 decimal places")
        BigDecimal interestRate,

        @NotNull(message = "Tenure is required")
        @Min(value = 1, message = "Tenure must be at least 1 month")
        @Max(value = 360, message = "Tenure cannot exceed 360 months")
        Integer tenureMonths) {
}
