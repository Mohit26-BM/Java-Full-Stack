package com.company.wallet.exception;

/*
A multi-statement operation failed for a TECHNICAL reason
(SQL error, forced failure ...) and was ROLLED BACK.
Nothing was saved.
*/
public class TransactionFailedException extends WalletException {

    private static final long serialVersionUID = 1L;

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}