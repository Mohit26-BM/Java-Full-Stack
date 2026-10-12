package com.company.jdbc.exception;

/*
Thrown when a product does not have enough stock for an order item.
*/
public class InsufficientStockException extends Exception {

    private static final long serialVersionUID = 1L;

    public InsufficientStockException(String message) {
        super(message);
    }
}