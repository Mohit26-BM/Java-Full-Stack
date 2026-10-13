package com.company.spring.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.company.spring.payment.PaymentReceipt;

/*
Saves and reads payments in a05_payments (plain JDBC on the
injected DataSource). Registered as a bean in CommonConfig.
*/
public class PaymentRepository {

    private final DataSource dataSource;

    public PaymentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PaymentReceipt save(String orderId, BigDecimal amount, String method, String reference) {

        String sql = "INSERT INTO a05_payments "
                   + "(order_id, amount, payment_method, payment_reference) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, orderId);
            statement.setBigDecimal(2, amount);
            statement.setString(3, method);
            statement.setString(4, reference);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                keys.next();

                return new PaymentReceipt(keys.getInt(1), orderId, amount, method, reference);
            }

        } catch (SQLException e) {

            throw new IllegalStateException("Could not save payment: " + e.getMessage()
                    + " (did you run schema.sql?)", e);
        }
    }

    public List<PaymentReceipt> findRecent(int limit) {

        String sql = "SELECT payment_id, order_id, amount, payment_method, payment_reference "
                   + "FROM a05_payments ORDER BY payment_id DESC LIMIT ?";

        List<PaymentReceipt> receipts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    receipts.add(new PaymentReceipt(
                            resultSet.getInt("payment_id"),
                            resultSet.getString("order_id"),
                            resultSet.getBigDecimal("amount"),
                            resultSet.getString("payment_method"),
                            resultSet.getString("payment_reference")));
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException("Could not read payments: " + e.getMessage(), e);
        }

        return receipts;
    }
}
