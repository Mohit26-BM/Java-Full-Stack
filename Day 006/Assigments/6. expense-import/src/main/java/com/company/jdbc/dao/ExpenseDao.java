package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.model.Expense;
import com.company.jdbc.model.ExpenseTotals;

public interface ExpenseDao {

    /*
    Inserts the expenses with addBatch() / executeBatch(), sending
    one batch every batchSize rows. Runs INSIDE the caller's
    transaction (does not commit).

    Returns the update counts of every executeBatch() call.
    */
    List<int[]> insertBatch(Connection connection,
                            List<Expense> expenses,
                            int batchSize)
            throws SQLException;

    /*
    Row count and SUM(amount) of the whole expenses table.
    */
    ExpenseTotals getTotals();

    /*
    Every expense row (with employee code), used to re-check
    the SQL report totals in Java.
    */
    List<Expense> findAll();
}
