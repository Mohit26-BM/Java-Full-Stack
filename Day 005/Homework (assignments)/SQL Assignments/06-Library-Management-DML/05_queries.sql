-- Library Management - select queries

USE library_db;

-- q1: books currently issued with member and due date
SELECT b.title, m.first_name, m.last_name, i.issue_date, i.due_date
FROM issues i
JOIN books b   ON i.book_id = b.book_id
JOIN members m ON i.member_id = m.member_id
WHERE i.return_date IS NULL
ORDER BY i.due_date;

-- q2: number of books in each status
SELECT status, COUNT(*) AS book_count
FROM books
GROUP BY status
ORDER BY status;

-- q3: number of loans per genre
SELECT b.genre, COUNT(i.issue_id) AS loan_count
FROM books b
JOIN issues i ON b.book_id = i.book_id
GROUP BY b.genre
ORDER BY loan_count DESC, b.genre;

-- q4: overdue books on 2025-10-06
SELECT b.title, m.first_name, m.last_name, i.due_date, b.status
FROM issues i
JOIN books b   ON i.book_id = b.book_id
JOIN members m ON i.member_id = m.member_id
WHERE i.return_date IS NULL
  AND i.due_date < '2025-10-06'
ORDER BY i.due_date;

-- q5: number of loans per member
SELECT m.first_name, m.last_name, m.membership_status, COUNT(i.issue_id) AS loan_count
FROM members m
LEFT JOIN issues i ON m.member_id = i.member_id
GROUP BY m.member_id, m.first_name, m.last_name, m.membership_status
ORDER BY loan_count DESC, m.last_name;

-- q6: books returned late
SELECT b.title, m.first_name, m.last_name, i.due_date, i.return_date
FROM issues i
JOIN books b   ON i.book_id = b.book_id
JOIN members m ON i.member_id = m.member_id
WHERE i.return_date > i.due_date;

-- q7: books never issued
SELECT b.book_id, b.title, b.status
FROM books b
LEFT JOIN issues i ON b.book_id = i.book_id
WHERE i.issue_id IS NULL;

-- q8: average loan length in days per genre for returned books
SELECT b.genre, ROUND(AVG(DATEDIFF(i.return_date, i.issue_date)), 1) AS average_days
FROM issues i
JOIN books b ON i.book_id = b.book_id
WHERE i.return_date IS NOT NULL
GROUP BY b.genre
ORDER BY average_days DESC;
