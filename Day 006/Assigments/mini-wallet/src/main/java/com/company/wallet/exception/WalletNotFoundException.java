package com.company.wallet.exception;

/*
No wallet exists for the given wallet number (e.g. invalid transfer destination).
*/
public class WalletNotFoundException extends WalletException {

    private static final long serialVersionUID = 1L;

    public WalletNotFoundException(String message) {
        super(message);
    }
}