package com.company.jdbc.exception;

/*
Thrown when the ISBN already exists (UNIQUE constraint).
*/
public class DuplicateBookException extends Exception {

    private static final long serialVersionUID = 1L;

    public DuplicateBookException(String message) {
        super(message);
    }
}