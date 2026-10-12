-- Customer Insights - negative tests (each statement should fail)

USE customer_insights_db;

-- t1 unique: duplicate email (error 1062)
INSERT INTO customers (first_name, last_name, email, city, signup_date)
VALUES ('Ayesha', 'Khan', 'ayesha.karim@example.com', 'Fairview', '2025-06-30');

-- t2 not null: missing city (error 1048)
INSERT INTO customers (first_name, last_name, email, city, signup_date)
VALUES ('Zain', 'Ali', 'zain.ali@example.com', NULL, '2025-06-30');

-- t3 foreign key: product 99 does not exist (error 1452)
INSERT INTO purchases (customer_id, product_id, quantity, unit_price, purchase_date, channel, payment_method)
VALUES (1, 99, 1, 10.00, '2025-06-30', 'Online', 'Card');

-- t4 check: zero quantity (error 3819)
INSERT INTO purchases (customer_id, product_id, quantity, unit_price, purchase_date, channel, payment_method)
VALUES (1, 3, 0, 18.00, '2025-06-30', 'Online', 'Card');

-- t5 enum: loyalty tier not allowed (error 1265)
UPDATE customers SET loyalty_tier = 'Platinum' WHERE customer_id = 1;

-- t6 delete blocked by foreign key: customer has purchases (error 1451)
DELETE FROM customers WHERE customer_id = 1;

-- row counts after tests
SELECT COUNT(*) AS customer_rows FROM customers;
SELECT COUNT(*) AS purchase_rows FROM purchases;
