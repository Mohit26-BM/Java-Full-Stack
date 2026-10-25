-- =========================================================
-- Day 07 - Assignment 16: Service Layer Business Logic
-- Shared database Day_007_Assigments; this script only touches a16_ tables.
-- Run once before starting the application:
--   mysql -u root -p < schema.sql
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;
USE Day_007_Assigments;

DROP TABLE IF EXISTS a16_orders;
DROP TABLE IF EXISTS a16_products;

-- Note: NO CHECK constraints on price/stock here on purpose -
-- the business rules are enforced by the SERVICE layer (see README).
CREATE TABLE a16_products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    price      DECIMAL(10,2) NOT NULL,
    stock      INT           NOT NULL,
    CONSTRAINT uq_a16_products_name UNIQUE (name)
);

CREATE TABLE a16_orders (
    order_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT        NOT NULL,
    quantity   INT           NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount   DECIMAL(10,2) NOT NULL,
    total      DECIMAL(10,2) NOT NULL,
    created_at DATETIME      NOT NULL,
    CONSTRAINT fk_a16_orders_product FOREIGN KEY (product_id) REFERENCES a16_products (product_id)
);

INSERT INTO a16_products (name, price, stock) VALUES
    ('Wireless Mouse',      799.00,  50),
    ('Mechanical Keyboard', 3499.00,  5),
    ('27-inch Monitor',    15999.00,  2);
