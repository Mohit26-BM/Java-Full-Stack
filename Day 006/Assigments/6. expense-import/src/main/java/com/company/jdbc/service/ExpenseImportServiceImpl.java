package com.company.jdbc.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.dao.EmployeeDao;
import com.company.jdbc.dao.ExpenseDao;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.ImportFailedException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Employee;
import com.company.jdbc.model.Expense;
import com.company.jdbc.model.ExpenseTotals;
import com.company.jdbc.model.ImportError;
import com.company.jdbc.model.ImportResult;

/*
HOW INVALID ROWS ARE HANDLED  (decision: "skip and report")
-----------------------------------------------------------
1. Every row is validated in Java BEFORE anything is sent to MySQL.
2. Invalid rows are NOT inserted. Each one is reported with its line
   number, the original text and every reason it failed.
3. All valid rows are inserted with addBatch()/executeBatch() inside
   ONE transaction:
     - success      -> COMMIT (all valid rows saved)
     - any DB error -> ROLLBACK (no row from this file is saved)
   So a file is either fully imported (minus the reported bad rows)
   or not imported at all - never half-imported.

Why not reject the whole file when one row is bad?
Finance usually wants the good expenses processed now and the few
bad lines sent back for correction. Rejecting everything would block
all employees because of one typo.

Note: importing the same file twice inserts its rows twice.
Duplicate detection is outside the scope of this exercise.
*/
public class ExpenseImportServiceImpl implements ExpenseImportService {

    /*
    executeBatch() is called every 10 rows, so the sample file shows
    several batches and their update counts. Real imports would use
    a larger size such as 500 or 1000.
    */
    private static final int BATCH_SIZE = 10;

    private static final String SAMPLE_FILE = "expenses.csv";

    private final EmployeeDao employeeDao;
    private final ExpenseDao expenseDao;

    public ExpenseImportServiceImpl(EmployeeDao employeeDao, ExpenseDao expenseDao) {
        this.employeeDao = employeeDao;
        this.expenseDao = expenseDao;
    }

    @Override
    public ImportResult importCsv(String filePath)
            throws InvalidInputException,
                   ImportFailedException {

        List<String> lines = readLines(filePath);

        // ---------- validate every row ----------

        ExpenseRowValidator validator =
                new ExpenseRowValidator(loadEmployeesByCode(), LocalDate.now());

        List<Expense> validRows = new ArrayList<>();
        List<ImportError> errors = new ArrayList<>();
        int dataRowCount = 0;

        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i);
            int lineNumber = i + 1;

            if (isSkippable(line, i)) {
                continue;
            }

            dataRowCount++;

            List<String> problems = new ArrayList<>();
            Expense expense = validator.validate(line, problems);

            if (expense == null) {
                errors.add(new ImportError(lineNumber, line, String.join("; ", problems)));
            } else {
                validRows.add(expense);
            }
        }

        if (dataRowCount == 0) {
            throw new InvalidInputException("The file contains no data rows.");
        }

        // ---------- insert valid rows in one transaction ----------

        ExpenseTotals before = expenseDao.getTotals();

        List<int[]> updateCounts = validRows.isEmpty()
                ? Collections.<int[]>emptyList()
                : insertInOneTransaction(validRows);

        ExpenseTotals after = expenseDao.getTotals();

        return new ImportResult(dataRowCount, validRows, errors, updateCounts, before, after);
    }

    private List<int[]> insertInOneTransaction(List<Expense> validRows)
            throws ImportFailedException {

        try (Connection connection = DBConnection.getConnection()) {

            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {

                List<int[]> updateCounts =
                        expenseDao.insertBatch(connection, validRows, BATCH_SIZE);

                connection.commit();

                return updateCounts;

            } catch (SQLException | RuntimeException e) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw new ImportFailedException(
                        "Import failed and was ROLLED BACK. No rows were saved. "
                        + "Reason: " + e.getMessage(),
                        e
                );

            } finally {

                try {
                    connection.setAutoCommit(originalAutoCommit);
                } catch (SQLException ignored) {
                    // The connection is closed right after this anyway.
                }
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to connect to the database", e);
        }
    }

    @Override
    public List<Employee> getEmployees() {
        return employeeDao.findAll();
    }

    @Override
    public int getBatchSize() {
        return BATCH_SIZE;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /*
    Blank lines, comment lines (#) and the header line are skipped
    and not counted as data rows.
    */
    private boolean isSkippable(String line, int index) {

        String trimmed = line.trim();

        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return true;
        }

        return index == 0 && trimmed.toLowerCase().startsWith("employee_code");
    }

    private Map<String, Employee> loadEmployeesByCode() {

        Map<String, Employee> byCode = new HashMap<>();

        for (Employee employee : employeeDao.findAll()) {
            byCode.put(employee.getEmployeeCode().toUpperCase(), employee);
        }

        return byCode;
    }

    private List<String> readLines(String filePath)
            throws InvalidInputException {

        try (InputStream input = openSource(filePath);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(input, StandardCharsets.UTF_8))) {

            List<String> lines = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            return lines;

        } catch (FileNotFoundException e) {

            throw new InvalidInputException("File not found: " + filePath);

        } catch (IOException e) {

            throw new InvalidInputException("Unable to read file: " + e.getMessage());
        }
    }

    private InputStream openSource(String filePath)
            throws IOException {

        if (filePath != null) {
            return new FileInputStream(filePath);
        }

        InputStream sample =
                getClass().getClassLoader().getResourceAsStream(SAMPLE_FILE);

        if (sample == null) {
            throw new FileNotFoundException(SAMPLE_FILE + " (classpath)");
        }

        return sample;
    }
}
