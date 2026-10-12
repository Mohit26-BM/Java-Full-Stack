-- Customer Insights - purchasing behavior using filters, grouping and ordering

USE customer_insights_db;

-- spending per customer

-- q1: purchases, total spend and average purchase value per customer
SELECT c.first_name, c.last_name, c.loyalty_tier,
       COUNT(p.purchase_id)                    AS purchases,
       SUM(p.quantity * p.unit_price)          AS total_spent,
       ROUND(AVG(p.quantity * p.unit_price), 2) AS average_purchase
FROM customers c
JOIN purchases p ON c.customer_id = p.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name, c.loyalty_tier
ORDER BY total_spent DESC;

-- q2: top 3 customers by total spend
SELECT c.first_name, c.last_name, SUM(p.quantity * p.unit_price) AS total_spent
FROM customers c
JOIN purchases p ON c.customer_id = p.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY total_spent DESC
LIMIT 3;

-- purchase frequency

-- q3: repeat customers with 3 or more purchases
SELECT c.first_name, c.last_name, COUNT(p.purchase_id) AS purchases
FROM customers c
JOIN purchases p ON c.customer_id = p.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
HAVING COUNT(p.purchase_id) >= 3
ORDER BY purchases DESC, c.last_name;

-- q4: one-time buyers
SELECT c.first_name, c.last_name, MIN(p.purchase_date) AS only_purchase
FROM customers c
JOIN purchases p ON c.customer_id = p.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
HAVING COUNT(p.purchase_id) = 1
ORDER BY only_purchase;

-- q5: first and last purchase date per customer, most recent first
SELECT c.first_name, c.last_name,
       MIN(p.purchase_date) AS first_purchase,
       MAX(p.purchase_date) AS last_purchase
FROM customers c
JOIN purchases p ON c.customer_id = p.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY last_purchase DESC;

-- q6: customers with no purchase since 2025-04-01
SELECT c.first_name, c.last_name, c.email, MAX(p.purchase_date) AS last_purchase
FROM customers c
JOIN purchases p ON c.customer_id = p.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name, c.email
HAVING MAX(p.purchase_date) < '2025-04-01'
ORDER BY last_purchase;

-- q7: customers who signed up but never purchased
SELECT c.first_name, c.last_name, c.signup_date
FROM customers c
LEFT JOIN purchases p ON c.customer_id = p.customer_id
WHERE p.purchase_id IS NULL;

-- customer segments

-- q8: spending by loyalty tier
SELECT c.loyalty_tier,
       COUNT(DISTINCT c.customer_id)   AS customers,
       COUNT(p.purchase_id)            AS purchases,
       SUM(p.quantity * p.unit_price)  AS total_spent
FROM customers c
JOIN purchases p ON c.customer_id = p.customer_id
GROUP BY c.loyalty_tier
ORDER BY total_spent DESC;

-- q9: spending by city
SELECT c.city,
       COUNT(DISTINCT c.customer_id)  AS customers,
       SUM(p.quantity * p.unit_price) AS total_spent
FROM customers c
JOIN purchases p ON c.customer_id = p.customer_id
GROUP BY c.city
ORDER BY total_spent DESC;

-- shopping preferences

-- q10: purchases and spend by channel
SELECT channel, COUNT(*) AS purchases, SUM(quantity * unit_price) AS total_spent
FROM purchases
GROUP BY channel
ORDER BY total_spent DESC;

-- q11: purchases by payment method
SELECT payment_method, COUNT(*) AS purchases
FROM purchases
GROUP BY payment_method
ORDER BY purchases DESC;

-- q12: number of different customers buying from each category
SELECT cat.category_name,
       COUNT(DISTINCT p.customer_id)   AS customers,
       SUM(p.quantity * p.unit_price)  AS total_spent
FROM categories cat
JOIN products pr  ON cat.category_id = pr.category_id
JOIN purchases p  ON pr.product_id = p.product_id
GROUP BY cat.category_id, cat.category_name
ORDER BY customers DESC, total_spent DESC;

-- q13: online purchases over 100 by Gold customers
SELECT c.first_name, c.last_name, pr.product_name, p.purchase_date,
       p.quantity * p.unit_price AS amount
FROM purchases p
JOIN customers c ON p.customer_id = c.customer_id
JOIN products pr ON p.product_id = pr.product_id
WHERE c.loyalty_tier = 'Gold'
  AND p.channel = 'Online'
  AND p.quantity * p.unit_price > 100
ORDER BY p.purchase_date;

-- q14: active customers per month
SELECT DATE_FORMAT(purchase_date, '%Y-%m') AS purchase_month,
       COUNT(DISTINCT customer_id)         AS active_customers,
       COUNT(*)                            AS purchases
FROM purchases
GROUP BY DATE_FORMAT(purchase_date, '%Y-%m')
ORDER BY purchase_month;
