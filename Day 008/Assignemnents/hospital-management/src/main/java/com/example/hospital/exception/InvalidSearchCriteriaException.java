package com.example.hospital.exception;

public class InvalidSearchCriteriaException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public InvalidSearchCriteriaException(String message) {
        super("INVALID_SEARCH_CRITERIA", message);
    }
}
