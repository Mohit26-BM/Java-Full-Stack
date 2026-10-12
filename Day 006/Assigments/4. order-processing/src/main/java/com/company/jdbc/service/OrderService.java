package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.exception.CustomerNotFoundException;
import com.company.jdbc.exception.InsufficientStockException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.exception.OrderAlreadyCancelledException;
import com.company.jdbc.exception.OrderFailedException;
import com.company.jdbc.exception.OrderNotFoundException;
import com.company.jdbc.exception.ProductNotFoundException;
import com.company.jdbc.model.Order;
import com.company.jdbc.model.OrderItemRequest;

public interface OrderService {

    /*
    Places the whole order in ONE transaction.
    Returns the generated order_id.
    */
    long placeOrder(long customerId, List<OrderItemRequest> items)
            throws InvalidInputException,
                   CustomerNotFoundException,
                   ProductNotFoundException,
                   InsufficientStockException,
                   OrderFailedException;

    /*
    Rollback demo: does all the work (order header, every item,
    every stock reduction), then fails just before the commit.
    */
    long placeOrderWithSimulatedFailure(long customerId, List<OrderItemRequest> items)
            throws InvalidInputException,
                   CustomerNotFoundException,
                   ProductNotFoundException,
                   InsufficientStockException,
                   OrderFailedException;

    /*
    Order + customer + items + product names (JOINs).
    */
    Order getOrderDetails(long orderId)
            throws OrderNotFoundException;

    List<Order> getAllOrders();

    /*
    Optional challenge: restores stock; an order can be cancelled only once.
    */
    void cancelOrder(long orderId)
            throws OrderNotFoundException,
                   OrderAlreadyCancelledException,
                   OrderFailedException;

    int countOrders();

    int countOrderItems();
}
