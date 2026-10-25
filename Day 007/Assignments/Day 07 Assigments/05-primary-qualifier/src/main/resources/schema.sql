-- =========================================================
-- Day 07 - Assignment 05: @Primary and @Qualifier
-- Shared Day 07 database; table prefixed a05_ so it is
-- uniquely identifiable. Only a05_payments is dropped/recreated.
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;

USE Day_007_Assigments;

DROP TABLE IF EXISTS a05_payments;

-- One row per successful payment. payment_method records WHICH
-- PaymentService implementation Spring injected (CARD / UPI / WALLET).
CREATE TABLE a05_payments (
    payment_id        INT PRIMARY KEY AUTO_INCREMENT,
    order_id          VARCHAR(20)   NOT NULL,
    amount            DECIMAL(10,2) NOT NULL,
    payment_method    VARCHAR(20)   NOT NULL,
    payment_reference VARCHAR(40)   NOT NULL,
    paid_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
