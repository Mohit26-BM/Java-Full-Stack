# DataSource and Connection Pooling (JDBC)

An audit-event repository that gets its connections from a `DataSource` backed by a HikariCP connection pool of 5 connections, exercised by many worker threads at once.

## Tech

Java 8, Maven, MySQL 8, **HikariCP 4.0.3** (the last version supporting Java 8), SLF4J + slf4j-simple for logging.

## Structure

```text
src/main/java/com/company/jdbc
├── config/       ConnectionPool (builds HikariCP → DataSource), DbSettings,
│                 PoolStats, PropertiesLoader          ← configuration only
├── model/        AuditEvent
├── repository/   AuditEventRepository + JdbcAuditEventRepository (uses DataSource)
├── service/      AuditService + implementation (validation)
├── exception/    DataAccessException, PoolExhaustedException, InvalidInputException
└── app/          AuditApp (main menu), WorkerLoadTest, PoolExhaustionDemo,
                  ConnectionBenchmark, ConsoleInput
src/main/resources
├── schema.sql / sample-data.sql   database audit_pool, table audit_events
├── db.properties                  URL / user / password (not committed)
├── pool.properties                pool size, timeouts, leak detection
└── simplelogger.properties        log format (time + thread name)
```

## Setup

1. Run `schema.sql` (and optionally `sample-data.sql`) in MySQL.
2. In Eclipse: **File → Import → Maven → Existing Maven Projects**. Eclipse downloads HikariCP and SLF4J.
3. Run `com.company.jdbc.app.AuditApp`.

## Design

- **Configuration is separate from code.** Pool settings live in `pool.properties` and credentials in `db.properties`. Only `ConnectionPool` knows HikariCP exists. The repository receives a plain `javax.sql.DataSource` through its constructor, so the pool library or its size can change without touching repository code.
- **Connections always go back to the pool.** Every repository method uses `try (Connection c = dataSource.getConnection())`. With a pool, `close()` doesn't disconnect; it returns the connection for reuse. try-with-resources guarantees this on success *and* on exceptions. `leakDetectionThresholdMs` logs a warning if a connection is ever held longer than 10 s.
- **Pool exhaustion is predictable.** A caller waits at most `connectionTimeoutMs` (3 s) for a free connection, then gets a `PoolExhaustedException` (the repository translates HikariCP's `SQLTransientConnectionException`). There's no endless hang, and the caller can retry, reject, or report "busy".
- **Logging.** The repository logs every successful operation at INFO and every database error at ERROR, with time and thread name, e.g. `12:01:05.123 [pool-2-thread-3] INFO JdbcAuditEventRepository - Saved audit event 42`.

## Pool settings (`pool.properties`)

| Setting | Value | Meaning |
|---|---|---|
| `pool.maximumPoolSize` | 5 | at most 5 physical connections, shared by all threads |
| `pool.minimumIdle` | 5 | keep all 5 open and ready |
| `pool.connectionTimeoutMs` | 3000 | max wait for a free connection |
| `pool.idleTimeoutMs` | 600000 | close extra idle connections after 10 min |
| `pool.maxLifetimeMs` | 1800000 | recycle each connection after 30 min |
| `pool.leakDetectionThresholdMs` | 10000 | warn about connections held > 10 s |

## Acceptance checks (menu options)

| Check | Option | What you see |
|---|---|---|
| DAO obtains connections from the DataSource | 1, 2 | Log lines from `JdbcAuditEventRepository`; pool status shows a fixed set of 5 connections |
| Connections return to the pool | 4 | 10 threads × 5 inserts + selects; "Max active seen ≤ 5" and "active = 0" afterwards |
| Pool exhaustion handled predictably | 5 | all 5 connections are held; the next query fails after ~3000 ms with `PoolExhaustedException`; after release it works again |
| Resources closed on success and failure | 6 | an insert that MySQL rejects (NULL `event_type`) is logged as an error, and "active = 0" afterwards |

## Optional challenge: why pooling matters (option 7)

Option 7 runs *get connection → `SELECT 1` → close* N times, first with `DriverManager` and then with the pool.

| Approach | Total ms | Avg ms / op |
|---|---|---|
| DriverManager | | |
| Pool | | |

*(Fill in your own results. With a local MySQL, opening a new connection typically takes a few milliseconds, while borrowing from the pool takes a small fraction of a millisecond.)*

**Why the difference:** `DriverManager.getConnection()` opens a brand-new physical connection every time. That means a TCP handshake, MySQL authentication, and session setup, and `close()` tears it all down again. A pool pays that cost once at startup and then just hands out existing connections.

**Why it matters for server applications:**

1. **Latency.** A web request that opens its own connection adds the connect time to every request, often more than the query itself.
2. **Throughput and limits.** MySQL allows only `max_connections` (default 151). Without a pool, 500 simultaneous requests try to open 500 connections and fail or overload the database. A pool caps usage at a known number and makes extra requests queue briefly.
3. **Predictable failure.** When the database is slow, a pool turns overload into bounded waits and clear timeouts instead of piling up connections until everything collapses.
4. **Health and hygiene.** The pool validates, recycles (`maxLifetime`) and replaces broken connections, and can detect leaks, so application code doesn't have to.
