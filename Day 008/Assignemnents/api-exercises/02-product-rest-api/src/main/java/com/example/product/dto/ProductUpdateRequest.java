package com.example.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Body of PUT /api/products/{id} (replaces every field).
 * version = the version the client read. If the product changed since then,
 * the update is rejected with 409 instead of silently overwriting the other change.
 */
public record ProductUpdateRequest(

        @NotBlank(message = "name is required")
        @Size(min = 2, max = 100, message = "name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "category is required")
        @Size(max = 50, message = "category must be at most 50 characters")
        String category,

        @NotNull(message = "price is required")
        @Positive(message = "price must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "price may have at most 2 decimal places")
        BigDecimal price,

        @NotNull(message = "quantity is required")
        @PositiveOrZero(message = "quantity cannot be negative")
        Integer quantity,

        @Size(max = 50, message = "brand must be at most 50 characters")
        String brand,

        @NotNull(message = "active is required")
        Boolean active,

        @NotNull(message = "version is required")
        Long version) {
}
