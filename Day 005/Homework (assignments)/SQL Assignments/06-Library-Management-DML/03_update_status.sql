-- Library Management - issue, return and update book status (today is 2025-10-06)

USE library_db;

-- turn off safe update mode for this script
SET SQL_SAFE_UPDATES = 0;

-- book status before changes
SELECT book_id, title, status FROM books;

-- update 1: issue Database System Concepts to Emily Chen
START TRANSACTION;
INSERT INTO issues (book_id, member_id, issue_date, due_date, return_date)
VALUES (3, 6, '2025-10-06', '2025-10-20', NULL);
UPDATE books SET status = 'Issued' WHERE book_id = 3;
COMMIT;

-- update 2: Daniel Brooks returns Clean Code
START TRANSACTION;
UPDATE issues SET return_date = '2025-10-06' WHERE issue_id = 2;
UPDATE books SET status = 'Available' WHERE book_id = 2;
COMMIT;

-- update 3: Grace Lee renews Sapiens for two more weeks
UPDATE issues SET due_date = '2025-10-29' WHERE issue_id = 5;

-- update 4: mark books lost if not returned 90 days after the due date
UPDATE books
SET status = 'Lost'
WHERE book_id IN (SELECT book_id FROM issues
                  WHERE return_date IS NULL AND due_date < '2025-07-08');

-- update 5: withdraw outdated technology books
UPDATE books
SET status = 'Withdrawn'
WHERE genre = 'Technology'
  AND published_year < 1996
  AND status = 'Available';

-- update 6: expire memberships past their expiry date
UPDATE members
SET membership_status = 'Expired'
WHERE expiry_date < '2025-10-06'
  AND membership_status = 'Active';

-- book status after changes
SELECT book_id, title, status FROM books;
SELECT member_id, first_name, last_name, expiry_date, membership_status FROM members;
SELECT * FROM issues WHERE issue_id IN (2, 5, 15);

-- turn safe update mode back on
SET SQL_SAFE_UPDATES = 1;
