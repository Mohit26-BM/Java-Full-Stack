-- =========================================================
-- Employee Performance - Stored Procedure
-- Run AFTER schema.sql (in the mysql client or MySQL Workbench).
--
-- DELIMITER is needed because the procedure body contains ';'.
-- It tells the client that '$$' ends the CREATE statement.
-- =========================================================

USE employee_performance;

DROP PROCEDURE IF EXISTS sp_employee_review_summary;

DELIMITER $$

CREATE PROCEDURE sp_employee_review_summary(
    IN  p_employee_id    INT,
    OUT p_average_rating DECIMAL(4,2),
    OUT p_review_count   INT,
    OUT p_latest_period  VARCHAR(20),
    OUT p_rating_band    VARCHAR(30)
)
BEGIN
    -- 1. Aggregates for the employee.
    --    With no reviews: COUNT = 0, AVG = NULL, MAX = NULL.
    SELECT COUNT(*),
           ROUND(AVG(rating), 2),
           MAX(review_period)
      INTO p_review_count,
           p_average_rating,
           p_latest_period
      FROM performance_reviews
     WHERE employee_id = p_employee_id;

    -- 2. Rating band (configurable through the rating_bands table).
    IF p_review_count = 0 THEN

        SET p_rating_band = 'NO_REVIEWS';

    ELSE

        SET p_rating_band = COALESCE(
            (SELECT band_name
               FROM rating_bands
              WHERE min_rating <= p_average_rating
              ORDER BY min_rating DESC
              LIMIT 1),
            'UNCLASSIFIED'
        );

    END IF;

    -- 3. Result set: the individual reviews, newest period first.
    --    (Empty result set when there are no reviews.)
    SELECT review_id,
           review_period,
           rating,
           reviewer,
           comments
      FROM performance_reviews
     WHERE employee_id = p_employee_id
     ORDER BY review_period DESC, review_id DESC;
END$$

DELIMITER ;
