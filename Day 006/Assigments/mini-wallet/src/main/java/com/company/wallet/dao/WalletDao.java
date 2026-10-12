package com.company.wallet.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.company.wallet.model.Wallet;
import com.company.wallet.model.WalletStatus;

public interface WalletDao {

    // ---------- inside a transaction ----------

    /*
    Inserts an ACTIVE wallet with balance 0.
    Returns the generated wallet_id (getGeneratedKeys()).
    */
    long insert(Connection connection, long customerId) throws SQLException;

    void assignWalletNumber(Connection connection, long walletId, String walletNumber)
            throws SQLException;

    /*
    forUpdate = true adds FOR UPDATE: the row stays locked until
    commit/rollback. Returns null when not found.
    */
    Wallet findByNumber(Connection connection, String walletNumber, boolean forUpdate)
            throws SQLException;

    void updateBalance(Connection connection, long walletId, BigDecimal newBalance)
            throws SQLException;

    /*
    Batch: one UPDATE per entry (walletId -> new balance),
    sent with addBatch()/executeBatch(). Returns the update counts.
    */
    int[] updateBalancesBatch(Connection connection, Map<Long, BigDecimal> newBalances)
            throws SQLException;

    // ---------- standalone ----------

    Wallet findByNumber(String walletNumber);

    List<Wallet> findByCustomer(long customerId);

    List<Wallet> findAll();

    boolean updateStatus(String walletNumber, WalletStatus status);
}
