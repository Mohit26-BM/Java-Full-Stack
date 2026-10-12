-- Retail Store - insert sample data

USE retail_db;

-- insert categories
INSERT INTO categories (category_id, category_name, description) VALUES
(1, 'Beverages',     'Soft drinks, juices and water'),
(2, 'Snacks',        'Chips, biscuits and nuts'),
(3, 'Dairy',         'Milk, cheese and yogurt'),
(4, 'Household',     'Cleaning and laundry supplies'),
(5, 'Personal Care', NULL),
(6, 'Frozen Foods',  'Frozen meals and desserts');

-- insert suppliers
INSERT INTO suppliers (supplier_id, supplier_name, contact_name, email, phone, city) VALUES
(1, 'FreshFields Distributors', 'Nadia Karim',  'orders@freshfields.example.com', '555-2001', 'Springfield'),
(2, 'SnackWorld Wholesale',     'Peter Lawson', 'sales@snackworld.example.com',   '555-2002', 'Riverside'),
(3, 'PureDairy Farms',          'Usman Tariq',  'supply@puredairy.example.com',   '555-2003', 'Fairview'),
(4, 'CleanHome Supplies',       'Grace Miller', 'info@cleanhome.example.com',     '555-2004', 'Greenville'),
(5, 'Lakeside Imports',         'Kevin Osei',   'contact@lakeside.example.com',   '555-2005', 'Lakeside');

-- insert products
INSERT INTO products (product_id, sku, product_name, category_id, supplier_id, unit_price) VALUES
(1,  'BEV-001', 'Orange Juice 1L',          1, 1, 3.49),
(2,  'BEV-002', 'Mineral Water 1.5L',       1, 1, 0.99),
(3,  'BEV-003', 'Cola Can 330ml',           1, 2, 0.79),
(4,  'SNK-001', 'Salted Potato Chips 150g', 2, 2, 2.29),
(5,  'SNK-002', 'Chocolate Biscuits 200g',  2, 2, 1.89),
(6,  'DRY-001', 'Full Cream Milk 1L',       3, 3, 1.49),
(7,  'DRY-002', 'Cheddar Cheese 250g',      3, 3, 4.99),
(8,  'HSE-001', 'Laundry Detergent 2kg',    4, 4, 8.99),
(9,  'HSE-002', 'Dishwashing Liquid 750ml', 4, 4, 2.79),
(10, 'PRC-001', 'Herbal Shampoo 400ml',     5, 4, 5.49);

-- row counts
SELECT COUNT(*) AS category_rows FROM categories;
SELECT COUNT(*) AS supplier_rows FROM suppliers;
SELECT COUNT(*) AS product_rows  FROM products;

-- show all data
SELECT * FROM categories;
SELECT * FROM suppliers;
SELECT * FROM products;
