package com.company.wallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.company.wallet.exception.DataAccessException;
import com.company.wallet.model.CustomerPortfolio;
import com.company.wallet.model.MonthlySummary;
import com.company.wallet.model.ReconciliationRow;

public class ReportDaoImpl implements ReportDao {

    /*
    Credits = DEPOSIT + TRANSFER_IN, debits = WITHDRAWAL + TRANSFER_OUT.
    */
    private static final String CREDITS =
            "COALESCE(SUM(CASE WHEN t.transaction_type IN ('DEPOSIT','TRANSFER_IN') "
          + "THEN t.amount ELSE 0 END), 0)";

    private static final String DEBITS =
            "COALESCE(SUM(CASE WHEN t.transaction_type IN ('WITHDRAWAL','TRANSFER_OUT') "
          + "THEN t.amount ELSE 0 END), 0)";

    private final DataSource dataSource;

    public ReportDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<MonthlySummary> walletMonthlySummary(long walletId) {

        String sql = "SELECT DATE_FORMAT(t.created_at, '%Y-%m') AS month, "
                   + CREDITS + " AS credits, " + DEBITS + " AS debits, "
                   + "COUNT(*) AS txn_count "
                   + "FROM wallet_transactions t "
                   + "WHERE t.wallet_id = ? "
                   + "GROUP BY month "
                   + "ORDER BY month";

        List<MonthlySummary> rows = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, walletId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    rows.add(new MonthlySummary(
                            resultSet.getString("month"),
                            null,
                            null,
                            resultSet.getBigDecimal("credits"),
                            resultSet.getBigDecimal("debits"),
                            resultSet.getLong("txn_count")
                    ));
                }
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to build wallet monthly summary", e);
        }

        return rows;
    }

    @Override
    public List<MonthlySummary> monthlyReport(YearMonth month) {

        /*
        Three-table JOIN. The month is a date RANGE so the
        (wallet_id, created_at) index can be used.
        */
        String sql = "SELECT w.wallet_number, c.full_name, "
                   + CREDITS + " AS credits, " + DEBITS + " AS debits, "
                   + "COUNT(*) AS txn_count "
                   + "FROM wallet_transactions t "
                   + "JOIN wallets w   ON t.wallet_id = w.wallet_id "
                   + "JOIN customers c ON w.customer_id = c.customer_id "
                   + "WHERE t.created_at >= ? AND t.created_at < ? "
                   + "GROUP BY w.wallet_id, w.wallet_number, c.full_name "
                   + "ORDER BY w.wallet_number";

        List<MonthlySummary> rows = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(month.atDay(1).atStartOfDay()));
            statement.setTimestamp(2, Timestamp.valueOf(month.plusMonths(1).atDay(1).atStartOfDay()));

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    rows.add(new MonthlySummary(
                            month.toString(),
                            resultSet.getString("wallet_number"),
                            resultSet.getString("full_name"),
                            resultSet.getBigDecimal("credits"),
                            resultSet.getBigDecimal("debits"),
                            resultSet.getLong("txn_count")
                    ));
                }
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to build monthly report", e);
        }

        return rows;
    }

    @Override
    public List<CustomerPortfolio> customerPortfolio() {

        /*
        LEFT JOIN so customers without wallets still appear (0 wallets).
        */
        String sql = "SELECT c.customer_id, c.full_name, "
                   + "COUNT(w.wallet_id) AS wallet_count, "
                   + "COALESCE(SUM(w.balance), 0) AS total_balance "
                   + "FROM customers c "
                   + "LEFT JOIN wallets w ON w.customer_id = c.customer_id "
                   + "GROUP BY c.customer_id, c.full_name "
                   + "ORDER BY total_balance DESC";

        List<CustomerPortfolio> rows = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                rows.add(new CustomerPortfolio(
                        resultSet.getLong("customer_id"),
                        resultSet.getString("full_name"),
                        resultSet.getInt("wallet_count"),
                        resultSet.getBigDecimal("total_balance")
                ));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to build customer portfolio", e);
        }

        return rows;
    }

    @Override
    public List<ReconciliationRow> reconciliation() {

        String sql = "SELECT w.wallet_number, w.balance, "
                   + "COALESCE(SUM(CASE WHEN t.transaction_type IN ('DEPOSIT','TRANSFER_IN') "
                   + "THEN t.amount ELSE -t.amount END), 0) AS ledger_balance "
                   + "FROM wallets w "
                   + "LEFT JOIN wallet_transactions t ON t.wallet_id = w.wallet_id "
                   + "GROUP BY w.wallet_id, w.wallet_number, w.balance "
                   + "ORDER BY w.wallet_id";

        List<ReconciliationRow> rows = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                rows.add(new ReconciliationRow(
                        resultSet.getString("wallet_number"),
                        resultSet.getBigDecimal("balance"),
                        resultSet.getBigDecimal("ledger_balance")
                ));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to reconcile wallets", e);
        }

        return rows;
    }
}
