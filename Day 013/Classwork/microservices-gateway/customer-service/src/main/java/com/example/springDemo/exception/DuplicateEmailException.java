package com.example.springDemo.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("Email address already exists");
    }
}
