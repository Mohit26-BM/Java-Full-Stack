-- =========================================================
-- Day 07 - Assignment 13: Global Exception Handling
-- Shared database Day_007_Assigments; this script only touches a13_ tables.
-- Run once before starting the application:
--   mysql -u root -p < schema.sql
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;
USE Day_007_Assigments;

DROP TABLE IF EXISTS a13_accounts;

CREATE TABLE a13_accounts (
    account_id  INT AUTO_INCREMENT PRIMARY KEY,
    holder_name VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL,
    balance     DECIMAL(12,2) NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_a13_accounts_email UNIQUE (email),
    CONSTRAINT chk_a13_accounts_balance CHECK (balance >= 0)
);

INSERT INTO a13_accounts (holder_name, email, balance) VALUES
    ('Asha Rao',     'asha@example.com',  10000.00),
    ('Ravi Kumar',   'ravi@example.com',   2500.00),
    ('Meera Iyer',   'meera@example.com',     0.00);
