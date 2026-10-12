package com.company.jdbc.exception;

/*
Thrown when no customer exists for the given ID.
*/
public class CustomerNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public CustomerNotFoundException(String message) {
        super(message);
    }
}