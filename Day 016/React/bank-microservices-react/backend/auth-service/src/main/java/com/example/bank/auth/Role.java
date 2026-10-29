package com.example.bank.auth;

/**
 * CUSTOMER: sees and uses only the accounts of the customer record the login is linked to.
 * TELLER:   staff; works with any customer and account (cannot freeze or close accounts).
 * ADMIN:    staff plus account status changes, interest runs, audit log and managing users.
 */
public enum Role {
    CUSTOMER, TELLER, ADMIN
}
