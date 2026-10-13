package com.company.boot.product;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/*
JdbcTemplate is injected, but WE NEVER CREATED IT (and never created a
DataSource). Spring Boot's auto-configuration saw spring-boot-starter-jdbc
and the MySQL driver on the classpath, read spring.datasource.* from
application.properties, and registered both beans automatically.

JdbcTemplate also removes the try-with-resources / Connection /
PreparedStatement / ResultSet code of plain JDBC.
*/
@Repository
public class ProductRepository {

    private static final RowMapper<Product> PRODUCT_MAPPER = (resultSet, rowNumber) -> new Product(
            resultSet.getInt("product_id"),
            resultSet.getString("product_name"),
            resultSet.getString("category"),
            resultSet.getBigDecimal("price"));

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> findAll() {
        return jdbcTemplate.query(
                "SELECT product_id, product_name, category, price FROM a07_products ORDER BY product_id",
                PRODUCT_MAPPER);
    }

    public Optional<Product> findById(int productId) {
        return jdbcTemplate.query(
                "SELECT product_id, product_name, category, price FROM a07_products WHERE product_id = ?",
                PRODUCT_MAPPER, productId)
                .stream()
                .findFirst();
    }
}
