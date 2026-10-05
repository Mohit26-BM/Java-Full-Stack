package testbank.dao;

import java.util.ArrayList;
import java.util.List;

import testbank.entity.Account;

public class AccountDaoImpl implements AccountDao {

    private final List<Account> accounts;

    public AccountDaoImpl() {
        accounts = new ArrayList<>();
    }

    @Override
    public boolean addAccount(Account account) {

        if (account == null) {
            return false;
        }

        // Prevent duplicate account numbers
        if (getAccount(account.getAccountNumber()) != null) {
            return false;
        }

        accounts.add(account);
        return true;
    }

    @Override
    public Account getAccount(int accountNumber) {

        for (Account account : accounts) {

            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }

        return null;
    }

    @Override
    public boolean updateAccount(Account account) {

        if (account == null) {
            return false;
        }

        Account existingAccount =
                getAccount(account.getAccountNumber());

        if (existingAccount == null) {
            return false;
        }

        existingAccount.setCustomerId(
                account.getCustomerId()
        );

        existingAccount.setAccountType(
                account.getAccountType()
        );

        existingAccount.setBalance(
                account.getBalance()
        );

        existingAccount.setDateOfCreation(
                account.getDateOfCreation()
        );

        return true;
    }

    @Override
    public boolean deleteAccount(int accountNumber) {

        Account account = getAccount(accountNumber);

        if (account == null) {
            return false;
        }

        accounts.remove(account);
        return true;
    }

    @Override
    public List<Account> getAllAccounts() {

        return new ArrayList<>(accounts);
    }
}