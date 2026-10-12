-- Retail Store - select queries

USE retail_db;

-- q1: products with their category and supplier
SELECT p.sku, p.product_name, c.category_name, s.supplier_name, p.unit_price
FROM products p
JOIN categories c ON p.category_id = c.category_id
JOIN suppliers s  ON p.supplier_id = s.supplier_id
ORDER BY c.category_name, p.product_name;

-- q2: number of products in each category
SELECT c.category_name, COUNT(p.product_id) AS product_count
FROM categories c
LEFT JOIN products p ON c.category_id = p.category_id
GROUP BY c.category_id, c.category_name
ORDER BY product_count DESC, c.category_name;

-- q3: average, lowest and highest price per category
SELECT c.category_name,
       ROUND(AVG(p.unit_price), 2) AS average_price,
       MIN(p.unit_price)           AS lowest_price,
       MAX(p.unit_price)           AS highest_price
FROM categories c
JOIN products p ON c.category_id = p.category_id
GROUP BY c.category_id, c.category_name
ORDER BY average_price DESC;

-- q4: total stock value per supplier
SELECT s.supplier_name,
       SUM(p.stock_quantity)                AS total_units,
       SUM(p.unit_price * p.stock_quantity) AS stock_value
FROM suppliers s
JOIN products p ON s.supplier_id = p.supplier_id
GROUP BY s.supplier_id, s.supplier_name
ORDER BY stock_value DESC;

-- q5: in-stock products priced under 3
SELECT sku, product_name, unit_price, stock_quantity
FROM products
WHERE unit_price < 3
  AND stock_quantity > 0
ORDER BY unit_price;

-- q6: out-of-stock products with supplier contact
SELECT p.product_name, s.supplier_name, s.contact_name, s.phone
FROM products p
JOIN suppliers s ON p.supplier_id = s.supplier_id
WHERE p.stock_quantity = 0;

-- q7: suppliers with no products
SELECT s.supplier_id, s.supplier_name, s.city
FROM suppliers s
LEFT JOIN products p ON s.supplier_id = p.supplier_id
WHERE p.product_id IS NULL;

-- q8: categories with no description
SELECT category_id, category_name
FROM categories
WHERE description IS NULL;
