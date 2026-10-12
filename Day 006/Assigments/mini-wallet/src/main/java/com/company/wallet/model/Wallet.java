package com.company.wallet.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Wallet {

    private long walletId;
    private String walletNumber;
    private long customerId;
    private BigDecimal balance;
    private WalletStatus status;
    private LocalDateTime createdAt;

    /*
    Filled only by reads that JOIN customers (display).
    */
    private String customerName;

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {

        return String.format(
                "%-11s %-20.20s %16s  %s",
                walletNumber,
                customerName == null ? "customer " + customerId : customerName,
                "Rs. " + balance,
                status
        );
    }
}
