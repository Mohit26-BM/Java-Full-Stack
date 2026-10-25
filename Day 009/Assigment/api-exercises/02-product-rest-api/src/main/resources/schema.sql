-- =====================================================================
-- Exercise 2 - E-Commerce Product REST API
-- Run this first, then sample-data.sql:
--   mysql -u root -p < schema.sql
--   mysql -u root -p < sample-data.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS product_store;
USE product_store;

DROP TABLE IF EXISTS products;

CREATE TABLE products (
    product_id BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    category   VARCHAR(50)   NOT NULL,
    price      DECIMAL(10,2) NOT NULL,
    quantity   INT           NOT NULL,
    brand      VARCHAR(50),
    active     BOOLEAN       NOT NULL DEFAULT TRUE,
    version    BIGINT        NOT NULL DEFAULT 0,          -- optimistic locking (@Version)
    CONSTRAINT chk_products_price    CHECK (price > 0),
    CONSTRAINT chk_products_quantity CHECK (quantity >= 0)
);
