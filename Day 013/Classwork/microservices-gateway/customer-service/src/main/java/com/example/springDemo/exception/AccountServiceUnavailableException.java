package com.example.springDemo.exception;

public class AccountServiceUnavailableException extends RuntimeException {

    public AccountServiceUnavailableException(Throwable cause) {
        super("Account service is not available right now. Please try again later.", cause);
    }
}
