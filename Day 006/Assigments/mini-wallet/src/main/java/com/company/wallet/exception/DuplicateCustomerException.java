package com.company.wallet.exception;

/*
Email or phone number already belongs to another customer (UNIQUE constraint).
*/
public class DuplicateCustomerException extends WalletException {

    private static final long serialVersionUID = 1L;

    public DuplicateCustomerException(String message) {
        super(message);
    }
}