-- =========================================================
-- Day 07 - Assignment 12: Bean Validation (@Valid)
-- Shared database Day_007_Assigments; this script only touches a12_ tables.
-- Run once before starting the application:
--   mysql -u root -p < schema.sql
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;
USE Day_007_Assigments;

DROP TABLE IF EXISTS a12_products;
DROP TABLE IF EXISTS a12_categories;

-- Business-rule data: which categories exist and their price ceiling.
-- (Changing these rows changes the rules WITHOUT touching code or annotations.)
CREATE TABLE a12_categories (
    category_code VARCHAR(20)   PRIMARY KEY,
    name          VARCHAR(50)   NOT NULL,
    max_price     DECIMAL(10,2) NOT NULL
);

CREATE TABLE a12_products (
    product_id     INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100)  NOT NULL,
    sku            VARCHAR(9)    NOT NULL,
    category_code  VARCHAR(20)   NOT NULL,
    price          DECIMAL(10,2) NOT NULL,
    discount_price DECIMAL(10,2),
    stock          INT           NOT NULL,
    supplier_email VARCHAR(150)  NOT NULL,
    tags           VARCHAR(200),
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_a12_products_sku UNIQUE (sku),
    CONSTRAINT fk_a12_products_category FOREIGN KEY (category_code)
        REFERENCES a12_categories (category_code)
);

INSERT INTO a12_categories (category_code, name, max_price) VALUES
    ('ELECTRONICS', 'Electronics', 200000.00),
    ('BOOKS',       'Books',         5000.00),
    ('GROCERY',     'Grocery',       2000.00);
