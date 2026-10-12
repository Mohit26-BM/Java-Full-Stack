package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.Product;

public class ProductDaoImpl implements ProductDao {

    private static final String SELECT_PRODUCT =
            "SELECT product_id, product_name, unit_price, stock_qty "
          + "FROM products ";

    // =========================================================
    // STANDALONE (own connection)
    // =========================================================

    @Override
    public long add(Product product) {

        String sql = "INSERT INTO products "
                   + "(product_name, unit_price, stock_qty) "
                   + "VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, product.getProductName());
            statement.setBigDecimal(2, product.getUnitPrice());
            statement.setInt(3, product.getStockQty());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to add product", e);
        }
    }

    @Override
    public Product findById(long productId) {

        String sql = SELECT_PRODUCT + "WHERE product_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read product " + productId, e
            );
        }
    }

    @Override
    public List<Product> findAll() {

        List<Product> products = new ArrayList<>();

        String sql = SELECT_PRODUCT + "ORDER BY product_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read products", e);
        }

        return products;
    }

    @Override
    public boolean updatePrice(long productId, BigDecimal newPrice) {

        String sql = "UPDATE products SET unit_price = ? WHERE product_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, newPrice);
            statement.setLong(2, productId);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to update price of product " + productId, e
            );
        }
    }

    @Override
    public boolean addStock(long productId, int quantity) {

        try (Connection connection = DBConnection.getConnection()) {

            return changeStock(connection, productId, quantity) == 1;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to add stock to product " + productId, e
            );
        }
    }

    // =========================================================
    // INSIDE A TRANSACTION (caller's connection, not closed here)
    // =========================================================

    @Override
    public Product findByIdForUpdate(Connection connection, long productId)
            throws SQLException {

        String sql = SELECT_PRODUCT + "WHERE product_id = ? FOR UPDATE";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    @Override
    public boolean decreaseStock(Connection connection, long productId, int quantity)
            throws SQLException {

        /*
        "AND stock_qty >= ?" is a second safety net: even if the
        service check were skipped, stock can never go negative.
        */
        String sql = "UPDATE products "
                   + "SET stock_qty = stock_qty - ? "
                   + "WHERE product_id = ? AND stock_qty >= ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setLong(2, productId);
            statement.setInt(3, quantity);

            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public void increaseStock(Connection connection, long productId, int quantity)
            throws SQLException {

        if (changeStock(connection, productId, quantity) != 1) {
            throw new SQLException(
                    "Stock update affected no row for product " + productId
            );
        }
    }

    private int changeStock(Connection connection, long productId, int quantity)
            throws SQLException {

        String sql = "UPDATE products "
                   + "SET stock_qty = stock_qty + ? "
                   + "WHERE product_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setLong(2, productId);

            return statement.executeUpdate();
        }
    }

    private Product mapRow(ResultSet resultSet) throws SQLException {

        Product product = new Product(
                resultSet.getString("product_name"),
                resultSet.getBigDecimal("unit_price"),
                resultSet.getInt("stock_qty")
        );

        product.setProductId(resultSet.getLong("product_id"));

        return product;
    }
}
