package com.company.jdbc.exception;

/*
Thrown when a stock decrease would make stock_qty negative.
*/
public class InsufficientStockException extends Exception {

    private static final long serialVersionUID = 1L;

    public InsufficientStockException(String message) {
        super(message);
    }
}