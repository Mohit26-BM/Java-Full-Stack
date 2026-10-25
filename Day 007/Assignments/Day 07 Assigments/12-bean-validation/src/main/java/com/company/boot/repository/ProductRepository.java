package com.company.boot.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.company.boot.dto.ProductRequest;
import com.company.boot.dto.ProductResponse;

/** Data access for a12_categories and a12_products (JdbcTemplate). */
@Repository
public class ProductRepository {

    private static final String SELECT_COLUMNS =
            "SELECT product_id, name, sku, category_code, price, discount_price, stock, "
            + "supplier_email, tags, created_at FROM a12_products";

    private static final RowMapper<ProductResponse> ROW_MAPPER = (rs, rowNum) -> {
        String tags = rs.getString("tags");
        return new ProductResponse(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getString("sku"),
                rs.getString("category_code"),
                rs.getBigDecimal("price"),
                rs.getBigDecimal("discount_price"),
                rs.getInt("stock"),
                rs.getString("supplier_email"),
                tags == null ? Collections.emptyList() : Arrays.asList(tags.split(",")),
                rs.getTimestamp("created_at").toLocalDateTime());
    };

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<BigDecimal> findCategoryMaxPrice(String categoryCode) {
        return jdbcTemplate.queryForList(
                "SELECT max_price FROM a12_categories WHERE category_code = ?",
                BigDecimal.class, categoryCode).stream().findFirst();
    }

    public boolean skuExists(String sku) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM a12_products WHERE sku = ?", Integer.class, sku);
        return count != null && count > 0;
    }

    public int insert(ProductRequest request, String categoryCode) {
        String tags = (request.getTags() == null || request.getTags().isEmpty())
                ? null : String.join(",", request.getTags());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO a12_products (name, sku, category_code, price, discount_price, stock, "
                    + "supplier_email, tags) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getName().trim());
            ps.setString(2, request.getSku());
            ps.setString(3, categoryCode);
            ps.setBigDecimal(4, request.getPrice());
            ps.setBigDecimal(5, request.getDiscountPrice());
            ps.setInt(6, request.getStock());
            ps.setString(7, request.getSupplierEmail().trim().toLowerCase());
            ps.setString(8, tags);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public Optional<ProductResponse> findById(int id) {
        return jdbcTemplate.query(SELECT_COLUMNS + " WHERE product_id = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public List<ProductResponse> search(String categoryCode, BigDecimal maxPrice) {
        StringBuilder sql = new StringBuilder(SELECT_COLUMNS).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (categoryCode != null) {
            sql.append(" AND category_code = ?");
            params.add(categoryCode);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        sql.append(" ORDER BY product_id");
        return jdbcTemplate.query(sql.toString(), ROW_MAPPER, params.toArray());
    }
}
