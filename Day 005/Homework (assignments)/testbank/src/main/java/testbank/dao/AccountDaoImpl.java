package testbank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import testbank.entity.Account;
import testbank.entity.AccountType;
import testbank.util.DBConnection;

public class AccountDaoImpl implements AccountDao {

    @Override
    public boolean addAccount(Account account) {

        if (account == null) {
            return false;
        }

        String sql = "INSERT INTO account "
                   + "(account_number, customer_id, account_type, "
                   + "balance, date_of_creation) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, account.getAccountNumber());
            statement.setInt(2, account.getCustomerId());
            statement.setString(3, account.getAccountType().name());
            statement.setInt(4, account.getBalance());

            statement.setDate(
                    5,
                    new java.sql.Date(
                            account.getDateOfCreation().getTime()
                    )
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public int getNextAccountNumber() {

        String sql = "SELECT COALESCE(MAX(account_number), 1000) + 1 "
                   + "FROM account";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException(
                "Unable to generate the next account number."
        );
    }

    @Override
    public Account getAccount(int accountNumber) {

        String sql = "SELECT account_number, customer_id, "
                   + "account_type, balance, date_of_creation "
                   + "FROM account "
                   + "WHERE account_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    Account account = new Account(
                            resultSet.getInt("customer_id"),
                            AccountType.valueOf(
                                    resultSet.getString("account_type")
                            ),
                            resultSet.getInt("balance"),
                            resultSet.getDate("date_of_creation")
                    );

                    account.setAccountNumber(
                            resultSet.getInt("account_number")
                    );

                    return account;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updateAccount(Account account) {

        if (account == null) {
            return false;
        }

        String sql = "UPDATE account "
                   + "SET customer_id = ?, "
                   + "account_type = ?, "
                   + "balance = ?, "
                   + "date_of_creation = ? "
                   + "WHERE account_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, account.getCustomerId());

            statement.setString(
                    2,
                    account.getAccountType().name()
            );

            statement.setInt(3, account.getBalance());

            statement.setDate(
                    4,
                    new java.sql.Date(
                            account.getDateOfCreation().getTime()
                    )
            );

            statement.setInt(
                    5,
                    account.getAccountNumber()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteAccount(int accountNumber) {

        String sql = "DELETE FROM account "
                   + "WHERE account_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, accountNumber);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Account> getAllAccounts() {

        List<Account> accounts = new ArrayList<>();

        String sql = "SELECT account_number, customer_id, "
                   + "account_type, balance, date_of_creation "
                   + "FROM account";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Account account = new Account(
                        resultSet.getInt("customer_id"),
                        AccountType.valueOf(
                                resultSet.getString("account_type")
                        ),
                        resultSet.getInt("balance"),
                        resultSet.getDate("date_of_creation")
                );

                account.setAccountNumber(
                        resultSet.getInt("account_number")
                );

                accounts.add(account);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }
}

