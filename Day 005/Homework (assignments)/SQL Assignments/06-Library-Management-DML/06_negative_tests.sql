-- Library Management - negative tests (each statement should fail)

USE library_db;

-- t1 unique: duplicate isbn (error 1062)
INSERT INTO books (isbn, title, author, genre, published_year)
VALUES ('978-1-1111-0001-1', 'Refactoring', 'Martin Fowler', 'Technology', 2018);

-- t2 unique: duplicate member email (error 1062)
INSERT INTO members (first_name, last_name, email, phone, join_date, expiry_date)
VALUES ('Ayesha', 'Khan', 'ayesha.karim@example.com', '555-4101', '2025-10-06', '2026-10-06');

-- t3 not null: missing title (error 1048)
INSERT INTO books (isbn, title, author, genre, published_year)
VALUES ('978-1-1111-0016-5', NULL, 'Martin Fowler', 'Technology', 2018);

-- t4 foreign key: book 99 does not exist (error 1452)
INSERT INTO issues (book_id, member_id, issue_date, due_date)
VALUES (99, 1, '2025-10-06', '2025-10-20');

-- t5 check: due date before issue date (error 3819)
INSERT INTO issues (book_id, member_id, issue_date, due_date)
VALUES (4, 1, '2025-10-06', '2025-10-01');

-- t6 enum: book status not allowed (error 1265)
UPDATE books SET status = 'Damaged' WHERE book_id = 4;

-- t7 delete blocked by foreign key: withdrawn book has issue history (error 1451)
DELETE FROM books WHERE book_id = 14;

-- t8 delete blocked by foreign key: expired member has issue history (error 1451)
DELETE FROM members WHERE member_id = 4;

-- row counts after tests
SELECT COUNT(*) AS book_rows   FROM books;
SELECT COUNT(*) AS member_rows FROM members;
SELECT COUNT(*) AS issue_rows  FROM issues;
