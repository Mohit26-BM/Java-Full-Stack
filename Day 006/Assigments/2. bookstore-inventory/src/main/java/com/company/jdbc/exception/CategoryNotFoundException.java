package com.company.jdbc.exception;

/*
Thrown when no category exists for the given ID.
*/
public class CategoryNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public CategoryNotFoundException(String message) {
        super(message);
    }
}