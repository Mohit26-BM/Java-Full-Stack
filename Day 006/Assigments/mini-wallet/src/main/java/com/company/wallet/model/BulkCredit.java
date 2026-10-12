package com.company.wallet.model;

import java.math.BigDecimal;

/*
One line of a bulk credit file (e.g. cashback campaign).
*/
public class BulkCredit {

    private final int lineNumber;
    private final String walletNumber;
    private final BigDecimal amount;
    private final String description;

    public BulkCredit(int lineNumber, String walletNumber, BigDecimal amount, String description) {
        this.lineNumber = lineNumber;
        this.walletNumber = walletNumber;
        this.amount = amount;
        this.description = description;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
