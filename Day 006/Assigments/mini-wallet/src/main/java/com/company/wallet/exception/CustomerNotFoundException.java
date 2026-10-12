package com.company.wallet.exception;

/*
No customer exists for the given ID or email.
*/
public class CustomerNotFoundException extends WalletException {

    private static final long serialVersionUID = 1L;

    public CustomerNotFoundException(String message) {
        super(message);
    }
}