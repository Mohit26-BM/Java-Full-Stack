package com.example.portal.exception;

/** -> 409 */
public class DuplicateEmailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String email) {
        super("An employee with email " + email + " already exists");
    }
}
