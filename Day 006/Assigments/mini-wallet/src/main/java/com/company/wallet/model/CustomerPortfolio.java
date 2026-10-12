package com.company.wallet.model;

import java.math.BigDecimal;

/*
One row of the customer portfolio report (customers JOIN wallets).
*/
public class CustomerPortfolio {

    private final long customerId;
    private final String fullName;
    private final int walletCount;
    private final BigDecimal totalBalance;

    public CustomerPortfolio(long customerId, String fullName, int walletCount, BigDecimal totalBalance) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.walletCount = walletCount;
        this.totalBalance = totalBalance;
    }

    public long getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public int getWalletCount() {
        return walletCount;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }
}
