package com.company.wallet.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.company.wallet.exception.DataAccessException;
import com.company.wallet.model.Wallet;
import com.company.wallet.model.WalletStatus;

public class WalletDaoImpl implements WalletDao {

    /*
    JOIN with customers so wallet listings show the owner's name.
    */
    private static final String SELECT_WALLET =
            "SELECT w.wallet_id, w.wallet_number, w.customer_id, w.balance, "
          + "w.status, w.created_at, c.full_name "
          + "FROM wallets w "
          + "JOIN customers c ON w.customer_id = c.customer_id ";

    private final DataSource dataSource;

    public WalletDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // =========================================================
    // INSIDE A TRANSACTION
    // =========================================================

    @Override
    public long insert(Connection connection, long customerId) throws SQLException {

        String sql = "INSERT INTO wallets (customer_id, balance, status) VALUES (?, 0, ?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, customerId);
            statement.setString(2, WalletStatus.ACTIVE.name());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException("No wallet_id was generated.");
                }

                return keys.getLong(1);
            }
        }
    }

    @Override
    public void assignWalletNumber(Connection connection, long walletId, String walletNumber)
            throws SQLException {

        String sql = "UPDATE wallets SET wallet_number = ? WHERE wallet_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, walletNumber);
            statement.setLong(2, walletId);

            requireOneRow(statement.executeUpdate(), walletId);
        }
    }

    @Override
    public Wallet findByNumber(Connection connection, String walletNumber, boolean forUpdate)
            throws SQLException {

        /*
        "FOR UPDATE OF w" (MySQL 8) locks ONLY the wallets row, not the
        JOINed customers row, so transfers on two wallets of the same
        customer do not block each other through the customer row.
        */
        String sql = SELECT_WALLET
                   + "WHERE w.wallet_number = ?"
                   + (forUpdate ? " FOR UPDATE OF w" : "");

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, walletNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    @Override
    public void updateBalance(Connection connection, long walletId, BigDecimal newBalance)
            throws SQLException {

        String sql = "UPDATE wallets SET balance = ? WHERE wallet_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, newBalance);
            statement.setLong(2, walletId);

            requireOneRow(statement.executeUpdate(), walletId);
        }
    }

    @Override
    public int[] updateBalancesBatch(Connection connection, Map<Long, BigDecimal> newBalances)
            throws SQLException {

        String sql = "UPDATE wallets SET balance = ? WHERE wallet_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Map.Entry<Long, BigDecimal> entry : newBalances.entrySet()) {

                statement.setBigDecimal(1, entry.getValue());
                statement.setLong(2, entry.getKey());

                statement.addBatch();
            }

            return statement.executeBatch();
        }
    }

    // =========================================================
    // STANDALONE (own pooled connection)
    // =========================================================

    @Override
    public Wallet findByNumber(String walletNumber) {

        try (Connection connection = dataSource.getConnection()) {

            return findByNumber(connection, walletNumber, false);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read wallet " + walletNumber, e);
        }
    }

    @Override
    public List<Wallet> findByCustomer(long customerId) {

        return findList(SELECT_WALLET + "WHERE w.customer_id = ? ORDER BY w.wallet_id", customerId);
    }

    @Override
    public List<Wallet> findAll() {

        return findList(SELECT_WALLET + "ORDER BY w.wallet_id", null);
    }

    @Override
    public boolean updateStatus(String walletNumber, WalletStatus status) {

        String sql = "UPDATE wallets SET status = ? WHERE wallet_number = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status.name());
            statement.setString(2, walletNumber);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new DataAccessException("Unable to update status of " + walletNumber, e);
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private List<Wallet> findList(String sql, Long customerId) {

        List<Wallet> wallets = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (customerId != null) {
                statement.setLong(1, customerId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    wallets.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read wallets", e);
        }

        return wallets;
    }

    private void requireOneRow(int updated, long walletId) throws SQLException {

        if (updated != 1) {
            throw new SQLException("Expected to update wallet " + walletId + " but updated " + updated);
        }
    }

    private Wallet mapRow(ResultSet resultSet) throws SQLException {

        Wallet wallet = new Wallet();

        wallet.setWalletId(resultSet.getLong("wallet_id"));
        wallet.setWalletNumber(resultSet.getString("wallet_number"));
        wallet.setCustomerId(resultSet.getLong("customer_id"));
        wallet.setBalance(resultSet.getBigDecimal("balance"));
        wallet.setStatus(WalletStatus.valueOf(resultSet.getString("status")));
        wallet.setCustomerName(resultSet.getString("full_name"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        wallet.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());

        return wallet;
    }
}
