-- Sales Analytics - negative tests (each statement should fail)

USE sales_db;

-- t1 unique: duplicate sku (error 1062)
INSERT INTO products (sku, product_name, category_id, list_price)
VALUES ('ELE-001', 'Laptop 15 inch', 1, 950.00);

-- t2 not null: missing product name (error 1048)
INSERT INTO products (sku, product_name, category_id, list_price)
VALUES ('ELE-005', NULL, 1, 60.00);

-- t3 foreign key: product 99 does not exist (error 1452)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES (1, 99, 1, 10.00);

-- t4 check: zero quantity (error 3819)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES (1, 12, 0, 40.00);

-- t5 primary key: same product twice in one order (error 1062)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES (1, 1, 2, 850.00);

-- t6 enum: order status not allowed (error 1265)
UPDATE orders SET status = 'Shipped' WHERE order_id = 1;

-- t7 delete blocked by foreign key: product has been sold (error 1451)
DELETE FROM products WHERE product_id = 1;

-- row counts after tests
SELECT COUNT(*) AS product_rows    FROM products;
SELECT COUNT(*) AS order_rows      FROM orders;
SELECT COUNT(*) AS order_item_rows FROM order_items;
