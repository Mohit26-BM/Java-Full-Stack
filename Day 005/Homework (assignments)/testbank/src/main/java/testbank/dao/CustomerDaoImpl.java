package testbank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import testbank.entity.Customer;
import testbank.util.DBConnection;

public class CustomerDaoImpl implements CustomerDao {

    @Override
    public boolean addCustomer(Customer customer) {

        if (customer == null) {
            return false;
        }

        String sql = "INSERT INTO customer "
                   + "(Customer_ID, Name, Email, Phone) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, customer.getCustomerId());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getPhone());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Customer getCustomerById(int customerId) {

        String sql = "SELECT Customer_ID, Name, Email, Phone "
                   + "FROM customer "
                   + "WHERE Customer_ID = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Customer(
                            resultSet.getInt("Customer_ID"),
                            resultSet.getString("Name"),
                            resultSet.getString("Email"),
                            resultSet.getString("Phone")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Customer> getAllCustomers() {

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT Customer_ID, Name, Email, Phone "
                   + "FROM customer";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Customer customer = new Customer(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Email"),
                        resultSet.getString("Phone")
                );

                customers.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    @Override
    public boolean updateCustomer(Customer customer) {

        if (customer == null) {
            return false;
        }

        String sql = "UPDATE customer "
                   + "SET Name = ?, Email = ?, Phone = ? "
                   + "WHERE Customer_ID = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPhone());
            statement.setInt(4, customer.getCustomerId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int customerId) {

        String sql = "DELETE FROM customer "
                   + "WHERE Customer_ID = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}