package com.company.boot.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Response DTO - what the client gets back after a successful create / read. */
public record ProductResponse(int id,
                              String name,
                              String sku,
                              String category,
                              BigDecimal price,
                              BigDecimal discountPrice,
                              int stock,
                              String supplierEmail,
                              List<String> tags,
                              LocalDateTime createdAt) {
}
