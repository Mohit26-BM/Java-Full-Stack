-- =========================================================
-- Mini Wallet Platform - Schema
-- Run this first, then sample-data.sql.
-- WARNING: drops and recreates all tables in mini_wallet only.
-- Requires MySQL 8.0.16+ (CHECK constraints are enforced).
-- =========================================================

CREATE DATABASE IF NOT EXISTS mini_wallet;

USE mini_wallet;

DROP TABLE IF EXISTS wallet_transactions;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS customers;

-- ---------------------------------------------------------
-- Customers. Email and phone must be unique; the constraint
-- names are used by the app to say WHICH value is duplicated.
-- ---------------------------------------------------------
CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name   VARCHAR(120) NOT NULL,
    email       VARCHAR(120) NOT NULL,
    phone       VARCHAR(10)  NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_customers_email UNIQUE (email),
    CONSTRAINT uk_customers_phone UNIQUE (phone)
);

-- ---------------------------------------------------------
-- Wallets. A customer can own several wallets.
-- wallet_number (WAL0000001 ...) is built from the generated
-- wallet_id, so it is NULL for a moment inside the creating
-- transaction and set before commit.
-- The CHECK is a last safety net: the balance can never be
-- negative even if the application had a bug.
-- ---------------------------------------------------------
CREATE TABLE wallets (
    wallet_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    wallet_number VARCHAR(20) UNIQUE,
    customer_id   BIGINT NOT NULL,
    balance       DECIMAL(15,2) NOT NULL DEFAULT 0,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallets_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CONSTRAINT chk_wallets_balance CHECK (balance >= 0)
);

-- ---------------------------------------------------------
-- Ledger: one row per balance change of one wallet.
-- A transfer writes TWO rows (TRANSFER_OUT on the sender,
-- TRANSFER_IN on the receiver) that share the same reference.
-- balance_after = the wallet balance right after this row.
-- ---------------------------------------------------------
CREATE TABLE wallet_transactions (
    transaction_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    reference              VARCHAR(40) NOT NULL,
    wallet_id              BIGINT NOT NULL,
    transaction_type       VARCHAR(20) NOT NULL,
    amount                 DECIMAL(15,2) NOT NULL,
    balance_after          DECIMAL(15,2) NOT NULL,
    counterparty_wallet_id BIGINT,
    description            VARCHAR(255),
    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_wallet       FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id),
    CONSTRAINT fk_txn_counterparty FOREIGN KEY (counterparty_wallet_id) REFERENCES wallets(wallet_id),
    CONSTRAINT chk_txn_amount      CHECK (amount > 0),
    INDEX idx_txn_wallet_time (wallet_id, created_at),
    INDEX idx_txn_reference (reference)
);
