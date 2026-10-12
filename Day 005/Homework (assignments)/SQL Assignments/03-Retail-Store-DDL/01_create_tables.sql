-- Retail Store - create database and tables

CREATE DATABASE retail_db;
USE retail_db;

-- categories table
CREATE TABLE categories (
    category_id    INT AUTO_INCREMENT PRIMARY KEY,
    category_name  VARCHAR(50) NOT NULL UNIQUE,
    description    VARCHAR(200)
);

-- suppliers table
CREATE TABLE suppliers (
    supplier_id    INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name  VARCHAR(100) NOT NULL UNIQUE,
    contact_name   VARCHAR(50)  NOT NULL,
    email          VARCHAR(100) NOT NULL,
    phone          VARCHAR(15)  NOT NULL,
    city           VARCHAR(50)  NOT NULL
);

-- products table
CREATE TABLE products (
    product_id    INT AUTO_INCREMENT PRIMARY KEY,
    sku           VARCHAR(20)  NOT NULL UNIQUE,
    product_name  VARCHAR(100) NOT NULL,
    category_id   INT NOT NULL,
    supplier_id   INT NOT NULL,
    unit_price    DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- show tables
SHOW TABLES;
DESCRIBE categories;
DESCRIBE suppliers;
DESCRIBE products;
