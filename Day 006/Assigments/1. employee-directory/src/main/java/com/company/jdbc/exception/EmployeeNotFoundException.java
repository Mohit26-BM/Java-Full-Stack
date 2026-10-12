package com.company.jdbc.exception;

/*
Thrown when no employee exists for the given ID.
*/
public class EmployeeNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
