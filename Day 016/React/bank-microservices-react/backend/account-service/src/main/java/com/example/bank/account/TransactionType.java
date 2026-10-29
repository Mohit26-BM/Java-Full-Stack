package com.example.bank.account;

public enum TransactionType {
    DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT,
    /** Monthly interest credited by the bank. */
    INTEREST
}
