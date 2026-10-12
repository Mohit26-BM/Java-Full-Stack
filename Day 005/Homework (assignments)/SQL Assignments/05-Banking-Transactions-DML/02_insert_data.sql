-- Banking Transactions - insert sample data

USE bank_db;

-- insert customers
INSERT INTO customers (customer_id, first_name, last_name, email, phone, city, date_joined) VALUES
(1,  'Ayesha', 'Karim',   'ayesha.karim@example.com',  '555-3001', 'Springfield', '2018-02-14'),
(2,  'Daniel', 'Brooks',  'daniel.brooks@example.com', '555-3002', 'Riverside',   '2019-05-03'),
(3,  'Fatima', 'Malik',   'fatima.malik@example.com',  '555-3003', 'Springfield', '2017-09-21'),
(4,  'James',  'Carter',  'james.carter@example.com',  '555-3004', 'Fairview',    '2020-01-11'),
(5,  'Bilal',  'Hussain', 'bilal.hussain@example.com', '555-3005', 'Greenville',  '2016-11-30'),
(6,  'Emily',  'Chen',    'emily.chen@example.com',    '555-3006', 'Riverside',   '2021-03-08'),
(7,  'Omar',   'Farooq',  'omar.farooq@example.com',   '555-3007', 'Lakeside',    '2022-07-19'),
(8,  'Grace',  'Lee',     'grace.lee@example.com',     '555-3008', 'Fairview',    '2019-12-02'),
(9,  'Lucas',  'Silva',   'lucas.silva@example.com',   '555-3009', 'Greenville',  '2023-04-25'),
(10, 'Nadia',  'Rahman',  'nadia.rahman@example.com',  '555-3010', 'Lakeside',    '2024-08-30');

-- insert accounts
INSERT INTO accounts (account_id, account_number, customer_id, account_type, balance, status, opened_date) VALUES
(1,  'ACC-10001', 1, 'Savings', 15000.00, 'Active', '2018-02-14'),
(2,  'ACC-10002', 1, 'Current',  3200.00, 'Active', '2019-06-01'),
(3,  'ACC-10003', 2, 'Savings',  8200.00, 'Active', '2019-05-03'),
(4,  'ACC-10004', 3, 'Savings', 42000.00, 'Active', '2017-09-21'),
(5,  'ACC-10005', 3, 'Current',  5600.00, 'Active', '2018-03-15'),
(6,  'ACC-10006', 4, 'Current',  1250.00, 'Active', '2020-01-11'),
(7,  'ACC-10007', 5, 'Savings', 27500.00, 'Active', '2016-11-30'),
(8,  'ACC-10008', 5, 'Current',     0.00, 'Closed', '2017-04-10'),
(9,  'ACC-10009', 6, 'Savings',  9800.00, 'Active', '2021-03-08'),
(10, 'ACC-10010', 7, 'Current',   640.00, 'Active', '2022-07-19'),
(11, 'ACC-10011', 7, 'Savings',     0.00, 'Closed', '2023-01-05'),
(12, 'ACC-10012', 8, 'Savings', 18750.00, 'Active', '2019-12-02'),
(13, 'ACC-10013', 9, 'Savings',     0.00, 'Active', '2023-04-25'),
(14, 'ACC-10014', 9, 'Current',  2300.00, 'Active', '2023-04-25');

-- insert account transactions
INSERT INTO account_transactions (transaction_id, account_id, txn_type, amount, txn_date) VALUES
(1,  1,  'Deposit',     5000.00, '2025-09-01'),
(2,  3,  'Deposit',     1500.00, '2025-09-03'),
(3,  4,  'Withdrawal',  3000.00, '2025-09-04'),
(4,  1,  'Withdrawal',  1200.00, '2025-09-05'),
(5,  5,  'Deposit',     2500.00, '2025-09-06'),
(6,  6,  'Withdrawal',   400.00, '2025-09-07'),
(7,  7,  'Deposit',    10000.00, '2025-09-08'),
(8,  8,  'Withdrawal',   850.00, '2025-03-12'),
(9,  9,  'Deposit',     3000.00, '2025-09-09'),
(10, 10, 'Deposit',      640.00, '2025-09-10'),
(11, 12, 'Withdrawal',  1250.00, '2025-09-11'),
(12, 14, 'Deposit',     2300.00, '2025-09-12'),
(13, 4,  'Deposit',     6000.00, '2025-09-15'),
(14, 7,  'Withdrawal',  2500.00, '2025-09-16'),
(15, 9,  'Withdrawal',   700.00, '2025-09-18');

-- row counts
SELECT COUNT(*) AS customer_rows    FROM customers;
SELECT COUNT(*) AS account_rows     FROM accounts;
SELECT COUNT(*) AS transaction_rows FROM account_transactions;

-- show all data
SELECT * FROM customers;
SELECT * FROM accounts;
SELECT * FROM account_transactions;
