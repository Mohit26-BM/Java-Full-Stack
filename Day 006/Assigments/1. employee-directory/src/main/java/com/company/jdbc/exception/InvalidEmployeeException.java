package com.company.jdbc.exception;

/*
Thrown when employee input fails validation before reaching the database.
*/
public class InvalidEmployeeException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidEmployeeException(String message) {
        super(message);
    }
}
