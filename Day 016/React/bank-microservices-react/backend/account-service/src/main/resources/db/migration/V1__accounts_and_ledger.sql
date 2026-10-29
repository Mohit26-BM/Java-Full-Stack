-- Baseline. IF NOT EXISTS lets this run safely on a database where Hibernate already created the tables.
CREATE TABLE IF NOT EXISTS accounts (
    accountno    BIGINT         NOT NULL AUTO_INCREMENT,
    account_type VARCHAR(20)    NOT NULL,
    balance      DECIMAL(15, 2) NOT NULL,
    customer_id  BIGINT         NOT NULL,
    opening_date DATE           NOT NULL,
    status       VARCHAR(10)    NOT NULL DEFAULT 'ACTIVE',
    version      BIGINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (accountno)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS account_transactions (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    accountno         BIGINT         NOT NULL,
    amount            DECIMAL(15, 2) NOT NULL,
    balance_after     DECIMAL(15, 2) NOT NULL,
    created_at        DATETIME(6)    NOT NULL,
    description       VARCHAR(200)   NULL,
    idempotency_key   VARCHAR(64)    NULL,
    performed_by      VARCHAR(80)    NOT NULL,
    reference         VARCHAR(36)    NOT NULL,
    related_accountno BIGINT         NULL,
    type              VARCHAR(20)    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tx_idempotency_key (idempotency_key),
    KEY idx_tx_account (accountno, id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
