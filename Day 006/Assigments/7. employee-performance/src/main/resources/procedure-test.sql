-- =========================================================
-- Acceptance test: the procedure works from a SQL client
-- BEFORE any Java code is involved.
-- Run after schema.sql, procedure.sql and sample-data.sql.
--
-- OUT parameters are received into session variables (@...).
-- Each CALL also returns a result set with the review rows.
-- =========================================================

USE employee_performance;

-- Employee with several reviews
CALL sp_employee_review_summary(101, @avg, @count, @latest, @band);
SELECT @avg AS average_rating, @count AS review_count,
       @latest AS latest_period, @band AS rating_band;
-- expected: 4.65 | 4 | 2026-Q2 | OUTSTANDING

-- Boundary case: exactly 3.75
CALL sp_employee_review_summary(106, @avg, @count, @latest, @band);
SELECT @avg, @count, @latest, @band;
-- expected: 3.75 | 1 | 2026-Q3 | EXCEEDS_EXPECTATIONS

-- Employee with NO reviews
CALL sp_employee_review_summary(107, @avg, @count, @latest, @band);
SELECT @avg, @count, @latest, @band;
-- expected: NULL | 0 | NULL | NO_REVIEWS

-- Configurable bands: raise OUTSTANDING to 4.70 and call again
UPDATE rating_bands SET min_rating = 4.70 WHERE band_name = 'OUTSTANDING';
CALL sp_employee_review_summary(101, @avg, @count, @latest, @band);
SELECT @avg, @band;
-- expected: 4.65 | EXCEEDS_EXPECTATIONS

-- Put it back
UPDATE rating_bands SET min_rating = 4.50 WHERE band_name = 'OUTSTANDING';
