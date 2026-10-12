-- Inventory Monitoring - insert sample data

USE inventory_db;

-- insert categories
INSERT INTO categories (category_id, category_name) VALUES
(1, 'Electronics'),
(2, 'Office Supplies'),
(3, 'Furniture'),
(4, 'Cleaning'),
(5, 'Kitchen');

-- insert suppliers
INSERT INTO suppliers (supplier_id, supplier_name, email, phone) VALUES
(1, 'TechSource Ltd',         'sales@techsource.example.com',  '555-6001'),
(2, 'OfficeWorld Supplies',   'orders@officeworld.example.com', '555-6002'),
(3, 'HomeLine Furniture',     'trade@homeline.example.com',    '555-6003'),
(4, 'CleanPro Distributors',  'supply@cleanpro.example.com',   '555-6004');

-- insert products
INSERT INTO products (product_id, sku, product_name, category_id, supplier_id, unit_cost, unit_price, quantity_on_hand, reorder_level, status) VALUES
(1,  'ELE-101', 'Laptop 14 inch',        1, 1, 620.00, 850.00,  18,  10, 'Active'),
(2,  'ELE-102', 'Wireless Mouse',        1, 1,  12.00,  25.00,   6,  30, 'Active'),
(3,  'ELE-103', 'Monitor 27 inch',       1, 1, 150.00, 220.00,  25,   8, 'Active'),
(4,  'ELE-104', 'USB-C Dock',            1, 1,  80.00, 120.00,   0,  10, 'Active'),
(5,  'ELE-105', 'VGA Cable',             1, 1,   3.00,   8.00, 140,  20, 'Discontinued'),
(6,  'OFF-201', 'A4 Paper Ream',         2, 2,   3.50,   6.50, 420, 200, 'Active'),
(7,  'OFF-202', 'Gel Pen Pack',          2, 2,   2.00,   4.00,  35,  50, 'Active'),
(8,  'OFF-203', 'Stapler',               2, 2,   6.00,  12.00,  60,  15, 'Active'),
(9,  'OFF-204', 'Fax Paper Roll',        2, 2,   4.00,   9.00,  75,  10, 'Discontinued'),
(10, 'FUR-301', 'Office Chair',          3, 3, 110.00, 180.00,   9,  10, 'Active'),
(11, 'FUR-302', 'Standing Desk',         3, 3, 280.00, 420.00,  14,   5, 'Active'),
(12, 'FUR-303', 'Filing Cabinet',        3, 3,  95.00, 150.00,  22,   5, 'Active'),
(13, 'CLN-401', 'Disinfectant Spray',    4, 4,   2.50,   5.00,   0,  40, 'Active'),
(14, 'CLN-402', 'Microfiber Cloth Pack', 4, 4,   4.00,   8.00, 150,  30, 'Active'),
(15, 'KIT-501', 'Coffee Machine',        5, 1,  95.00, 150.00,  40,   5, 'Active'),
(16, 'KIT-502', 'Water Dispenser',       5, 3, 160.00, 240.00,  12,   4, 'Active');

-- insert stock movements
INSERT INTO stock_movements (movement_id, product_id, movement_type, quantity, movement_date) VALUES
(1,  1,  'Restock',  20, '2025-06-02'),
(2,  1,  'Sale',      6, '2025-08-14'),
(3,  1,  'Sale',      4, '2025-09-21'),
(4,  2,  'Restock',  50, '2025-05-10'),
(5,  2,  'Sale',     30, '2025-07-25'),
(6,  2,  'Sale',     14, '2025-09-30'),
(7,  3,  'Restock',  30, '2025-06-15'),
(8,  3,  'Sale',      5, '2025-08-03'),
(9,  4,  'Sale',     12, '2025-07-19'),
(10, 4,  'Sale',      8, '2025-09-02'),
(11, 5,  'Sale',     10, '2024-11-20'),
(12, 6,  'Restock', 500, '2025-07-01'),
(13, 6,  'Sale',    180, '2025-08-12'),
(14, 6,  'Sale',    100, '2025-09-25'),
(15, 7,  'Sale',     40, '2025-08-28'),
(16, 7,  'Sale',     25, '2025-10-01'),
(17, 8,  'Sale',      5, '2025-03-14'),
(18, 10, 'Restock',  15, '2025-06-20'),
(19, 10, 'Sale',      6, '2025-09-10'),
(20, 11, 'Sale',      3, '2025-07-30'),
(21, 12, 'Restock',  10, '2025-04-22'),
(22, 13, 'Sale',     60, '2025-08-05'),
(23, 13, 'Sale',     40, '2025-09-15'),
(24, 14, 'Restock', 100, '2025-07-12'),
(25, 14, 'Sale',     45, '2025-09-18'),
(26, 15, 'Sale',      5, '2025-08-22'),
(27, 15, 'Restock',  20, '2025-09-05');

-- row counts
SELECT COUNT(*) AS category_rows FROM categories;
SELECT COUNT(*) AS supplier_rows FROM suppliers;
SELECT COUNT(*) AS product_rows  FROM products;
SELECT COUNT(*) AS movement_rows FROM stock_movements;

-- show all data
SELECT * FROM categories;
SELECT * FROM suppliers;
SELECT * FROM products;
SELECT * FROM stock_movements;
