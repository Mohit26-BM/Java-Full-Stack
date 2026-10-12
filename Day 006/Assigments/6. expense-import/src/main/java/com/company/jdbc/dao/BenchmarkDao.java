package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.model.Expense;

/*
Works on the separate expenses_benchmark table only.
All methods use the caller's connection, so the caller
controls auto-commit and timing.
*/
public interface BenchmarkDao {

    void clear(Connection connection) throws SQLException;

    /*
    One executeUpdate() (one network round trip) per row.
    */
    void insertOneByOne(Connection connection, List<Expense> expenses)
            throws SQLException;

    /*
    addBatch() for every row, executeBatch() every batchSize rows.
    */
    void insertInBatches(Connection connection, List<Expense> expenses, int batchSize)
            throws SQLException;

    int count(Connection connection) throws SQLException;
}
