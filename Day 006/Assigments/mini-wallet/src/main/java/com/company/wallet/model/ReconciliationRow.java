package com.company.wallet.model;

import java.math.BigDecimal;

/*
Compares a wallet's stored balance with the balance implied by its
ledger (sum of credits minus sum of debits). They must be equal.
*/
public class ReconciliationRow {

    private final String walletNumber;
    private final BigDecimal storedBalance;
    private final BigDecimal ledgerBalance;

    public ReconciliationRow(String walletNumber, BigDecimal storedBalance, BigDecimal ledgerBalance) {
        this.walletNumber = walletNumber;
        this.storedBalance = storedBalance;
        this.ledgerBalance = ledgerBalance;
    }

    public boolean isMatching() {
        return storedBalance.compareTo(ledgerBalance) == 0;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public BigDecimal getStoredBalance() {
        return storedBalance;
    }

    public BigDecimal getLedgerBalance() {
        return ledgerBalance;
    }
}
