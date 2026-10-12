package com.company.jdbc.exception;

/*
Thrown when the given department ID does not exist.
*/
public class DepartmentNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public DepartmentNotFoundException(String message) {
        super(message);
    }
}
