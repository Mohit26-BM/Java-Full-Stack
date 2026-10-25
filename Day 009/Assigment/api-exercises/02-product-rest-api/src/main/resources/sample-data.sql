-- 12 sample products (run after schema.sql)
USE product_store;

INSERT INTO products (name, category, price, quantity, brand, active) VALUES
    ('Wireless Mouse',              'Electronics',   799.00,  50, 'Logitech', TRUE),
    ('Mechanical Keyboard',         'Electronics',  3499.00,  20, 'Keychron', TRUE),
    ('27-inch Monitor',             'Electronics', 15999.00,   8, 'Dell',     TRUE),
    ('Noise Cancelling Headphones', 'Electronics',  8999.00,  12, 'Sony',     TRUE),
    ('USB-C Hub',                   'Electronics',  1299.00,  35, 'Anker',    TRUE),
    ('Running Shoes',               'Footwear',     4599.00,  25, 'Nike',     TRUE),
    ('Trail Shoes',                 'Footwear',     5299.00,  10, 'Salomon',  TRUE),
    ('Cotton T-Shirt',              'Clothing',      599.00, 100, 'Uniqlo',   TRUE),
    ('Denim Jacket',                'Clothing',     2999.00,  15, 'Levis',    TRUE),
    ('Clean Code',                  'Books',         450.00,  30, 'Pearson',  TRUE),
    ('Effective Java',              'Books',         899.00,  18, 'Pearson',  TRUE),
    ('Old Wired Mouse',             'Electronics',   199.00,   0, 'Generic',  FALSE);
