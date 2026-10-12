package com.company.wallet.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
One ledger row (wallet_transactions).
*/
public class WalletTransaction {

    private long transactionId;
    private String reference;
    private long walletId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private Long counterpartyWalletId;
    private String description;
    private LocalDateTime createdAt;

    /*
    Filled only by reads that JOIN wallets (display).
    */
    private String counterpartyWalletNumber;

    public WalletTransaction() {
    }

    public WalletTransaction(String reference,
                             long walletId,
                             TransactionType transactionType,
                             BigDecimal amount,
                             BigDecimal balanceAfter,
                             Long counterpartyWalletId,
                             String description) {

        this.reference = reference;
        this.walletId = walletId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.counterpartyWalletId = counterpartyWalletId;
        this.description = description;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Long getCounterpartyWalletId() {
        return counterpartyWalletId;
    }

    public void setCounterpartyWalletId(Long counterpartyWalletId) {
        this.counterpartyWalletId = counterpartyWalletId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCounterpartyWalletNumber() {
        return counterpartyWalletNumber;
    }

    public void setCounterpartyWalletNumber(String counterpartyWalletNumber) {
        this.counterpartyWalletNumber = counterpartyWalletNumber;
    }

    @Override
    public String toString() {

        String sign = transactionType.isCredit() ? "+" : "-";

        return String.format(
                "%-6d %-16s %-13s %15s %15s  %-11s %s",
                transactionId,
                createdAt == null ? "-" : createdAt.toString().replace('T', ' ').substring(0, 16),
                transactionType,
                sign + " Rs. " + amount,
                "Rs. " + balanceAfter,
                counterpartyWalletNumber == null ? "-" : counterpartyWalletNumber,
                description == null ? "" : description
        );
    }
}
