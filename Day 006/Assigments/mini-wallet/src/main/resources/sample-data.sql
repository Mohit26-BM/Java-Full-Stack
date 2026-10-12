-- =========================================================
-- Mini Wallet Platform - Sample Data
-- Run after schema.sql.
--
-- 5 customers, 6 wallets (WAL0000005 is BLOCKED),
-- 16 ledger rows across Aug - Oct 2026.
-- Every wallet balance equals the sum of its ledger rows
-- (menu "Reconciliation" checks this).
-- =========================================================

USE mini_wallet;

INSERT INTO customers (customer_id, full_name, email, phone) VALUES
(1, 'Ravi Kumar',   'ravi.kumar@example.com',   '9810000001'),
(2, 'Priya Sharma', 'priya.sharma@example.com', '9810000002'),
(3, 'Amit Verma',   'amit.verma@example.com',   '9810000003'),
(4, 'Neha Iyer',    'neha.iyer@example.com',    '9810000004'),
(5, 'Karan Patel',  'karan.patel@example.com',  '9810000005');

-- Final balances after the ledger below
INSERT INTO wallets (wallet_id, wallet_number, customer_id, balance, status) VALUES
(1, 'WAL0000001', 1, 13500.00, 'ACTIVE'),
(2, 'WAL0000002', 2, 13500.00, 'ACTIVE'),
(3, 'WAL0000003', 3,  9000.00, 'ACTIVE'),
(4, 'WAL0000004', 4, 14000.00, 'ACTIVE'),
(5, 'WAL0000005', 5,  5000.00, 'BLOCKED'),
(6, 'WAL0000006', 1, 11000.00, 'ACTIVE');   -- Ravi's second wallet

INSERT INTO wallet_transactions
(reference, wallet_id, transaction_type, amount, balance_after, counterparty_wallet_id, description, created_at)
VALUES
-- August 2026
('TXN-SAMPLE-0001', 1, 'DEPOSIT',       20000.00, 20000.00, NULL, 'Opening deposit',        '2026-08-01 10:00:00'),
('TXN-SAMPLE-0002', 2, 'DEPOSIT',       15000.00, 15000.00, NULL, 'Opening deposit',        '2026-08-01 11:30:00'),
('TXN-SAMPLE-0003', 3, 'DEPOSIT',        8000.00,  8000.00, NULL, 'Opening deposit',        '2026-08-02 09:15:00'),
('TXN-SAMPLE-0004', 4, 'DEPOSIT',       12000.00, 12000.00, NULL, 'Opening deposit',        '2026-08-03 14:00:00'),
('TXN-SAMPLE-0005', 5, 'DEPOSIT',        5000.00,  5000.00, NULL, 'Opening deposit',        '2026-08-03 16:45:00'),
('TXN-SAMPLE-0006', 1, 'TRANSFER_OUT',   2500.00, 17500.00, 2,    'Rent share',             '2026-08-10 19:00:00'),
('TXN-SAMPLE-0006', 2, 'TRANSFER_IN',    2500.00, 17500.00, 1,    'Rent share',             '2026-08-10 19:00:00'),
('TXN-SAMPLE-0008', 3, 'WITHDRAWAL',     1500.00,  6500.00, NULL, 'ATM withdrawal',         '2026-08-15 12:20:00'),
-- September 2026
('TXN-SAMPLE-0009', 1, 'WITHDRAWAL',     3000.00, 14500.00, NULL, 'ATM withdrawal',         '2026-09-05 18:10:00'),
('TXN-SAMPLE-0010', 2, 'TRANSFER_OUT',   4000.00, 13500.00, 4,    'Trip expenses',          '2026-09-12 21:00:00'),
('TXN-SAMPLE-0010', 4, 'TRANSFER_IN',    4000.00, 16000.00, 2,    'Trip expenses',          '2026-09-12 21:00:00'),
('TXN-SAMPLE-0012', 6, 'DEPOSIT',       10000.00, 10000.00, NULL, 'Opening deposit',        '2026-09-20 10:05:00'),
('TXN-SAMPLE-0013', 3, 'DEPOSIT',        2500.00,  9000.00, NULL, 'Salary advance',         '2026-09-25 09:00:00'),
-- October 2026
('TXN-SAMPLE-0014', 4, 'WITHDRAWAL',     2000.00, 14000.00, NULL, 'Bill payment',           '2026-10-02 13:30:00'),
('TXN-SAMPLE-0015', 1, 'TRANSFER_OUT',   1000.00, 13500.00, 6,    'Move to savings wallet', '2026-10-05 08:00:00'),
('TXN-SAMPLE-0015', 6, 'TRANSFER_IN',    1000.00, 11000.00, 1,    'Move to savings wallet', '2026-10-05 08:00:00');
