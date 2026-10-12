package com.company.wallet.model;

import java.math.BigDecimal;

/*
One row of a monthly summary / monthly report:
money in (credits), money out (debits) and the count of ledger rows.
customerName is empty for the single-wallet summary.
*/
public class MonthlySummary {

    private final String month;
    private final String walletNumber;
    private final String customerName;
    private final BigDecimal totalCredits;
    private final BigDecimal totalDebits;
    private final long transactionCount;

    public MonthlySummary(String month,
                          String walletNumber,
                          String customerName,
                          BigDecimal totalCredits,
                          BigDecimal totalDebits,
                          long transactionCount) {

        this.month = month;
        this.walletNumber = walletNumber;
        this.customerName = customerName;
        this.totalCredits = totalCredits;
        this.totalDebits = totalDebits;
        this.transactionCount = transactionCount;
    }

    public BigDecimal getNetChange() {
        return totalCredits.subtract(totalDebits);
    }

    public String getMonth() {
        return month;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getTotalCredits() {
        return totalCredits;
    }

    public BigDecimal getTotalDebits() {
        return totalDebits;
    }

    public long getTransactionCount() {
        return transactionCount;
    }
}
