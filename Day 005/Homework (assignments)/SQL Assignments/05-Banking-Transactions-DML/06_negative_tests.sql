-- Banking Transactions - negative tests (each statement should fail)

USE bank_db;

-- t1 unique: duplicate account number (error 1062)
INSERT INTO accounts (account_number, customer_id, account_type, opened_date)
VALUES ('ACC-10001', 2, 'Current', '2025-10-05');

-- t2 unique: duplicate customer email (error 1062)
INSERT INTO customers (first_name, last_name, email, phone, city, date_joined)
VALUES ('Ayesha', 'Khan', 'ayesha.karim@example.com', '555-3201', 'Fairview', '2025-10-05');

-- t3 not null: missing last name (error 1048)
INSERT INTO customers (first_name, last_name, email, phone, city, date_joined)
VALUES ('Hamza', NULL, 'hamza.n@example.com', '555-3202', 'Fairview', '2025-10-05');

-- t4 foreign key: customer 99 does not exist (error 1452)
INSERT INTO accounts (account_number, customer_id, account_type, opened_date)
VALUES ('ACC-10099', 99, 'Savings', '2025-10-05');

-- t5 check: withdrawal larger than balance (error 3819)
UPDATE accounts SET balance = balance - 1000.00 WHERE account_id = 6;

-- t6 enum: account type not allowed (error 1265)
INSERT INTO accounts (account_number, customer_id, account_type, opened_date)
VALUES ('ACC-10098', 2, 'Fixed', '2025-10-05');

-- t7 delete blocked by foreign key: customer still has accounts (error 1451)
DELETE FROM customers WHERE customer_id = 1;

-- t8 delete blocked by foreign key: closed account still has transaction history (error 1451)
DELETE FROM accounts WHERE account_id = 8;

-- row counts after tests
SELECT COUNT(*) AS customer_rows    FROM customers;
SELECT COUNT(*) AS account_rows     FROM accounts;
SELECT COUNT(*) AS transaction_rows FROM account_transactions;
