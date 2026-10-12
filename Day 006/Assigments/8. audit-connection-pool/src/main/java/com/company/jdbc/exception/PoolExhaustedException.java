package com.company.jdbc.exception;

/*
Thrown when no pooled connection became free within the pool's
connectionTimeout. Callers can catch this specific case and react
predictably (retry later, reject the request, show "busy" ...)
instead of hanging forever.
*/
public class PoolExhaustedException extends DataAccessException {

    private static final long serialVersionUID = 1L;

    public PoolExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }
}