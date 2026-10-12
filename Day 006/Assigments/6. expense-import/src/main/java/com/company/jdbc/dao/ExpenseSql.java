package com.company.jdbc.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.company.jdbc.model.Expense;

/*
INSERT statements and parameter binding shared by
ExpenseDaoImpl (real import) and BenchmarkDaoImpl.
*/
final class ExpenseSql {

    static final String INSERT_EXPENSE =
            "INSERT INTO expenses "
          + "(employee_id, expense_date, category, amount, description) "
          + "VALUES (?, ?, ?, ?, ?)";

    static final String INSERT_BENCHMARK =
            "INSERT INTO expenses_benchmark "
          + "(employee_id, expense_date, category, amount, description) "
          + "VALUES (?, ?, ?, ?, ?)";

    private ExpenseSql() {
    }

    /*
    Sets the 5 parameters of an INSERT for one expense.
    */
    static void bind(PreparedStatement statement, Expense expense)
            throws SQLException {

        statement.setInt(1, expense.getEmployeeId());
        statement.setDate(2, Date.valueOf(expense.getExpenseDate()));
        statement.setString(3, expense.getCategory().name());
        statement.setBigDecimal(4, expense.getAmount());

        if (expense.getDescription() == null) {
            statement.setNull(5, Types.VARCHAR);
        } else {
            statement.setString(5, expense.getDescription());
        }
    }
}
