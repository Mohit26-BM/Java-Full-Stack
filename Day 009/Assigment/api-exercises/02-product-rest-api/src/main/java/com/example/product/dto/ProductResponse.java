package com.example.product.dto;

import java.math.BigDecimal;

import com.example.product.model.Product;

public record ProductResponse(Long id,
                              String name,
                              String category,
                              BigDecimal price,
                              Integer quantity,
                              String brand,
                              Boolean active,
                              Long version) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getCategory(), product.getPrice(),
                product.getQuantity(), product.getBrand(), product.getActive(), product.getVersion());
    }
}
