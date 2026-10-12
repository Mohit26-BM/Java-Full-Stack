-- Retail Store - schema changes with ALTER

USE retail_db;

-- structure before alter
DESCRIBE products;
DESCRIBE suppliers;

-- add stock column with default 0
ALTER TABLE products
    ADD COLUMN stock_quantity INT NOT NULL DEFAULT 0;

-- fill stock for existing products
UPDATE products SET stock_quantity = 120 WHERE product_id = 1;
UPDATE products SET stock_quantity = 300 WHERE product_id = 2;
UPDATE products SET stock_quantity = 450 WHERE product_id = 3;
UPDATE products SET stock_quantity = 80  WHERE product_id = 4;
UPDATE products SET stock_quantity = 60  WHERE product_id = 6;
UPDATE products SET stock_quantity = 25  WHERE product_id = 7;
UPDATE products SET stock_quantity = 40  WHERE product_id = 8;
UPDATE products SET stock_quantity = 70  WHERE product_id = 9;
UPDATE products SET stock_quantity = 35  WHERE product_id = 10;

-- add check on price
ALTER TABLE products
    ADD CHECK (unit_price > 0);

-- add unique on supplier email
ALTER TABLE suppliers
    ADD UNIQUE (email);

-- make phone column longer
ALTER TABLE suppliers
    MODIFY COLUMN phone VARCHAR(20) NOT NULL;

-- structure after alter
DESCRIBE products;
DESCRIBE suppliers;
SHOW CREATE TABLE products;
SELECT product_id, sku, product_name, unit_price, stock_quantity FROM products;
