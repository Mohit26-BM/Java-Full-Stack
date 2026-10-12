package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.OrderItem;

public class OrderItemDaoImpl implements OrderItemDao {

    @Override
    public long insert(Connection connection, OrderItem item)
            throws SQLException {

        String sql = "INSERT INTO order_items "
                   + "(order_id, product_id, quantity, unit_price) "
                   + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, item.getOrderId());
            statement.setLong(2, item.getProductId());
            statement.setInt(3, item.getQuantity());
            statement.setBigDecimal(4, item.getUnitPrice());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException("No order_item_id was generated.");
                }

                return keys.getLong(1);
            }
        }
    }

    @Override
    public List<OrderItem> findByOrderId(Connection connection, long orderId)
            throws SQLException {

        String sql = "SELECT order_item_id, order_id, product_id, quantity, unit_price "
                   + "FROM order_items "
                   + "WHERE order_id = ? "
                   + "ORDER BY product_id";

        List<OrderItem> items = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    OrderItem item = new OrderItem(
                            resultSet.getLong("order_id"),
                            resultSet.getLong("product_id"),
                            resultSet.getInt("quantity"),
                            resultSet.getBigDecimal("unit_price")
                    );

                    item.setOrderItemId(resultSet.getLong("order_item_id"));

                    items.add(item);
                }
            }
        }

        return items;
    }

    @Override
    public int countAll() {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT COUNT(*) FROM order_items");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt(1);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to count order items", e);
        }
    }
}
