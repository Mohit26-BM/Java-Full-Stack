package com.company.jdbc.service;

import com.company.jdbc.exception.InvalidInputException;

/*
Small shared validation helpers used by the services.
*/
final class InputValidator {

    private InputValidator() {
    }

    /*
    Returns the trimmed text, or throws when it is blank / too long.
    */
    static String requireText(String value, String fieldName, int maxLength)
            throws InvalidInputException {

        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(
                    fieldName + " cannot be blank."
            );
        }

        String cleaned = value.trim();

        if (cleaned.length() > maxLength) {
            throw new InvalidInputException(
                    fieldName + " cannot be longer than "
                    + maxLength + " characters."
            );
        }

        return cleaned;
    }

    static void requirePositive(int value, String fieldName)
            throws InvalidInputException {

        if (value <= 0) {
            throw new InvalidInputException(
                    fieldName + " must be greater than 0."
            );
        }
    }
}
