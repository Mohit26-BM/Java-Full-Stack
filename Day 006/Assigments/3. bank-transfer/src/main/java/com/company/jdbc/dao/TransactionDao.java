package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.model.AccountTransaction;

public interface TransactionDao {

    /*
    Runs INSIDE the caller's transaction.
    Returns the generated transaction_id.
    */
    long insert(Connection connection, AccountTransaction transaction)
            throws SQLException;

    // ---------- standalone (own connection) ----------

    List<AccountTransaction> findAll();

    /*
    Transactions where the account is sender OR receiver.
    */
    List<AccountTransaction> findByAccountId(long accountId);

    int countAll();
}
