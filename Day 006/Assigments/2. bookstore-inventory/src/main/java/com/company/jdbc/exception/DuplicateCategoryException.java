package com.company.jdbc.exception;

/*
Thrown when the category name already exists (UNIQUE constraint).
*/
public class DuplicateCategoryException extends Exception {

    private static final long serialVersionUID = 1L;

    public DuplicateCategoryException(String message) {
        super(message);
    }
}