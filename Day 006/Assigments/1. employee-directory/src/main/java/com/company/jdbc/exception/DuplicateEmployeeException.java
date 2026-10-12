package com.company.jdbc.exception;

/*
Thrown when employee_code or email already exists (UNIQUE constraint).
*/
public class DuplicateEmployeeException extends Exception {

    private static final long serialVersionUID = 1L;

    public DuplicateEmployeeException(String message) {
        super(message);
    }
}
