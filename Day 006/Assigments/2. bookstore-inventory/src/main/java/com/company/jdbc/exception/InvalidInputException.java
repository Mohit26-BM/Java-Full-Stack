package com.company.jdbc.exception;

/*
Thrown when input fails validation before reaching the database.
*/
public class InvalidInputException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}