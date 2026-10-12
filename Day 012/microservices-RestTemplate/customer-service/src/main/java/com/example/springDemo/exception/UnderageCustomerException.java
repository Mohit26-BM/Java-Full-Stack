package com.example.springDemo.exception;

public class UnderageCustomerException extends RuntimeException {

    public UnderageCustomerException(int minimumAge) {
        super("Customer must be at least " + minimumAge + " years old");
    }
}
