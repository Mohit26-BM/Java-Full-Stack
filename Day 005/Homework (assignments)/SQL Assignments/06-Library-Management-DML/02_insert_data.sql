-- Library Management - load books, members and issue records

USE library_db;

-- insert books
INSERT INTO books (book_id, isbn, title, author, genre, published_year, status) VALUES
(1,  '978-1-1111-0001-1', 'The Pragmatic Programmer',      'David Thomas',         'Technology', 1999, 'Issued'),
(2,  '978-1-1111-0002-8', 'Clean Code',                    'Robert C. Martin',     'Technology', 2008, 'Issued'),
(3,  '978-1-1111-0003-5', 'Database System Concepts',      'Abraham Silberschatz', 'Technology', 2019, 'Available'),
(4,  '978-1-1111-0004-2', 'To Kill a Mockingbird',         'Harper Lee',           'Fiction',    1960, 'Available'),
(5,  '978-1-1111-0005-9', '1984',                          'George Orwell',        'Fiction',    1949, 'Issued'),
(6,  '978-1-1111-0006-6', 'Pride and Prejudice',           'Jane Austen',          'Fiction',    1813, 'Available'),
(7,  '978-1-1111-0007-3', 'A Brief History of Time',       'Stephen Hawking',      'Science',    1988, 'Available'),
(8,  '978-1-1111-0008-0', 'The Selfish Gene',              'Richard Dawkins',      'Science',    1976, 'Issued'),
(9,  '978-1-1111-0009-7', 'Sapiens',                       'Yuval Noah Harari',    'History',    2011, 'Issued'),
(10, '978-1-1111-0010-3', 'Guns, Germs, and Steel',        'Jared Diamond',        'History',    1997, 'Available'),
(11, '978-1-1111-0011-0', 'Introduction to Algorithms',    'Thomas H. Cormen',     'Technology', 2009, 'Available'),
(12, '978-1-1111-0012-7', 'Learning SQL',                  'Alan Beaulieu',        'Technology', 2020, 'Available'),
(13, '978-1-1111-0013-4', 'Getting Started with MS-DOS 6', 'Linda Grey',           'Technology', 1993, 'Available'),
(14, '978-1-1111-0014-1', 'Mastering Windows 95',          'Mark Davis',           'Technology', 1995, 'Available'),
(15, '978-1-1111-0015-8', 'Cosmos',                        'Carl Sagan',           'Science',    1980, 'Available');

-- insert members
INSERT INTO members (member_id, first_name, last_name, email, phone, join_date, expiry_date, membership_status) VALUES
(1,  'Ayesha', 'Karim',   'ayesha.karim@example.com',  '555-4001', '2023-01-10', '2026-01-10', 'Active'),
(2,  'Daniel', 'Brooks',  'daniel.brooks@example.com', '555-4002', '2022-03-15', '2026-03-15', 'Active'),
(3,  'Fatima', 'Malik',   'fatima.malik@example.com',  '555-4003', '2024-02-01', '2026-02-01', 'Active'),
(4,  'James',  'Carter',  'james.carter@example.com',  '555-4004', '2021-06-20', '2025-06-20', 'Active'),
(5,  'Bilal',  'Hussain', 'bilal.hussain@example.com', '555-4005', '2023-09-05', '2026-09-05', 'Active'),
(6,  'Emily',  'Chen',    'emily.chen@example.com',    NULL,       '2024-05-12', '2026-05-12', 'Active'),
(7,  'Omar',   'Farooq',  'omar.farooq@example.com',   '555-4007', '2020-11-01', '2024-11-01', 'Active'),
(8,  'Grace',  'Lee',     'grace.lee@example.com',     '555-4008', '2024-08-18', '2026-08-18', 'Active'),
(9,  'Lucas',  'Silva',   'lucas.silva@example.com',   '555-4009', '2022-01-25', '2025-01-25', 'Active'),
(10, 'Nadia',  'Rahman',  'nadia.rahman@example.com',  NULL,       '2025-07-01', '2026-07-01', 'Active');

-- insert issues
INSERT INTO issues (issue_id, book_id, member_id, issue_date, due_date, return_date) VALUES
(1,  1,  1, '2025-09-20', '2025-10-04', NULL),
(2,  2,  2, '2025-09-28', '2025-10-12', NULL),
(3,  5,  3, '2025-09-15', '2025-09-29', NULL),
(4,  8,  5, '2025-05-02', '2025-05-16', NULL),
(5,  9,  8, '2025-10-01', '2025-10-15', NULL),
(6,  4,  1, '2025-08-01', '2025-08-15', '2025-08-14'),
(7,  6,  2, '2025-07-10', '2025-07-24', '2025-07-30'),
(8,  3,  3, '2025-08-05', '2025-08-19', '2025-08-18'),
(9,  7,  4, '2025-03-01', '2025-03-15', '2025-03-14'),
(10, 10, 5, '2025-06-01', '2025-06-15', '2025-06-20'),
(11, 14, 4, '2019-04-10', '2019-04-24', '2019-04-22'),
(12, 11, 6, '2025-09-01', '2025-09-15', '2025-09-12'),
(13, 4,  8, '2025-09-05', '2025-09-19', '2025-09-19'),
(14, 15, 2, '2025-09-10', '2025-09-24', '2025-09-23');

-- row counts
SELECT COUNT(*) AS book_rows   FROM books;
SELECT COUNT(*) AS member_rows FROM members;
SELECT COUNT(*) AS issue_rows  FROM issues;

-- show all data
SELECT * FROM books;
SELECT * FROM members;
SELECT * FROM issues;
