package com.example.bank.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record AccountRequest(
        @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId,
        @NotBlank(message = "Account type is required") @Pattern(regexp = "SAVINGS|CURRENT|FIXED_DEPOSIT", message = "Account type must be SAVINGS, CURRENT or FIXED_DEPOSIT") String accountType,
        @NotNull(message = "Opening date is required") @PastOrPresent(message = "Opening date cannot be in the future") LocalDate openingDate,
        @NotNull(message = "Opening balance is required") @PositiveOrZero(message = "Opening balance cannot be negative") @Digits(integer = 13, fraction = 2, message = "Opening balance can have at most 2 decimal places") BigDecimal balance) {
}
