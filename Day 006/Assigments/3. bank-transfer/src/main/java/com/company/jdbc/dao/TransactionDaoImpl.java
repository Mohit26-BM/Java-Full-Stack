package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.AccountTransaction;
import com.company.jdbc.model.TransactionType;

public class TransactionDaoImpl implements TransactionDao {

    /*
    LEFT JOINs turn account IDs into account numbers for display.
    LEFT because from_account_id is NULL for opening deposits.
    */
    private static final String SELECT_TRANSACTION =
            "SELECT t.transaction_id, t.from_account_id, t.to_account_id, "
          + "t.amount, t.transaction_type, t.transaction_time, "
          + "f.account_number AS from_number, "
          + "r.account_number AS to_number "
          + "FROM account_transactions t "
          + "LEFT JOIN bank_accounts f ON t.from_account_id = f.account_id "
          + "LEFT JOIN bank_accounts r ON t.to_account_id = r.account_id ";

    @Override
    public long insert(Connection connection, AccountTransaction transaction)
            throws SQLException {

        /*
        transaction_time is filled by MySQL (DEFAULT CURRENT_TIMESTAMP).
        */
        String sql = "INSERT INTO account_transactions "
                   + "(from_account_id, to_account_id, amount, transaction_type) "
                   + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            setNullableLong(statement, 1, transaction.getFromAccountId());
            setNullableLong(statement, 2, transaction.getToAccountId());
            statement.setBigDecimal(3, transaction.getAmount());
            statement.setString(4, transaction.getTransactionType().name());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException("No transaction_id was generated.");
                }

                return keys.getLong(1);
            }
        }
    }

    @Override
    public List<AccountTransaction> findAll() {

        String sql = SELECT_TRANSACTION + "ORDER BY t.transaction_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            return readList(statement);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read transactions", e);
        }
    }

    @Override
    public List<AccountTransaction> findByAccountId(long accountId) {

        String sql = SELECT_TRANSACTION
                   + "WHERE t.from_account_id = ? OR t.to_account_id = ? "
                   + "ORDER BY t.transaction_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, accountId);
            statement.setLong(2, accountId);

            return readList(statement);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read transactions for account " + accountId, e
            );
        }
    }

    @Override
    public int countAll() {

        String sql = "SELECT COUNT(*) FROM account_transactions";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();

            return resultSet.getInt(1);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to count transactions", e);
        }
    }

    private List<AccountTransaction> readList(PreparedStatement statement)
            throws SQLException {

        List<AccountTransaction> transactions = new ArrayList<>();

        try (ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                transactions.add(mapRow(resultSet));
            }
        }

        return transactions;
    }

    private AccountTransaction mapRow(ResultSet resultSet) throws SQLException {

        AccountTransaction transaction = new AccountTransaction();

        transaction.setTransactionId(resultSet.getLong("transaction_id"));

        /*
        getLong() returns 0 for SQL NULL, so wasNull() is checked.
        */
        long fromId = resultSet.getLong("from_account_id");
        transaction.setFromAccountId(resultSet.wasNull() ? null : fromId);

        long toId = resultSet.getLong("to_account_id");
        transaction.setToAccountId(resultSet.wasNull() ? null : toId);

        transaction.setAmount(resultSet.getBigDecimal("amount"));
        transaction.setTransactionType(
                TransactionType.valueOf(resultSet.getString("transaction_type")));

        Timestamp time = resultSet.getTimestamp("transaction_time");
        transaction.setTransactionTime(
                time == null ? null : time.toLocalDateTime());

        transaction.setFromAccountNumber(resultSet.getString("from_number"));
        transaction.setToAccountNumber(resultSet.getString("to_number"));

        return transaction;
    }

    private void setNullableLong(PreparedStatement statement,
                                 int index,
                                 Long value) throws SQLException {

        if (value == null) {
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }
}
