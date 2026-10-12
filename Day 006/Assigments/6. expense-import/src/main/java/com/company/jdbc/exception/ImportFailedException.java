package com.company.jdbc.exception;

/*
Thrown when inserting the batch failed and the whole import
was ROLLED BACK. No rows from the file were saved, so the
file can simply be imported again after the problem is fixed.
*/
public class ImportFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public ImportFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}