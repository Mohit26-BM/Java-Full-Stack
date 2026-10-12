package com.company.jdbc.exception;

/*
Unchecked exception that wraps an SQLException from the repository.
The repository logs the error before throwing this.
*/
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}