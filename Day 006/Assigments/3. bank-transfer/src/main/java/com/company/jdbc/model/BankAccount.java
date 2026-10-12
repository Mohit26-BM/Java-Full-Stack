package com.company.jdbc.model;

import java.math.BigDecimal;

public class BankAccount {

    private long accountId;
    private String accountNumber;
    private String customerName;
    private BigDecimal balance;
    private AccountStatus status;

    public BankAccount() {
    }

    public BankAccount(String accountNumber,
                       String customerName,
                       BigDecimal balance,
                       AccountStatus status) {

        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.balance = balance;
        this.status = status;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    @Override
    public String toString() {

        return String.format(
                "%-4d %-12s %-22.22s %16s  %s",
                accountId,
                accountNumber,
                customerName,
                "Rs. " + balance,
                status
        );
    }
}
