# Online Bookstore Inventory (JDBC)

Console application for bookstore staff to manage books, authors and categories, search the catalog, change stock and produce a low-stock report, using plain JDBC with MySQL.

## Tech

Java 8, Maven, MySQL 8 (`mysql-connector-j`), JDBC (`PreparedStatement`, JOIN, LIKE, batch updates).

## Structure

```text
src/main/java/com/company/jdbc
├── config/     DBConnection, AppConfig, PropertiesLoader
├── model/      Book, Author, Category, RestockRequest
├── dao/        Book/Author/Category DAOs + JDBC implementations
├── service/    Book/Author/Category services (validation, business rules)
├── exception/  custom exceptions
└── app/        BookstoreApp (main menu), ConsoleInput
src/main/resources
├── schema.sql        creates the database and tables
├── sample-data.sql   7 authors, 5 categories, 16 books
├── db.properties     MySQL URL / user / password (not committed)
└── app.properties    default low-stock threshold
```

## Setup

1. Run `schema.sql`, then `sample-data.sql`, in MySQL.
2. Check the MySQL password in `src/main/resources/db.properties`.
3. In Eclipse: **File → Import → Maven → Existing Maven Projects**, select this folder.
4. Run `com.company.jdbc.app.BookstoreApp` as a Java Application.

## Features

- CRUD for books, authors and categories
- Title search with `LIKE` (partial, parameterized)
- Search by category and/or price range
- Stock increase / decrease; stock can never become negative
- Low-stock report with a configurable threshold (`app.properties`)
- Reports use JOINs to show author and category names
- Optional challenge: bulk restock with `addBatch()` / `executeBatch()` in one transaction, printing update counts
