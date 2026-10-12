-- Banking Transactions - create database and tables

CREATE DATABASE bank_db;
USE bank_db;

-- customers table
CREATE TABLE customers (
    customer_id  INT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    phone        VARCHAR(15)  NOT NULL UNIQUE,
    city         VARCHAR(50)  NOT NULL,
    date_joined  DATE NOT NULL
);

-- accounts table
CREATE TABLE accounts (
    account_id      INT AUTO_INCREMENT PRIMARY KEY,
    account_number  VARCHAR(20) NOT NULL UNIQUE,
    customer_id     INT NOT NULL,
    account_type    ENUM('Savings', 'Current') NOT NULL,
    balance         DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    status          ENUM('Active', 'Closed') NOT NULL DEFAULT 'Active',
    opened_date     DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- account_transactions table
CREATE TABLE account_transactions (
    transaction_id  INT AUTO_INCREMENT PRIMARY KEY,
    account_id      INT NOT NULL,
    txn_type        ENUM('Deposit', 'Withdrawal', 'Transfer In', 'Transfer Out') NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,
    txn_date        DATE NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

-- show tables
SHOW TABLES;
DESCRIBE customers;
DESCRIBE accounts;
DESCRIBE account_transactions;
