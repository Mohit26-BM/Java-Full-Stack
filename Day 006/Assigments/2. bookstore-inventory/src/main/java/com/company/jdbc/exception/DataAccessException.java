package com.company.jdbc.exception;

/*
Unchecked exception that wraps an unexpected SQLException
(database down, wrong password, bad SQL ...).

It is never used for normal business cases such as
"book not found", so a real database problem is never
reported to the user as "not found".
*/
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}