package com.company.boot.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.company.boot.entity.PurchaseOrder;

public record OrderResponse(Long orderId,
                            Long productId,
                            String productName,
                            Integer quantity,
                            BigDecimal unitPrice,
                            BigDecimal discount,
                            BigDecimal total,
                            Integer remainingStock,
                            LocalDateTime createdAt) {

    public static OrderResponse from(PurchaseOrder order) {
        return new OrderResponse(order.getId(), order.getProduct().getId(), order.getProduct().getName(),
                order.getQuantity(), order.getUnitPrice(), order.getDiscount(), order.getTotal(),
                order.getProduct().getStock(), order.getCreatedAt());
    }
}
