package com.example.bank.account;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Body of POST /accounts/{accountno}/deposit and /withdraw. */
public record AmountRequest(
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be greater than zero") @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimal places") BigDecimal amount,
        @Size(max = 200, message = "Description must be at most 200 characters") String description) {
}
