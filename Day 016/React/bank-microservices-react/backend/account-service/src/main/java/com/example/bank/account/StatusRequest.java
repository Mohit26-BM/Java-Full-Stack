package com.example.bank.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Body of PATCH /accounts/{accountno}/status. */
public record StatusRequest(
        @NotBlank(message = "Status is required") @Pattern(regexp = "ACTIVE|FROZEN|CLOSED", message = "Status must be ACTIVE, FROZEN or CLOSED") String status) {
}
