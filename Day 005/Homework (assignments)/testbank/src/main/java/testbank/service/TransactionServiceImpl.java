package testbank.service;

import java.util.List;

import testbank.dao.TransactionDao;
import testbank.entity.Transaction;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionDao;

    private static int nextTransactionId = 1;

    public TransactionServiceImpl(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public void addTransaction(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null."
            );
        }

        transaction.setId(nextTransactionId++);

        boolean added =
                transactionDao.addTransaction(transaction);

        if (!added) {
            throw new IllegalStateException(
                    "Unable to record transaction."
            );
        }
    }

    @Override
    public List<Transaction> getAllTransactions() {

        return transactionDao.getAllTransactions();
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(
            int accountId) {

        if (accountId <= 0) {
            throw new IllegalArgumentException(
                    "Account ID must be greater than 0."
            );
        }

        return transactionDao.getTransactionsByAccountId(accountId);
    }

    @Override
    public List<Transaction> getTransactionsByType(
            String type) {

        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Transaction type cannot be empty."
            );
        }

        return transactionDao.getTransactionsByType(type);
    }

    @Override
    public List<Transaction> getTransactionsByDateRange(
            String startDate,
            String endDate) {

        if (startDate == null || startDate.trim().isEmpty()
                || endDate == null || endDate.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Start date and end date cannot be empty."
            );
        }

        return transactionDao.getTransactionsByDateRange(
                startDate,
                endDate
        );
    }
}