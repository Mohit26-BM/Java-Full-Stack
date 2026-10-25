package com.example.product.exception;

/** Invalid query parameters: page, size, sortBy, direction or price range (-> 400). */
public class InvalidQueryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidQueryException(String message) {
        super(message);
    }
}
