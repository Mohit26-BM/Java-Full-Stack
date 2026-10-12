package com.company.jdbc.exception;

/*
Thrown when no product exists for the given ID.
*/
public class ProductNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public ProductNotFoundException(String message) {
        super(message);
    }
}