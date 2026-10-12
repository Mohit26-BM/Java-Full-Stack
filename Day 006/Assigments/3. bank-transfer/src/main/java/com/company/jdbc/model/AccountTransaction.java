package com.company.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
One row of account_transactions.

fromAccountId is null for an opening deposit.
The *AccountNumber fields are filled only when reading (JOIN),
for display.
*/
public class AccountTransaction {

    private long transactionId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDateTime transactionTime;

    private String fromAccountNumber;
    private String toAccountNumber;

    public AccountTransaction() {
    }

    public AccountTransaction(Long fromAccountId,
                              Long toAccountId,
                              BigDecimal amount,
                              TransactionType transactionType) {

        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    @Override
    public String toString() {

        return String.format(
                "%-5d %-12s %-12s %16s  %-16s %s",
                transactionId,
                fromAccountNumber == null ? "-" : fromAccountNumber,
                toAccountNumber == null ? "-" : toAccountNumber,
                "Rs. " + amount,
                transactionType,
                transactionTime == null ? "-" : transactionTime.toString().replace('T', ' ')
        );
    }
}
