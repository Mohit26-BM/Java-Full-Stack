package com.company.jdbc.exception;

/*
Thrown when no author exists for the given ID.
*/
public class AuthorNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public AuthorNotFoundException(String message) {
        super(message);
    }
}