-- Banking Transactions - select queries

USE bank_db;

-- q1: customers with their accounts and balances
SELECT c.first_name, c.last_name, a.account_number, a.account_type, a.balance, a.status
FROM customers c
JOIN accounts a ON c.customer_id = a.customer_id
ORDER BY c.last_name, a.account_number;

-- q2: number of accounts and total balance per customer
SELECT c.first_name, c.last_name,
       COUNT(a.account_id) AS account_count,
       SUM(a.balance)      AS total_balance
FROM customers c
JOIN accounts a ON c.customer_id = a.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY total_balance DESC;

-- q3: total and average balance by account type for active accounts
SELECT account_type,
       COUNT(*)                AS account_count,
       SUM(balance)            AS total_balance,
       ROUND(AVG(balance), 2)  AS average_balance
FROM accounts
WHERE status = 'Active'
GROUP BY account_type;

-- q4: active accounts with a balance under 1000
SELECT a.account_number, c.first_name, c.last_name, a.balance
FROM accounts a
JOIN customers c ON a.customer_id = c.customer_id
WHERE a.status = 'Active'
  AND a.balance < 1000
ORDER BY a.balance;

-- q5: number and total amount of each transaction type
SELECT txn_type, COUNT(*) AS transaction_count, SUM(amount) AS total_amount
FROM account_transactions
GROUP BY txn_type
ORDER BY txn_type;

-- q6: statement for account ACC-10004
SELECT a.account_number, t.txn_date, t.txn_type, t.amount
FROM account_transactions t
JOIN accounts a ON t.account_id = a.account_id
WHERE a.account_number = 'ACC-10004'
ORDER BY t.txn_date;

-- q7: active accounts with no transactions
SELECT a.account_number, a.account_type, a.balance
FROM accounts a
LEFT JOIN account_transactions t ON a.account_id = t.account_id
WHERE a.status = 'Active'
  AND t.transaction_id IS NULL;

-- q8: number of customers and total balance per city
SELECT c.city,
       COUNT(DISTINCT c.customer_id) AS customer_count,
       SUM(a.balance)                AS total_balance
FROM customers c
JOIN accounts a ON c.customer_id = a.customer_id
GROUP BY c.city
ORDER BY total_balance DESC;
