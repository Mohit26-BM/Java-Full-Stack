package com.company.boot.dto;

import java.math.BigDecimal;

/**
 * Request body for POST /api/products.
 * Deliberately NO validation annotations: every rule is checked in the
 * service, so it applies to every caller (REST, bulk import, tests ...).
 */
public record CreateProductRequest(String name, BigDecimal price, Integer stock) {
}
