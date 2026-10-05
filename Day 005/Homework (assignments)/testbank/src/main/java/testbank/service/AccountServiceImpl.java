package testbank.service;

import java.util.Date;
import java.util.List;

import testbank.dao.AccountDao;
import testbank.dao.CustomerDao;

import testbank.entity.Account;
import testbank.entity.AccountType;
import testbank.entity.Transaction;
import testbank.entity.TransactionType;

import testbank.exception.AccountNotFoundException;
import testbank.exception.CustomerNotFoundException;
import testbank.exception.DuplicateAccountException;
import testbank.exception.InsufficientBalanceException;
import testbank.exception.InvalidAmountException;
import testbank.exception.AccountHasBalanceException;

public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;
    private final CustomerDao customerDao;
    private final TransactionService transactionService;

    private static int nextAccountNumber = 1001;

    public AccountServiceImpl(AccountDao accountDao,
                              CustomerDao customerDao,
                              TransactionService transactionService) {

        this.accountDao = accountDao;
        this.customerDao = customerDao;
        this.transactionService = transactionService;
    }

    // =========================================================
    // CREATE ACCOUNT
    // =========================================================

    @Override
    public int createAccount(
            int customerId,
            AccountType accountType,
            int balance,
            Date dateOfCreation)
            throws DuplicateAccountException,
                   CustomerNotFoundException {

        // Validate customer ID
        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than 0."
            );
        }

        // Check whether customer exists
        if (customerDao.getCustomerById(customerId) == null) {
            throw new CustomerNotFoundException(
                    "Customer with ID "
                    + customerId
                    + " does not exist. "
                    + "Please create the customer first."
            );
        }

        // Validate account type
        if (accountType == null) {
            throw new IllegalArgumentException(
                    "Account type cannot be null."
            );
        }

        // Validate initial balance
        if (balance < 0) {
            throw new IllegalArgumentException(
                    "Initial balance cannot be negative."
            );
        }

        // Validate creation date
        if (dateOfCreation == null) {
            throw new IllegalArgumentException(
                    "Date of creation cannot be null."
            );
        }

        // Generate account number
        int accountNumber = generateAccountNumber();

        Account account = new Account(
                customerId,
                accountType,
                balance,
                dateOfCreation
        );

        account.setAccountNumber(accountNumber);

        boolean added = accountDao.addAccount(account);

        if (!added) {
            throw new DuplicateAccountException(
                    "Account with number "
                    + accountNumber
                    + " already exists."
            );
        }
        if (balance > 0) {

            Transaction transaction = new Transaction(
                    0,
                    accountNumber,
                    new Date(),
                    balance,
                    TransactionType.DEPOSIT,
                    "Initial account deposit"
            );

            transactionService.addTransaction(transaction);
        }

        return accountNumber;
    }

    // =========================================================
    // GENERATE ACCOUNT NUMBER
    // =========================================================

    private int generateAccountNumber() {

        while (accountDao.getAccount(nextAccountNumber) != null) {
            nextAccountNumber++;
        }

        return nextAccountNumber++;
    }

 // =========================================================
 // CLOSE ACCOUNT
 // =========================================================

 @Override
 public void closeAccount(int accountNumber)
         throws AccountNotFoundException,
                AccountHasBalanceException {

     if (accountNumber <= 0) {
         throw new IllegalArgumentException(
                 "Account number must be greater than 0."
         );
     }

     Account account =
             accountDao.getAccount(accountNumber);

     if (account == null) {
         throw new AccountNotFoundException(
                 "Account with number "
                 + accountNumber
                 + " was not found."
         );
     }

     // Account cannot be closed if money is still present
     if (account.getBalance() > 0) {
         throw new AccountHasBalanceException(
                 "Account cannot be closed because "
                 + "the current balance is ₹"
                 + account.getBalance()
                 + ". Please withdraw or transfer the remaining "
                 + "balance first."
         );
     }

     boolean deleted =
             accountDao.deleteAccount(accountNumber);

     if (!deleted) {
         throw new IllegalStateException(
                 "Unable to close the account. "
                 + "Please try again."
         );
     }
 }
    // =========================================================
    // DEPOSIT
    // =========================================================

    @Override
    public void deposit(
            int accountNumber,
            int amount)
            throws AccountNotFoundException,
                   InvalidAmountException {

        if (accountNumber <= 0) {
            throw new IllegalArgumentException(
                    "Account number must be greater than 0."
            );
        }

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than 0."
            );
        }

        Account account =
                accountDao.getAccount(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException(
                    "Account with number "
                    + accountNumber
                    + " was not found."
            );
        }

        int newBalance =
                account.getBalance() + amount;

        account.setBalance(newBalance);

        boolean updated =
                accountDao.updateAccount(account);

        if (!updated) {
            throw new IllegalStateException(
                    "Unable to process deposit. "
                    + "Please try again."
            );
        }

        // Record deposit transaction
        Transaction transaction = new Transaction(
                0,
                accountNumber,
                new Date(),
                amount,
                TransactionType.DEPOSIT,
                "Cash deposit"
        );

        transactionService.addTransaction(transaction);
    }

    // =========================================================
    // WITHDRAW
    // =========================================================

    @Override
    public void withdraw(
            int accountNumber,
            int amount)
            throws AccountNotFoundException,
                   InvalidAmountException,
                   InsufficientBalanceException {

        if (accountNumber <= 0) {
            throw new IllegalArgumentException(
                    "Account number must be greater than 0."
            );
        }

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be greater than 0."
            );
        }

        Account account =
                accountDao.getAccount(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException(
                    "Account with number "
                    + accountNumber
                    + " was not found."
            );
        }

        if (amount > account.getBalance()) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. "
                    + "Available balance: ₹"
                    + account.getBalance()
                    + "."
            );
        }

        int newBalance =
                account.getBalance() - amount;

        account.setBalance(newBalance);

        boolean updated =
                accountDao.updateAccount(account);

        if (!updated) {
            throw new IllegalStateException(
                    "Unable to process withdrawal. "
                    + "Please try again."
            );
        }

        // Record withdrawal transaction
        Transaction transaction = new Transaction(
                0,
                accountNumber,
                new Date(),
                amount,
                TransactionType.WITHDRAWAL,
                "Cash withdrawal"
        );

        transactionService.addTransaction(transaction);
    }

    // =========================================================
    // FUND TRANSFER
    // =========================================================

    @Override
    public void fundsTransfer(
            int fromAccountNumber,
            int toAccountNumber,
            int amount)
            throws AccountNotFoundException,
                   InvalidAmountException,
                   InsufficientBalanceException {

        if (fromAccountNumber <= 0 ||
            toAccountNumber <= 0) {

            throw new IllegalArgumentException(
                    "Account numbers must be greater than 0."
            );
        }

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be greater than 0."
            );
        }

        if (fromAccountNumber == toAccountNumber) {
            throw new IllegalArgumentException(
                    "Source and destination accounts "
                    + "cannot be the same."
            );
        }

        Account fromAccount =
                accountDao.getAccount(fromAccountNumber);

        if (fromAccount == null) {
            throw new AccountNotFoundException(
                    "Source account with number "
                    + fromAccountNumber
                    + " was not found."
            );
        }

        Account toAccount =
                accountDao.getAccount(toAccountNumber);

        if (toAccount == null) {
            throw new AccountNotFoundException(
                    "Destination account with number "
                    + toAccountNumber
                    + " was not found."
            );
        }

        if (amount > fromAccount.getBalance()) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in source account. "
                    + "Available balance: ₹"
                    + fromAccount.getBalance()
                    + "."
            );
        }

        // Deduct amount from source account
        fromAccount.setBalance(
                fromAccount.getBalance() - amount
        );

        // Add amount to destination account
        toAccount.setBalance(
                toAccount.getBalance() + amount
        );

        boolean sourceUpdated =
                accountDao.updateAccount(fromAccount);

        boolean destinationUpdated =
                accountDao.updateAccount(toAccount);

        if (!sourceUpdated || !destinationUpdated) {
            throw new IllegalStateException(
                    "Unable to complete the fund transfer. "
                    + "Please try again."
            );
        }

        // Record sender transaction
        Transaction senderTransaction = new Transaction(
                0,
                fromAccountNumber,
                new Date(),
                amount,
                TransactionType.TRANSFER,
                "Transfer to account "
                + toAccountNumber
        );

        // Record receiver transaction
        Transaction receiverTransaction = new Transaction(
                0,
                toAccountNumber,
                new Date(),
                amount,
                TransactionType.TRANSFER,
                "Transfer from account "
                + fromAccountNumber
        );

        transactionService.addTransaction(senderTransaction);
        transactionService.addTransaction(receiverTransaction);
    }

    // =========================================================
    // GET ALL ACCOUNTS
    // =========================================================

    @Override
    public List<Account> getAllAccounts() {

        return accountDao.getAllAccounts();
    }
}