-- =========================================================
-- Day 07 - Assignment 14: CRUD with Spring Data JPA
-- Shared database Day_007_Assigments; this script only touches a14_ tables.
-- Run once before starting the application:
--   mysql -u root -p < schema.sql
-- (spring.jpa.hibernate.ddl-auto=validate: Hibernate checks this table, never creates it.)
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;
USE Day_007_Assigments;

DROP TABLE IF EXISTS a14_products;

CREATE TABLE a14_products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    category   VARCHAR(50)   NOT NULL,
    price      DECIMAL(10,2) NOT NULL,
    stock      INT           NOT NULL,
    created_at DATETIME      NOT NULL,
    updated_at DATETIME      NOT NULL,
    CONSTRAINT uq_a14_products_name UNIQUE (name)
);

-- 12 sample rows so that pagination has something to page through
INSERT INTO a14_products (name, category, price, stock, created_at, updated_at) VALUES
    ('Wireless Mouse',      'Electronics',   799.00,  50, NOW(), NOW()),
    ('Mechanical Keyboard', 'Electronics',  3499.00,  20, NOW(), NOW()),
    ('USB-C Hub',           'Electronics',  1299.00,  35, NOW(), NOW()),
    ('27-inch Monitor',     'Electronics', 15999.00,   8, NOW(), NOW()),
    ('Noise Cancelling Headphones', 'Electronics', 8999.00, 12, NOW(), NOW()),
    ('Laptop Stand',        'Accessories',  1599.00,  40, NOW(), NOW()),
    ('Desk Lamp',           'Accessories',   999.00,  25, NOW(), NOW()),
    ('Mouse Pad XL',        'Accessories',   499.00, 100, NOW(), NOW()),
    ('Clean Code',          'Books',         450.00,  30, NOW(), NOW()),
    ('Effective Java',      'Books',         899.00,  18, NOW(), NOW()),
    ('Spring in Action',    'Books',         749.00,  22, NOW(), NOW()),
    ('Java Concurrency in Practice', 'Books', 1099.00, 0, NOW(), NOW());
