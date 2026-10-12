package com.company.jdbc.service;

import java.math.BigDecimal;
import java.util.List;

import com.company.jdbc.exception.AccountNotFoundException;
import com.company.jdbc.exception.DuplicateAccountException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.AccountStatus;
import com.company.jdbc.model.AccountTransaction;
import com.company.jdbc.model.BankAccount;

public interface AccountService {

    /*
    Creates an ACTIVE account. Returns the generated account_id.
    */
    long createAccount(String accountNumber,
                       String customerName,
                       BigDecimal openingBalance)
            throws InvalidInputException,
                   DuplicateAccountException;

    /*
    Balance lookup.
    */
    BankAccount getAccount(String accountNumber)
            throws AccountNotFoundException;

    List<BankAccount> getAllAccounts();

    void changeStatus(String accountNumber, AccountStatus status)
            throws AccountNotFoundException;

    List<AccountTransaction> getHistory(String accountNumber)
            throws AccountNotFoundException;

    List<AccountTransaction> getAllHistory();

    int countTransactions();
}
