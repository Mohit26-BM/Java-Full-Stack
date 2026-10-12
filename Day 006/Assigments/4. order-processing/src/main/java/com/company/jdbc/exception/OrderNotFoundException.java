package com.company.jdbc.exception;

/*
Thrown when no order exists for the given ID.
*/
public class OrderNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public OrderNotFoundException(String message) {
        super(message);
    }
}