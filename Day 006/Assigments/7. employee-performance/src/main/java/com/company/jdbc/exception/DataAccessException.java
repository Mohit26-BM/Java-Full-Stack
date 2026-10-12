package com.company.jdbc.exception;

/*
Unchecked exception that wraps an unexpected SQLException
(database down, procedure missing, wrong password ...), so a real
database problem is reported as such instead of crashing the menu.
*/
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}