-- Advanced Reporting - nested queries and aggregate calculations

USE advanced_report_db;

-- subquery returning one value

-- q1: products priced above the average product price
SELECT product_name, unit_price
FROM products
WHERE unit_price > (SELECT AVG(unit_price) FROM products)
ORDER BY unit_price DESC;

-- q2: second most expensive product
SELECT product_name, unit_price
FROM products
WHERE unit_price = (SELECT MAX(unit_price)
                    FROM products
                    WHERE unit_price < (SELECT MAX(unit_price) FROM products));

-- q3: each customer's share of total revenue
SELECT c.customer_name,
       SUM(oi.quantity * p.unit_price) AS total_spent,
       ROUND(SUM(oi.quantity * p.unit_price) * 100 /
             (SELECT SUM(oi2.quantity * p2.unit_price)
              FROM order_items oi2
              JOIN products p2 ON oi2.product_id = p2.product_id), 2) AS percent_of_revenue
FROM customers c
JOIN orders o       ON c.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p     ON oi.product_id = p.product_id
GROUP BY c.customer_id, c.customer_name
ORDER BY total_spent DESC;

-- subquery returning a list

-- q4: products that have never been ordered
SELECT product_name, unit_price
FROM products
WHERE product_id NOT IN (SELECT product_id FROM order_items);

-- q5: customers who bought at least one Electronics product
SELECT customer_name, city
FROM customers
WHERE customer_id IN (SELECT o.customer_id
                      FROM orders o
                      JOIN order_items oi ON o.order_id = oi.order_id
                      WHERE oi.product_id IN (SELECT product_id
                                              FROM products
                                              WHERE category_id = (SELECT category_id
                                                                   FROM categories
                                                                   WHERE category_name = 'Electronics')))
ORDER BY customer_name;

-- q6: customers who placed at least one order worth more than 1000
SELECT customer_name, city
FROM customers
WHERE customer_id IN (SELECT o.customer_id
                      FROM orders o
                      JOIN order_items oi ON o.order_id = oi.order_id
                      JOIN products p     ON oi.product_id = p.product_id
                      GROUP BY o.order_id, o.customer_id
                      HAVING SUM(oi.quantity * p.unit_price) > 1000)
ORDER BY customer_name;

-- subquery used as a table (derived table)

-- q7: orders worth more than the average order value
SELECT ot.order_id, ot.order_total
FROM (SELECT oi.order_id, SUM(oi.quantity * p.unit_price) AS order_total
      FROM order_items oi
      JOIN products p ON oi.product_id = p.product_id
      GROUP BY oi.order_id) AS ot
WHERE ot.order_total > (SELECT AVG(t.order_total)
                        FROM (SELECT oi.order_id, SUM(oi.quantity * p.unit_price) AS order_total
                              FROM order_items oi
                              JOIN products p ON oi.product_id = p.product_id
                              GROUP BY oi.order_id) AS t)
ORDER BY ot.order_total DESC;

-- q8: customers who spent more than the average customer
SELECT c.customer_name, SUM(oi.quantity * p.unit_price) AS total_spent
FROM customers c
JOIN orders o       ON c.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p     ON oi.product_id = p.product_id
GROUP BY c.customer_id, c.customer_name
HAVING SUM(oi.quantity * p.unit_price) > (SELECT AVG(cs.spent)
                                          FROM (SELECT o2.customer_id, SUM(oi2.quantity * p2.unit_price) AS spent
                                                FROM orders o2
                                                JOIN order_items oi2 ON o2.order_id = oi2.order_id
                                                JOIN products p2     ON oi2.product_id = p2.product_id
                                                GROUP BY o2.customer_id) AS cs)
ORDER BY total_spent DESC;

-- q9: best-selling product by units (ties included)
SELECT p.product_name, SUM(oi.quantity) AS units_sold
FROM products p
JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id, p.product_name
HAVING SUM(oi.quantity) = (SELECT MAX(u.units)
                           FROM (SELECT product_id, SUM(quantity) AS units
                                 FROM order_items
                                 GROUP BY product_id) AS u);

-- q10: categories with revenue above the average category revenue
SELECT cat.category_name, SUM(oi.quantity * p.unit_price) AS revenue
FROM categories cat
JOIN products p     ON cat.category_id = p.category_id
JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY cat.category_id, cat.category_name
HAVING SUM(oi.quantity * p.unit_price) > (SELECT AVG(r.revenue)
                                          FROM (SELECT p2.category_id, SUM(oi2.quantity * p2.unit_price) AS revenue
                                                FROM order_items oi2
                                                JOIN products p2 ON oi2.product_id = p2.product_id
                                                GROUP BY p2.category_id) AS r)
ORDER BY revenue DESC;

-- q11: products that sold more units than the average product
SELECT p.product_name, SUM(oi.quantity) AS units_sold
FROM products p
JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id, p.product_name
HAVING SUM(oi.quantity) > (SELECT AVG(u.units)
                           FROM (SELECT product_id, SUM(quantity) AS units
                                 FROM order_items
                                 GROUP BY product_id) AS u)
ORDER BY units_sold DESC;

-- correlated subqueries

-- q12: most expensive product in each category
SELECT cat.category_name, p.product_name, p.unit_price
FROM products p
JOIN categories cat ON p.category_id = cat.category_id
WHERE p.unit_price = (SELECT MAX(p2.unit_price)
                      FROM products p2
                      WHERE p2.category_id = p.category_id)
ORDER BY cat.category_name;

-- q13: customers who have never placed an order
SELECT c.customer_name, c.city
FROM customers c
WHERE NOT EXISTS (SELECT 1
                  FROM orders o
                  WHERE o.customer_id = c.customer_id);
