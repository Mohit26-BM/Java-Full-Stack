package com.company.boot.exception;

/** -> 404 Not Found */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String resourceName, Object id) {
        super(resourceName + " not found with id " + id);
    }
}
