package com.company.boot.dto;

/** Request body for POST /api/products/{id}/restock */
public record RestockRequest(Integer quantity) {
}
