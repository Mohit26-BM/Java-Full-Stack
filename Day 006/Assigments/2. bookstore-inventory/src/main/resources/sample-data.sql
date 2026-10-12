-- =========================================================
-- Online Bookstore Inventory - Sample Data
-- Run after schema.sql
-- 7 authors, 5 categories, 16 books (some with low stock)
-- =========================================================

USE bookstore_inventory;

INSERT INTO authors (author_name) VALUES
('J.K. Rowling'),          -- 1
('J.R.R. Tolkien'),        -- 2
('Joshua Bloch'),          -- 3
('Robert C. Martin'),      -- 4
('Yuval Noah Harari'),     -- 5
('Chetan Bhagat'),         -- 6
('Agatha Christie');       -- 7

INSERT INTO categories (category_name) VALUES
('Fantasy'),               -- 1
('Programming'),           -- 2
('History'),               -- 3
('Fiction'),               -- 4
('Mystery');               -- 5

INSERT INTO books (isbn, title, author_id, category_id, price, stock_qty) VALUES
('9780747532699', 'Harry Potter and the Philosopher''s Stone', 1, 1,  499.00, 25),
('9780747538493', 'Harry Potter and the Chamber of Secrets',   1, 1,  499.00,  4),
('9780747542155', 'Harry Potter and the Prisoner of Azkaban',  1, 1,  549.00, 12),
('9780261102217', 'The Hobbit',                                2, 1,  399.00,  2),
('9780261103252', 'The Lord of the Rings',                     2, 1, 1299.00,  8),
('9780134685991', 'Effective Java',                            3, 2, 3299.00,  6),
('9780132350884', 'Clean Code',                                4, 2, 2799.00,  3),
('9780134494166', 'Clean Architecture',                        4, 2, 2599.00, 15),
('9780137081073', 'The Clean Coder',                           4, 2, 2199.00,  0),
('9780099590088', 'Sapiens: A Brief History of Humankind',     5, 3,  599.00, 30),
('9781784703974', 'Homo Deus: A Brief History of Tomorrow',    5, 3,  650.00,  9),
('9788129115300', 'Five Point Someone',                        6, 4,  176.00, 40),
('9788129135728', '2 States',                                  6, 4,  195.00,  1),
('9780007119318', 'Murder on the Orient Express',              7, 5,  299.00, 18),
('9780007136834', 'And Then There Were None',                  7, 5,  299.00,  5),
('9780062073488', 'The Murder of Roger Ackroyd',               7, 5,  349.00, 11);
