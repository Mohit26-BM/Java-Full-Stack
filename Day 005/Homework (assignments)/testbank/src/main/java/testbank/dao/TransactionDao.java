package testbank.dao;

import java.util.List;

import testbank.entity.Transaction;

public interface TransactionDao {

    boolean addTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByAccountId(int accountId);

    List<Transaction> getTransactionsByType(String type);

    List<Transaction> getTransactionsByDateRange(
            String startDate,
            String endDate);
}