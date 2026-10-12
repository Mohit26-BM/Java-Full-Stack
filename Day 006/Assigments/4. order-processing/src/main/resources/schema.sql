-- =========================================================
-- E-Commerce Order Processing - Schema
-- Run this first. WARNING: drops and recreates all tables.
-- =========================================================

CREATE DATABASE IF NOT EXISTS ecommerce_orders;

USE ecommerce_orders;

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(120) NOT NULL,
    email       VARCHAR(120) UNIQUE NOT NULL
);

CREATE TABLE products (
    product_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    unit_price   DECIMAL(12,2) NOT NULL,
    stock_qty    INT NOT NULL
);

CREATE TABLE orders (
    order_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id  BIGINT NOT NULL,
    order_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status       VARCHAR(30) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE order_items (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id      BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    quantity      INT NOT NULL,
    unit_price    DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
