package com.company.wallet.model;

import java.math.BigDecimal;

/*
Outcome of a bulk credit: the executeBatch() update counts of the
two batches (wallet balance updates and ledger inserts).
*/
public class BulkCreditResult {

    private final int lineCount;
    private final int walletCount;
    private final BigDecimal totalAmount;
    private final int[] balanceUpdateCounts;
    private final int[] ledgerInsertCounts;

    public BulkCreditResult(int lineCount,
                            int walletCount,
                            BigDecimal totalAmount,
                            int[] balanceUpdateCounts,
                            int[] ledgerInsertCounts) {

        this.lineCount = lineCount;
        this.walletCount = walletCount;
        this.totalAmount = totalAmount;
        this.balanceUpdateCounts = balanceUpdateCounts;
        this.ledgerInsertCounts = ledgerInsertCounts;
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getWalletCount() {
        return walletCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public int[] getBalanceUpdateCounts() {
        return balanceUpdateCounts;
    }

    public int[] getLedgerInsertCounts() {
        return ledgerInsertCounts;
    }
}
