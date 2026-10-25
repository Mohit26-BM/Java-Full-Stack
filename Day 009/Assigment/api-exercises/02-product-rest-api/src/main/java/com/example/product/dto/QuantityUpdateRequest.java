package com.example.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Body of PATCH /api/products/{id}/quantity - sets the stock level only. */
public record QuantityUpdateRequest(

        @NotNull(message = "quantity is required")
        @PositiveOrZero(message = "quantity cannot be negative")
        Integer quantity) {
}
