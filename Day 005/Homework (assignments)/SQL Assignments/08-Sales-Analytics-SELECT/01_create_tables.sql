-- Sales Analytics - create database and tables

CREATE DATABASE sales_db;
USE sales_db;

-- categories table
CREATE TABLE categories (
    category_id    INT AUTO_INCREMENT PRIMARY KEY,
    category_name  VARCHAR(50) NOT NULL UNIQUE
);

-- products table
CREATE TABLE products (
    product_id    INT AUTO_INCREMENT PRIMARY KEY,
    sku           VARCHAR(20)  NOT NULL UNIQUE,
    product_name  VARCHAR(100) NOT NULL,
    category_id   INT NOT NULL,
    list_price    DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- customers table
CREATE TABLE customers (
    customer_id    INT AUTO_INCREMENT PRIMARY KEY,
    company_name   VARCHAR(100) NOT NULL UNIQUE,
    contact_email  VARCHAR(100) NOT NULL UNIQUE,
    region         VARCHAR(20)  NOT NULL,
    city           VARCHAR(50)  NOT NULL
);

-- orders table
CREATE TABLE orders (
    order_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id  INT NOT NULL,
    order_date   DATE NOT NULL,
    status       ENUM('Completed', 'Cancelled') NOT NULL DEFAULT 'Completed',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- order_items table
CREATE TABLE order_items (
    order_id    INT NOT NULL,
    product_id  INT NOT NULL,
    quantity    INT NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(10,2) NOT NULL,
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
