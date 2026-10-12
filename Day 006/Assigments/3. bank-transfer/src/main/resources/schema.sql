-- =========================================================
-- Bank Account Transfer - Schema
-- Run this first. WARNING: drops and recreates both tables.
-- Tables use InnoDB (MySQL default), which supports
-- transactions and row locking (SELECT ... FOR UPDATE).
-- =========================================================

CREATE DATABASE IF NOT EXISTS bank_transfer;

USE bank_transfer;

DROP TABLE IF EXISTS account_transactions;
DROP TABLE IF EXISTS bank_accounts;

CREATE TABLE bank_accounts (
    account_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    customer_name  VARCHAR(120) NOT NULL,
    balance        DECIMAL(15,2) NOT NULL,
    status         VARCHAR(20) NOT NULL
);

CREATE TABLE account_transactions (
    transaction_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_account_id  BIGINT,
    to_account_id    BIGINT,
    amount           DECIMAL(15,2) NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
