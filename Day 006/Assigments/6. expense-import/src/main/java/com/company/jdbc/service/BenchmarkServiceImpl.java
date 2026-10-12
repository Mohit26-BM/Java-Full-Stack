package com.company.jdbc.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.dao.BenchmarkDao;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.BenchmarkResult;
import com.company.jdbc.model.Expense;
import com.company.jdbc.model.ExpenseCategory;

/*
OPTIONAL CHALLENGE: batch insertion vs one-row-at-a-time.

The same generated rows are inserted into expenses_benchmark
with four strategies:

  1. One row at a time, auto-commit ON
       every INSERT is a network round trip AND its own commit
       (MySQL flushes to disk on every commit) -> slowest

  2. One row at a time, one transaction
       still one round trip per row, but only one commit

  3. addBatch() / executeBatch(), one transaction
       rows are grouped into far fewer round trips

  4. Same as 3 with rewriteBatchedStatements=true
       the driver rewrites each batch into a single multi-row
       INSERT ... VALUES (...), (...), ... -> usually fastest

The table is emptied (TRUNCATE) before each run, outside the timing.
*/
public class BenchmarkServiceImpl implements BenchmarkService {

    private static final int BENCHMARK_BATCH_SIZE = 500;

    private final BenchmarkDao benchmarkDao;

    public BenchmarkServiceImpl(BenchmarkDao benchmarkDao) {
        this.benchmarkDao = benchmarkDao;
    }

    @Override
    public List<BenchmarkResult> run(int rowCount) {

        List<Expense> rows = generateRows(rowCount);

        List<BenchmarkResult> results = new ArrayList<>();

        results.add(runStrategy("1. One row at a time, auto-commit ON",
                rows, false, true, false));

        results.add(runStrategy("2. One row at a time, one transaction",
                rows, false, false, false));

        results.add(runStrategy("3. Batch (" + BENCHMARK_BATCH_SIZE + "), one transaction",
                rows, true, false, false));

        results.add(runStrategy("4. Batch + rewriteBatchedStatements",
                rows, true, false, true));

        return results;
    }

    private BenchmarkResult runStrategy(String name,
                                        List<Expense> rows,
                                        boolean useBatch,
                                        boolean autoCommit,
                                        boolean rewriteBatched) {

        try (Connection connection = DBConnection.getConnection(rewriteBatched)) {

            benchmarkDao.clear(connection);

            connection.setAutoCommit(autoCommit);

            long start = System.nanoTime();

            try {

                if (useBatch) {
                    benchmarkDao.insertInBatches(connection, rows, BENCHMARK_BATCH_SIZE);
                } else {
                    benchmarkDao.insertOneByOne(connection, rows);
                }

                if (!autoCommit) {
                    connection.commit();
                }

            } catch (SQLException | RuntimeException e) {

                if (!autoCommit) {
                    connection.rollback();
                }

                throw e;
            }

            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

            connection.setAutoCommit(true);

            /*
            Sanity check: every strategy must insert exactly rowCount rows.
            */
            int inserted = benchmarkDao.count(connection);

            return new BenchmarkResult(name, inserted, elapsedMillis);

        } catch (SQLException e) {

            throw new DataAccessException("Benchmark failed: " + name, e);
        }
    }

    /*
    Deterministic test data: employees 1-6, dates in 2026,
    all categories, amounts between 100 and 5099.
    */
    private List<Expense> generateRows(int rowCount) {

        List<Expense> rows = new ArrayList<>(rowCount);

        ExpenseCategory[] categories = ExpenseCategory.values();
        LocalDate firstDay = LocalDate.of(2026, 1, 1);

        for (int i = 0; i < rowCount; i++) {

            rows.add(new Expense(
                    (i % 6) + 1,
                    null,
                    firstDay.plusDays(i % 270),
                    categories[i % categories.length],
                    BigDecimal.valueOf(100 + (i * 37) % 5000, 0).setScale(2),
                    "Benchmark row " + (i + 1)
            ));
        }

        return rows;
    }
}
