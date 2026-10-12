package com.company.jdbc.service;

import java.math.BigDecimal;

import com.company.jdbc.exception.AccountInactiveException;
import com.company.jdbc.exception.AccountNotFoundException;
import com.company.jdbc.exception.InsufficientBalanceException;
import com.company.jdbc.exception.InvalidTransferException;
import com.company.jdbc.exception.TransferFailedException;

public interface TransferService {

    /*
    Normal transfer. Returns the generated transaction_id.
    */
    long transfer(String fromAccountNumber,
                  String toAccountNumber,
                  BigDecimal amount)
            throws InvalidTransferException,
                   AccountNotFoundException,
                   AccountInactiveException,
                   InsufficientBalanceException,
                   TransferFailedException;

    /*
    Rollback demo: debits the source account, then deliberately
    fails BEFORE the credit. The whole transfer must be rolled back.
    Always ends with TransferFailedException (or a validation error).
    */
    long transferWithFailureAfterDebit(String fromAccountNumber,
                                       String toAccountNumber,
                                       BigDecimal amount)
            throws InvalidTransferException,
                   AccountNotFoundException,
                   AccountInactiveException,
                   InsufficientBalanceException,
                   TransferFailedException;

    /*
    Concurrency demo only.
    useRowLock = false skips FOR UPDATE to show what goes wrong.
    pauseMillis waits between the balance check and the debit,
    so two threads overlap reliably.
    */
    long transferForConcurrencyDemo(String fromAccountNumber,
                                    String toAccountNumber,
                                    BigDecimal amount,
                                    boolean useRowLock,
                                    long pauseMillis)
            throws InvalidTransferException,
                   AccountNotFoundException,
                   AccountInactiveException,
                   InsufficientBalanceException,
                   TransferFailedException;
}
