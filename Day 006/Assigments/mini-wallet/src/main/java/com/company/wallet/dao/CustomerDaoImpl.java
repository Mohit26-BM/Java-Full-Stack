package com.company.wallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.company.wallet.exception.DataAccessException;
import com.company.wallet.exception.DuplicateCustomerException;
import com.company.wallet.model.Customer;

public class CustomerDaoImpl implements CustomerDao {

    private static final int MYSQL_DUPLICATE_KEY = 1062;

    private static final String SELECT_CUSTOMER =
            "SELECT customer_id, full_name, email, phone, created_at FROM customers ";

    private final DataSource dataSource;

    public CustomerDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public long insert(Connection connection, Customer customer)
            throws SQLException, DuplicateCustomerException {

        String sql = "INSERT INTO customers (full_name, email, phone) VALUES (?, ?, ?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, customer.getFullName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPhone());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getErrorCode() != MYSQL_DUPLICATE_KEY) {
                throw e;
            }

            /*
            The constraint name in the MySQL message tells which
            value is duplicated, e.g.
            Duplicate entry 'x' for key 'customers.uk_customers_email'
            */
            if (e.getMessage().contains("uk_customers_email")) {
                throw new DuplicateCustomerException(
                        "A customer with email " + customer.getEmail() + " already exists.");
            }

            if (e.getMessage().contains("uk_customers_phone")) {
                throw new DuplicateCustomerException(
                        "A customer with phone " + customer.getPhone() + " already exists.");
            }

            throw new DuplicateCustomerException("Duplicate customer data: " + e.getMessage());
        }
    }

    @Override
    public boolean exists(Connection connection, long customerId) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement("SELECT 1 FROM customers WHERE customer_id = ?")) {

            statement.setLong(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public Customer findById(long customerId) {

        return findOne(SELECT_CUSTOMER + "WHERE customer_id = ?", customerId);
    }

    @Override
    public Customer findByEmail(String email) {

        return findOne(SELECT_CUSTOMER + "WHERE email = ?", email);
    }

    @Override
    public List<Customer> findAll() {

        List<Customer> customers = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_CUSTOMER + "ORDER BY customer_id");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                customers.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read customers", e);
        }

        return customers;
    }

    private Customer findOne(String sql, Object parameter) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, parameter);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read customer " + parameter, e);
        }
    }

    private Customer mapRow(ResultSet resultSet) throws SQLException {

        Customer customer = new Customer(
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("phone")
        );

        customer.setCustomerId(resultSet.getLong("customer_id"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        customer.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());

        return customer;
    }
}
