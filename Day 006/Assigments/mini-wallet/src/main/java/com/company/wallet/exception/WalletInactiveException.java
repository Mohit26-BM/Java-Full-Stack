package com.company.wallet.exception;

/*
The wallet is INACTIVE or BLOCKED and cannot send or receive money.
*/
public class WalletInactiveException extends WalletException {

    private static final long serialVersionUID = 1L;

    public WalletInactiveException(String message) {
        super(message);
    }
}