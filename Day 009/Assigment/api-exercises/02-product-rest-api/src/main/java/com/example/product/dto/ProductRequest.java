package com.example.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Body of POST /api/products. "active" may be left out (a new product is active by default). */
public record ProductRequest(

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

        Boolean active) {
}
