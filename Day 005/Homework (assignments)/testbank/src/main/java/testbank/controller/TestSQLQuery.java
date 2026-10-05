package testbank.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import testbank.util.DBConnection;

public class TestSQLQuery {

    public static void main(String[] args) {

        String sql = "SELECT * FROM testbank.customer";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                int customerId = resultSet.getInt("customer_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");

                System.out.println(
                        customerId + " | "
                        + name + " | "
                        + email + " | "
                        + phone
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}