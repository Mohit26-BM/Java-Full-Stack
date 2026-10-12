# Bank Account Transfer (JDBC Transactions)

Console application that transfers money between bank accounts as one atomic transaction: debit, credit and history insert either all commit or all roll back.

## Tech

Java 8, Maven, MySQL 8 / InnoDB (`mysql-connector-j`), JDBC transactions, generated keys, `SELECT ... FOR UPDATE`.

## Structure

```text
src/main/java/com/company/jdbc
├── config/     DBConnection (reads db.properties)
├── model/      BankAccount, AccountTransaction, AccountStatus, TransactionType
├── dao/        AccountDao, TransactionDao + JDBC implementations
├── service/    AccountService, TransferService (+ implementations)
├── exception/  custom exceptions
└── app/        BankTransferApp (main menu), ConcurrencyDemo, ConsoleInput
src/main/resources
├── schema.sql        creates the database and tables
├── sample-data.sql   6 accounts (4 ACTIVE, 1 INACTIVE, 1 BLOCKED)
└── db.properties     MySQL URL / user / password (not committed)
```

## Setup

1. Run `schema.sql`, then `sample-data.sql`, in MySQL.
2. Check the MySQL password in `src/main/resources/db.properties`.
3. In Eclipse: **File → Import → Maven → Existing Maven Projects**, select this folder.
4. Run `com.company.jdbc.app.BankTransferApp` as a Java Application.

## How the transfer works (`TransferServiceImpl`)

1. `setAutoCommit(false)` (the original value is saved and restored in `finally`)
2. Lock both account rows with `SELECT ... FOR UPDATE`, always in the same order to avoid deadlocks
3. Validate: accounts exist, both ACTIVE, amount > 0, enough balance
4. Debit source → credit destination → insert history row (generated key)
5. `commit()`. On any failure, `rollback()`
6. All `Connection`, `PreparedStatement` and `ResultSet` objects are closed by try-with-resources

## Demos

- **Rollback demo (menu 5):** debits the source, then throws a deliberate failure before the credit. The app shows that both balances and the history count are unchanged.
- **Concurrency demo (menu 8):** two threads transfer Rs. 800 from an account holding Rs. 1000 at the same time.
  - Without `FOR UPDATE`, both read the old balance of 1000, both pass the check, and the account ends at -600 (a race condition).
  - With `FOR UPDATE`, the second thread waits for the first to commit, then reads 200 and is rejected.
  - The demo creates its own `DN...` / `DL...` accounts, so the sample accounts are not touched.
