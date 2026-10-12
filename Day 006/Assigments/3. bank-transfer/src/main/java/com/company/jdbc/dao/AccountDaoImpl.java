package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.AccountStatus;
import com.company.jdbc.model.BankAccount;

public class AccountDaoImpl implements AccountDao {

    private static final String SELECT_ACCOUNT =
            "SELECT account_id, account_number, customer_name, balance, status "
          + "FROM bank_accounts ";

    // =========================================================
    // INSIDE A TRANSACTION (caller's connection)
    // The connection is NOT closed here; only the statement
    // and result set are closed by try-with-resources.
    // =========================================================

    @Override
    public long insert(Connection connection, BankAccount account)
            throws SQLException {

        String sql = "INSERT INTO bank_accounts "
                   + "(account_number, customer_name, balance, status) "
                   + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, account.getAccountNumber());
            statement.setString(2, account.getCustomerName());
            statement.setBigDecimal(3, account.getBalance());
            statement.setString(4, account.getStatus().name());

            statement.executeUpdate();

            /*
            Generated keys: MySQL tells us the new AUTO_INCREMENT id.
            */
            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException("No account_id was generated.");
                }

                return keys.getLong(1);
            }
        }
    }

    @Override
    public BankAccount findByNumber(Connection connection,
                                    String accountNumber,
                                    boolean lockRow)
            throws SQLException {

        /*
        FOR UPDATE locks the selected row. Any other transaction that
        tries to lock or update the same row WAITS until this
        transaction commits or rolls back.
        */
        String sql = SELECT_ACCOUNT
                   + "WHERE account_number = ?"
                   + (lockRow ? " FOR UPDATE" : "");

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    @Override
    public void debit(Connection connection, long accountId, BigDecimal amount)
            throws SQLException {

        changeBalance(connection, accountId, amount.negate());
    }

    @Override
    public void credit(Connection connection, long accountId, BigDecimal amount)
            throws SQLException {

        changeBalance(connection, accountId, amount);
    }

    private void changeBalance(Connection connection,
                               long accountId,
                               BigDecimal change)
            throws SQLException {

        String sql = "UPDATE bank_accounts "
                   + "SET balance = balance + ? "
                   + "WHERE account_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, change);
            statement.setLong(2, accountId);

            if (statement.executeUpdate() != 1) {
                throw new SQLException(
                        "Balance update affected no row for account_id "
                        + accountId
                );
            }
        }
    }

    // =========================================================
    // STANDALONE (own connection, auto-commit)
    // =========================================================

    @Override
    public BankAccount findByNumber(String accountNumber) {

        try (Connection connection = DBConnection.getConnection()) {

            return findByNumber(connection, accountNumber, false);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read account " + accountNumber, e
            );
        }
    }

    @Override
    public List<BankAccount> findAll() {

        List<BankAccount> accounts = new ArrayList<>();

        String sql = SELECT_ACCOUNT + "ORDER BY account_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                accounts.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read accounts", e);
        }

        return accounts;
    }

    @Override
    public boolean updateStatus(String accountNumber, AccountStatus status) {

        String sql = "UPDATE bank_accounts "
                   + "SET status = ? "
                   + "WHERE account_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status.name());
            statement.setString(2, accountNumber);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to update status of " + accountNumber, e
            );
        }
    }

    private BankAccount mapRow(ResultSet resultSet) throws SQLException {

        BankAccount account = new BankAccount();

        account.setAccountId(resultSet.getLong("account_id"));
        account.setAccountNumber(resultSet.getString("account_number"));
        account.setCustomerName(resultSet.getString("customer_name"));
        account.setBalance(resultSet.getBigDecimal("balance"));
        account.setStatus(AccountStatus.valueOf(resultSet.getString("status")));

        return account;
    }
}
