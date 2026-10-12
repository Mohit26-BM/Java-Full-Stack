-- Retail Store - negative tests (each statement should fail)

USE retail_db;

-- t1 unique: duplicate category name (error 1062)
INSERT INTO categories (category_name, description) VALUES ('Snacks', 'Party snacks');

-- t2 unique: duplicate sku (error 1062)
INSERT INTO products (sku, product_name, category_id, supplier_id, unit_price)
VALUES ('BEV-001', 'Apple Juice 1L', 1, 1, 3.29);

-- t3 not null: missing product name (error 1048)
INSERT INTO products (sku, product_name, category_id, supplier_id, unit_price)
VALUES ('BEV-004', NULL, 1, 1, 1.99);

-- t4 foreign key: category 99 does not exist (error 1452)
INSERT INTO products (sku, product_name, category_id, supplier_id, unit_price)
VALUES ('BEV-005', 'Lemon Soda 330ml', 99, 1, 0.89);

-- t5 delete blocked by foreign key: supplier has products (error 1451)
DELETE FROM suppliers WHERE supplier_id = 2;

-- t6 check after alter: zero price, run after 03 (error 3819)
INSERT INTO products (sku, product_name, category_id, supplier_id, unit_price)
VALUES ('SNK-003', 'Salted Peanuts 100g', 2, 2, 0.00);

-- t7 unique after alter: duplicate supplier email, run after 03 (error 1062)
INSERT INTO suppliers (supplier_name, contact_name, email, phone, city)
VALUES ('Metro Foods', 'Sam Lee', 'orders@freshfields.example.com', '555-2101', 'Springfield');

-- t8 not null after alter: missing stock quantity, run after 03 (error 1048)
INSERT INTO products (sku, product_name, category_id, supplier_id, unit_price, stock_quantity)
VALUES ('DRY-003', 'Greek Yogurt 500g', 3, 3, 2.49, NULL);

-- t9 not null kept after modify: missing phone, run after 03 (error 1048)
INSERT INTO suppliers (supplier_name, contact_name, email, phone, city)
VALUES ('Metro Foods', 'Sam Lee', 'sam@metrofoods.example.com', NULL, 'Springfield');

-- row counts after tests
SELECT COUNT(*) AS category_rows FROM categories;
SELECT COUNT(*) AS supplier_rows FROM suppliers;
SELECT COUNT(*) AS product_rows  FROM products;
