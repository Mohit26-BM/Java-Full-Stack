package com.company.rbac.security;

/**
 * Fine-grained permissions. Stored as plain authorities WITHOUT a prefix
 * ("EMPLOYEE_WRITE") and checked with hasAuthority("EMPLOYEE_WRITE").
 *
 * A role says WHO someone is (HR); a permission says WHAT they may do
 * (write employee records). Two people with the same role can hold
 * different permissions, e.g. an HR intern who may only read.
 */
public enum Permission {
    EMPLOYEE_READ,
    EMPLOYEE_WRITE,
    EMPLOYEE_DELETE
}
