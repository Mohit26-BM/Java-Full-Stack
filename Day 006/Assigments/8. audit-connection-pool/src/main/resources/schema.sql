-- =========================================================
-- DataSource and Connection Pooling - Schema
-- Run this first. WARNING: drops and recreates audit_events
-- in the audit_pool database only.
-- =========================================================

CREATE DATABASE IF NOT EXISTS audit_pool;

USE audit_pool;

DROP TABLE IF EXISTS audit_events;

CREATE TABLE audit_events (
    event_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_type    VARCHAR(80) NOT NULL,
    event_message VARCHAR(500),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
