package com.company.jdbc.exception;

/*
Thrown when the customer email already exists (UNIQUE constraint).
*/
public class DuplicateCustomerException extends Exception {

    private static final long serialVersionUID = 1L;

    public DuplicateCustomerException(String message) {
        super(message);
    }
}