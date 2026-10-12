-- Banking Transactions - delete unused records safely

USE bank_db;

-- turn off safe update mode for this script
SET SQL_SAFE_UPDATES = 0;

-- closed accounts and how many transactions they have
SELECT a.account_id, a.account_number, a.balance, COUNT(t.transaction_id) AS transaction_count
FROM accounts a
LEFT JOIN account_transactions t ON a.account_id = t.account_id
WHERE a.status = 'Closed'
GROUP BY a.account_id, a.account_number, a.balance;

-- delete closed accounts with zero balance and no transactions
DELETE FROM accounts
WHERE status = 'Closed'
  AND balance = 0
  AND account_id NOT IN (SELECT account_id FROM account_transactions);

-- customers with no accounts
SELECT c.customer_id, c.first_name, c.last_name
FROM customers c
LEFT JOIN accounts a ON c.customer_id = a.customer_id
WHERE a.account_id IS NULL;

-- delete customers with no accounts
DELETE FROM customers
WHERE customer_id NOT IN (SELECT customer_id FROM accounts);

-- row counts after delete
SELECT COUNT(*) AS customer_rows    FROM customers;
SELECT COUNT(*) AS account_rows     FROM accounts;
SELECT COUNT(*) AS transaction_rows FROM account_transactions;

-- turn safe update mode back on
SET SQL_SAFE_UPDATES = 1;
