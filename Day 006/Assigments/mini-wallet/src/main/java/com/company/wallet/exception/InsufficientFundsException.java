package com.company.wallet.exception;

/*
The wallet balance is lower than the requested amount.
*/
public class InsufficientFundsException extends WalletException {

    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(String message) {
        super(message);
    }
}