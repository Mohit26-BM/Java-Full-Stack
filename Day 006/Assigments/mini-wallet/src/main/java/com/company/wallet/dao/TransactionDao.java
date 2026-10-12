package com.company.wallet.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.wallet.model.WalletTransaction;

public interface TransactionDao {

    // ---------- inside a transaction ----------

    /*
    Returns the generated transaction_id.
    */
    long insert(Connection connection, WalletTransaction transaction) throws SQLException;

    /*
    Batch insert with addBatch()/executeBatch(). Returns the update counts.
    */
    int[] insertBatch(Connection connection, List<WalletTransaction> transactions)
            throws SQLException;

    // ---------- standalone ----------

    /*
    Newest first, with the counterparty wallet number (JOIN).
    */
    List<WalletTransaction> findByWallet(long walletId, int limit);

    long countAll();
}
