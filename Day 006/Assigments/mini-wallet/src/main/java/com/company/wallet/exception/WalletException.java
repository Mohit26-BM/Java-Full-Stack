package com.company.wallet.exception;

/*
Base class of every BUSINESS error in the wallet platform.

All business exceptions extend this one, so the application can
handle them in ONE place (app/ErrorHandler) instead of repeating
catch blocks in every menu action.
*/
public class WalletException extends Exception {

    private static final long serialVersionUID = 1L;

    public WalletException(String message) {
        super(message);
    }

    public WalletException(String message, Throwable cause) {
        super(message, cause);
    }
}