# Mini Wallet Platform (JDBC Capstone)

A console wallet/banking platform that combines the JDBC concepts from the lab:
- connection pooling (DataSource)
- PreparedStatement and generated keys
- atomic multi-statement transactions and row locking
- batch processing and JOIN-based reporting
- a layered DAO/service design with centralized exception handling

## Tech

Java 8 · Maven · MySQL 8.0.16+ · JDBC · HikariCP 4.0.3 (connection pool) · SLF4J

## Setup and execution

1. **Create the database.** In the MySQL client or Workbench, run in this order:
   - `src/main/resources/schema.sql` creates database `mini_wallet` and its 3 tables
   - `src/main/resources/sample-data.sql` adds 5 customers, 6 wallets and 16 ledger rows
2. **Credentials.** Set your MySQL user and password in `src/main/resources/db.properties`. This file is git-ignored.
3. **Import into Eclipse.** Use **File → Import → Maven → Existing Maven Projects** and select the `mini-wallet` folder. Eclipse downloads the dependencies.
4. **Build.** Right-click the project → **Run As → Maven install**, or run `mvn clean package` from a terminal. The build needs no database.
5. **Run.** Run `com.company.wallet.app.WalletApp` as a Java Application.
6. **Test evidence.** Choose menu **17** to run every mandatory demonstration. It prints `[PASS]`/`[FAIL]` for each check.

To reset everything, run `schema.sql` and `sample-data.sql` again.

## Architecture

```text
src/main/java/com/company/wallet
├── config/      DatabaseConfig - builds the HikariCP pool from db.properties + pool.properties.
│                The ONLY class that sees the URL/password.
├── model/       Customer, Wallet, WalletTransaction, enums, report rows
├── exception/   WalletException (base) + 7 business exceptions, DataAccessException
├── dao/         CustomerDao, WalletDao, TransactionDao, ReportDao (+Impl) - SQL only
├── service/     TransactionManager - commit/rollback in ONE place
│                CustomerService, WalletService, ReportService (+Impl) - business rules
│                InputRules - all validation; BulkCreditFileReader - CSV parsing
└── app/         WalletApp (menu), AppContext (wiring), ErrorHandler (central error handling),
                 DemoRunner (mandatory demos), ConcurrencyExperiment, Printer, ConsoleInput
src/main/resources
├── schema.sql, sample-data.sql, bulk-credits.csv
├── db.properties (credentials, git-ignored), pool.properties, simplelogger.properties
```

**Flow of a request:** `WalletApp` → service (validate → `TransactionManager.inTransaction(...)`) → DAOs (`PreparedStatement` on the transaction's connection) → MySQL.

**Database design (`schema.sql`):**
- `customers` has unique email and phone, with named constraints so duplicates can be reported precisely.
- `wallets` has `CHECK (balance >= 0)` as a last safety net.
- `wallet_transactions` is a ledger with one row per balance change. A transfer writes `TRANSFER_OUT` and `TRANSFER_IN` rows that share one reference, and each row stores `balance_after`.

## How each requirement is met

| Requirement | Where |
|---|---|
| Customer registration and lookup | `CustomerServiceImpl.register / findById / findByEmail` (menu 1–3) |
| Wallet creation | `WalletServiceImpl.createWallet`. `getGeneratedKeys()` gives `wallet_id`, which becomes the `WAL0000007`-style number in the same transaction (menu 4) |
| Deposit and withdrawal | `deposit` / `withdraw`: lock the row, update the balance, insert a ledger row (menu 8, 9) |
| Wallet-to-wallet transfer | `executeTransfer`: 2 row locks, 2 balance updates, 2 ledger rows, one commit (menu 10) |
| History and monthly summary | `TransactionDao.findByWallet` (LEFT JOIN for the counterparty) and `ReportDao.walletMonthlySummary` (menu 12, 13) |
| Atomic multi-statement transactions | `TransactionManager`: auto-commit off, commit on success, rollback on any failure, auto-commit restored |
| PreparedStatement throughout | Every SQL statement. No `Statement`/string-built values (only fixed SQL fragments are concatenated) |
| DAO + service layers | `dao/` contains only SQL; `service/` contains only rules and transactions; `app/` contains only I/O |
| Centralized exception handling | Every business error extends `WalletException`. Menu actions do not catch; `ErrorHandler.handle()` reports all errors in one place |
| Batch operation | `bulkCredit`: `addBatch`/`executeBatch` for the balance UPDATEs and the ledger INSERTs, in one transaction (menu 11) |
| Reporting JOINs | Monthly report (3-table JOIN), customer portfolio (LEFT JOIN), reconciliation (menu 14–16) |
| Connection pooling | HikariCP, 5 connections. Every DAO/transaction borrows with try-with-resources, so connections are always returned |

## Mandatory demonstrations (menu 17 runs them all)

| # | Demonstration | Expected result |
|---|---|---|
| 1 | Register 2 customers, create 2 wallets | Generated IDs; wallet numbers built from generated keys |
| 2 | Duplicate data | Same email → `DuplicateCustomerException`; same phone → `DuplicateCustomerException` |
| 3 | Successful transfer of Rs. 1500 | 5000/1000 → 3500/2500; both ledger rows share one reference |
| 4 | Insufficient funds (Rs. 100000) | `InsufficientFundsException`; balances unchanged |
| 5 | Invalid destination | Unknown wallet → `WalletNotFoundException`; same wallet → `ValidationException`; BLOCKED wallet → `WalletInactiveException`; balances unchanged |
| 6 | Forced failure after the debit | `TransactionFailedException`; balances **and** ledger row count unchanged (rollback) |
| 7 | Batch bulk credit | Update counts printed; balances +150 / +200. A batch containing an unknown wallet changes nothing |
| 8 | JOIN report | Monthly report shows customer names; net change = balance; ledger reconciles |
| 9 | Concurrency experiment | See below |
| 10 | Resources | 0 active pool connections at the end |

The runner creates fresh customers and wallets each time, using unique emails and phones, so it can be run repeatedly without touching the sample data.

Individual demos are also available: menu **18** runs a forced-failure transfer on wallets you choose, and menu **19** runs the concurrency experiment.

## Concurrency experiment (menu 19, also part of 17)

**Question:** what happens when two requests update the same row at the same time?

**Setup:**
- A new wallet holds Rs. 1000.
- Two threads each withdraw Rs. 800 at the same moment.
- Each thread pauses 1.5 s between *reading* the balance and *writing* the new one, so the two threads always overlap.

| Run | What happens | Result |
|---|---|---|
| **Without a row lock** (plain `SELECT`) | Both threads read 1000 and pass the funds check. T1 writes balance = 200 and commits; T2 also writes 200 and commits, overwriting T1's update. | **Lost update:** two withdrawals (Rs. 1600) are in the ledger, but the balance is 200. Reconciliation shows the wallet as `MISMATCH`. |
| **With `SELECT ... FOR UPDATE`** | T1 reads and **locks** the row. T2's read **waits** until T1 commits, then sees the real balance of 200. | T2 gets `InsufficientFundsException`. One withdrawal, balance 200, ledger matches. |

**Conclusion:** a read-check-write sequence must lock the row it is going to change; otherwise another transaction can slip in between the check and the write. The real operations in this app always lock: `deposit`, `withdraw`, `transfer` and `bulkCredit` all use `FOR UPDATE`. Transfers lock both wallets in a fixed order (by wallet number) so that A→B and B→A running together cannot deadlock.

The "without lock" wallets created by this experiment stay mismatched on purpose. Menu 16 (Reconciliation) flags them, which is exactly the kind of problem reconciliation exists to find.

## Test evidence

Run menu 17 and paste the console output here:

```text
(paste the output of "Run ALL mandatory demonstrations" here)
```

## Submission checklist

| Item | Status |
|---|---|
| Maven project builds successfully | Standard Maven layout, Java 8, no tests requiring a DB |
| `schema.sql` and `sample-data.sql` included | `src/main/resources/` |
| README explains setup and execution | This file |
| No hard-coded credentials in business classes | Only `db.properties` (git-ignored), read by `config/DatabaseConfig` |
| PreparedStatement and try-with-resources | All DAOs and `TransactionManager` |
| Transactions use commit/rollback | `TransactionManager` |
| Test evidence: success and failure cases | Menu 17 output (see above) |
| Logical layers | config / model / exception / dao / service / app |

## Trainer discussion questions

**Why is PreparedStatement preferred for user input?**
Values are sent separately from the SQL text, so input such as `' OR '1'='1` is treated as data, never as SQL (no SQL injection). The SQL is also parsed once and reused, and types are handled correctly (dates, decimals, NULL).

**What changes when auto-commit is disabled?**
Statements no longer become permanent one by one. Nothing is visible to other connections, or survives a crash, until `commit()`. `rollback()` undoes everything since the last commit, and row locks are held until commit or rollback. The original setting must be restored before a pooled connection is reused.

**When should multiple SQL statements belong to one transaction?**
When they form one business action that must be all-or-nothing. A transfer debits, credits and writes two ledger rows; doing only some of those would create or destroy money.

**What does `getGeneratedKeys()` solve?**
It returns the AUTO_INCREMENT ID of the row just inserted, on the same connection, with no race. Here it gives the new `wallet_id` (turned into the wallet number) and new IDs for customers and ledger rows. The alternative, `SELECT MAX(id)`, is wrong when two inserts happen at once.

**Why is batch processing useful?**
Many statements go to the database in fewer round trips, which is much faster for large volumes, and the batch can be committed as one unit. The bulk credit sends all balance updates and all ledger rows as two batches.

**When is CallableStatement appropriate?**
When the logic lives in the database as a stored procedure: shared by several applications, needing IN/OUT parameters, or heavy data processing best done close to the data. This project keeps its rules in Java services. Use Case 7 (employee-performance) shows `CallableStatement`.

**What problem does connection pooling solve?**
Opening a physical connection (TCP, authentication, session setup) is slow and the database allows only a limited number. A pool opens a few connections once and lends them out, giving lower latency per request, a hard cap on database connections, and predictable behaviour under load: callers wait briefly or get a clear timeout.

**What can happen when two requests update the same row concurrently?**
Lost updates (one write silently overwrites the other), overdrafts, and data that no longer adds up. See the concurrency experiment. The fixes are `SELECT ... FOR UPDATE` (pessimistic locking), atomic `UPDATE ... SET balance = balance - ? WHERE balance >= ?`, or a version column (optimistic locking).

**How would you separate database access from business logic in a larger application?**
- Keep SQL in DAO/repository classes behind interfaces.
- Put rules and transaction boundaries in services.
- Keep I/O (console, REST controllers) thin.
- Inject dependencies instead of creating them, as `AppContext` does by hand here.
- Keep configuration such as credentials and pool size out of the code.

In a larger Java application, Spring Boot provides this structure: `@Repository`, `@Service` with `@Transactional`, and `@RestController`.
