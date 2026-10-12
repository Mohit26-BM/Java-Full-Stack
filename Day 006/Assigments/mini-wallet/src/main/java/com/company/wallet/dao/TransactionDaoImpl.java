package com.company.wallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.company.wallet.exception.DataAccessException;
import com.company.wallet.model.TransactionType;
import com.company.wallet.model.WalletTransaction;

public class TransactionDaoImpl implements TransactionDao {

    private static final String INSERT_TRANSACTION =
            "INSERT INTO wallet_transactions "
          + "(reference, wallet_id, transaction_type, amount, balance_after, "
          + "counterparty_wallet_id, description) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private final DataSource dataSource;

    public TransactionDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public long insert(Connection connection, WalletTransaction transaction) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(INSERT_TRANSACTION, Statement.RETURN_GENERATED_KEYS)) {

            bind(statement, transaction);

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
    public int[] insertBatch(Connection connection, List<WalletTransaction> transactions)
            throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(INSERT_TRANSACTION)) {

            for (WalletTransaction transaction : transactions) {

                bind(statement, transaction);

                statement.addBatch();
            }

            return statement.executeBatch();
        }
    }

    @Override
    public List<WalletTransaction> findByWallet(long walletId, int limit) {

        /*
        LEFT JOIN: deposits and withdrawals have no counterparty.
        */
        String sql = "SELECT t.transaction_id, t.reference, t.wallet_id, t.transaction_type, "
                   + "t.amount, t.balance_after, t.counterparty_wallet_id, t.description, "
                   + "t.created_at, cw.wallet_number AS counterparty_number "
                   + "FROM wallet_transactions t "
                   + "LEFT JOIN wallets cw ON t.counterparty_wallet_id = cw.wallet_id "
                   + "WHERE t.wallet_id = ? "
                   + "ORDER BY t.created_at DESC, t.transaction_id DESC "
                   + "LIMIT ?";

        List<WalletTransaction> transactions = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, walletId);
            statement.setInt(2, limit);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    transactions.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read transaction history", e);
        }

        return transactions;
    }

    @Override
    public long countAll() {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT COUNT(*) FROM wallet_transactions");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();

            return resultSet.getLong(1);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to count transactions", e);
        }
    }

    private void bind(PreparedStatement statement, WalletTransaction transaction)
            throws SQLException {

        statement.setString(1, transaction.getReference());
        statement.setLong(2, transaction.getWalletId());
        statement.setString(3, transaction.getTransactionType().name());
        statement.setBigDecimal(4, transaction.getAmount());
        statement.setBigDecimal(5, transaction.getBalanceAfter());

        if (transaction.getCounterpartyWalletId() == null) {
            statement.setNull(6, Types.BIGINT);
        } else {
            statement.setLong(6, transaction.getCounterpartyWalletId());
        }

        if (transaction.getDescription() == null) {
            statement.setNull(7, Types.VARCHAR);
        } else {
            statement.setString(7, transaction.getDescription());
        }
    }

    private WalletTransaction mapRow(ResultSet resultSet) throws SQLException {

        WalletTransaction transaction = new WalletTransaction();

        transaction.setTransactionId(resultSet.getLong("transaction_id"));
        transaction.setReference(resultSet.getString("reference"));
        transaction.setWalletId(resultSet.getLong("wallet_id"));
        transaction.setTransactionType(TransactionType.valueOf(resultSet.getString("transaction_type")));
        transaction.setAmount(resultSet.getBigDecimal("amount"));
        transaction.setBalanceAfter(resultSet.getBigDecimal("balance_after"));

        long counterpartyId = resultSet.getLong("counterparty_wallet_id");
        transaction.setCounterpartyWalletId(resultSet.wasNull() ? null : counterpartyId);

        transaction.setDescription(resultSet.getString("description"));
        transaction.setCounterpartyWalletNumber(resultSet.getString("counterparty_number"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        transaction.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());

        return transaction;
    }
}
