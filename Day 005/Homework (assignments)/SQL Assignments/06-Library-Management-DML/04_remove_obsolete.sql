-- Library Management - remove obsolete records safely

USE library_db;

-- turn off safe update mode for this script
SET SQL_SAFE_UPDATES = 0;

-- withdrawn books and how many times they were issued
SELECT b.book_id, b.title, COUNT(i.issue_id) AS times_issued
FROM books b
LEFT JOIN issues i ON b.book_id = i.book_id
WHERE b.status = 'Withdrawn'
GROUP BY b.book_id, b.title;

-- delete withdrawn books that were never issued
DELETE FROM books
WHERE status = 'Withdrawn'
  AND book_id NOT IN (SELECT book_id FROM issues);

-- expired members and how many books they borrowed
SELECT m.member_id, m.first_name, m.last_name, COUNT(i.issue_id) AS times_borrowed
FROM members m
LEFT JOIN issues i ON m.member_id = i.member_id
WHERE m.membership_status = 'Expired'
GROUP BY m.member_id, m.first_name, m.last_name;

-- delete expired members who never borrowed a book
DELETE FROM members
WHERE membership_status = 'Expired'
  AND member_id NOT IN (SELECT member_id FROM issues);

-- row counts after delete
SELECT COUNT(*) AS book_rows   FROM books;
SELECT COUNT(*) AS member_rows FROM members;
SELECT COUNT(*) AS issue_rows  FROM issues;

-- turn safe update mode back on
SET SQL_SAFE_UPDATES = 1;
