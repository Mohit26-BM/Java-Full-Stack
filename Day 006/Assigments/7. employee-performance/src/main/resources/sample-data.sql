-- =========================================================
-- Employee Performance - Sample Data
-- Run after schema.sql and procedure.sql
-- 8 employees; 107 and 108 have NO reviews.
-- =========================================================

USE employee_performance;

INSERT INTO employees (employee_id, employee_name) VALUES
(101, 'Ravi Kumar'),
(102, 'Priya Sharma'),
(103, 'Amit Verma'),
(104, 'Neha Iyer'),
(105, 'Karan Patel'),
(106, 'Sneha Reddy'),
(107, 'Arjun Mehta'),     -- no reviews
(108, 'Pooja Menon');     -- no reviews

-- Default rating bands (rating scale 1.00 - 5.00)
INSERT INTO rating_bands (band_name, min_rating) VALUES
('OUTSTANDING',          4.50),
('EXCEEDS_EXPECTATIONS', 3.75),
('MEETS_EXPECTATIONS',   3.00),
('NEEDS_IMPROVEMENT',    2.00),
('UNSATISFACTORY',       0.00);

-- Expected results (with the default bands):
--   101 avg 4.65  count 4  latest 2026-Q2  OUTSTANDING
--   102 avg 4.00  count 3  latest 2026-Q2  EXCEEDS_EXPECTATIONS
--   103 avg 3.37  count 3  latest 2025-Q4  MEETS_EXPECTATIONS
--   104 avg 2.65  count 2  latest 2026-Q2  NEEDS_IMPROVEMENT
--   105 avg 1.65  count 2  latest 2026-Q2  UNSATISFACTORY
--   106 avg 3.75  count 1  latest 2026-Q3  EXCEEDS_EXPECTATIONS (boundary)
--   107 / 108     count 0  NULL / NULL     NO_REVIEWS
INSERT INTO performance_reviews (employee_id, review_period, rating, reviewer, comments) VALUES
(101, '2025-Q3', 4.80, 'Anjali Gupta', 'Led the payments migration ahead of schedule.'),
(101, '2025-Q4', 4.60, 'Anjali Gupta', 'Strong mentoring of new joiners.'),
(101, '2026-Q1', 4.70, 'Anjali Gupta', 'Excellent production support.'),
(101, '2026-Q2', 4.50, 'Rahul Singh',  'Consistently high quality deliveries.'),

(102, '2025-Q4', 4.00, 'Rahul Singh',  'Reliable and proactive.'),
(102, '2026-Q1', 4.20, 'Rahul Singh',  'Improved test coverage of her module.'),
(102, '2026-Q2', 3.80, 'Meera Joshi',  'Good work; needs to delegate more.'),

(103, '2025-Q2', 3.50, 'Meera Joshi',  'Meets deadlines.'),
(103, '2025-Q3', 3.20, 'Meera Joshi',  'Code reviews need more depth.'),
(103, '2025-Q4', 3.40, 'Meera Joshi',  NULL),

(104, '2026-Q1', 2.50, 'Vikram Nair',  'Missed two sprint commitments.'),
(104, '2026-Q2', 2.80, 'Vikram Nair',  'Improving; on a development plan.'),

(105, '2026-Q1', 1.80, 'Vikram Nair',  'Frequent quality issues.'),
(105, '2026-Q2', 1.50, 'Vikram Nair',  'Performance improvement plan started.'),

(106, '2026-Q3', 3.75, 'Anjali Gupta', 'First review after joining; strong start.');
