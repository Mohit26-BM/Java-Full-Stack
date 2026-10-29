package com.example.bank.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/** Body of PUT /auth/admin/users/{username}. customerId is optional (staff do not need one). */
public record UserAdminRequest(
        @NotBlank(message = "role is required") @Pattern(regexp = "CUSTOMER|TELLER|ADMIN", message = "role must be CUSTOMER, TELLER or ADMIN") String role,
        @Positive(message = "customerId must be positive") Long customerId) {
}
