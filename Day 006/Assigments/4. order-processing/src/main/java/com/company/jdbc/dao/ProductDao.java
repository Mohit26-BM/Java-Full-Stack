package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.model.Product;

/*
Methods with a Connection parameter run INSIDE the caller's
transaction and throw SQLException so the caller can roll back.
The others open and close their own connection.
*/
public interface ProductDao {

    // ---------- standalone ----------

    long add(Product product);

    Product findById(long productId);

    List<Product> findAll();

    boolean updatePrice(long productId, BigDecimal newPrice);

    boolean addStock(long productId, int quantity);

    // ---------- inside a transaction ----------

    /*
    SELECT ... FOR UPDATE: locks the product row until commit/rollback,
    so no other order can take the same stock at the same time.
    */
    Product findByIdForUpdate(Connection connection, long productId)
            throws SQLException;

    /*
    Returns false when there is not enough stock (nothing changed).
    */
    boolean decreaseStock(Connection connection, long productId, int quantity)
            throws SQLException;

    void increaseStock(Connection connection, long productId, int quantity)
            throws SQLException;
}
