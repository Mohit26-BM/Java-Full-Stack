package com.company.jdbc.exception;

/*
Thrown when user input is unusable before any database work
(file not found, empty file, bad month format ...).
*/
public class InvalidInputException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}