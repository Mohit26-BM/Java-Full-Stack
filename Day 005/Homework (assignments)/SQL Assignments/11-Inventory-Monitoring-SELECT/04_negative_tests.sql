-- Inventory Monitoring - negative tests (each statement should fail)

USE inventory_db;

-- t1 unique: duplicate sku (error 1062)
INSERT INTO products (sku, product_name, category_id, supplier_id, unit_cost, unit_price, quantity_on_hand)
VALUES ('ELE-101', 'Laptop 15 inch', 1, 1, 700.00, 950.00, 5);

-- t2 not null: missing product name (error 1048)
INSERT INTO products (sku, product_name, category_id, supplier_id, unit_cost, unit_price, quantity_on_hand)
VALUES ('ELE-106', NULL, 1, 1, 20.00, 35.00, 5);

-- t3 foreign key: supplier 99 does not exist (error 1452)
INSERT INTO products (sku, product_name, category_id, supplier_id, unit_cost, unit_price, quantity_on_hand)
VALUES ('ELE-107', 'Webcam HD', 1, 99, 30.00, 55.00, 5);

-- t4 check: negative stock (error 3819)
UPDATE products SET quantity_on_hand = -5 WHERE product_id = 2;

-- t5 enum: status not allowed (error 1265)
UPDATE products SET status = 'Archived' WHERE product_id = 5;

-- t6 delete blocked by foreign key: product has stock movements (error 1451)
DELETE FROM products WHERE product_id = 1;

-- row counts after tests
SELECT COUNT(*) AS product_rows  FROM products;
SELECT COUNT(*) AS movement_rows FROM stock_movements;
