-- Customer Insights - insert sample data

USE customer_insights_db;

-- insert customers
INSERT INTO customers (customer_id, first_name, last_name, email, city, signup_date, loyalty_tier) VALUES
(1,  'Ayesha', 'Karim',   'ayesha.karim@example.com',  'Springfield', '2023-02-10', 'Gold'),
(2,  'Daniel', 'Brooks',  'daniel.brooks@example.com', 'Riverside',   '2023-06-21', 'Silver'),
(3,  'Fatima', 'Malik',   'fatima.malik@example.com',  'Springfield', '2022-11-05', 'Gold'),
(4,  'James',  'Carter',  'james.carter@example.com',  'Fairview',    '2024-01-15', 'Standard'),
(5,  'Bilal',  'Hussain', 'bilal.hussain@example.com', 'Greenville',  '2024-03-30', 'Standard'),
(6,  'Emily',  'Chen',    'emily.chen@example.com',    'Riverside',   '2022-08-12', 'Silver'),
(7,  'Omar',   'Farooq',  'omar.farooq@example.com',   'Lakeside',    '2024-07-04', 'Standard'),
(8,  'Grace',  'Lee',     'grace.lee@example.com',     'Fairview',    '2023-09-18', 'Gold'),
(9,  'Lucas',  'Silva',   'lucas.silva@example.com',   'Greenville',  '2024-10-22', 'Standard'),
(10, 'Nadia',  'Rahman',  'nadia.rahman@example.com',  'Lakeside',    '2025-01-09', 'Standard'),
(11, 'Sophie', 'Clark',   'sophie.clark@example.com',  'Springfield', '2024-12-01', 'Silver'),
(12, 'Hamza',  'Iqbal',   'hamza.iqbal@example.com',   'Riverside',   '2025-05-28', 'Standard');

-- insert categories
INSERT INTO categories (category_id, category_name) VALUES
(1, 'Clothing'),
(2, 'Electronics'),
(3, 'Home'),
(4, 'Beauty');

-- insert products
INSERT INTO products (product_id, product_name, category_id, list_price) VALUES
(1,  'Denim Jacket',       1,  79.00),
(2,  'Running Shoes',      1,  95.00),
(3,  'Cotton T-Shirt',     1,  18.00),
(4,  'Wireless Earbuds',   2, 120.00),
(5,  'Smart Watch',        2, 199.00),
(6,  'Phone Charger',      2,  22.00),
(7,  'Scented Candle Set', 3,  30.00),
(8,  'Cookware Set',       3, 145.00),
(9,  'Face Moisturizer',   4,  26.00),
(10, 'Perfume 50ml',       4,  68.00);

-- insert purchases
INSERT INTO purchases (purchase_id, customer_id, product_id, quantity, unit_price, purchase_date, channel, payment_method) VALUES
(1,  1,  5,  1, 199.00, '2025-01-05', 'Online',   'Card'),
(2,  1,  9,  2,  26.00, '2025-01-20', 'In-Store', 'Card'),
(3,  1,  1,  1,  79.00, '2025-02-14', 'Online',   'Wallet'),
(4,  1,  10, 1,  68.00, '2025-03-22', 'Online',   'Card'),
(5,  1,  8,  1, 145.00, '2025-05-09', 'In-Store', 'Card'),
(6,  1,  3,  3,  18.00, '2025-06-18', 'Online',   'Wallet'),
(7,  2,  4,  1, 120.00, '2025-01-11', 'Online',   'Card'),
(8,  2,  6,  2,  22.00, '2025-03-03', 'Online',   'Wallet'),
(9,  2,  2,  1,  95.00, '2025-04-27', 'In-Store', 'Cash'),
(10, 3,  8,  1, 145.00, '2025-01-08', 'In-Store', 'Card'),
(11, 3,  5,  1, 189.00, '2025-02-02', 'Online',   'Card'),
(12, 3,  7,  2,  30.00, '2025-02-25', 'In-Store', 'Cash'),
(13, 3,  10, 2,  68.00, '2025-04-12', 'Online',   'Card'),
(14, 3,  2,  1,  95.00, '2025-06-07', 'Online',   'Card'),
(15, 4,  3,  4,  18.00, '2025-01-26', 'In-Store', 'Cash'),
(16, 4,  6,  1,  22.00, '2025-02-09', 'In-Store', 'Cash'),
(17, 5,  2,  1,  95.00, '2025-03-15', 'Online',   'Wallet'),
(18, 6,  9,  3,  26.00, '2025-01-14', 'Online',   'Card'),
(19, 6,  7,  1,  30.00, '2025-03-09', 'Online',   'Card'),
(20, 6,  10, 1,  68.00, '2025-05-21', 'Online',   'Wallet'),
(21, 6,  1,  1,  79.00, '2025-06-11', 'In-Store', 'Card'),
(22, 7,  4,  1, 120.00, '2025-02-17', 'Online',   'Card'),
(23, 7,  3,  2,  18.00, '2025-05-30', 'In-Store', 'Cash'),
(24, 8,  5,  1, 199.00, '2025-01-30', 'Online',   'Card'),
(25, 8,  8,  1, 145.00, '2025-03-28', 'Online',   'Card'),
(26, 8,  4,  1, 115.00, '2025-04-19', 'Online',   'Wallet'),
(27, 8,  9,  2,  26.00, '2025-06-02', 'In-Store', 'Card'),
(28, 9,  6,  3,  22.00, '2025-01-18', 'In-Store', 'Cash'),
(29, 10, 1,  1,  79.00, '2025-02-22', 'Online',   'Card'),
(30, 10, 3,  2,  18.00, '2025-04-05', 'Online',   'Wallet'),
(31, 11, 7,  1,  30.00, '2025-06-14', 'In-Store', 'Card'),
(32, 2,  9,  1,  26.00, '2025-06-25', 'Online',   'Card');

-- row counts
SELECT COUNT(*) AS customer_rows FROM customers;
SELECT COUNT(*) AS category_rows FROM categories;
SELECT COUNT(*) AS product_rows  FROM products;
SELECT COUNT(*) AS purchase_rows FROM purchases;

-- show all data
SELECT * FROM customers;
SELECT * FROM categories;
SELECT * FROM products;
SELECT * FROM purchases;
