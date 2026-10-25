CREATE TABLE refresh_tokens (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    user_id        BIGINT      NOT NULL,
    token_hash     CHAR(64)    NOT NULL,
    family_id      CHAR(36)    NOT NULL,
    expires_at     DATETIME    NOT NULL,
    created_at     DATETIME    NOT NULL,
    revoked_at     DATETIME    NULL,
    revoked_reason VARCHAR(20) NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uq_refresh_tokens_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX ix_refresh_tokens_family ON refresh_tokens (family_id);
