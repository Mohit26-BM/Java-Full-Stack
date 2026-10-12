package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.model.OrderItem;

public interface OrderItemDao {

    /*
    Inside the caller's transaction. Returns the generated order_item_id.
    */
    long insert(Connection connection, OrderItem item)
            throws SQLException;

    /*
    Inside the caller's transaction (used by cancellation).
    */
    List<OrderItem> findByOrderId(Connection connection, long orderId)
            throws SQLException;

    int countAll();
}
