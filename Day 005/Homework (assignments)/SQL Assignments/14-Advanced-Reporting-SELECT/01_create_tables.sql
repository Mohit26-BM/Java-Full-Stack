-- Advanced Reporting - create database and tables

CREATE DATABASE advanced_report_db;
USE advanced_report_db;

-- categories table
CREATE TABLE categories (
    category_id    INT AUTO_INCREMENT PRIMARY KEY,
    category_name  VARCHAR(50) NOT NULL UNIQUE
);

-- products table
CREATE TABLE products (
    product_id    INT AUTO_INCREMENT PRIMARY KEY,
    product_name  VARCHAR(100) NOT NULL UNIQUE,
    category_id   INT NOT NULL,
    unit_price    DECIMAL(10,2) NOT NULL CHECK (unit_price > 0),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- customers table
CREATE TABLE customers (
    customer_id    INT AUTO_INCREMENT PRIMARY KEY,
    customer_name  VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    city           VARCHAR(50)  NOT NULL
);

-- orders table
CREATE TABLE orders (
    order_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id  INT NOT NULL,
    order_date   DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- order_items table
CREATE TABLE order_items (
    order_id    INT NOT NULL,
    product_id  INT NOT NULL,
    quantity    INT NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- show tables
SHOW TABLES;
DESCRIBE categories;
DESCRIBE products;
DESCRIBE customers;
DESCRIBE orders;
DESCRIBE order_items;
