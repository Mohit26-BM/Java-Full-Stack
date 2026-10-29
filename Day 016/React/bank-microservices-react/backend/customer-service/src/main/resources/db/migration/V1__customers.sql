-- Baseline. IF NOT EXISTS lets this run safely on a database where Hibernate already created the table.
CREATE TABLE IF NOT EXISTS customers (
    customer_id   BIGINT       NOT NULL AUTO_INCREMENT,
    city          VARCHAR(80)  NOT NULL,
    date_of_birth DATE         NOT NULL,
    email         VARCHAR(255) NOT NULL,
    gender        VARCHAR(10)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    PRIMARY KEY (customer_id),
    UNIQUE KEY uk_customers_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
