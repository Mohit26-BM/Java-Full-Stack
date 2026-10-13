-- =========================================================
-- Day 07 - Assignment 17: Transactions (@Transactional)
-- Shared database Day_007_Assigments; this script only touches a17_ tables.
-- Run once before starting the application:
--   mysql -u root -p < schema.sql
-- (InnoDB = MySQL's default engine, which supports transactions.)
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;
USE Day_007_Assigments;

DROP TABLE IF EXISTS a17_accounts;

CREATE TABLE a17_accounts (
    account_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    holder_name VARCHAR(100)  NOT NULL,
    balance     DECIMAL(12,2) NOT NULL
) ENGINE = InnoDB;

-- total = 15000.00 ; a correct transfer never changes the total
INSERT INTO a17_accounts (holder_name, balance) VALUES
    ('Asha Rao',   10000.00),
    ('Ravi Kumar',  5000.00);
