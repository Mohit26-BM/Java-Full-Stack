-- Banking Transactions - inserts and updates for business requirements

USE bank_db;

-- turn off safe update mode for this script
SET SQL_SAFE_UPDATES = 0;

-- balances before changes
SELECT account_id, account_number, account_type, balance, status FROM accounts;

-- requirement 1: new customer opens a savings account with a 5000 deposit
INSERT INTO customers (customer_id, first_name, last_name, email, phone, city, date_joined)
VALUES (11, 'Zainab', 'Qureshi', 'zainab.qureshi@example.com', '555-3011', 'Riverside', '2025-10-01');

INSERT INTO accounts (account_id, account_number, customer_id, account_type, opened_date)
VALUES (15, 'ACC-10015', 11, 'Savings', '2025-10-01');

START TRANSACTION;
UPDATE accounts SET balance = balance + 5000.00 WHERE account_id = 15;
INSERT INTO account_transactions (account_id, txn_type, amount, txn_date)
VALUES (15, 'Deposit', 5000.00, '2025-10-01');
COMMIT;

-- requirement 2: customer moves and changes phone number
UPDATE customers
SET phone = '555-3102', city = 'Springfield'
WHERE customer_id = 2;

-- requirement 3: deposit 2500 into account 3
START TRANSACTION;
UPDATE accounts SET balance = balance + 2500.00 WHERE account_id = 3;
INSERT INTO account_transactions (account_id, txn_type, amount, txn_date)
VALUES (3, 'Deposit', 2500.00, '2025-10-02');
COMMIT;

-- requirement 4: withdraw 1000 from account 6
START TRANSACTION;
UPDATE accounts SET balance = balance - 1000.00 WHERE account_id = 6;
INSERT INTO account_transactions (account_id, txn_type, amount, txn_date)
VALUES (6, 'Withdrawal', 1000.00, '2025-10-03');
COMMIT;

-- requirement 5: transfer 4000 from account 4 to account 5
START TRANSACTION;
UPDATE accounts SET balance = balance - 4000.00 WHERE account_id = 4;
UPDATE accounts SET balance = balance + 4000.00 WHERE account_id = 5;
INSERT INTO account_transactions (account_id, txn_type, amount, txn_date)
VALUES (4, 'Transfer Out', 4000.00, '2025-10-04'),
       (5, 'Transfer In',  4000.00, '2025-10-04');
COMMIT;

-- requirement 6: 2% interest on active savings accounts
UPDATE accounts
SET balance = balance * 1.02
WHERE account_type = 'Savings' AND status = 'Active';

-- requirement 7: cancelled withdrawal is rolled back
START TRANSACTION;
UPDATE accounts SET balance = balance - 500.00 WHERE account_id = 10;
SELECT account_id, balance FROM accounts WHERE account_id = 10;
ROLLBACK;
SELECT account_id, balance FROM accounts WHERE account_id = 10;

-- requirement 8: close an unused zero-balance account
UPDATE accounts
SET status = 'Closed'
WHERE account_id = 13 AND balance = 0;

-- balances after changes
SELECT account_id, account_number, account_type, balance, status FROM accounts;
SELECT * FROM customers WHERE customer_id IN (2, 11);
SELECT * FROM account_transactions WHERE txn_date >= '2025-10-01';

-- turn safe update mode back on
SET SQL_SAFE_UPDATES = 1;
