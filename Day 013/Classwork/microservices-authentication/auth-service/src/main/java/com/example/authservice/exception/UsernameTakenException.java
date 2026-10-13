package com.example.authservice.exception;

public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException() {
        super("Username is already taken");
    }
}
