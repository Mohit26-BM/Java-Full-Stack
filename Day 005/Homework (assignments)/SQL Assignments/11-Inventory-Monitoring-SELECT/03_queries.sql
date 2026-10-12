-- Inventory Monitoring - low stock, high-value inventory and inactive items (today is 2025-10-06)

USE inventory_db;

-- inventory overview

-- q1: full stock list with category, supplier and stock value
SELECT p.sku, p.product_name, c.category_name, s.supplier_name,
       p.quantity_on_hand, p.unit_cost,
       p.quantity_on_hand * p.unit_cost AS stock_value,
       p.status
FROM products p
JOIN categories c ON p.category_id = c.category_id
JOIN suppliers s  ON p.supplier_id = s.supplier_id
ORDER BY c.category_name, p.product_name;

-- low-stock products

-- q2: active products at or below their reorder level
SELECT p.sku, p.product_name, p.quantity_on_hand, p.reorder_level,
       p.reorder_level - p.quantity_on_hand AS shortfall
FROM products p
WHERE p.status = 'Active'
  AND p.quantity_on_hand <= p.reorder_level
ORDER BY shortfall DESC;

-- q3: active products that are out of stock
SELECT p.sku, p.product_name, s.supplier_name, s.phone
FROM products p
JOIN suppliers s ON p.supplier_id = s.supplier_id
WHERE p.status = 'Active'
  AND p.quantity_on_hand = 0;

-- q4: number of low-stock products per supplier
SELECT s.supplier_name, s.email, COUNT(p.product_id) AS low_stock_items
FROM suppliers s
JOIN products p ON s.supplier_id = p.supplier_id
WHERE p.status = 'Active'
  AND p.quantity_on_hand <= p.reorder_level
GROUP BY s.supplier_id, s.supplier_name, s.email
ORDER BY low_stock_items DESC, s.supplier_name;

-- high-value inventory

-- q5: total inventory value at cost and at selling price
SELECT SUM(quantity_on_hand)              AS total_units,
       SUM(quantity_on_hand * unit_cost)  AS value_at_cost,
       SUM(quantity_on_hand * unit_price) AS value_at_price
FROM products;

-- q6: inventory value by category
SELECT c.category_name,
       COUNT(p.product_id)                AS products,
       SUM(p.quantity_on_hand * p.unit_cost) AS stock_value
FROM categories c
JOIN products p ON c.category_id = p.category_id
GROUP BY c.category_id, c.category_name
ORDER BY stock_value DESC;

-- q7: top 5 products by stock value
SELECT sku, product_name, quantity_on_hand, unit_cost,
       quantity_on_hand * unit_cost AS stock_value
FROM products
ORDER BY stock_value DESC
LIMIT 5;

-- q8: products holding more than 3000 in stock value
SELECT sku, product_name, quantity_on_hand * unit_cost AS stock_value
FROM products
WHERE quantity_on_hand * unit_cost > 3000
ORDER BY stock_value DESC;

-- inactive items

-- q9: products with no stock movement in the last 90 days (since 2025-07-08)
SELECT p.sku, p.product_name, p.status, MAX(m.movement_date) AS last_movement
FROM products p
JOIN stock_movements m ON p.product_id = m.product_id
GROUP BY p.product_id, p.sku, p.product_name, p.status
HAVING MAX(m.movement_date) < '2025-07-08'
ORDER BY last_movement;

-- q10: products with no stock movement recorded at all
SELECT p.sku, p.product_name, p.status, p.quantity_on_hand
FROM products p
LEFT JOIN stock_movements m ON p.product_id = m.product_id
WHERE m.movement_id IS NULL;

-- q11: discontinued products still holding stock
SELECT sku, product_name, quantity_on_hand,
       quantity_on_hand * unit_cost AS dead_stock_value
FROM products
WHERE status = 'Discontinued'
  AND quantity_on_hand > 0
ORDER BY dead_stock_value DESC;

-- q12: units sold per product in the last 90 days
SELECT p.sku, p.product_name, SUM(m.quantity) AS units_sold
FROM products p
JOIN stock_movements m ON p.product_id = m.product_id
WHERE m.movement_type = 'Sale'
  AND m.movement_date >= '2025-07-08'
GROUP BY p.product_id, p.sku, p.product_name
ORDER BY units_sold DESC, p.product_name;
