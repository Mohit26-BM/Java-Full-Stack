package com.example.authservice.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String name) {
        super("User already exists with name: " + name);
    }
}
