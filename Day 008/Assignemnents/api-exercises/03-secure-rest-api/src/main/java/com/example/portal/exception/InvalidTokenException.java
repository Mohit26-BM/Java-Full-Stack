package com.example.portal.exception;

/** The refresh token is invalid, expired, or not a refresh token (-> 401). */
public class InvalidTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String message) {
        super(message);
    }
}
