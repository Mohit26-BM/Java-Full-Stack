-- Advanced Reporting - negative tests (each statement should fail)

USE advanced_report_db;

-- t1 unique: duplicate customer email (error 1062)
INSERT INTO customers (customer_name, email, city)
VALUES ('Ayesha Khan', 'ayesha.karim@example.com', 'Fairview');

-- t2 not null: missing product name (error 1048)
INSERT INTO products (product_name, category_id, unit_price)
VALUES (NULL, 1, 60.00);

-- t3 foreign key: category 99 does not exist (error 1452)
INSERT INTO products (product_name, category_id, unit_price)
VALUES ('Webcam HD', 99, 55.00);

-- t4 check: zero price (error 3819)
INSERT INTO products (product_name, category_id, unit_price)
VALUES ('Webcam HD', 1, 0.00);

-- t5 primary key: same product twice in one order (error 1062)
INSERT INTO order_items (order_id, product_id, quantity)
VALUES (1, 1, 3);

-- t6 delete blocked by foreign key: customer has orders (error 1451)
DELETE FROM customers WHERE customer_id = 1;

-- row counts after tests
SELECT COUNT(*) AS product_rows    FROM products;
SELECT COUNT(*) AS customer_rows   FROM customers;
SELECT COUNT(*) AS order_item_rows FROM order_items;
