-- Advanced Reporting - insert sample data

USE advanced_report_db;

-- insert categories
INSERT INTO categories (category_id, category_name) VALUES
(1, 'Electronics'),
(2, 'Furniture'),
(3, 'Stationery'),
(4, 'Appliances');

-- insert products
INSERT INTO products (product_id, product_name, category_id, unit_price) VALUES
(1,  'Laptop',         1, 850.00),
(2,  'Monitor',        1, 220.00),
(3,  'Wireless Mouse', 1,  25.00),
(4,  'Headphones',     1, 120.00),
(5,  'Office Chair',   2, 180.00),
(6,  'Standing Desk',  2, 420.00),
(7,  'Bookshelf',      2,  95.00),
(8,  'Notebook Set',   3,  12.00),
(9,  'Gel Pen Pack',   3,   4.00),
(10, 'Desk Organizer', 3,  18.00),
(11, 'Coffee Machine', 4, 150.00),
(12, 'Air Purifier',   4, 210.00);

-- insert customers
INSERT INTO customers (customer_id, customer_name, email, city) VALUES
(1, 'Ayesha Karim',  'ayesha.karim@example.com',  'Springfield'),
(2, 'Daniel Brooks', 'daniel.brooks@example.com', 'Riverside'),
(3, 'Fatima Malik',  'fatima.malik@example.com',  'Springfield'),
(4, 'James Carter',  'james.carter@example.com',  'Fairview'),
(5, 'Bilal Hussain', 'bilal.hussain@example.com', 'Greenville'),
(6, 'Emily Chen',    'emily.chen@example.com',    'Riverside'),
(7, 'Omar Farooq',   'omar.farooq@example.com',   'Lakeside'),
(8, 'Grace Lee',     'grace.lee@example.com',     'Fairview');

-- insert orders
INSERT INTO orders (order_id, customer_id, order_date) VALUES
(1,  1, '2025-01-10'),
(2,  2, '2025-01-18'),
(3,  3, '2025-02-02'),
(4,  1, '2025-02-20'),
(5,  4, '2025-03-05'),
(6,  5, '2025-03-14'),
(7,  3, '2025-04-01'),
(8,  6, '2025-04-15'),
(9,  2, '2025-05-03'),
(10, 7, '2025-05-20'),
(11, 1, '2025-06-02'),
(12, 3, '2025-06-18'),
(13, 5, '2025-07-07'),
(14, 6, '2025-07-21'),
(15, 4, '2025-08-09');

-- insert order items
INSERT INTO order_items (order_id, product_id, quantity) VALUES
(1,  1,  1),
(1,  3,  2),
(2,  5,  2),
(2,  8, 10),
(3,  6,  1),
(3,  2,  2),
(4,  9, 20),
(4, 10,  5),
(5, 11,  1),
(5,  3,  1),
(6,  1,  2),
(6,  4,  2),
(7,  7,  3),
(7,  8, 15),
(8,  2,  1),
(8,  9, 10),
(9,  4,  1),
(10, 5,  1),
(10, 10, 4),
(11, 6,  2),
(11, 5,  2),
(12, 1,  1),
(12, 11, 1),
(13, 3,  4),
(13, 8,  6),
(14, 7,  1),
(15, 2,  2),
(15, 9, 25);

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
