package com.company.jdbc.exception;

/*
Unchecked exception that wraps an unexpected SQLException
(database down, wrong password ...).

It is never used for business cases such as "product not found",
so a real database problem is never reported as "not found".
*/
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}