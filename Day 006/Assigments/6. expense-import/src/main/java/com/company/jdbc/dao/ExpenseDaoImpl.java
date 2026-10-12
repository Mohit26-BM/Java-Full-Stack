package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.Expense;
import com.company.jdbc.model.ExpenseCategory;
import com.company.jdbc.model.ExpenseTotals;

public class ExpenseDaoImpl implements ExpenseDao {

    @Override
    public List<int[]> insertBatch(Connection connection,
                                   List<Expense> expenses,
                                   int batchSize)
            throws SQLException {

        List<int[]> updateCounts = new ArrayList<>();

        /*
        ONE PreparedStatement is reused for every row:
        the SQL is parsed once, only the parameters change.
        */
        try (PreparedStatement statement =
                     connection.prepareStatement(ExpenseSql.INSERT_EXPENSE)) {

            int rowsInBatch = 0;

            for (Expense expense : expenses) {

                ExpenseSql.bind(statement, expense);

                /*
                addBatch() only queues the row in memory.
                Nothing is sent to MySQL yet.
                */
                statement.addBatch();
                rowsInBatch++;

                if (rowsInBatch == batchSize) {

                    /*
                    executeBatch() sends the queued rows together and
                    returns one update count per row (1 = inserted).
                    */
                    updateCounts.add(statement.executeBatch());
                    rowsInBatch = 0;
                }
            }

            if (rowsInBatch > 0) {
                updateCounts.add(statement.executeBatch());
            }
        }

        return updateCounts;
    }

    @Override
    public ExpenseTotals getTotals() {

        String sql = "SELECT COUNT(*), COALESCE(SUM(amount), 0) FROM expenses";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();

            return new ExpenseTotals(
                    resultSet.getLong(1),
                    resultSet.getBigDecimal(2)
            );

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read expense totals", e);
        }
    }

    @Override
    public List<Expense> findAll() {

        String sql = "SELECT x.expense_id, x.employee_id, e.employee_code, "
                   + "x.expense_date, x.category, x.amount, x.description "
                   + "FROM expenses x "
                   + "JOIN employees e ON x.employee_id = e.employee_id "
                   + "ORDER BY x.expense_id";

        List<Expense> expenses = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                BigDecimal amount = resultSet.getBigDecimal("amount");

                Expense expense = new Expense(
                        resultSet.getInt("employee_id"),
                        resultSet.getString("employee_code"),
                        resultSet.getDate("expense_date").toLocalDate(),
                        ExpenseCategory.valueOf(resultSet.getString("category")),
                        amount,
                        resultSet.getString("description")
                );

                expense.setExpenseId(resultSet.getLong("expense_id"));

                expenses.add(expense);
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read expenses", e);
        }

        return expenses;
    }
}
