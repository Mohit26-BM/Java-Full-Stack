package com.example.bank.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountno;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 20)
    private String accountType;

    @Column(nullable = false)
    private LocalDate openingDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 10)
    private AccountStatus status = AccountStatus.ACTIVE;

    /** Optimistic lock: a second, conflicting update of the same row fails instead of overwriting the first. */
    @Version
    @Column(nullable = false)
    private Long version;

    protected Account() {
    }

    public Account(Long customerId, String accountType, LocalDate openingDate, BigDecimal balance) {
        this.customerId = customerId;
        this.accountType = accountType;
        this.openingDate = openingDate;
        this.balance = balance;
    }

    public Long getAccountno() {
        return accountno;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public AccountType type() {
        return AccountType.valueOf(accountType);
    }
}
