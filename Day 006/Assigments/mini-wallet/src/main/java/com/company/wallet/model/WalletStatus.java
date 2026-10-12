package com.company.wallet.model;

/*
Only ACTIVE wallets may send or receive money.
*/
public enum WalletStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED
}
