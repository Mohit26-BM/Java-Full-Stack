package com.company.boot.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Body of POST and PUT /api/products. */
public class ProductRequest {

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @NotBlank(message = "category is required")
    @Size(max = 50, message = "category must be at most 50 characters")
    private String category;

    @NotNull(message = "price is required")
    @Positive(message = "price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "price may have at most 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "stock is required")
    @PositiveOrZero(message = "stock cannot be negative")
    private Integer stock;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
