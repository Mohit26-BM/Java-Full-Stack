-- =========================================================
-- Day 07 - Assignment 06: Bean Lifecycle
-- Shared Day 07 database; table prefixed a06_ so it is
-- uniquely identifiable. Only a06_lifecycle_events is
-- dropped/recreated.
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;

USE Day_007_Assigments;

DROP TABLE IF EXISTS a06_lifecycle_events;

-- DatabaseClient writes one row per lifecycle phase that has a
-- database connection (@PostConstruct, business method, @PreDestroy).
-- run_id groups the rows of one program run.
-- TIMESTAMP(3) keeps milliseconds, so the order is visible.
CREATE TABLE a06_lifecycle_events (
    event_id  INT PRIMARY KEY AUTO_INCREMENT,
    run_id    VARCHAR(12)  NOT NULL,
    bean_name VARCHAR(60)  NOT NULL,
    phase     VARCHAR(30)  NOT NULL,
    detail    VARCHAR(200),
    logged_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)
);
