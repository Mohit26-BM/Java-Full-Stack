package testbank.dao;

import java.util.List;

import testbank.entity.Account;

public interface AccountDao {

    boolean addAccount(Account account);

    Account getAccount(int accountNumber);

    boolean updateAccount(Account account);

    boolean deleteAccount(int accountNumber);

    List<Account> getAllAccounts();
}