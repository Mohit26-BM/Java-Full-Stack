package com.company.jdbc.exception;

/*
Thrown when placing or cancelling an order failed for a technical
reason (SQL error, simulated failure ...) and was ROLLED BACK.
Nothing was saved and no stock was changed.
*/
public class OrderFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public OrderFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}