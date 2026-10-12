package com.company.jdbc.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.dao.AccountDao;
import com.company.jdbc.dao.TransactionDao;
import com.company.jdbc.exception.AccountInactiveException;
import com.company.jdbc.exception.AccountNotFoundException;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.InsufficientBalanceException;
import com.company.jdbc.exception.InvalidTransferException;
import com.company.jdbc.exception.TransferFailedException;
import com.company.jdbc.model.AccountStatus;
import com.company.jdbc.model.AccountTransaction;
import com.company.jdbc.model.BankAccount;
import com.company.jdbc.model.TransactionType;

/*
A transfer is ONE business transaction:

    1. lock both account rows          (SELECT ... FOR UPDATE)
    2. validate status and balance
    3. debit the source account        (UPDATE)
    4. credit the destination account  (UPDATE)
    5. insert the history row          (INSERT, generated key)
    6. COMMIT

If anything fails at any step -> ROLLBACK, and the database looks
exactly as it did before step 1. The history row is part of the
same transaction, so it only becomes permanent when the transfer
commits successfully.
*/
public class TransferServiceImpl implements TransferService {

    private static final BigDecimal MAX_AMOUNT =
            new BigDecimal("9999999999999.99");

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    public TransferServiceImpl(AccountDao accountDao,
                               TransactionDao transactionDao) {

        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public long transfer(String fromAccountNumber,
                         String toAccountNumber,
                         BigDecimal amount)
            throws InvalidTransferException,
                   AccountNotFoundException,
                   AccountInactiveException,
                   InsufficientBalanceException,
                   TransferFailedException {

        return executeTransfer(
                fromAccountNumber, toAccountNumber, amount,
                true, false, 0);
    }

    @Override
    public long transferWithFailureAfterDebit(String fromAccountNumber,
                                              String toAccountNumber,
                                              BigDecimal amount)
            throws InvalidTransferException,
                   AccountNotFoundException,
                   AccountInactiveException,
                   InsufficientBalanceException,
                   TransferFailedException {

        return executeTransfer(
                fromAccountNumber, toAccountNumber, amount,
                true, true, 0);
    }

    @Override
    public long transferForConcurrencyDemo(String fromAccountNumber,
                                           String toAccountNumber,
                                           BigDecimal amount,
                                           boolean useRowLock,
                                           long pauseMillis)
            throws InvalidTransferException,
                   AccountNotFoundException,
                   AccountInactiveException,
                   InsufficientBalanceException,
                   TransferFailedException {

        return executeTransfer(
                fromAccountNumber, toAccountNumber, amount,
                useRowLock, false, pauseMillis);
    }

    // =========================================================
    // THE TRANSACTION
    // =========================================================

    private long executeTransfer(String fromAccountNumber,
                                 String toAccountNumber,
                                 BigDecimal amount,
                                 boolean useRowLock,
                                 boolean failAfterDebit,
                                 long pauseMillis)
            throws InvalidTransferException,
                   AccountNotFoundException,
                   AccountInactiveException,
                   InsufficientBalanceException,
                   TransferFailedException {

        String fromNumber = normalize(fromAccountNumber);
        String toNumber = normalize(toAccountNumber);

        /*
        Checks that need no database are done first.
        */
        validateAmount(amount);

        if (fromNumber.equals(toNumber)) {
            throw new InvalidTransferException(
                    "Source and destination accounts must be different."
            );
        }

        /*
        try-with-resources closes the connection in every case:
        success, business error or SQL error.
        */
        try (Connection connection = DBConnection.getConnection()) {

            /*
            Auto-commit is handled explicitly:
            remember it, turn it off, and put it back in finally.
            */
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {

                // ---------- Step 1: lock both rows ----------

                BankAccount[] accounts =
                        lockAccounts(connection, fromNumber, toNumber, useRowLock);

                BankAccount from = accounts[0];
                BankAccount to = accounts[1];

                // ---------- Step 2: validate ----------

                requireActive(from);
                requireActive(to);

                pause(pauseMillis);

                if (from.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientBalanceException(
                            "Insufficient balance in " + fromNumber
                            + ". Available: Rs. " + from.getBalance()
                            + ", requested: Rs. " + amount + "."
                    );
                }

                // ---------- Step 3: debit ----------

                accountDao.debit(connection, from.getAccountId(), amount);

                if (failAfterDebit) {

                    /*
                    Deliberate failure AFTER the debit has run.
                    Without rollback, the money would simply vanish
                    from the source account.
                    */
                    throw new IllegalStateException(
                            "Simulated failure after debit "
                            + "(e.g. server crash before credit)."
                    );
                }

                // ---------- Step 4: credit ----------

                accountDao.credit(connection, to.getAccountId(), amount);

                // ---------- Step 5: history ----------

                long transactionId = transactionDao.insert(
                        connection,
                        new AccountTransaction(
                                from.getAccountId(),
                                to.getAccountId(),
                                amount,
                                TransactionType.TRANSFER
                        )
                );

                // ---------- Step 6: commit ----------

                connection.commit();

                return transactionId;

            } catch (AccountNotFoundException
                     | AccountInactiveException
                     | InsufficientBalanceException e) {

                /*
                Business rule broken. Nothing was written yet, but
                rollback still releases the row locks immediately.
                */
                rollbackQuietly(connection, e);
                throw e;

            } catch (SQLException | RuntimeException e) {

                rollbackQuietly(connection, e);

                throw new TransferFailedException(
                        "Transfer failed and was ROLLED BACK. "
                        + "No balance was changed. Reason: " + e.getMessage(),
                        e
                );

            } finally {

                restoreAutoCommit(connection, originalAutoCommit);
            }

        } catch (SQLException e) {

            /*
            Could not even open the connection (MySQL down,
            wrong password ...). Nothing was started.
            */
            throw new DataAccessException("Unable to connect to the database", e);
        }
    }

    /*
    Locks the two rows ALWAYS in the same order (alphabetical
    account number), whichever direction the money moves.

    Why: if transfer A->B locked A then B, while B->A locked B then A,
    each could hold one lock and wait forever for the other (deadlock).
    A fixed order makes that impossible.

    Returns { fromAccount, toAccount }.
    */
    private BankAccount[] lockAccounts(Connection connection,
                                       String fromNumber,
                                       String toNumber,
                                       boolean useRowLock)
            throws SQLException,
                   AccountNotFoundException {

        boolean fromFirst = fromNumber.compareTo(toNumber) < 0;

        String firstNumber = fromFirst ? fromNumber : toNumber;
        String secondNumber = fromFirst ? toNumber : fromNumber;

        BankAccount first =
                accountDao.findByNumber(connection, firstNumber, useRowLock);

        BankAccount second =
                accountDao.findByNumber(connection, secondNumber, useRowLock);

        BankAccount from = fromFirst ? first : second;
        BankAccount to = fromFirst ? second : first;

        if (from == null) {
            throw new AccountNotFoundException(
                    "Source account " + fromNumber + " was not found."
            );
        }

        if (to == null) {
            throw new AccountNotFoundException(
                    "Destination account " + toNumber + " was not found."
            );
        }

        return new BankAccount[] { from, to };
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private void validateAmount(BigDecimal amount)
            throws InvalidTransferException {

        if (amount == null || amount.signum() <= 0) {
            throw new InvalidTransferException(
                    "Transfer amount must be greater than 0."
            );
        }

        if (amount.scale() > 2) {
            throw new InvalidTransferException(
                    "Transfer amount can have at most 2 decimal places."
            );
        }

        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new InvalidTransferException("Transfer amount is too large.");
        }
    }

    private void requireActive(BankAccount account)
            throws AccountInactiveException {

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException(
                    "Account " + account.getAccountNumber()
                    + " is " + account.getStatus()
                    + ". Only ACTIVE accounts can transfer money."
            );
        }
    }

    private String normalize(String accountNumber) {
        return accountNumber == null ? "" : accountNumber.trim().toUpperCase();
    }

    private void pause(long millis) {

        if (millis <= 0) {
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void rollbackQuietly(Connection connection, Exception cause) {

        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            cause.addSuppressed(rollbackError);
        }
    }

    private void restoreAutoCommit(Connection connection, boolean original) {

        try {
            connection.setAutoCommit(original);
        } catch (SQLException ignored) {
            // The connection is closed right after this anyway.
        }
    }
}
