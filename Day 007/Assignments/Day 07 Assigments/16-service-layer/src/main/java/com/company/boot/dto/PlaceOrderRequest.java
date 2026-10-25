package com.company.boot.dto;

/**
 * Request body for POST /api/orders.
 * The client sends only WHAT it wants; price, discount and total are decided by the service.
 */
public record PlaceOrderRequest(Long productId, Integer quantity) {
}
