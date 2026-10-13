package com.company.boot.service;

import com.company.boot.dto.OrderResponse;
import com.company.boot.dto.PlaceOrderRequest;

/** SERVICE LAYER contract for orders. */
public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    OrderResponse findById(Long orderId);
}
