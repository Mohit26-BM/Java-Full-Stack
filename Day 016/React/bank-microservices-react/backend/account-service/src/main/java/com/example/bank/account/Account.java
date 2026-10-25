package com.example.bank.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
}
