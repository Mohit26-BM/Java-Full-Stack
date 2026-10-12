package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.model.Expense;

public class BenchmarkDaoImpl implements BenchmarkDao {

    @Override
    public void clear(Connection connection) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement("TRUNCATE TABLE expenses_benchmark")) {

            statement.executeUpdate();
        }
    }

    @Override
    public void insertOneByOne(Connection connection, List<Expense> expenses)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(ExpenseSql.INSERT_BENCHMARK)) {

            for (Expense expense : expenses) {

                ExpenseSql.bind(statement, expense);

                statement.executeUpdate();
            }
        }
    }

    @Override
    public void insertInBatches(Connection connection,
                                List<Expense> expenses,
                                int batchSize)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(ExpenseSql.INSERT_BENCHMARK)) {

            int rowsInBatch = 0;

            for (Expense expense : expenses) {

                ExpenseSql.bind(statement, expense);

                statement.addBatch();
                rowsInBatch++;

                if (rowsInBatch == batchSize) {
                    statement.executeBatch();
                    rowsInBatch = 0;
                }
            }

            if (rowsInBatch > 0) {
                statement.executeBatch();
            }
        }
    }

    @Override
    public int count(Connection connection) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement("SELECT COUNT(*) FROM expenses_benchmark");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();

            return resultSet.getInt(1);
        }
    }
}
