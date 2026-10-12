package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.model.Order;
import com.company.jdbc.model.OrderStatus;

public interface OrderDao {

    // ---------- inside a transaction ----------

    /*
    Inserts the order header with total 0.
    Returns the generated order_id (getGeneratedKeys()).
    */
    long insert(Connection connection, long customerId, OrderStatus status)
            throws SQLException;

    void updateTotal(Connection connection, long orderId, BigDecimal total)
            throws SQLException;

    /*
    Order header only, row locked with FOR UPDATE. Null if not found.
    */
    Order findByIdForUpdate(Connection connection, long orderId)
            throws SQLException;

    /*
    Changes PLACED -> CANCELLED. Returns false if the order was
    not PLACED any more (e.g. already cancelled).
    */
    boolean markCancelled(Connection connection, long orderId)
            throws SQLException;

    // ---------- standalone ----------

    /*
    Order + customer + items + product names in ONE JOIN query.
    Returns null when the order does not exist.
    */
    Order findDetails(long orderId);

    /*
    Order headers with customer name, newest first.
    */
    List<Order> findAll();

    int countAll();
}
