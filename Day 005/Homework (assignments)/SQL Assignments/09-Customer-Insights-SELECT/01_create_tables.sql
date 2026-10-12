-- Customer Insights - create database and tables

CREATE DATABASE customer_insights_db;
USE customer_insights_db;

-- customers table
CREATE TABLE customers (
    customer_id   INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    city          VARCHAR(50)  NOT NULL,
    signup_date   DATE NOT NULL,
    loyalty_tier  ENUM('Standard', 'Silver', 'Gold') NOT NULL DEFAULT 'Standard'
);

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
    list_price    DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- purchases table
CREATE TABLE purchases (
    purchase_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id     INT NOT NULL,
    product_id      INT NOT NULL,
    quantity        INT NOT NULL CHECK (quantity > 0),
    unit_price      DECIMAL(10,2) NOT NULL,
    purchase_date   DATE NOT NULL,
    channel         ENUM('Online', 'In-Store') NOT NULL,
    payment_method  ENUM('Card', 'Cash', 'Wallet') NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- show tables
SHOW TABLES;
DESCRIBE customers;
DESCRIBE categories;
DESCRIBE products;
DESCRIBE purchases;
