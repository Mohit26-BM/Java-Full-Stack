package com.company.wallet.app;

import com.company.wallet.exception.CustomerNotFoundException;
import com.company.wallet.exception.DataAccessException;
import com.company.wallet.exception.DuplicateCustomerException;
import com.company.wallet.exception.InsufficientFundsException;
import com.company.wallet.exception.TransactionFailedException;
import com.company.wallet.exception.ValidationException;
import com.company.wallet.exception.WalletException;
import com.company.wallet.exception.WalletInactiveException;
import com.company.wallet.exception.WalletNotFoundException;

/*
CENTRALIZED EXCEPTION HANDLING.

Menu actions do not catch exceptions themselves. Everything they
throw reaches the main loop, which calls handle() - so every error
is reported in ONE consistent way, and adding a new exception type
means changing only this class.
*/
public final class ErrorHandler {

    private ErrorHandler() {
    }

    public static void handle(Exception e) {

        System.out.println();

        if (e instanceof ValidationException) {
            System.out.println("[VALIDATION] " + e.getMessage());

        } else if (e instanceof DuplicateCustomerException) {
            System.out.println("[DUPLICATE] " + e.getMessage());

        } else if (e instanceof CustomerNotFoundException
                || e instanceof WalletNotFoundException) {
            System.out.println("[NOT FOUND] " + e.getMessage());

        } else if (e instanceof InsufficientFundsException) {
            System.out.println("[REJECTED] " + e.getMessage());

        } else if (e instanceof WalletInactiveException) {
            System.out.println("[REJECTED] " + e.getMessage());

        } else if (e instanceof TransactionFailedException) {
            System.out.println("[ROLLED BACK] " + e.getMessage());

        } else if (e instanceof WalletException) {
            System.out.println("[ERROR] " + e.getMessage());

        } else if (e instanceof DataAccessException) {
            System.out.println("[DATABASE] " + e.getMessage());

            if (e.getCause() != null) {
                System.out.println("            cause: " + e.getCause().getMessage());
            }

        } else {
            System.out.println("[UNEXPECTED] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /*
    Short label of an exception, used by the demo runner.
    */
    public static String label(Exception e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }
}
