-- Sales Analytics - sales summaries, top products and management reports

USE sales_db;

-- sales summaries

-- q1: overall sales summary for completed orders
SELECT COUNT(DISTINCT o.order_id)          AS total_orders,
       SUM(oi.quantity)                    AS units_sold,
       SUM(oi.quantity * oi.unit_price)    AS total_revenue,
       ROUND(SUM(oi.quantity * oi.unit_price) / COUNT(DISTINCT o.order_id), 2) AS average_order_value
FROM orders o
JOIN order_items oi ON o.order_id = oi.order_id
WHERE o.status = 'Completed';

-- q2: monthly sales summary
SELECT DATE_FORMAT(o.order_date, '%Y-%m')  AS sales_month,
       COUNT(DISTINCT o.order_id)          AS orders,
       SUM(oi.quantity * oi.unit_price)    AS revenue
FROM orders o
JOIN order_items oi ON o.order_id = oi.order_id
WHERE o.status = 'Completed'
GROUP BY DATE_FORMAT(o.order_date, '%Y-%m')
ORDER BY sales_month;

-- q3: revenue and units by category
SELECT c.category_name,
       SUM(oi.quantity)                 AS units_sold,
       SUM(oi.quantity * oi.unit_price) AS revenue
FROM categories c
JOIN products p     ON c.category_id = p.category_id
JOIN order_items oi ON p.product_id = oi.product_id
JOIN orders o       ON oi.order_id = o.order_id
WHERE o.status = 'Completed'
GROUP BY c.category_id, c.category_name
ORDER BY revenue DESC;

-- top-performing products

-- q4: top 5 products by revenue
SELECT p.product_name,
       SUM(oi.quantity)                 AS units_sold,
       SUM(oi.quantity * oi.unit_price) AS revenue
FROM products p
JOIN order_items oi ON p.product_id = oi.product_id
JOIN orders o       ON oi.order_id = o.order_id
WHERE o.status = 'Completed'
GROUP BY p.product_id, p.product_name
ORDER BY revenue DESC
LIMIT 5;

-- q5: top 3 products by units sold
SELECT p.product_name, SUM(oi.quantity) AS units_sold
FROM products p
JOIN order_items oi ON p.product_id = oi.product_id
JOIN orders o       ON oi.order_id = o.order_id
WHERE o.status = 'Completed'
GROUP BY p.product_id, p.product_name
ORDER BY units_sold DESC
LIMIT 3;

-- q6: average selling price compared with list price
SELECT p.product_name,
       p.list_price,
       ROUND(AVG(oi.unit_price), 2) AS average_selling_price,
       MIN(oi.unit_price)           AS lowest_price,
       MAX(oi.unit_price)           AS highest_price
FROM products p
JOIN order_items oi ON p.product_id = oi.product_id
JOIN orders o       ON oi.order_id = o.order_id
WHERE o.status = 'Completed'
GROUP BY p.product_id, p.product_name, p.list_price
HAVING MIN(oi.unit_price) < p.list_price;

-- q7: products never ordered
SELECT p.sku, p.product_name, p.list_price
FROM products p
LEFT JOIN order_items oi ON p.product_id = oi.product_id
WHERE oi.order_id IS NULL;

-- management reports

-- q8: revenue by region
SELECT cu.region,
       COUNT(DISTINCT o.order_id)       AS orders,
       SUM(oi.quantity * oi.unit_price) AS revenue
FROM customers cu
JOIN orders o       ON cu.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
WHERE o.status = 'Completed'
GROUP BY cu.region
ORDER BY revenue DESC;

-- q9: customers who spent more than 5000
SELECT cu.company_name,
       COUNT(DISTINCT o.order_id)       AS orders,
       SUM(oi.quantity * oi.unit_price) AS total_spent
FROM customers cu
JOIN orders o       ON cu.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
WHERE o.status = 'Completed'
GROUP BY cu.customer_id, cu.company_name
HAVING SUM(oi.quantity * oi.unit_price) > 5000
ORDER BY total_spent DESC;

-- q10: completed orders worth more than 2000
SELECT o.order_id, cu.company_name, o.order_date,
       SUM(oi.quantity * oi.unit_price) AS order_total
FROM orders o
JOIN customers cu   ON o.customer_id = cu.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
WHERE o.status = 'Completed'
GROUP BY o.order_id, cu.company_name, o.order_date
HAVING SUM(oi.quantity * oi.unit_price) > 2000
ORDER BY order_total DESC;

-- q11: number and value of cancelled orders
SELECT COUNT(DISTINCT o.order_id)       AS cancelled_orders,
       SUM(oi.quantity * oi.unit_price) AS lost_revenue
FROM orders o
JOIN order_items oi ON o.order_id = oi.order_id
WHERE o.status = 'Cancelled';

-- q12: customers with no orders
SELECT cu.company_name, cu.region
FROM customers cu
LEFT JOIN orders o ON cu.customer_id = o.customer_id
WHERE o.order_id IS NULL;
