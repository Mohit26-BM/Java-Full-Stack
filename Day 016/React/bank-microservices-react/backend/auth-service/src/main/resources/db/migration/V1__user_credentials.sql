-- Baseline. IF NOT EXISTS lets this run safely on a database where Hibernate already created the table.
CREATE TABLE IF NOT EXISTS user_credentials (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(80)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_credentials_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
