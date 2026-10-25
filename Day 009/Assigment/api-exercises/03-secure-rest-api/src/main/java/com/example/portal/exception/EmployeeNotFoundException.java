package com.example.portal.exception;

/** -> 404 */
public class EmployeeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id " + id);
    }
}
