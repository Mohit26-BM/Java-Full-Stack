-- Sales Analytics - insert sample data

USE sales_db;

-- insert categories
INSERT INTO categories (category_id, category_name) VALUES
(1, 'Electronics'),
(2, 'Furniture'),
(3, 'Stationery'),
(4, 'Appliances');

-- insert products
INSERT INTO products (product_id, sku, product_name, category_id, list_price) VALUES
(1,  'ELE-001', 'Laptop 14 inch',     1, 850.00),
(2,  'ELE-002', 'Wireless Mouse',     1,  25.00),
(3,  'ELE-003', 'Monitor 27 inch',    1, 220.00),
(4,  'ELE-004', 'USB-C Dock',         1, 120.00),
(5,  'FUR-001', 'Office Chair',       2, 180.00),
(6,  'FUR-002', 'Standing Desk',      2, 420.00),
(7,  'FUR-003', 'Bookshelf',          2,  95.00),
(8,  'STA-001', 'A4 Paper Ream',      3,   6.50),
(9,  'STA-002', 'Gel Pen Pack',       3,   4.00),
(10, 'STA-003', 'Notebook Set',       3,  12.00),
(11, 'APP-001', 'Coffee Machine',     4, 150.00),
(12, 'APP-002', 'Desk Fan',           4,  40.00),
(13, 'APP-003', 'Air Purifier',       4, 210.00);

-- insert customers
INSERT INTO customers (customer_id, company_name, contact_email, region, city) VALUES
(1, 'Brightside Consulting', 'orders@brightside.example.com', 'North', 'Springfield'),
(2, 'Coastal Logistics',     'buying@coastal.example.com',    'South', 'Riverside'),
(3, 'Evergreen Schools',     'admin@evergreen.example.com',   'East',  'Fairview'),
(4, 'Summit Health Clinic',  'office@summit.example.com',     'West',  'Greenville'),
(5, 'Pioneer Tech Labs',     'it@pioneer.example.com',        'North', 'Springfield'),
(6, 'Harbor Legal Partners', 'admin@harbor.example.com',      'South', 'Lakeside'),
(7, 'Maple Accounting',      'office@maple.example.com',      'East',  'Fairview'),
(8, 'Lakeview Startup Hub',  'hello@lakeview.example.com',    'West',  'Lakeside');

-- insert orders
INSERT INTO orders (order_id, customer_id, order_date, status) VALUES
(1,  1, '2025-01-08', 'Completed'),
(2,  2, '2025-01-15', 'Completed'),
(3,  3, '2025-01-22', 'Completed'),
(4,  5, '2025-02-03', 'Completed'),
(5,  4, '2025-02-11', 'Completed'),
(6,  6, '2025-02-19', 'Cancelled'),
(7,  1, '2025-03-04', 'Completed'),
(8,  7, '2025-03-12', 'Completed'),
(9,  2, '2025-03-25', 'Completed'),
(10, 3, '2025-04-02', 'Completed'),
(11, 5, '2025-04-14', 'Completed'),
(12, 4, '2025-04-28', 'Cancelled'),
(13, 6, '2025-05-06', 'Completed'),
(14, 1, '2025-05-19', 'Completed'),
(15, 7, '2025-05-27', 'Completed'),
(16, 2, '2025-06-03', 'Completed'),
(17, 5, '2025-06-16', 'Completed'),
(18, 3, '2025-06-24', 'Completed');

-- insert order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1,  1,   5, 850.00),
(1,  2,   5,  25.00),
(1,  3,   5, 220.00),
(2,  5,  10, 180.00),
(2,  8,  40,   6.50),
(3,  10, 100, 12.00),
(3,  9,  60,   4.00),
(3,  8,  80,   6.50),
(4,  1,   8, 820.00),
(4,  4,   8, 120.00),
(5,  11,  2, 150.00),
(5,  12,  6,  40.00),
(6,  6,   4, 420.00),
(7,  6,   3, 420.00),
(7,  5,   3, 180.00),
(8,  8,  30,   6.50),
(8,  10, 20,  12.00),
(9,  3,   4, 220.00),
(9,  2,  10,  25.00),
(10, 7,  12,  95.00),
(10, 9,  50,   4.00),
(11, 1,   4, 850.00),
(11, 3,   4, 220.00),
(11, 4,   4, 120.00),
(12, 11,  3, 150.00),
(13, 5,   6, 180.00),
(13, 6,   2, 420.00),
(14, 2,  15,  25.00),
(14, 12, 10,  40.00),
(15, 11,  1, 150.00),
(15, 8,  25,   6.50),
(16, 5,   8, 175.00),
(16, 7,   4,  95.00),
(17, 1,   6, 830.00),
(17, 2,   6,  25.00),
(18, 10, 80,  12.00),
(18, 9,  40,   4.00);

-- row counts
SELECT COUNT(*) AS category_rows   FROM categories;
SELECT COUNT(*) AS product_rows    FROM products;
SELECT COUNT(*) AS customer_rows   FROM customers;
SELECT COUNT(*) AS order_rows      FROM orders;
SELECT COUNT(*) AS order_item_rows FROM order_items;

-- show all data
SELECT * FROM categories;
SELECT * FROM products;
SELECT * FROM customers;
SELECT * FROM orders;
SELECT * FROM order_items;
