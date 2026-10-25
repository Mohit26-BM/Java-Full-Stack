package com.company.boot.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.dto.OrderResponse;
import com.company.boot.dto.PlaceOrderRequest;
import com.company.boot.service.OrderService;

/** CONTROLLER LAYER - HTTP only, no business rules. */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger("CONTROLLER");

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        log.info("POST /api/orders received -> delegating to OrderService.placeOrder()");
        OrderResponse order = orderService.placeOrder(request);
        log.info("returning 201 Created");
        return ResponseEntity.created(URI.create("/api/orders/" + order.orderId())).body(order);
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable("id") Long id) {
        return orderService.findById(id);
    }
}
