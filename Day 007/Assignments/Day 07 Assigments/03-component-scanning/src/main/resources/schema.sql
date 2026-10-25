-- =========================================================
-- Day 07 - Assignment 03: Component Scanning
-- Shared Day 07 database; every table is prefixed with its
-- assignment number (a03_) so it is uniquely identifiable.
--
-- Run this once. It only drops/recreates a03_customers -
-- tables of other assignments are not touched.
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;

USE Day_007_Assigments;

DROP TABLE IF EXISTS a03_customers;

CREATE TABLE a03_customers (
    customer_id   INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    city          VARCHAR(60)  NOT NULL
);

INSERT INTO a03_customers (customer_name, email, city) VALUES
('Ravi Kumar',   'ravi.kumar@example.com',   'Pune'),
('Priya Sharma', 'priya.sharma@example.com', 'Mumbai'),
('Amit Verma',   'amit.verma@example.com',   'Pune'),
('Neha Iyer',    'neha.iyer@example.com',    'Bengaluru'),
('Karan Patel',  'karan.patel@example.com',  'Pune');
