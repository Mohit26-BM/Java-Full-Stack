package com.company.wallet.model;

/*
Ledger entry types. DEPOSIT and TRANSFER_IN add money to the
wallet (credits); WITHDRAWAL and TRANSFER_OUT remove it (debits).
*/
public enum TransactionType {

    DEPOSIT(true),
    WITHDRAWAL(false),
    TRANSFER_IN(true),
    TRANSFER_OUT(false);

    private final boolean credit;

    TransactionType(boolean credit) {
        this.credit = credit;
    }

    public boolean isCredit() {
        return credit;
    }
}
