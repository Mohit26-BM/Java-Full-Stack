package com.example.bank.account;

/** ACTIVE accounts can move money; FROZEN ones cannot (reversible); CLOSED is final and needs a zero balance. */
public enum AccountStatus {
    ACTIVE, FROZEN, CLOSED
}
