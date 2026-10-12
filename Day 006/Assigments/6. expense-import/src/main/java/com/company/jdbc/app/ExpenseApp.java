package com.company.jdbc.app;

import java.math.BigDecimal;
import java.sql.Statement;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.company.jdbc.dao.BenchmarkDaoImpl;
import com.company.jdbc.dao.EmployeeDao;
import com.company.jdbc.dao.EmployeeDaoImpl;
import com.company.jdbc.dao.ExpenseDao;
import com.company.jdbc.dao.ExpenseDaoImpl;
import com.company.jdbc.dao.ReportDaoImpl;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.ImportFailedException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.BenchmarkResult;
import com.company.jdbc.model.Employee;
import com.company.jdbc.model.ImportError;
import com.company.jdbc.model.ImportResult;
import com.company.jdbc.model.MonthlyTotal;
import com.company.jdbc.model.ReconciliationResult;
import com.company.jdbc.service.BenchmarkService;
import com.company.jdbc.service.BenchmarkServiceImpl;
import com.company.jdbc.service.ExpenseImportService;
import com.company.jdbc.service.ExpenseImportServiceImpl;
import com.company.jdbc.service.ReportService;
import com.company.jdbc.service.ReportServiceImpl;

public class ExpenseApp {

    private static final String LINE =
            "------------------------------------------------------------------------";

    private static ExpenseImportService importService;
    private static ReportService reportService;
    private static BenchmarkService benchmarkService;

    public static void main(String[] args) {

        /*
        Manual dependency injection: DAO -> Service.
        */
        EmployeeDao employeeDao = new EmployeeDaoImpl();
        ExpenseDao expenseDao = new ExpenseDaoImpl();

        importService = new ExpenseImportServiceImpl(employeeDao, expenseDao);
        reportService = new ReportServiceImpl(new ReportDaoImpl(), expenseDao);
        benchmarkService = new BenchmarkServiceImpl(new BenchmarkDaoImpl());

        while (true) {

            showMenu();

            int choice = ConsoleInput.readPositiveInt("Enter your choice: ");

            if (choice == 7) {
                System.out.println("\nThank you for using Expense Import and Reporting.");
                return;
            }

            try {

                handleChoice(choice);

            } catch (DataAccessException e) {

                System.out.println("\nDatabase Error: " + e.getMessage());

                if (e.getCause() != null) {
                    System.out.println("Cause: " + e.getCause().getMessage());
                }

            } catch (IllegalStateException e) {

                System.out.println("\nConfiguration Error: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {

        System.out.println("\n========================================");
        System.out.println("   EXPENSE IMPORT AND MONTHLY REPORTING");
        System.out.println("========================================");
        System.out.println("1. View Employees");
        System.out.println("2. Import Expenses from CSV");
        System.out.println("3. Monthly Total by Employee");
        System.out.println("4. Monthly Total by Category");
        System.out.println("5. Verify Reports against Raw Data");
        System.out.println("6. Benchmark: Batch vs One-Row-at-a-Time");
        System.out.println("7. Exit");
        System.out.println("========================================");
    }

    private static void handleChoice(int choice) {

        switch (choice) {
            case 1: viewEmployees();        break;
            case 2: importExpenses();       break;
            case 3: monthlyReport(true);    break;
            case 4: monthlyReport(false);   break;
            case 5: verifyReports();        break;
            case 6: runBenchmark();         break;
            default:
                System.out.println("\nInvalid choice. Please select 1 to 7.");
        }
    }

    // =========================================================
    // EMPLOYEES
    // =========================================================

    private static void viewEmployees() {

        System.out.println("\n========== Employees ==========");
        System.out.println(String.format("%-4s %-8s %s", "ID", "Code", "Name"));

        for (Employee employee : importService.getEmployees()) {
            System.out.println(employee);
        }
    }

    // =========================================================
    // IMPORT
    // =========================================================

    private static void importExpenses() {

        System.out.println("\n========== Import Expenses ==========");
        System.out.println("Format: employee_code,expense_date(yyyy-MM-dd),category,amount,description");

        String path = ConsoleInput.readLine(
                "CSV file path (Enter for the bundled sample expenses.csv): ");

        try {

            ImportResult result = importService.importCsv(path.isEmpty() ? null : path);

            printImportResult(result);

        } catch (InvalidInputException e) {

            System.out.println("\nError: " + e.getMessage());

        } catch (ImportFailedException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void printImportResult(ImportResult result) {

        int validCount = result.getValidRows().size();
        int invalidCount = result.getErrors().size();

        System.out.println("\nRows read     : " + result.getDataRowCount());
        System.out.println("Valid rows    : " + validCount);
        System.out.println("Invalid rows  : " + invalidCount + " (skipped, not inserted)");

        // ---------- invalid rows ----------

        if (invalidCount > 0) {

            System.out.println("\nINVALID ROWS");
            System.out.println(LINE);

            for (ImportError error : result.getErrors()) {

                System.out.println("Line " + error.getLineNumber() + ": " + error.getRawLine());
                System.out.println("   -> " + error.getReason());
            }

            System.out.println(LINE);
        }

        // ---------- batch update counts ----------

        if (validCount == 0) {
            System.out.println("\nNo valid rows to insert.");
            return;
        }

        System.out.println("\nBATCH UPDATE COUNTS (executeBatch every "
                + importService.getBatchSize() + " rows)");

        int batchNumber = 1;
        int rowsReported = 0;

        for (int[] counts : result.getBatchUpdateCounts()) {

            StringBuilder text = new StringBuilder();
            int inserted = 0;

            for (int count : counts) {

                text.append(describeCount(count)).append(' ');

                if (count == 1 || count == Statement.SUCCESS_NO_INFO) {
                    inserted++;
                }
            }

            rowsReported += inserted;

            System.out.println("Batch " + batchNumber++ + " (" + counts.length + " rows): "
                    + text.toString().trim());
        }

        System.out.println("Rows reported inserted by executeBatch(): " + rowsReported);

        // ---------- verification ----------

        long rowsAdded = result.getTotalsAfter().getRowCount()
                - result.getTotalsBefore().getRowCount();

        BigDecimal amountAdded = result.getTotalsAfter().getTotalAmount()
                .subtract(result.getTotalsBefore().getTotalAmount());

        boolean rowsOk = rowsAdded == validCount;
        boolean amountOk = amountAdded.compareTo(result.getValidAmountTotal()) == 0;

        System.out.println("\nChecks:");
        System.out.println((rowsOk ? "[OK] " : "[PROBLEM] ")
                + "expenses table grew by " + rowsAdded
                + " rows (valid rows in file: " + validCount + ")");
        System.out.println((amountOk ? "[OK] " : "[PROBLEM] ")
                + "SUM(amount) grew by Rs. " + amountAdded
                + " (valid amounts in file: Rs. " + result.getValidAmountTotal() + ")");
    }

    private static String describeCount(int count) {

        if (count == Statement.SUCCESS_NO_INFO) {
            return "OK";
        }

        if (count == Statement.EXECUTE_FAILED) {
            return "FAILED";
        }

        return String.valueOf(count);
    }

    // =========================================================
    // REPORTS
    // =========================================================

    private static void monthlyReport(boolean byEmployee) {

        System.out.println(byEmployee
                ? "\n========== Monthly Total by Employee =========="
                : "\n========== Monthly Total by Category ==========");

        YearMonth month;

        try {
            month = readOptionalMonth();
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        List<MonthlyTotal> rows = byEmployee
                ? reportService.monthlyTotalsByEmployee(month)
                : reportService.monthlyTotalsByCategory(month);

        if (rows.isEmpty()) {
            System.out.println("No expenses found. Import the CSV first (option 2).");
            return;
        }

        System.out.println(LINE);

        if (byEmployee) {
            System.out.println(String.format("%-8s %-8s %-20s %6s %16s",
                    "Month", "Code", "Employee", "Count", "Total"));
        } else {
            System.out.println(String.format("%-8s %-20s %6s %16s",
                    "Month", "Category", "Count", "Total"));
        }

        System.out.println(LINE);

        String currentMonth = null;
        BigDecimal monthTotal = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (MonthlyTotal row : rows) {

            if (currentMonth != null && !currentMonth.equals(row.getMonth())) {
                printMonthSubtotal(currentMonth, monthTotal, byEmployee);
                monthTotal = BigDecimal.ZERO;
            }

            currentMonth = row.getMonth();
            monthTotal = monthTotal.add(row.getTotalAmount());
            grandTotal = grandTotal.add(row.getTotalAmount());

            if (byEmployee) {
                System.out.println(String.format("%-8s %-8s %-20.20s %6d %16s",
                        row.getMonth(), row.getGroupKey(), row.getGroupLabel(),
                        row.getExpenseCount(), "Rs. " + row.getTotalAmount()));
            } else {
                System.out.println(String.format("%-8s %-20s %6d %16s",
                        row.getMonth(), row.getGroupKey(),
                        row.getExpenseCount(), "Rs. " + row.getTotalAmount()));
            }
        }

        printMonthSubtotal(currentMonth, monthTotal, byEmployee);

        System.out.println(LINE);
        System.out.println(String.format(byEmployee ? "%-45s %16s" : "%-36s %16s",
                "GRAND TOTAL", "Rs. " + grandTotal));
    }

    private static void printMonthSubtotal(String month, BigDecimal total, boolean byEmployee) {

        System.out.println(String.format(byEmployee ? "%45s %16s" : "%36s %16s",
                "Subtotal " + month + ":", "Rs. " + total));
    }

    private static YearMonth readOptionalMonth() throws InvalidInputException {

        String input = ConsoleInput.readLine("Month (yyyy-MM, Enter for all months): ");

        if (input.isEmpty()) {
            return null;
        }

        try {
            return YearMonth.parse(input);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Month must look like 2026-09.");
        }
    }

    private static void verifyReports() {

        System.out.println("\n========== Verify Reports ==========");
        System.out.println("Re-calculating every monthly total in Java from the raw rows");
        System.out.println("and comparing with the SQL GROUP BY results...");

        ReconciliationResult result = reportService.verifyReports();

        System.out.println("\nRaw expense rows read     : " + result.getRawRowCount());
        System.out.println("Employee-month groups     : " + result.getEmployeeGroupsChecked());
        System.out.println("Category-month groups     : " + result.getCategoryGroupsChecked());

        if (result.isMatching()) {
            System.out.println("[OK] Every SQL total and count matches the raw data.");
        } else {
            System.out.println("[PROBLEM] " + result.getMismatches().size() + " mismatch(es):");

            for (String mismatch : result.getMismatches()) {
                System.out.println("  " + mismatch);
            }
        }
    }

    // =========================================================
    // BENCHMARK
    // =========================================================

    private static void runBenchmark() {

        System.out.println("\n========== Benchmark ==========");
        System.out.println("Inserts generated rows into the separate expenses_benchmark table");
        System.out.println("(your real expenses and reports are not touched).");

        int rows = ConsoleInput.readPositiveInt("Number of rows (e.g. 2000): ");

        if (rows < 1000) {
            System.out.println("Note: the assignment asks for 1000+ rows; small numbers "
                    + "show smaller differences.");
        }

        System.out.println("\nRunning 4 strategies with " + rows + " rows each. "
                + "The first one can take a while...");

        List<BenchmarkResult> results = benchmarkService.run(rows);

        System.out.println(LINE);
        System.out.println(String.format("%-40s %8s %10s %10s",
                "Strategy", "Rows", "Time (ms)", "Rows/sec"));
        System.out.println(LINE);

        long slowest = 1;

        for (BenchmarkResult result : results) {
            slowest = Math.max(slowest, result.getElapsedMillis());
        }

        for (BenchmarkResult result : results) {

            System.out.println(String.format("%-40s %8d %10d %10d",
                    result.getStrategy(),
                    result.getRowsInserted(),
                    result.getElapsedMillis(),
                    result.getRowsPerSecond()));
        }

        System.out.println(LINE);

        for (BenchmarkResult result : results) {

            long millis = Math.max(1, result.getElapsedMillis());

            System.out.println(String.format("%-40s %6.1fx faster than the slowest",
                    result.getStrategy(), (double) slowest / millis));
        }

        System.out.println("\nWrite these numbers into the README table "
                + "(timings depend on your machine).");
    }
}
