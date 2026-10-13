package com.company.boot.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Body of POST /api/accounts (validated - optional extension). */
public class AccountRequest {

    @NotBlank(message = "holderName is required")
    @Size(max = 100, message = "holderName must be at most 100 characters")
    private String holderName;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid e-mail address")
    private String email;

    @NotNull(message = "openingBalance is required")
    @PositiveOrZero(message = "openingBalance cannot be negative")
    private BigDecimal openingBalance;

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }
}
