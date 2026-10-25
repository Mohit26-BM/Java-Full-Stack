package com.company.boot.dto;

import java.math.BigDecimal;

import com.company.boot.entity.Product;

public record ProductResponse(Long id, String name, BigDecimal price, Integer stock) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }
}
