package com.company.jdbc.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.dao.AccountDao;
import com.company.jdbc.dao.TransactionDao;
import com.company.jdbc.exception.AccountNotFoundException;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.DuplicateAccountException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.AccountStatus;
import com.company.jdbc.model.AccountTransaction;
import com.company.jdbc.model.BankAccount;
import com.company.jdbc.model.TransactionType;

public class AccountServiceImpl implements AccountService {

    private static final int MYSQL_DUPLICATE_KEY = 1062;

    private static final String ACCOUNT_NUMBER_PATTERN = "[A-Z0-9]{4,20}";

    private static final String NAME_PATTERN = "[A-Za-z .'-]+";

    /*
    Largest value that fits in DECIMAL(15,2).
    */
    private static final BigDecimal MAX_AMOUNT =
            new BigDecimal("9999999999999.99");

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    public AccountServiceImpl(AccountDao accountDao,
                              TransactionDao transactionDao) {

        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public long createAccount(String accountNumber,
                              String customerName,
                              BigDecimal openingBalance)
            throws InvalidInputException,
                   DuplicateAccountException {

        String number = validateAccountNumber(accountNumber);
        String name = validateName(customerName);
        validateOpeningBalance(openingBalance);

        BankAccount account =
                new BankAccount(number, name, openingBalance, AccountStatus.ACTIVE);

        /*
        The account row and its OPENING_DEPOSIT history row are saved
        in ONE transaction: either both exist or neither does.
        */
        try (Connection connection = DBConnection.getConnection()) {

            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {

                long accountId = accountDao.insert(connection, account);

                if (openingBalance.signum() > 0) {

                    transactionDao.insert(
                            connection,
                            new AccountTransaction(
                                    null,
                                    accountId,
                                    openingBalance,
                                    TransactionType.OPENING_DEPOSIT
                            )
                    );
                }

                connection.commit();

                return accountId;

            } catch (SQLException | RuntimeException e) {

                rollbackQuietly(connection, e);
                throw e;

            } finally {

                restoreAutoCommit(connection, originalAutoCommit);
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getErrorCode() == MYSQL_DUPLICATE_KEY) {
                throw new DuplicateAccountException(
                        "Account number " + number + " already exists."
                );
            }

            throw new DataAccessException("Unable to create account", e);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to create account", e);
        }
    }

    @Override
    public BankAccount getAccount(String accountNumber)
            throws AccountNotFoundException {

        String number = normalize(accountNumber);

        BankAccount account = accountDao.findByNumber(number);

        if (account == null) {
            throw new AccountNotFoundException(
                    "Account " + number + " was not found."
            );
        }

        return account;
    }

    @Override
    public List<BankAccount> getAllAccounts() {
        return accountDao.findAll();
    }

    @Override
    public void changeStatus(String accountNumber, AccountStatus status)
            throws AccountNotFoundException {

        String number = normalize(accountNumber);

        if (!accountDao.updateStatus(number, status)) {
            throw new AccountNotFoundException(
                    "Account " + number + " was not found."
            );
        }
    }

    @Override
    public List<AccountTransaction> getHistory(String accountNumber)
            throws AccountNotFoundException {

        BankAccount account = getAccount(accountNumber);

        return transactionDao.findByAccountId(account.getAccountId());
    }

    @Override
    public List<AccountTransaction> getAllHistory() {
        return transactionDao.findAll();
    }

    @Override
    public int countTransactions() {
        return transactionDao.countAll();
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String normalize(String accountNumber) {
        return accountNumber == null ? "" : accountNumber.trim().toUpperCase();
    }

    private String validateAccountNumber(String accountNumber)
            throws InvalidInputException {

        String number = normalize(accountNumber);

        if (!number.matches(ACCOUNT_NUMBER_PATTERN)) {
            throw new InvalidInputException(
                    "Account number must be 4 to 20 letters or digits."
            );
        }

        return number;
    }

    private String validateName(String customerName)
            throws InvalidInputException {

        if (customerName == null || customerName.trim().isEmpty()) {
            throw new InvalidInputException("Customer name cannot be blank.");
        }

        String name = customerName.trim();

        if (name.length() > 120) {
            throw new InvalidInputException(
                    "Customer name cannot be longer than 120 characters."
            );
        }

        if (!name.matches(NAME_PATTERN)) {
            throw new InvalidInputException(
                    "Customer name can contain only letters, spaces, "
                    + "dots, hyphens and apostrophes."
            );
        }

        return name;
    }

    private void validateOpeningBalance(BigDecimal balance)
            throws InvalidInputException {

        if (balance == null || balance.signum() < 0) {
            throw new InvalidInputException(
                    "Opening balance cannot be negative."
            );
        }

        if (balance.scale() > 2) {
            throw new InvalidInputException(
                    "Opening balance can have at most 2 decimal places."
            );
        }

        if (balance.compareTo(MAX_AMOUNT) > 0) {
            throw new InvalidInputException("Opening balance is too large.");
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
