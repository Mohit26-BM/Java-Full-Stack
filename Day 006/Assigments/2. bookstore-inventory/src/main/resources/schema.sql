-- =========================================================
-- Online Bookstore Inventory - Schema
-- Run this first. WARNING: drops and recreates all tables.
-- =========================================================

CREATE DATABASE IF NOT EXISTS bookstore_inventory;

USE bookstore_inventory;

DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS authors;

CREATE TABLE authors (
    author_id   INT PRIMARY KEY AUTO_INCREMENT,
    author_name VARCHAR(120) NOT NULL
);

CREATE TABLE categories (
    category_id   INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(80) UNIQUE NOT NULL
);

CREATE TABLE books (
    book_id     INT PRIMARY KEY AUTO_INCREMENT,
    isbn        VARCHAR(20) UNIQUE NOT NULL,
    title       VARCHAR(200) NOT NULL,
    author_id   INT NOT NULL,
    category_id INT NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    stock_qty   INT NOT NULL,
    FOREIGN KEY (author_id)   REFERENCES authors(author_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
