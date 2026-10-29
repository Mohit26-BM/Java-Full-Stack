package com.example.bank.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** One line of the ledger. Rows are only ever inserted, never changed or deleted. */
@Entity
@Table(name = "account_transactions", indexes = @Index(name = "idx_tx_account", columnList = "accountno, id"))
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountno;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** The account balance right after this line was applied. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    /** The other account of a transfer. */
    private Long relatedAccountno;

    /** Both lines of one transfer share the same reference. */
    @Column(nullable = false, length = 36)
    private String reference;

    @Column(length = 200)
    private String description;

    /** The username from the caller's token. */
    @Column(nullable = false, length = 80)
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** Optional client-supplied key that makes a retried request safe; unique when present. */
    @Column(length = 64, unique = true)
    private String idempotencyKey;

    protected AccountTransaction() {
    }

    public AccountTransaction(Long accountno, TransactionType type, BigDecimal amount, BigDecimal balanceAfter,
                              Long relatedAccountno, String reference, String description, String performedBy,
                              String idempotencyKey) {
        this.accountno = accountno;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.relatedAccountno = relatedAccountno;
        this.reference = reference;
        this.description = description;
        this.performedBy = performedBy;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    public Long getId() {
        return id;
    }

    public Long getAccountno() {
        return accountno;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public Long getRelatedAccountno() {
        return relatedAccountno;
    }

    public String getReference() {
        return reference;
    }

    public String getDescription() {
        return description;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
