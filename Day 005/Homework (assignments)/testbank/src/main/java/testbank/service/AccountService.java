package testbank.service;

import java.util.Date;
import java.util.List;

import testbank.entity.Account;
import testbank.entity.AccountType;
import testbank.exception.AccountNotFoundException;
import testbank.exception.CustomerNotFoundException;
import testbank.exception.DuplicateAccountException;
import testbank.exception.InsufficientBalanceException;
import testbank.exception.InvalidAmountException;
import testbank.exception.AccountHasBalanceException;

public interface AccountService {

    int createAccount(
            int customerId,
            AccountType accountType,
            int balance,
            Date dateOfCreation)
            throws DuplicateAccountException, CustomerNotFoundException;

    void closeAccount(int accountNumber)
            throws AccountNotFoundException,
                   AccountHasBalanceException;

    void deposit(
            int accountNumber,
            int amount)
            throws AccountNotFoundException,
                   InvalidAmountException;

    void withdraw(
            int accountNumber,
            int amount)
            throws AccountNotFoundException,
                   InvalidAmountException,
                   InsufficientBalanceException;

    void fundsTransfer(
            int fromAccountNumber,
            int toAccountNumber,
            int amount)
            throws AccountNotFoundException,
                   InvalidAmountException,
                   InsufficientBalanceException;

    List<Account> getAllAccounts();
}