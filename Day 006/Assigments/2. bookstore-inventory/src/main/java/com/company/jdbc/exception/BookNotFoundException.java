package com.company.jdbc.exception;

/*
Thrown when no book exists for the given ID.
*/
public class BookNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public BookNotFoundException(String message) {
        super(message);
    }
}