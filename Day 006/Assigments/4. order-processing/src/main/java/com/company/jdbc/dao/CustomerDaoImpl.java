package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.DuplicateCustomerException;
import com.company.jdbc.model.Customer;

public class CustomerDaoImpl implements CustomerDao {

    private static final int MYSQL_DUPLICATE_KEY = 1062;

    @Override
    public long add(Customer customer)
            throws DuplicateCustomerException {

        String sql = "INSERT INTO customers (name, email) VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getErrorCode() == MYSQL_DUPLICATE_KEY) {
                throw new DuplicateCustomerException(
                        "A customer with email " + customer.getEmail()
                        + " already exists."
                );
            }

            throw new DataAccessException("Unable to add customer", e);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to add customer", e);
        }
    }

    @Override
    public Customer findById(long customerId) {

        try (Connection connection = DBConnection.getConnection()) {

            return findById(connection, customerId);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read customer " + customerId, e
            );
        }
    }

    @Override
    public Customer findById(Connection connection, long customerId)
            throws SQLException {

        String sql = "SELECT customer_id, name, email "
                   + "FROM customers "
                   + "WHERE customer_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    @Override
    public List<Customer> findAll() {

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT customer_id, name, email "
                   + "FROM customers "
                   + "ORDER BY customer_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                customers.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read customers", e);
        }

        return customers;
    }

    private Customer mapRow(ResultSet resultSet) throws SQLException {

        Customer customer = new Customer(
                resultSet.getString("name"),
                resultSet.getString("email")
        );

        customer.setCustomerId(resultSet.getLong("customer_id"));

        return customer;
    }
}
