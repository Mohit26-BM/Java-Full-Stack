-- Who did what to the accounts themselves (the ledger already records the money). Insert-only.
CREATE TABLE account_audit (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    actor      VARCHAR(80)  NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    action     VARCHAR(40)  NOT NULL,
    accountno  BIGINT       NULL,
    detail     VARCHAR(300) NULL,
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY idx_audit_account (accountno)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
