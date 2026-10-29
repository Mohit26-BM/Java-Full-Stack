package com.example.bank.account;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Body of POST /accounts/transfer. */
public record TransferRequest(
        @NotNull(message = "From account is required") @Positive(message = "From account must be positive") Long fromAccount,
        @NotNull(message = "To account is required") @Positive(message = "To account must be positive") Long toAccount,
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be greater than zero") @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimal places") BigDecimal amount,
        @Size(max = 200, message = "Description must be at most 200 characters") String description) {
}
