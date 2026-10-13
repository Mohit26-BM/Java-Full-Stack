package com.company.boot.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.boot.dto.OrderResponse;
import com.company.boot.dto.PlaceOrderRequest;
import com.company.boot.entity.Product;
import com.company.boot.entity.PurchaseOrder;
import com.company.boot.exception.BusinessRuleException;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.repository.OrderRepository;
import com.company.boot.repository.ProductRepository;
import com.company.boot.service.OrderService;

/**
 * SERVICE LAYER - order rules:
 *   O1 productId is required
 *   O2 quantity must be between 1 and 10 per order
 *   O3 the product must exist
 *   O4 there must be enough stock
 *   O5 the PRICE comes from the database, never from the client
 *   O6 10% discount when the order value is 5000 or more
 *   O7 stock decrease + order insert happen in ONE transaction
 */
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    public static final int MAX_QUANTITY_PER_ORDER = 10;
    public static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("5000");
    public static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    private static final Logger log = LoggerFactory.getLogger("SERVICE");

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        log.info("placeOrder: checking rules O1-O4");
        if (request.productId() == null) {
            throw new BusinessRuleException("PRODUCT_ID_REQUIRED", "productId is required");
        }
        Integer quantity = request.quantity();
        if (quantity == null || quantity < 1 || quantity > MAX_QUANTITY_PER_ORDER) {
            throw new BusinessRuleException("QUANTITY_OUT_OF_RANGE",
                    "Quantity must be between 1 and " + MAX_QUANTITY_PER_ORDER + " (got " + quantity + ")");
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.productId()));

        if (product.getStock() < quantity) {
            throw new BusinessRuleException("INSUFFICIENT_STOCK",
                    "Only " + product.getStock() + " unit(s) of '" + product.getName()
                    + "' in stock, " + quantity + " requested", BusinessRuleException.Type.STATE_CONFLICT);
        }

        // O5 + O6: the service calculates the money, not the client
        BigDecimal unitPrice = product.getPrice();
        BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discount = gross.compareTo(DISCOUNT_THRESHOLD) >= 0
                ? gross.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2);
        BigDecimal total = gross.subtract(discount).setScale(2, RoundingMode.HALF_UP);
        log.info("rules passed: gross={} discount={} total={}", gross, discount, total);

        // O7: both changes commit together or not at all
        product.setStock(product.getStock() - quantity);
        PurchaseOrder saved = orderRepository.save(new PurchaseOrder(product, quantity, unitPrice, discount, total));
        log.info("order {} saved, stock of product {} now {}", saved.getId(), product.getId(), product.getStock());
        return OrderResponse.from(saved);
    }

    @Override
    public OrderResponse findById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(OrderResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }
}
