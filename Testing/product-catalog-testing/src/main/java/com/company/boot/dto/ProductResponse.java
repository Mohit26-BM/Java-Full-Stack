package com.company.boot.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.company.boot.entity.Product;

/** What the API returns - the entity itself is never serialised directly. */
public record ProductResponse(Long id,
                              String name,
                              String category,
                              BigDecimal price,
                              Integer stock,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getCategory(),
                product.getPrice(), product.getStock(), product.getCreatedAt(), product.getUpdatedAt());
    }
}
