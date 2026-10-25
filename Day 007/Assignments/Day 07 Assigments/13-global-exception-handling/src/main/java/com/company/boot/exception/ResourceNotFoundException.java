package com.company.boot.exception;

/**
 * Thrown when a requested resource does not exist -> 404 Not Found.
 * Example message: "Account not found with id 99"
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(resourceName + " not found with " + fieldName + " " + fieldValue);
    }
}
