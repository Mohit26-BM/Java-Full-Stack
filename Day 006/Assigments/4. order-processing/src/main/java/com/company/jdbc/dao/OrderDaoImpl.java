package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.Order;
import com.company.jdbc.model.OrderItem;
import com.company.jdbc.model.OrderStatus;

public class OrderDaoImpl implements OrderDao {

    // =========================================================
    // INSIDE A TRANSACTION (caller's connection, not closed here)
    // =========================================================

    @Override
    public long insert(Connection connection, long customerId, OrderStatus status)
            throws SQLException {

        /*
        order_date is filled by MySQL; total_amount starts at its
        DEFAULT 0 and is updated after all items are inserted.
        */
        String sql = "INSERT INTO orders (customer_id, status) VALUES (?, ?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, customerId);
            statement.setString(2, status.name());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException("No order_id was generated.");
                }

                return keys.getLong(1);
            }
        }
    }

    @Override
    public void updateTotal(Connection connection, long orderId, BigDecimal total)
            throws SQLException {

        String sql = "UPDATE orders SET total_amount = ? WHERE order_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, total);
            statement.setLong(2, orderId);

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Order " + orderId + " not found for total update.");
            }
        }
    }

    @Override
    public Order findByIdForUpdate(Connection connection, long orderId)
            throws SQLException {

        String sql = "SELECT order_id, customer_id, order_date, status, total_amount "
                   + "FROM orders "
                   + "WHERE order_id = ? "
                   + "FOR UPDATE";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                Order order = new Order();
                mapHeader(resultSet, order);
                return order;
            }
        }
    }

    @Override
    public boolean markCancelled(Connection connection, long orderId)
            throws SQLException {

        /*
        "AND status = 'PLACED'" guarantees an order is cancelled
        at most once, even if two cancel requests arrive together.
        */
        String sql = "UPDATE orders "
                   + "SET status = ? "
                   + "WHERE order_id = ? AND status = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, OrderStatus.CANCELLED.name());
            statement.setLong(2, orderId);
            statement.setString(3, OrderStatus.PLACED.name());

            return statement.executeUpdate() == 1;
        }
    }

    // =========================================================
    // STANDALONE (own connection)
    // =========================================================

    @Override
    public Order findDetails(long orderId) {

        /*
        One query, four tables:
          orders      -> header
          customers   -> customer name and email
          order_items -> one row per item
          products    -> product name for each item
        The header columns repeat on every row; they are read once.
        */
        String sql = "SELECT o.order_id, o.customer_id, o.order_date, "
                   + "o.status, o.total_amount, "
                   + "c.name AS customer_name, c.email AS customer_email, "
                   + "oi.order_item_id, oi.product_id, oi.quantity, oi.unit_price, "
                   + "p.product_name "
                   + "FROM orders o "
                   + "JOIN customers c ON o.customer_id = c.customer_id "
                   + "LEFT JOIN order_items oi ON oi.order_id = o.order_id "
                   + "LEFT JOIN products p ON oi.product_id = p.product_id "
                   + "WHERE o.order_id = ? "
                   + "ORDER BY oi.order_item_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {

                Order order = null;

                while (resultSet.next()) {

                    if (order == null) {
                        order = new Order();
                        mapHeader(resultSet, order);
                        order.setCustomerName(resultSet.getString("customer_name"));
                        order.setCustomerEmail(resultSet.getString("customer_email"));
                    }

                    long itemId = resultSet.getLong("order_item_id");

                    if (!resultSet.wasNull()) {

                        OrderItem item = new OrderItem(
                                resultSet.getLong("order_id"),
                                resultSet.getLong("product_id"),
                                resultSet.getInt("quantity"),
                                resultSet.getBigDecimal("unit_price")
                        );

                        item.setOrderItemId(itemId);
                        item.setProductName(resultSet.getString("product_name"));

                        order.getItems().add(item);
                    }
                }

                return order;
            }

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read order " + orderId, e
            );
        }
    }

    @Override
    public List<Order> findAll() {

        String sql = "SELECT o.order_id, o.customer_id, o.order_date, "
                   + "o.status, o.total_amount, c.name AS customer_name "
                   + "FROM orders o "
                   + "JOIN customers c ON o.customer_id = c.customer_id "
                   + "ORDER BY o.order_id DESC";

        List<Order> orders = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Order order = new Order();
                mapHeader(resultSet, order);
                order.setCustomerName(resultSet.getString("customer_name"));

                orders.add(order);
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read orders", e);
        }

        return orders;
    }

    @Override
    public int countAll() {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT COUNT(*) FROM orders");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt(1);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to count orders", e);
        }
    }

    private void mapHeader(ResultSet resultSet, Order order) throws SQLException {

        order.setOrderId(resultSet.getLong("order_id"));
        order.setCustomerId(resultSet.getLong("customer_id"));

        Timestamp date = resultSet.getTimestamp("order_date");
        order.setOrderDate(date == null ? null : date.toLocalDateTime());

        order.setStatus(OrderStatus.valueOf(resultSet.getString("status")));
        order.setTotalAmount(resultSet.getBigDecimal("total_amount"));
    }
}
