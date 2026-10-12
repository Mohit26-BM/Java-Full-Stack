-- =========================================================
-- E-Commerce Order Processing - Sample Data
-- Run after schema.sql
-- 5 customers, 8 products, 2 existing orders
-- (stock_qty values already reflect those 2 orders)
-- =========================================================

USE ecommerce_orders;

INSERT INTO customers (name, email) VALUES
('Ravi Kumar',   'ravi.kumar@example.com'),     -- 1
('Priya Sharma', 'priya.sharma@example.com'),   -- 2
('Amit Verma',   'amit.verma@example.com'),     -- 3
('Neha Iyer',    'neha.iyer@example.com'),      -- 4
('Karan Patel',  'karan.patel@example.com');    -- 5

INSERT INTO products (product_name, unit_price, stock_qty) VALUES
('Wireless Mouse',          799.00, 48),   -- 1
('Mechanical Keyboard',    3499.00, 19),   -- 2
('27-inch Monitor',       14999.00,  5),   -- 3
('USB-C Hub',              1299.00, 30),   -- 4
('Laptop Stand',           1599.00, 12),   -- 5
('Noise Cancelling Headphones', 8999.00, 3),  -- 6
('Webcam 1080p',           2499.00,  0),   -- 7 (out of stock)
('HDMI Cable 2m',           349.00, 99);   -- 8

-- Order 1: Ravi - 2 x Wireless Mouse + 1 x Mechanical Keyboard = 1598 + 3499 = 5097
INSERT INTO orders (customer_id, status, total_amount) VALUES (1, 'PLACED', 5097.00);
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 2,  799.00),
(1, 2, 1, 3499.00);

-- Order 2: Priya - 1 x HDMI Cable = 349
INSERT INTO orders (customer_id, status, total_amount) VALUES (2, 'PLACED', 349.00);
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(2, 8, 1, 349.00);
