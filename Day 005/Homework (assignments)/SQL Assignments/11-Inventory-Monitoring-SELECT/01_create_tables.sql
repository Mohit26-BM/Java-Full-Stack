-- Inventory Monitoring - create database and tables

CREATE DATABASE inventory_db;
USE inventory_db;

-- categories table
CREATE TABLE categories (
    category_id    INT AUTO_INCREMENT PRIMARY KEY,
    category_name  VARCHAR(50) NOT NULL UNIQUE
);

-- suppliers table
CREATE TABLE suppliers (
    supplier_id    INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name  VARCHAR(100) NOT NULL UNIQUE,
    email          VARCHAR(100) NOT NULL UNIQUE,
    phone          VARCHAR(15)  NOT NULL
);

-- products table
CREATE TABLE products (
    product_id        INT AUTO_INCREMENT PRIMARY KEY,
    sku               VARCHAR(20)  NOT NULL UNIQUE,
    product_name      VARCHAR(100) NOT NULL,
    category_id       INT NOT NULL,
    supplier_id       INT NOT NULL,
    unit_cost         DECIMAL(10,2) NOT NULL,
    unit_price        DECIMAL(10,2) NOT NULL,
    quantity_on_hand  INT NOT NULL CHECK (quantity_on_hand >= 0),
    reorder_level     INT NOT NULL DEFAULT 10,
    status            ENUM('Active', 'Discontinued') NOT NULL DEFAULT 'Active',
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- stock_movements table
CREATE TABLE stock_movements (
    movement_id    INT AUTO_INCREMENT PRIMARY KEY,
    product_id     INT NOT NULL,
    movement_type  ENUM('Sale', 'Restock') NOT NULL,
    quantity       INT NOT NULL,
    movement_date  DATE NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- show tables
SHOW TABLES;
DESCRIBE categories;
DESCRIBE suppliers;
DESCRIBE products;
DESCRIBE stock_movements;
