package com.company.jdbc.model;

/*
Stored in bank_accounts.status as text.
Only ACTIVE accounts may send or receive money.
*/
public enum AccountStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED
}
