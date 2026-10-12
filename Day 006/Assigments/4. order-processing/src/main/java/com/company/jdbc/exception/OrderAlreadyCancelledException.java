package com.company.jdbc.exception;

/*
Thrown when trying to cancel an order that is already CANCELLED.
*/
public class OrderAlreadyCancelledException extends Exception {

    private static final long serialVersionUID = 1L;

    public OrderAlreadyCancelledException(String message) {
        super(message);
    }
}