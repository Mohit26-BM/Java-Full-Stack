package com.example.accountservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SavingsAccountRequest(

        @NotNull(message = "Customer id is required")
        @Positive(message = "Customer id must be positive")
        Long customerId,

        @NotNull(message = "Initial deposit is required")
        @Positive(message = "Initial deposit must be greater than zero")
        @Digits(integer = 13, fraction = 2, message = "Initial deposit can have at most 2 decimal places")
        BigDecimal initialDeposit) {
}
