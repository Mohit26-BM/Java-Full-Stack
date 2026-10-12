package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.MonthlyTotal;

/*
The totals are calculated by MySQL with GROUP BY, COUNT and SUM.

The optional month filter uses a date RANGE
    expense_date >= first day of month AND < first day of next month
instead of applying a function to the column, so an index on
expense_date could be used if one is added later.
*/
public class ReportDaoImpl implements ReportDao {

    @Override
    public List<MonthlyTotal> monthlyTotalsByEmployee(YearMonth month) {

        String sql = "SELECT DATE_FORMAT(x.expense_date, '%Y-%m') AS month, "
                   + "e.employee_code, e.employee_name, "
                   + "COUNT(*) AS expense_count, SUM(x.amount) AS total_amount "
                   + "FROM expenses x "
                   + "JOIN employees e ON x.employee_id = e.employee_id "
                   + monthFilter(month)
                   + "GROUP BY month, e.employee_id, e.employee_code, e.employee_name "
                   + "ORDER BY month, e.employee_code";

        return runReport(sql, month, true);
    }

    @Override
    public List<MonthlyTotal> monthlyTotalsByCategory(YearMonth month) {

        String sql = "SELECT DATE_FORMAT(x.expense_date, '%Y-%m') AS month, "
                   + "x.category, "
                   + "COUNT(*) AS expense_count, SUM(x.amount) AS total_amount "
                   + "FROM expenses x "
                   + monthFilter(month)
                   + "GROUP BY month, x.category "
                   + "ORDER BY month, x.category";

        return runReport(sql, month, false);
    }

    private String monthFilter(YearMonth month) {

        return month == null
                ? ""
                : "WHERE x.expense_date >= ? AND x.expense_date < ? ";
    }

    private List<MonthlyTotal> runReport(String sql,
                                         YearMonth month,
                                         boolean byEmployee) {

        List<MonthlyTotal> rows = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            if (month != null) {
                statement.setDate(1, Date.valueOf(month.atDay(1)));
                statement.setDate(2, Date.valueOf(month.plusMonths(1).atDay(1)));
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    rows.add(new MonthlyTotal(
                            resultSet.getString("month"),
                            byEmployee
                                    ? resultSet.getString("employee_code")
                                    : resultSet.getString("category"),
                            byEmployee
                                    ? resultSet.getString("employee_name")
                                    : null,
                            resultSet.getLong("expense_count"),
                            resultSet.getBigDecimal("total_amount")
                    ));
                }
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to build monthly report", e);
        }

        return rows;
    }
}
