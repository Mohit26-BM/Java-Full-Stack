package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.model.AccountStatus;
import com.company.jdbc.model.BankAccount;

/*
Two kinds of methods:

1. Methods that take a Connection parameter run INSIDE the caller's
   transaction. They do not open, commit or close the connection, and
   they throw SQLException so the caller can roll back.

2. Methods without a Connection parameter open and close their own
   connection (auto-commit) and wrap errors in DataAccessException.
*/
public interface AccountDao {

    // ---------- inside a transaction ----------

    /*
    Returns the generated account_id.
    */
    long insert(Connection connection, BankAccount account)
            throws SQLException;

    /*
    lockRow = true adds FOR UPDATE: the row stays locked until the
    transaction commits or rolls back. Returns null if not found.
    */
    BankAccount findByNumber(Connection connection,
                             String accountNumber,
                             boolean lockRow)
            throws SQLException;

    void debit(Connection connection, long accountId, BigDecimal amount)
            throws SQLException;

    void credit(Connection connection, long accountId, BigDecimal amount)
            throws SQLException;

    // ---------- standalone (own connection) ----------

    BankAccount findByNumber(String accountNumber);

    List<BankAccount> findAll();

    boolean updateStatus(String accountNumber, AccountStatus status);
}
