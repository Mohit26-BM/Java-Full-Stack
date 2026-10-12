-- =========================================================
-- Bank Account Transfer - Sample Data
-- Run after schema.sql
-- 6 accounts: 4 ACTIVE, 1 INACTIVE, 1 BLOCKED
-- =========================================================

USE bank_transfer;

INSERT INTO bank_accounts (account_number, customer_name, balance, status) VALUES
('ACC1001', 'Ravi Kumar',   50000.00, 'ACTIVE'),
('ACC1002', 'Priya Sharma', 25000.00, 'ACTIVE'),
('ACC1003', 'Amit Verma',    1500.00, 'ACTIVE'),
('ACC1004', 'Neha Iyer',    80000.00, 'ACTIVE'),
('ACC1005', 'Karan Patel',  10000.00, 'INACTIVE'),
('ACC1006', 'Sneha Reddy',  15000.00, 'BLOCKED');

-- Opening deposits (no "from" account)
INSERT INTO account_transactions (from_account_id, to_account_id, amount, transaction_type) VALUES
(NULL, 1, 50000.00, 'OPENING_DEPOSIT'),
(NULL, 2, 25000.00, 'OPENING_DEPOSIT'),
(NULL, 3,  1500.00, 'OPENING_DEPOSIT'),
(NULL, 4, 80000.00, 'OPENING_DEPOSIT'),
(NULL, 5, 10000.00, 'OPENING_DEPOSIT'),
(NULL, 6, 15000.00, 'OPENING_DEPOSIT');
