package testbank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import testbank.entity.Transaction;
import testbank.entity.TransactionType;
import testbank.util.DBConnection;

public class TransactionDaoImpl implements TransactionDao {

    @Override
    public boolean addTransaction(Transaction transaction) {

        if (transaction == null) {
            return false;
        }

        String sql = "INSERT INTO `transaction` "
                   + "(account_id, transaction_date, amount, "
                   + "transaction_type, description) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, transaction.getAccountId());

            statement.setTimestamp(
                    2,
                    new java.sql.Timestamp(
                            transaction.getTransactionDate().getTime()
                    )
            );

            statement.setInt(3, transaction.getAmount());

            statement.setString(
                    4,
                    transaction.getTransactionType().name()
            );

            statement.setString(
                    5,
                    transaction.getDescription()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Transaction> getAllTransactions() {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT id, account_id, transaction_date, "
                   + "amount, transaction_type, description "
                   + "FROM `transaction`";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Transaction transaction =
                        createTransactionFromResultSet(resultSet);

                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(
            int accountId) {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT id, account_id, transaction_date, "
                   + "amount, transaction_type, description "
                   + "FROM `transaction` "
                   + "WHERE account_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Transaction transaction =
                            createTransactionFromResultSet(resultSet);

                    transactions.add(transaction);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByType(String type) {

        List<Transaction> transactions = new ArrayList<>();

        if (type == null || type.trim().isEmpty()) {
            return transactions;
        }

        String sql = "SELECT id, account_id, transaction_date, "
                   + "amount, transaction_type, description "
                   + "FROM `transaction` "
                   + "WHERE transaction_type = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    type.trim().toUpperCase()
            );

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Transaction transaction =
                            createTransactionFromResultSet(resultSet);

                    transactions.add(transaction);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByDateRange(
            String startDate, String endDate) {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT id, account_id, transaction_date, "
                   + "amount, transaction_type, description "
                   + "FROM `transaction` "
                   + "WHERE transaction_date >= ? "
                   + "AND transaction_date < ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            java.sql.Date start =
                    java.sql.Date.valueOf(
                            convertDateFormat(startDate)
                    );

            java.sql.Date end =
                    java.sql.Date.valueOf(
                            convertDateFormat(endDate)
                    );

            if (start.after(end)) {
                throw new IllegalArgumentException(
                        "Start date cannot be after end date."
                );
            }

            // Include the complete end date.
            java.sql.Date nextDay =
                    new java.sql.Date(
                            end.getTime() + (24 * 60 * 60 * 1000)
                    );

            statement.setDate(1, start);
            statement.setDate(2, nextDay);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Transaction transaction =
                            createTransactionFromResultSet(resultSet);

                    transactions.add(transaction);
                }
            }

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Invalid date format. "
                    + "Please use dd-MM-yyyy."
            );

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return transactions;
    }

    private Transaction createTransactionFromResultSet(
            ResultSet resultSet) throws SQLException {

        return new Transaction(
                resultSet.getInt("id"),
                resultSet.getInt("account_id"),
                resultSet.getTimestamp("transaction_date"),
                resultSet.getInt("amount"),
                TransactionType.valueOf(
                        resultSet.getString("transaction_type")
                ),
                resultSet.getString("description")
        );
    }

    private String convertDateFormat(String date) {

        String[] parts = date.split("-");

        if (parts.length != 3) {
            throw new IllegalArgumentException();
        }

        return parts[2] + "-" + parts[1] + "-" + parts[0];
    }
}