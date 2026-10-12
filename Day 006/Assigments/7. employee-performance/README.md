# Employee Performance via Stored Procedure (JDBC)

A MySQL stored procedure calculates an employee's review summary (average rating, review count, latest period and rating band). The Java console application calls it with a `CallableStatement` and reads every OUT parameter plus the returned review rows.

## Tech

Java 8, Maven, MySQL 8 stored procedures, JDBC `CallableStatement` with IN/OUT parameters.

## Structure

```text
src/main/java/com/company/jdbc
├── config/     DBConnection (reads db.properties)
├── model/      Employee, PerformanceReview, ReviewSummary, RatingBand
├── dao/        ReviewSummaryDao (CallableStatement), ReviewDao, EmployeeDao, RatingBandDao
├── service/    PerformanceService + implementation (validation)
├── exception/  custom exceptions
└── app/        PerformanceApp (main menu), ConsoleInput
src/main/resources
├── schema.sql           database employee_performance and its tables
├── procedure.sql        CREATE PROCEDURE sp_employee_review_summary
├── sample-data.sql      8 employees (107 and 108 have no reviews), bands, 15 reviews
├── procedure-test.sql   calls the procedure from a SQL client (no Java)
└── db.properties        MySQL URL / user / password (not committed)
```

## Setup

Run the SQL files in this order (mysql client or MySQL Workbench):

1. `schema.sql`
2. `procedure.sql`. It uses `DELIMITER $$`, which both the mysql client and Workbench understand.
3. `sample-data.sql`
4. `procedure-test.sql`. Check the results against the expected values in its comments **before** running the Java app.

Then in Eclipse: **File → Import → Maven → Existing Maven Projects**, and run `com.company.jdbc.app.PerformanceApp`.

## Stored procedure contract

```sql
sp_employee_review_summary(
    IN  p_employee_id    INT,
    OUT p_average_rating DECIMAL(4,2),
    OUT p_review_count   INT,
    OUT p_latest_period  VARCHAR(20),
    OUT p_rating_band    VARCHAR(30)
)
```

| Parameter | Direction | Meaning | When the employee has no reviews |
|---|---|---|---|
| `p_employee_id` | IN | Employee to summarise | n/a |
| `p_average_rating` | OUT | `ROUND(AVG(rating), 2)` | `NULL` |
| `p_review_count` | OUT | Number of reviews | `0` |
| `p_latest_period` | OUT | `MAX(review_period)` (most recent period) | `NULL` |
| `p_rating_band` | OUT | Band name from `rating_bands` | `'NO_REVIEWS'` |

**Result set:** the procedure also returns the employee's review rows (`review_id, review_period, rating, reviewer, comments`), newest period first. The result set is empty when there are no reviews.

**Assumptions:**

- `review_period` uses the format `YYYY-Qn` (e.g. `2026-Q2`). This format sorts correctly as text, which is why `MAX(review_period)` gives the latest period. The Java app only accepts this format.
- Ratings are on a 1.00–5.00 scale.
- The procedure does not check whether the employee exists. An unknown ID gives the same output as "no reviews", so the Java service checks the `employees` table first. That table is an addition to the assignment's design.

**Calling from SQL:**

```sql
CALL sp_employee_review_summary(101, @avg, @count, @latest, @band);
SELECT @avg, @count, @latest, @band;
```

**Calling from Java** (`ReviewSummaryDaoImpl`):

1. `prepareCall("{call sp_employee_review_summary(?, ?, ?, ?, ?)}")`
2. `setInt(1, id)` for the IN parameter, then `registerOutParameter(2..5, type)` for each OUT parameter
3. `execute()`, then read the result set(s) with `getResultSet()` / `getMoreResults()`
4. Read the OUT parameters with `getBigDecimal`, `getInt` and `getString`. `wasNull()` detects the NULL average.
5. The `CallableStatement`, every `ResultSet` and the `Connection` are closed by try-with-resources.

## Rating bands (optional challenge)

The band is calculated inside the procedure from the configurable `rating_bands` table.

**Rule:** an employee's band is the one with the **highest `min_rating` that is ≤ their average rating**. With no reviews, the band is `NO_REVIEWS`. If no band matches, it is `UNCLASSIFIED`.

| Band | Average rating |
|---|---|
| OUTSTANDING | ≥ 4.50 |
| EXCEEDS_EXPECTATIONS | ≥ 3.75 and < 4.50 |
| MEETS_EXPECTATIONS | ≥ 3.00 and < 3.75 |
| NEEDS_IMPROVEMENT | ≥ 2.00 and < 3.00 |
| UNSATISFACTORY | < 2.00 |
| NO_REVIEWS | review count = 0 |

- Thresholds can be changed with `UPDATE rating_bands ...` or from the app (menu 5) **without changing the procedure**. The next call uses the new values.
- Each threshold must be unique (enforced by a `UNIQUE` constraint).
- Bands are compared against the **rounded** average (2 decimals). A 3.75 average is EXCEEDS_EXPECTATIONS, as employee 106 shows.

## Acceptance checks

| Check | How |
|---|---|
| Procedure works from a SQL client first | `procedure-test.sql` (expected values are in its comments) |
| Java receives every OUT parameter | Menu 2 prints all four OUT values exactly as received |
| No-review case handled | Employee 107 or 108 → average NULL, count 0, latest NULL, band NO_REVIEWS |
| CallableStatement closed correctly | try-with-resources in `ReviewSummaryDaoImpl` |
