package com.example.order;

public record Order(Long id, Long productId, int quantity, Product product, double total, String status) {
}
