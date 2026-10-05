package testbank.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import testbank.entity.Transaction;

public class TransactionDaoImpl implements TransactionDao {

    private final List<Transaction> transactions;

    public TransactionDaoImpl() {
        transactions = new ArrayList<>();
    }

    @Override
    public List<Transaction> getAllTransactions() {

        return new ArrayList<>(transactions);
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(int accountId) {

        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : transactions) {

            if (transaction.getAccountId() == accountId) {
                result.add(transaction);
            }
        }

        return result;
    }
    @Override
    public boolean addTransaction(Transaction transaction) {

        if (transaction == null) {
            return false;
        }

        transactions.add(transaction);
        return true;
    }

    @Override
    public List<Transaction> getTransactionsByType(String type) {

        List<Transaction> result = new ArrayList<>();

        if (type == null || type.trim().isEmpty()) {
            return result;
        }

        for (Transaction transaction : transactions) {

            if (transaction.getTransactionType()
                    .name()
                    .equalsIgnoreCase(type.trim())) {

                result.add(transaction);
            }
        }

        return result;
    }

    @Override
    public List<Transaction> getTransactionsByDateRange(
            String startDate, String endDate) {

        List<Transaction> result = new ArrayList<>();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd-MM-yyyy");

        dateFormat.setLenient(false);

        try {

            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            // Make the end date inclusive for the entire day
            end.setTime(
                    end.getTime() + (24 * 60 * 60 * 1000) - 1
            );

            // Make sure start date is not after end date
            if (start.after(end)) {
                throw new IllegalArgumentException(
                        "Start date cannot be after end date."
                );
            }

            for (Transaction transaction : transactions) {

                Date transactionDate =
                        transaction.getTransactionDate();

                if (!transactionDate.before(start)
                        && !transactionDate.after(end)) {

                    result.add(transaction);
                }
            }

        } catch (ParseException e) {

            throw new IllegalArgumentException(
                    "Invalid date format. "
                    + "Please use dd-MM-yyyy."
            );
        }

        return result;
    }
}