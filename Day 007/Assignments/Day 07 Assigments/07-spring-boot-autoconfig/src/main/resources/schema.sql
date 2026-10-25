-- =========================================================
-- Day 07 - Assignment 07: Spring Boot Auto-Configuration
-- Shared Day 07 database; table prefixed a07_ so it is
-- uniquely identifiable. Only a07_products is dropped/recreated.
--
-- Run this manually in MySQL (Boot does NOT run it automatically,
-- because spring.sql.init.mode is not set to "always").
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;

USE Day_007_Assigments;

DROP TABLE IF EXISTS a07_products;

CREATE TABLE a07_products (
    product_id   INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100)  NOT NULL,
    category     VARCHAR(50)   NOT NULL,
    price        DECIMAL(10,2) NOT NULL
);

INSERT INTO a07_products (product_name, category, price) VALUES
('Wireless Mouse',      'Electronics',  799.00),
('Mechanical Keyboard', 'Electronics', 3499.00),
('Laptop Stand',        'Accessories', 1599.00),
('USB-C Hub',           'Accessories', 1299.00);
