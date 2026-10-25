package com.company.boot.exception;

/** -> 404 */
public class AccountNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(Long accountId) {
        super("Account not found with id " + accountId);
    }
}
