# Expense Import and Monthly Reporting (JDBC)

Console application that imports employee expenses from a CSV file, validates every row, inserts the valid rows with JDBC batch processing, and produces monthly totals by employee and by category.

## Tech

Java 8, Maven, MySQL 8 (`mysql-connector-j`), JDBC `addBatch()` / `executeBatch()`, transactions, `GROUP BY` aggregation.

## Structure

```text
src/main/java/com/company/jdbc
├── config/     DBConnection (reads db.properties)
├── model/      Employee, Expense, ExpenseCategory, ImportResult, ImportError,
│               MonthlyTotal, ExpenseTotals, ReconciliationResult, BenchmarkResult
├── dao/        EmployeeDao, ExpenseDao, ReportDao, BenchmarkDao + implementations
├── service/    ExpenseImportService, ExpenseRowValidator, ReportService, BenchmarkService
├── exception/  custom exceptions
└── app/        ExpenseApp (main menu), ConsoleInput
src/main/resources
├── schema.sql        creates database expense_reporting and its tables
├── sample-data.sql   6 employees (EMP001-EMP006)
├── expenses.csv      44 expense rows: 32 valid, 12 deliberately invalid
└── db.properties     MySQL URL / user / password (not committed)
```

## Setup

1. Run `schema.sql`, then `sample-data.sql`, in MySQL.
2. In Eclipse: **File → Import → Maven → Existing Maven Projects**, select this folder.
3. Run `com.company.jdbc.app.ExpenseApp` and choose option 2. Press Enter to import the bundled `expenses.csv`.

## CSV format

```text
employee_code,expense_date,category,amount,description
EMP001,2026-08-04,TRAVEL,1850.00,Cab to client site Pune
```

- **Employee code:** must exist in `employees` (case-insensitive).
- **Date:** a real date in `yyyy-MM-dd`, not in the future.
- **Category:** one of TRAVEL, MEALS, ACCOMMODATION, OFFICE_SUPPLIES, TRAINING, COMMUNICATION (case-insensitive).
- **Amount:** a number greater than 0, with at most 2 decimals.
- **Description:** optional, up to 255 characters. It may contain commas, because everything after the 4th comma is treated as the description. Quoted values are not supported.

## How invalid rows are handled ("skip and report")

1. Every row is validated in Java before anything is sent to MySQL.
2. Invalid rows are **not inserted**. Each one is reported with its line number, the original text and every reason it failed.
3. All valid rows are inserted with `addBatch()` / `executeBatch()` (one `executeBatch()` every 10 rows) inside **one transaction**. On success it commits. On any database error it rolls back, so no row from that file is saved.

A file is therefore either fully imported (minus the reported bad rows) or not imported at all. It is never half-imported.

Importing the same file twice inserts its rows twice. To start over, run `schema.sql` and `sample-data.sql` again.

## Acceptance checks built into the app

- **Valid rows inserted correctly:** after the import, the table row count and `SUM(amount)` must grow by exactly the number and total of valid rows (32 rows, Rs. 71934.25 for the sample file).
- **Invalid rows reported clearly:** printed with line number and reason.
- **Batch update counts displayed:** one line per `executeBatch()`.
- **Aggregation matches data:** option 5 recomputes every monthly total in Java from the raw rows and compares it with the SQL `GROUP BY` results.

## Optional challenge: batch vs one-row-at-a-time (option 6)

Inserts N generated rows into the separate `expenses_benchmark` table with four strategies:

| Strategy | What happens |
|---|---|
| 1. One row at a time, auto-commit ON | one network round trip and one commit (disk flush) per row |
| 2. One row at a time, one transaction | one round trip per row, a single commit |
| 3. Batch of 500, one transaction | rows are sent in groups, far fewer round trips |
| 4. Batch + `rewriteBatchedStatements=true` | the driver turns each batch into one multi-row `INSERT` |

Observed results (fill in after running with 2000 rows on your machine):

| Strategy | Time (ms) | Rows/sec |
|---|---|---|
| 1 | | |
| 2 | | |
| 3 | | |
| 4 | | |

Expected pattern: 1 is by far the slowest, because each commit waits for the disk. 2 and 3 are much faster. 4 is usually the fastest. Plain batching helps less on MySQL than you might expect unless `rewriteBatchedStatements` is enabled.
