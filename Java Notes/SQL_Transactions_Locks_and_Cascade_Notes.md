# SQL, Transactions, Isolation Anomalies, Locks and Cascade: Detailed Notes

Core SQL (queries, joins, DML/DDL, constraints), foreign keys and **CASCADE** rules, normalization and indexes, then the deep part: **transactions and ACID**, **concurrency anomalies** (dirty read, non-repeatable read, phantom read, lost update, write skew), **isolation levels**, **locking vs MVCC**, **locks in depth**, **deadlocks**, optimistic vs pessimistic locking, transactions in JDBC and Spring/JPA, a hands-on two-terminal lab, puzzles, interview questions and practice tasks.

> **Database used:** **MySQL 8 with InnoDB** (the default engine), matching your JPA notes. Differences for PostgreSQL, Oracle and SQL Server are flagged where they matter, since isolation behavior varies by database.

---

## Table of Contents

1. [SQL and Relational Basics](#1-sql-and-relational-basics)
2. [Core Querying](#2-core-querying)
3. [Changing Data and Schema](#3-changing-data-and-schema)
4. [Foreign Keys and Referential Actions (CASCADE)](#4-foreign-keys-and-referential-actions-cascade)
5. [Normalization and Indexes](#5-normalization-and-indexes)
6. [Transactions and ACID](#6-transactions-and-acid)
7. [Concurrency Problems (Anomalies)](#7-concurrency-problems-anomalies)
8. [Isolation Levels](#8-isolation-levels)
9. [How Databases Enforce Isolation: Locking vs MVCC](#9-how-databases-enforce-isolation-locking-vs-mvcc)
10. [Locks in Depth](#10-locks-in-depth)
11. [Deadlocks](#11-deadlocks)
12. [Preventing Lost Updates and Write Skew](#12-preventing-lost-updates-and-write-skew)
13. [Transactions in Java: JDBC and Spring/JPA](#13-transactions-in-java-jdbc-and-springjpa)
14. [Transaction Best Practices](#14-transaction-best-practices)
15. [Hands-On Lab: Reproduce Every Anomaly](#15-hands-on-lab-reproduce-every-anomaly)
16. [Common Mistakes](#16-common-mistakes)
17. [Puzzles](#17-puzzles)
18. [Interview Questions](#18-interview-questions)
19. [Cheat Sheet](#19-cheat-sheet)
20. [Practice Tasks with Solutions](#20-practice-tasks-with-solutions)

---

## 1. SQL and Relational Basics

### What is a relational database

Data is stored in **tables** (rows and columns) related through **keys**. **SQL (Structured Query Language)** is the standard language to define, change and query that data.

| Term | Meaning |
|---|---|
| **Table** | A set of rows with the same columns |
| **Row (record, tuple)** | One entry |
| **Column (attribute, field)** | One property |
| **Primary key** | Uniquely identifies a row; not null, unique |
| **Foreign key** | A column that refers to the primary key of another table |
| **Candidate key** | Any column set that could be a primary key |
| **Composite key** | A key made of several columns |

### Categories of SQL

| Category | Full name | Statements | Purpose |
|---|---|---|---|
| **DDL** | Data Definition Language | `CREATE`, `ALTER`, `DROP`, `TRUNCATE`, `RENAME` | Define structure |
| **DML** | Data Manipulation Language | `INSERT`, `UPDATE`, `DELETE` | Change data |
| **DQL** | Data Query Language | `SELECT` | Read data |
| **DCL** | Data Control Language | `GRANT`, `REVOKE` | Permissions |
| **TCL** | Transaction Control Language | `START TRANSACTION`, `COMMIT`, `ROLLBACK`, `SAVEPOINT` | Transactions |

### Sample data used throughout

```sql
CREATE TABLE departments (
    dept_id   INT PRIMARY KEY,
    dept_name VARCHAR(50) NOT NULL
);

CREATE TABLE employees (
    emp_id   INT PRIMARY KEY,
    emp_name VARCHAR(50) NOT NULL,
    salary   DECIMAL(10,2),
    dept_id  INT,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

INSERT INTO departments VALUES (1, 'IT'), (2, 'HR'), (3, 'Finance');

INSERT INTO employees VALUES
    (1, 'Asha',  85000, 1),
    (2, 'Ravi',  72000, 1),
    (3, 'Neha',  55000, 2),
    (4, 'Kiran', 60000, NULL);
```

```
departments                 employees
dept_id | dept_name         emp_id | emp_name | salary   | dept_id
   1    | IT                  1    | Asha     | 85000.00 |   1
   2    | HR                  2    | Ravi     | 72000.00 |   1
   3    | Finance             3    | Neha     | 55000.00 |   2
                              4    | Kiran    | 60000.00 |  NULL
```

Notice: **Finance has no employees**, and **Kiran has no department**. These two cases make the join examples meaningful.

---

## 2. Core Querying

### Basic `SELECT`

```sql
SELECT emp_name, salary
FROM employees
WHERE salary > 60000
ORDER BY salary DESC;
```

```
Asha  | 85000.00
Ravi  | 72000.00
```

| Clause | Purpose |
|---|---|
| `SELECT` | Which columns |
| `FROM` | Which table(s) |
| `WHERE` | Filter **rows** |
| `GROUP BY` | Group rows |
| `HAVING` | Filter **groups** |
| `ORDER BY` | Sort (`ASC` default, `DESC`) |
| `LIMIT n` | First `n` rows (MySQL, PostgreSQL) |
| `DISTINCT` | Remove duplicate rows |

### Filtering operators

```sql
WHERE salary BETWEEN 55000 AND 72000      -- inclusive range
WHERE dept_id IN (1, 2)                   -- one of a list
WHERE emp_name LIKE 'A%'                  -- starts with A   (% = any characters, _ = one character)
WHERE emp_name LIKE '%a'                  -- ends with a
WHERE dept_id IS NULL                     -- NULL test (never use = NULL)
WHERE salary > 60000 AND dept_id = 1      -- AND, OR, NOT
```

### Aggregate functions

| Function | Result |
|---|---|
| `COUNT(*)` | Number of rows |
| `COUNT(column)` | Number of **non-NULL** values |
| `SUM`, `AVG`, `MIN`, `MAX` | Totals (ignore NULLs) |

```sql
SELECT COUNT(*), AVG(salary) FROM employees;         -- 4 | 68000.00
SELECT COUNT(dept_id) FROM employees;                -- 3  (Kiran's NULL is not counted)
```

### `GROUP BY` and `HAVING`

```sql
SELECT d.dept_name, COUNT(*) AS emp_count, AVG(e.salary) AS avg_salary
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
GROUP BY d.dept_name
ORDER BY d.dept_name;
```

```
HR | 1 | 55000.00
IT | 2 | 78500.00
```

```sql
-- departments whose average salary is above 60000
SELECT d.dept_name, AVG(e.salary) AS avg_salary
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
GROUP BY d.dept_name
HAVING AVG(e.salary) > 60000;                         -- IT | 78500.00
```

| | `WHERE` | `HAVING` |
|---|---|---|
| Filters | Individual **rows**, **before** grouping | **Groups**, **after** grouping |
| Can use aggregates? | No | **Yes** |

### Joins

A **join** combines rows from two tables on a matching condition.

**Analogy:** matching a list of **students** with a list of **courses** using a shared roll or course number.

```sql
-- INNER JOIN: only rows that match in BOTH tables
SELECT e.emp_name, d.dept_name
FROM employees e
INNER JOIN departments d ON e.dept_id = d.dept_id;
```

```
Asha | IT
Ravi | IT
Neha | HR            (Kiran has no department, Finance has no employee: both excluded)
```

```sql
-- LEFT JOIN: ALL rows from the left table, matches from the right (NULL if none)
SELECT e.emp_name, d.dept_name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id;
```

```
Asha  | IT
Ravi  | IT
Neha  | HR
Kiran | NULL          (kept, with NULL for the missing department)
```

```sql
-- RIGHT JOIN: ALL rows from the right table
SELECT e.emp_name, d.dept_name
FROM employees e
RIGHT JOIN departments d ON e.dept_id = d.dept_id;
```

```
Asha | IT
Ravi | IT
Neha | HR
NULL | Finance       (kept, with NULL for the missing employee)
```

| Join | Returns |
|---|---|
| `INNER JOIN` | Only matching rows |
| `LEFT JOIN` | All left rows plus matches |
| `RIGHT JOIN` | All right rows plus matches |
| `FULL OUTER JOIN` | All rows from both (not in MySQL; emulate with `LEFT JOIN ... UNION ... RIGHT JOIN`) |
| `CROSS JOIN` | Every combination (Cartesian product) |
| **Self join** | A table joined to itself (for example employees and their managers) |

**Find rows with no match** (departments with no employees):

```sql
SELECT d.dept_name
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
WHERE e.emp_id IS NULL;                               -- Finance
```

### Subqueries

```sql
-- employees earning more than the average salary
SELECT emp_name, salary
FROM employees
WHERE salary > (SELECT AVG(salary) FROM employees);   -- Asha, Ravi   (average = 68000)

-- second-highest salary (a classic)
SELECT MAX(salary)
FROM employees
WHERE salary < (SELECT MAX(salary) FROM employees);   -- 72000.00
```

`IN` and `EXISTS` test membership against a subquery:

```sql
SELECT dept_name FROM departments
WHERE dept_id IN (SELECT dept_id FROM employees);                    -- IT, HR

SELECT dept_name FROM departments d
WHERE EXISTS (SELECT 1 FROM employees e WHERE e.dept_id = d.dept_id);   -- IT, HR
```

### `UNION`

```sql
SELECT emp_name FROM employees
UNION                                    -- removes duplicates (UNION ALL keeps them)
SELECT dept_name FROM departments;
```

### `CASE`

```sql
SELECT emp_name,
       CASE
           WHEN salary >= 80000 THEN 'High'
           WHEN salary >= 60000 THEN 'Medium'
           ELSE 'Low'
       END AS band
FROM employees;
```

### NULL: the three-valued logic

`NULL` means **unknown**, not zero or empty.

| Expression | Result |
|---|---|
| `NULL = NULL` | **Unknown** (not true) |
| `salary = NULL` | Never matches; use `IS NULL` |
| `5 + NULL` | `NULL` |
| `COUNT(col)` | Ignores NULLs |
| `NULL AND FALSE` | `FALSE` |

**The `NOT IN` trap:**

```sql
SELECT dept_name FROM departments
WHERE dept_id NOT IN (SELECT dept_id FROM employees);
-- returns NO ROWS, even though Finance has no employees!
```

The subquery returns `(1, 1, 2, NULL)`, and `3 NOT IN (1, 1, 2, NULL)` is **unknown**, not true. Fix it by filtering the NULL, or use `NOT EXISTS`:

```sql
SELECT dept_name FROM departments
WHERE dept_id NOT IN (SELECT dept_id FROM employees WHERE dept_id IS NOT NULL);   -- Finance
```

### Logical order of execution

```
FROM / JOIN  ->  WHERE  ->  GROUP BY  ->  HAVING  ->  SELECT  ->  DISTINCT  ->  ORDER BY  ->  LIMIT
```

That is why you cannot use a `SELECT` alias inside `WHERE`, but you can in `ORDER BY`.

---

## 3. Changing Data and Schema

### DML

```sql
INSERT INTO employees (emp_id, emp_name, salary, dept_id) VALUES (5, 'Meera', 90000, 3);
INSERT INTO employees VALUES (6, 'Arjun', 95000, 1), (7, 'Sara', 65000, 3);   -- several rows

UPDATE employees SET salary = salary + 5000 WHERE dept_id = 1;

DELETE FROM employees WHERE emp_id = 7;
```

> **`UPDATE` or `DELETE` without `WHERE` changes every row.** Run the same condition as a `SELECT` first, and do risky changes inside a transaction so you can `ROLLBACK`.

### DDL

```sql
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    price      DECIMAL(10,2) NOT NULL DEFAULT 0,
    CHECK (price >= 0)
);

ALTER TABLE products ADD COLUMN category VARCHAR(50);
ALTER TABLE products MODIFY COLUMN name VARCHAR(150) NOT NULL;
ALTER TABLE products DROP COLUMN category;
ALTER TABLE products RENAME TO items;

DROP TABLE items;
```

### Constraints

| Constraint | Rule |
|---|---|
| `PRIMARY KEY` | Unique and not null; one per table |
| `FOREIGN KEY` | Value must exist in the referenced table (or be NULL) |
| `UNIQUE` | No duplicates (NULLs allowed) |
| `NOT NULL` | Value required |
| `CHECK` | A condition on the value (enforced in MySQL 8.0.16+) |
| `DEFAULT` | Value used when none is supplied |
| `AUTO_INCREMENT` | Generated numeric key (MySQL) |

### `DELETE` vs `TRUNCATE` vs `DROP`

| | `DELETE` | `TRUNCATE` | `DROP` |
|---|---|---|---|
| Type | DML | DDL | DDL |
| Removes | Selected rows (`WHERE`) or all | **All rows** | **Rows and the table itself** |
| `WHERE` allowed | **Yes** | No | No |
| Can be rolled back | **Yes** (inside a transaction) | **No** in MySQL (implicit commit) | No |
| Resets `AUTO_INCREMENT` | No | **Yes** | n/a |
| Fires triggers | Yes | No | No |
| Speed | Slower (row by row, logged) | **Fast** | Fast |
| Table structure remains | Yes | Yes | **No** |

**Analogy:** `DELETE` removes selected pages from a notebook; `TRUNCATE` tears out **all pages** but keeps the cover; `DROP` throws away the **whole notebook**.

---

## 4. Foreign Keys and Referential Actions (CASCADE)

### What a foreign key protects

A foreign key guarantees **referential integrity**: a child row cannot point to a parent that does not exist, and a parent cannot disappear while children depend on it (unless you say otherwise).

```
departments (parent)               employees (child)
dept_id = 1  <------------------   dept_id = 1
```

### The two errors you will meet

```
ERROR 1452 (23000): Cannot add or update a child row: a foreign key constraint fails
    -> inserting an employee with a dept_id that does not exist

ERROR 1451 (23000): Cannot delete or update a parent row: a foreign key constraint fails
    -> deleting a department that still has employees
```

### Referential actions

When the **parent** row is deleted or its key is updated, the database can respond in different ways, set per foreign key:

```sql
FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
    ON DELETE <action>
    ON UPDATE <action>
```

| Action | Behavior when the parent is deleted or updated | Analogy |
|---|---|---|
| **`RESTRICT`** | **Reject** the operation if children exist (checked immediately) | "You cannot close this department while staff still work in it." |
| **`NO ACTION`** | Same effect as `RESTRICT` in MySQL/InnoDB (standard SQL checks at the end of the statement) | Same |
| **`CASCADE`** | **Apply the same change to the children**: delete them, or update their key | Deleting a **folder deletes the files inside** |
| **`SET NULL`** | Keep the children, set their foreign key to `NULL` (the column must be nullable) | The staff **remain employed**, but their department field becomes blank |
| **`SET DEFAULT`** | Set the foreign key to its default value (**not supported by InnoDB**) | |

If you write no `ON DELETE` clause, the default is **`RESTRICT`/`NO ACTION`**.

### `ON DELETE CASCADE` in action

```sql
CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer VARCHAR(50)
);

CREATE TABLE order_items (
    item_id  INT PRIMARY KEY,
    order_id INT NOT NULL,
    product  VARCHAR(50),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT INTO orders VALUES (1, 'Asha');
INSERT INTO order_items VALUES (1, 1, 'Pen'), (2, 1, 'Book');

DELETE FROM orders WHERE order_id = 1;
SELECT * FROM order_items;                     -- empty: both items were deleted automatically
```

`ON UPDATE CASCADE`: changing a parent key (`UPDATE orders SET order_id = 10 WHERE order_id = 1;`) updates `order_items.order_id` to `10` automatically. (Changing primary keys is rare; surrogate keys avoid it.)

### `ON DELETE SET NULL` in action

```sql
CREATE TABLE employees2 (
    emp_id   INT PRIMARY KEY,
    emp_name VARCHAR(50),
    dept_id  INT NULL,                          -- must allow NULL
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
        ON DELETE SET NULL
);

DELETE FROM departments WHERE dept_id = 1;      -- IT employees stay, with dept_id = NULL
```

### `RESTRICT` (the safe default)

```sql
DELETE FROM departments WHERE dept_id = 1;
-- ERROR 1451: the department still has employees
-- Delete or reassign the employees first, then the department.
```

### Choosing an action

| Relationship | Typical choice | Why |
|---|---|---|
| Order and its **items** (items have no life without the order) | **`CASCADE`** | Children are meaningless alone |
| **Employee and department** | `SET NULL` or `RESTRICT` | Employees outlive a department |
| **Customer and orders** (financial records) | **`RESTRICT`** | Never silently destroy business history |
| **Lookup tables** (status, country) | `RESTRICT` | Prevent deleting values still in use |
| Important data in general | `RESTRICT` plus **soft delete** | Safety |

### Cascade dangers and details

- **Chains cascade:** `customers -> orders -> order_items -> shipments`. Deleting one customer can wipe out a large tree. A mistaken `DELETE` becomes a disaster.
- **Soft delete** (keep the row, mark `is_deleted = 1` or `deleted_at`) is often safer for business data and enables audit and undo.
- **Performance and locking:** a cascade deletes many rows inside one transaction and **locks** them; index the foreign key column (InnoDB creates such an index automatically).
- **Triggers:** in MySQL, **cascaded actions do not activate triggers** on the child tables.
- **Circular references** between tables complicate inserts and deletes.

### Database cascade vs JPA cascade

These are **different mechanisms** that people confuse.

| | **DB `ON DELETE CASCADE`** | **JPA `CascadeType.REMOVE` / `ALL`** |
|---|---|---|
| Where it lives | In the **database schema** | In **Java entity mappings** |
| Who deletes children | The **database** automatically | **Hibernate** issues separate `DELETE`s |
| Works for raw SQL / other apps | **Yes** | **No** (only through JPA) |
| Fires JPA lifecycle callbacks | No | Yes |
| Round trips | One statement | Many statements (loads and deletes each child) |

```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items;                    // JPA cascade: Hibernate removes the items

@OneToMany(mappedBy = "order")
@OnDelete(action = OnDeleteAction.CASCADE)        // let the DATABASE cascade (Hibernate annotation)
private List<OrderItem> items2;
```

Use JPA cascade for **domain logic** that should work through your application, and a database cascade when the **database must guarantee** the rule regardless of who writes to it. Many teams use both deliberately.

---

## 5. Normalization and Indexes

### Normalization

> **Normalization** organizes tables so that **each fact is stored once**, avoiding redundancy and update problems.

**Analogy:** your phone's contacts app stores a person's address **once**, not in every message you sent them.

| Normal form | Rule | Violation example |
|---|---|---|
| **1NF** | Every column holds a single **atomic** value; no repeating groups | `courses = 'Java, SQL'` in one cell |
| **2NF** | 1NF **and** no **partial dependency** on part of a composite key | `(student_id, course_id, student_name)` where `student_name` depends only on `student_id` |
| **3NF** | 2NF **and** no **transitive dependency** (non-key depends on non-key) | `(emp_id, dept_id, dept_name)` where `dept_name` depends on `dept_id` |
| **BCNF** | Every determinant is a candidate key (a stricter 3NF) | Rare edge cases |

**Redundancy causes three data anomalies** (not to be confused with the *concurrency* anomalies in section 7):

| Anomaly | Example in `(emp_id, dept_id, dept_name)` |
|---|---|
| **Update anomaly** | Renaming a department requires editing many rows |
| **Insert anomaly** | Cannot record a new department until it has an employee |
| **Delete anomaly** | Deleting the last employee loses the department's name |

Fix: split into `employees(emp_id, dept_id)` and `departments(dept_id, dept_name)`, which is exactly the sample schema.

**Denormalization** (deliberately duplicating data) can speed up reads in reporting and read-heavy systems, at the cost of consistency work. Normalize first; denormalize with evidence.

### Indexes

> An **index** is a separate sorted structure that lets the database find rows **without scanning the whole table**.

**Analogy:** the **index at the back of a book**. Without it you read every page to find a topic.

```sql
CREATE INDEX idx_emp_dept ON employees(dept_id);
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE INDEX idx_cust_name_city ON customers(last_name, city);     -- composite index

EXPLAIN SELECT * FROM employees WHERE dept_id = 1;                 -- shows whether the index is used
```

| Concept | Meaning |
|---|---|
| **B-tree** | The default structure: sorted, supports `=`, ranges, `ORDER BY` |
| **Clustered index (InnoDB)** | The **table itself is stored in primary-key order**. The primary key *is* the clustered index |
| **Secondary index** | Stores the indexed columns plus the **primary key**; a lookup then fetches the row via the PK |
| **Composite index** | Follows the **leftmost-prefix rule**: `(last_name, city)` helps queries on `last_name` or both, not `city` alone |
| **Covering index** | Contains every column the query needs, so the table is not touched |
| **Selectivity** | An index on a column with few distinct values (gender) helps little |

**When an index is *not* used:**

- A **function** wraps the column: `WHERE YEAR(created_at) = 2026` (rewrite as a range).
- A **leading wildcard**: `LIKE '%abc'`.
- A **type mismatch**: comparing a string column to a number.
- The query returns a large share of the table (a full scan is cheaper).

**Costs:** indexes use space and slow down `INSERT`/`UPDATE`/`DELETE`. Index the columns you filter, join and sort on, not everything.

**Indexes and locking:** InnoDB places row locks **on index records**. An `UPDATE ... WHERE` on an **unindexed column** scans, and therefore locks, **many rows** (section 10), so indexing also reduces lock contention.

---

## 6. Transactions and ACID

### What is a transaction

> A **transaction** is a group of SQL statements that execute as **one logical unit of work**: either **all succeed** (commit) or **none take effect** (rollback).

**Analogy 1: a bank transfer.** Moving Rs. 300 from Asha to Ravi is *debit Asha* **and** *credit Ravi*. If the system crashes after the debit but before the credit, money vanishes. A transaction guarantees it never ends half-done.

**Analogy 2: moving house.** Either all your boxes arrive at the new house, or you stay where you are. "Half moved" is not an allowed state.

### The commands

```sql
START TRANSACTION;          -- or BEGIN

UPDATE accounts SET balance = balance - 300 WHERE id = 1;     -- debit Asha
UPDATE accounts SET balance = balance + 300 WHERE id = 2;     -- credit Ravi

COMMIT;                     -- make both changes permanent
-- or: ROLLBACK;            -- undo both
```

| Command | Effect |
|---|---|
| `START TRANSACTION` / `BEGIN` | Begin a transaction |
| `COMMIT` | **Make all changes permanent** and visible to others |
| `ROLLBACK` | **Undo all changes** since the transaction began |
| `SAVEPOINT name` | Mark a point inside the transaction |
| `ROLLBACK TO SAVEPOINT name` | Undo only back to that point |
| `RELEASE SAVEPOINT name` | Remove the savepoint |
| `SET autocommit = 0/1` | Turn automatic commit off/on |

### Autocommit

By default (MySQL, PostgreSQL clients), **every statement is its own transaction** that commits immediately (**autocommit = ON**). `START TRANSACTION` temporarily switches that off until `COMMIT`/`ROLLBACK`.

```sql
UPDATE accounts SET balance = 0 WHERE id = 1;   -- autocommit ON: already permanent
ROLLBACK;                                        -- too late: nothing to undo
```

### Savepoints

```sql
START TRANSACTION;
INSERT INTO accounts VALUES (3, 'Neha', 800);
SAVEPOINT after_neha;
INSERT INTO accounts VALUES (4, 'Kiran', 900);
ROLLBACK TO SAVEPOINT after_neha;         -- undoes only Kiran
COMMIT;                                    -- Neha is saved
```

### Statements that commit implicitly (MySQL)

In MySQL, **DDL** statements (`CREATE`, `ALTER`, `DROP`, `TRUNCATE`, `RENAME`) **implicitly commit** the current transaction. They cannot be rolled back.

```sql
START TRANSACTION;
INSERT INTO accounts VALUES (5, 'Test', 100);
ALTER TABLE accounts ADD COLUMN note VARCHAR(20);   -- implicit COMMIT happens here
ROLLBACK;                                            -- the insert is NOT undone
```

(PostgreSQL and SQL Server support transactional DDL; MySQL and Oracle do not.) Also, **MyISAM** tables have **no transactions** at all; InnoDB is the transactional engine and the default.

### Transaction states

```
Active  ->  Partially committed  ->  Committed
   |                                    
   +-->  Failed  ->  Aborted (rolled back)
```

### ACID

The four guarantees of a transactional database:

| Property | Meaning | Analogy | Typical mechanism |
|---|---|---|---|
| **A**tomicity | **All or nothing** | A **light switch**: on or off, never half | **Undo log** reverses uncommitted changes |
| **C**onsistency | Moves the database from one **valid state** to another; constraints always hold | **Balanced books**: total money before equals total after | Constraints, keys, triggers (plus your application's rules) |
| **I**solation | Concurrent transactions do not interfere; each behaves as if alone (to a degree set by the **isolation level**) | **Separate exam cubicles** | **Locks** and **MVCC** |
| **D**urability | Once committed, the change **survives crashes** | Written in **permanent ink** | **Redo log / write-ahead log (WAL)** flushed to disk |

Notes:

- **Atomicity example:** crash after the debit but before the credit: on restart the database **rolls back** the unfinished transaction using the undo log.
- **Durability example:** after `COMMIT` returns, power failure cannot lose the change; recovery **replays the redo log**. (`innodb_flush_log_at_trx_commit = 1`, the default, gives full durability; other values trade durability for speed.)
- **Consistency** is shared: the database enforces declared constraints, but **business invariants** ("total money is conserved") are your code's responsibility.
- **Isolation** is the tunable one: perfect isolation is expensive, so databases offer **levels** (section 8).

### Transactions and crash recovery

| At crash time | After restart |
|---|---|
| Transaction **committed** | Changes **present** (redo) |
| Transaction **not committed** | Changes **absent** (undo) |

---

## 7. Concurrency Problems (Anomalies)

When many transactions run at once, they can interfere in specific ways. These problems are the **anomalies** that isolation levels are designed to prevent.

Setup for all examples:

```sql
CREATE TABLE accounts (
    id      INT PRIMARY KEY,
    name    VARCHAR(50),
    balance INT
);
INSERT INTO accounts VALUES (1, 'Asha', 1000), (2, 'Ravi', 500);
```

`T1` and `T2` are two concurrent transactions (two database sessions).

### 7.1 Dirty read

> **T2 reads data written by T1 that is not yet committed.** If T1 rolls back, T2 has used a value that **never officially existed**.

| Time | T1 | T2 |
|---|---|---|
| 1 | `START TRANSACTION;` | |
| 2 | `UPDATE accounts SET balance = 0 WHERE id = 1;` (not committed) | |
| 3 | | `SELECT balance FROM accounts WHERE id = 1;` returns **0** (dirty) |
| 4 | `ROLLBACK;` (balance is 1000 again) | |
| 5 | | T2 acted on **0**, a value that never really existed |

**Analogy:** reading someone's **rough draft** or a **pencilled-in** seat booking that they then erase. You acted on information that was never final. Or a teacher announcing **provisional marks** that are later withdrawn.

**Consequence:** wrong decisions (declining a loan because a balance looked like 0), wrong reports.

**Prevented from:** `READ COMMITTED` upward.

### 7.2 Non-repeatable read

> **T1 reads the same row twice and gets different values**, because T2 modified and committed that row in between.

| Time | T1 | T2 |
|---|---|---|
| 1 | `START TRANSACTION;` | |
| 2 | `SELECT balance FROM accounts WHERE id = 1;` returns **1000** | |
| 3 | | `UPDATE accounts SET balance = 500 WHERE id = 1; COMMIT;` |
| 4 | `SELECT balance FROM accounts WHERE id = 1;` returns **500** | |

**Analogy:** you check a **product's price** on a shopping page (Rs. 1000); you refresh at checkout and it shows Rs. 500. The same item changed *within your single visit*.

**Consequence:** inconsistent calculations inside one transaction (a report whose numbers don't add up).

**Key:** the **same row**, a **different value**.

**Prevented from:** `REPEATABLE READ` upward.

### 7.3 Phantom read

> **T1 runs the same query twice and gets a different set of rows**, because T2 **inserted or deleted** rows matching the condition in between.

| Time | T1 | T2 |
|---|---|---|
| 1 | `START TRANSACTION;` | |
| 2 | `SELECT COUNT(*) FROM accounts WHERE balance > 400;` returns **2** | |
| 3 | | `INSERT INTO accounts VALUES (3, 'Neha', 900); COMMIT;` |
| 4 | `SELECT COUNT(*) FROM accounts WHERE balance > 400;` returns **3** | |

**Analogy:** you **count the people in a room** (2). Someone walks in. When you recount, there are 3. The new person is a **phantom**.

**Key:** the **same query**, a **different set of rows** (rows appearing or disappearing).

| Non-repeatable read | Phantom read |
|---|---|
| An **existing row's values** changed | **New or removed rows** match the query |
| Fixed by locking or versioning **rows** | Needs protection of the **range or predicate** |

**Prevented fully by:** `SERIALIZABLE` in the SQL standard (InnoDB's `REPEATABLE READ` also prevents it for ordinary reads; see section 8).

### 7.4 Lost update

> **Two transactions read the same value, both compute a new value from it, and both write back, so the second write overwrites the first**, and one update is lost.

Starting balance 1000. T1 deposits 100; T2 withdraws 200. Correct result: **900**.

| Time | T1 (deposit 100) | T2 (withdraw 200) |
|---|---|---|
| 1 | `SELECT balance` returns 1000 | |
| 2 | | `SELECT balance` returns 1000 |
| 3 | application computes 1000 + 100 = **1100** | |
| 4 | | application computes 1000 - 200 = **800** |
| 5 | `UPDATE accounts SET balance = 1100 WHERE id = 1; COMMIT;` | |
| 6 | | `UPDATE accounts SET balance = 800 WHERE id = 1; COMMIT;` |
| 7 | **Final balance: 800**, but the correct answer is **900**. T1's deposit was lost. | |

**Analogy:** two people **edit the same document offline** from the same starting copy and both save; the last save silently wipes the other's changes.

**Important:** this is a **read-modify-write** problem. It can happen **even at `REPEATABLE READ`** in MySQL and PostgreSQL if the application reads a value and writes back a *computed* value. Fixes are in section 12.

### 7.5 Write skew

> **Two transactions read overlapping data, each makes a decision based on what it read, then each updates a *different* row.** Each is correct alone, but together they break a rule.

**Classic example: on-call doctors** (rule: *at least one doctor must be on call*). Alice and Bob are both on call.

| Time | T1 (Alice wants to leave) | T2 (Bob wants to leave) |
|---|---|---|
| 1 | `SELECT COUNT(*) FROM doctors WHERE on_call = 1;` returns **2** (fine to leave) | |
| 2 | | `SELECT COUNT(*) FROM doctors WHERE on_call = 1;` returns **2** (fine to leave) |
| 3 | `UPDATE doctors SET on_call = 0 WHERE name = 'Alice'; COMMIT;` | |
| 4 | | `UPDATE doctors SET on_call = 0 WHERE name = 'Bob'; COMMIT;` |
| 5 | **Nobody is on call.** The rule is broken. | |

**Analogy:** two roommates each glance at the house, see the other is home, and **both leave**, leaving the house empty.

**Why isolation did not stop it:** they updated **different rows**, so there was no write conflict. It occurs under **snapshot isolation** (PostgreSQL `REPEATABLE READ`, InnoDB `REPEATABLE READ` with plain reads).

**Fixes:** `SERIALIZABLE`, or **lock the rows you read** (`SELECT ... FOR UPDATE`), or enforce the rule in a way the database can check (constraint, lock a parent row).

### 7.6 Other anomalies worth knowing

| Anomaly | Description |
|---|---|
| **Dirty write** | Overwriting another transaction's uncommitted change. **Prevented by every level** (writes always take row locks) |
| **Read skew** | T1 reads A, T2 moves money from B to A and commits, T1 reads B and sees a **total that does not add up**. A snapshot prevents it |
| **Lock wait / deadlock** | Not data anomalies but concurrency failures (sections 10 and 11) |

### Quick way to tell them apart

| Symptom | Anomaly |
|---|---|
| "I saw data that was **later rolled back**" | Dirty read |
| "**The same row** has a different value on re-read" | Non-repeatable read |
| "**The same query** returns different rows" | Phantom read |
| "My update **vanished**, overwritten by another" | Lost update |
| "Each check passed, but **together they broke a rule**" | Write skew |

---

## 8. Isolation Levels

### What is isolation

> **Isolation** decides **how much one transaction can see of the changes made by other concurrent transactions.**

Stricter isolation means **fewer anomalies** but **more waiting or aborts** (less concurrency). Databases let you pick a **level**.

**Analogy:** how much **privacy** each person gets in a shared office: open-plan (see everything) up to private rooms (see nothing of others' work in progress).

### The four standard levels

| Level | Dirty read | Non-repeatable read | Phantom read |
|---|---|---|---|
| **READ UNCOMMITTED** | **Possible** | Possible | Possible |
| **READ COMMITTED** | Prevented | **Possible** | Possible |
| **REPEATABLE READ** | Prevented | Prevented | **Possible** (standard); see database notes below |
| **SERIALIZABLE** | Prevented | Prevented | Prevented |

(The standard table says nothing about **lost update** and **write skew**; those depend on the implementation, see below.)

### What each level means

| Level | Meaning | Analogy |
|---|---|---|
| **READ UNCOMMITTED** | You may see other transactions' **uncommitted** changes | Reading **everyone's drafts** |
| **READ COMMITTED** | You see **only committed** data, but each statement sees the **latest** committed state | Reading **only published** pages, but refreshing each time |
| **REPEATABLE READ** | Rows you have read **stay the same** for your whole transaction (a stable view) | A **photograph** of the data taken at the start |
| **SERIALIZABLE** | Results are as if transactions ran **one after another** | A **single-person queue**: no overlap at all |

> **Serializable** guarantees the outcome is equivalent to *some* serial order. It does not literally run transactions one at a time.

### Setting the level

```sql
-- for the next transaction only
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

-- for the whole session
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- check the current level (MySQL 8)
SELECT @@transaction_isolation;
```

Spring: `@Transactional(isolation = Isolation.READ_COMMITTED)`. JDBC: `con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ)`.

### Defaults and behavior differ by database

| | MySQL (InnoDB) | PostgreSQL | Oracle | SQL Server |
|---|---|---|---|---|
| **Default level** | **REPEATABLE READ** | **READ COMMITTED** | READ COMMITTED | READ COMMITTED |
| **READ UNCOMMITTED** | Dirty reads possible | Acts as READ COMMITTED (**no dirty reads ever**) | Not offered | Supported (`NOLOCK` hint) |
| **REPEATABLE READ** | Stable **snapshot** for plain reads; **no phantoms** for plain reads | **Snapshot isolation**; no phantoms; can fail with a serialization error | Not offered | Supported (locking) |
| **SERIALIZABLE** | Plain reads become **locking reads** (readers block writers) | **SSI**: detects conflicts and **aborts** a transaction with error `40001` | Snapshot-style; may raise `ORA-08177` | Locking |
| **Model** | MVCC plus locks | MVCC | MVCC | Locks (MVCC via snapshot options) |
| **DDL in a transaction** | No (implicit commit) | **Yes** | No | Yes |

**Takeaway:** the same level name can behave differently on different databases. A Spring app tested on MySQL (`REPEATABLE READ`) may behave differently on PostgreSQL (`READ COMMITTED`) if it relies on the **default**.

### Which anomalies do the levels prevent in practice (InnoDB)

| | Dirty read | Non-repeatable read | Phantom (plain reads) | Lost update (read, compute, write) | Write skew |
|---|---|---|---|---|---|
| **READ UNCOMMITTED** | Possible | Possible | Possible | Possible | Possible |
| **READ COMMITTED** | No | Possible | Possible | **Possible** | Possible |
| **REPEATABLE READ** | No | No | **No** (snapshot) | **Possible** | **Possible** |
| **SERIALIZABLE** | No | No | No | **No** (a conflicting transaction waits or deadlocks) | **No** |

An atomic `UPDATE ... SET balance = balance - 100` is **safe at any level** because the update re-reads and locks the current row (section 12).

### Choosing a level

| Need | Level |
|---|---|
| Rough analytics where accuracy is not critical | READ UNCOMMITTED (rarely advisable) |
| Typical OLTP, short transactions, **most web apps** | **READ COMMITTED** or the database default |
| Reports needing a **consistent view** across several queries | **REPEATABLE READ** (snapshot) |
| Strict invariants where write skew or phantoms would be damaging | **SERIALIZABLE**, plus a **retry loop** |

Rule: **use the lowest level that is safe for the invariant**, and protect specific hot spots with explicit locks or atomic updates instead of raising the level everywhere.

---

## 9. How Databases Enforce Isolation: Locking vs MVCC

### Approach 1: lock-based (two-phase locking)

Transactions **lock** the data they touch; others **wait**.

- **Shared (S) lock** for reading, **exclusive (X) lock** for writing.
- **Two-phase locking (2PL):** a **growing** phase (acquire locks) then a **shrinking** phase (release); in practice locks are **held until commit or rollback**.

Pros: simple to reason about. Cons: **readers and writers block each other**, lowering concurrency; deadlocks.

### Approach 2: MVCC (Multi-Version Concurrency Control)

The database keeps **multiple versions** of each row. Each transaction reads a **consistent snapshot**, so **readers do not block writers and writers do not block readers**.

**Analogy:** each reader is handed a **photograph of the data** as of a particular moment. Writers keep editing the live version, and older "photographs" (row versions, kept in the **undo log**) are retained until no transaction needs them.

```
Time:    t1                t2 (T2 updates and commits)       t3
Row:     balance = 1000    ---------------> balance = 500
T1 (snapshot at t1) keeps reading 1000 even after t2 (REPEATABLE READ)
T3 (starts after t2) reads 500
```

How InnoDB decides what a transaction sees:

| Level | When the snapshot is taken |
|---|---|
| **READ COMMITTED** | A **new snapshot for every statement** |
| **REPEATABLE READ** | **One snapshot for the whole transaction** (taken at the **first read**; `START TRANSACTION WITH CONSISTENT SNAPSHOT` forces it at the start) |

### Consistent reads vs locking reads (InnoDB)

| Kind | Statements | Behavior |
|---|---|---|
| **Consistent (non-locking) read** | Plain `SELECT` | Reads from the **snapshot**; takes **no locks**; can be **stale** |
| **Locking read / current read** | `SELECT ... FOR UPDATE`, `SELECT ... FOR SHARE`, `UPDATE`, `DELETE`, `INSERT` | Reads the **latest committed** version, takes **locks**, and may wait |

This difference explains many surprises: inside one `REPEATABLE READ` transaction a plain `SELECT` returns the old snapshot, while an `UPDATE` or `SELECT ... FOR UPDATE` sees the **latest** data.

### An InnoDB phantom quirk

Under `REPEATABLE READ`, plain reads do not see rows inserted by others. But if **your own transaction updates** such a row, it becomes visible afterward:

```sql
-- T1: START TRANSACTION; SELECT COUNT(*) FROM accounts;      -- 2
-- T2: INSERT INTO accounts VALUES (3, 'Neha', 900); COMMIT;
-- T1: SELECT COUNT(*) FROM accounts;                          -- still 2 (snapshot)
-- T1: UPDATE accounts SET balance = balance WHERE id = 3;     -- touches the new row (current read)
-- T1: SELECT COUNT(*) FROM accounts;                          -- now 3
```

### PostgreSQL's equivalent

PostgreSQL uses MVCC at **every** level (readers never block writers). `REPEATABLE READ` is **snapshot isolation**; if two transactions try to update the same row, the second gets `ERROR: could not serialize access due to concurrent update` and must **retry**. `SERIALIZABLE` adds **SSI** conflict detection that aborts one transaction on write-skew patterns.

---

## 10. Locks in Depth

### Why locks exist

Locks make concurrent writers **take turns**, and protect rows you intend to change.

**Analogy:** a **washroom with a single key**. Whoever holds the key is inside; others queue.

### Lock modes

| Lock | Allows | Blocks | Analogy |
|---|---|---|---|
| **Shared (S)** (read lock) | Many holders may read together | Writers | A **library reference room**: many people may read the same book at once |
| **Exclusive (X)** (write lock) | **One** holder to modify | Everyone else (readers using locks, and writers) | **One editor** has checked out the manuscript |

**Compatibility:**

| Held \ Requested | S | X |
|---|---|---|
| **S** | Compatible | **Conflict** |
| **X** | **Conflict** | **Conflict** |

Read-read is fine; anything involving a write conflicts.

### Lock granularity

| Granularity | Locks | Concurrency | Overhead |
|---|---|---|---|
| **Row-level** (InnoDB default) | One row (an index record) | **High** | More locks to manage |
| **Page-level** | A block of rows | Medium | Medium |
| **Table-level** | The whole table | **Low** | Low |
| **Database-level** | Everything | Very low | Minimal |

**Analogy:** reserving **one seat** vs a **whole row** vs **the entire theatre**.

InnoDB uses **row-level locking** by default (MyISAM uses table locks, another reason to avoid it). Some databases, such as SQL Server, may **escalate** many row locks into a table lock.

### InnoDB lock types

| Lock | Meaning |
|---|---|
| **Record lock** | Locks a single **index record** |
| **Gap lock** | Locks the **gap between** index records, so nobody can insert into it (used at `REPEATABLE READ`) |
| **Next-key lock** | A record lock **plus** the gap before it. This is how InnoDB prevents phantoms for locking reads |
| **Insert intention lock** | A signal that an insert is waiting inside a gap |
| **Intent locks (IS, IX)** | **Table-level** flags saying "I hold or want row locks inside this table" |
| **Metadata (DDL) locks** | Taken by statements and `ALTER TABLE` on the table definition |
| **AUTO-INC lock** | Serializes auto-increment allocation briefly |

**Analogy for gap locks:** reserving the **empty seats between two booked seats** so nobody can sit there (prevents phantom "new people").

**Analogy for intent locks:** a sign on the **building door**: "someone is working inside on floor 3." It lets the system quickly decide whether a **whole-building** lock request must wait, without checking every room.

**Intent lock compatibility (table level):**

| Held \ Requested | X | IX | S | IS |
|---|---|---|---|---|
| **X** | Conflict | Conflict | Conflict | Conflict |
| **IX** | Conflict | OK | Conflict | OK |
| **S** | Conflict | Conflict | OK | OK |
| **IS** | Conflict | OK | OK | OK |

### Explicit locking statements

```sql
-- lock the selected rows for update (exclusive); others must wait
SELECT * FROM accounts WHERE id = 1 FOR UPDATE;

-- lock for reading (shared): others can read but not modify (MySQL 8: FOR SHARE)
SELECT * FROM accounts WHERE id = 1 FOR SHARE;

-- do not wait: fail immediately if a row is locked
SELECT * FROM accounts WHERE id = 1 FOR UPDATE NOWAIT;

-- skip locked rows (useful for job queues)
SELECT * FROM jobs WHERE status = 'NEW' LIMIT 1 FOR UPDATE SKIP LOCKED;

-- lock whole tables (rare)
LOCK TABLES accounts WRITE;
UNLOCK TABLES;
```

| Clause | Behavior |
|---|---|
| `FOR UPDATE` | Exclusive lock; blocks other `FOR UPDATE`/`FOR SHARE`/writes on those rows |
| `FOR SHARE` | Shared lock; blocks writers, allows other readers |
| `NOWAIT` | Error immediately instead of waiting |
| `SKIP LOCKED` | Ignore already-locked rows (workers pick different jobs) |

**Locks are held until the transaction ends** (`COMMIT`/`ROLLBACK`). `FOR UPDATE` only makes sense **inside a transaction**; with autocommit on, the lock is released immediately.

### Lock waits and timeouts

If a transaction needs a lock that another holds, it **waits**. After `innodb_lock_wait_timeout` (default **50 seconds**), it fails:

```
ERROR 1205 (HY000): Lock wait timeout exceeded; try restarting transaction
```

### Row locks need an index

InnoDB locks **index records**. If your `WHERE` column has **no usable index**, the database scans the table and **locks every row it examines** (under `REPEATABLE READ`, essentially the whole table).

```sql
-- no index on 'name': locks every row scanned, blocking other writers
UPDATE accounts SET balance = balance + 10 WHERE name = 'Asha';

-- primary key: locks exactly one row
UPDATE accounts SET balance = balance + 10 WHERE id = 1;
```

**Fix:** index the columns you filter on in write statements (`CREATE INDEX ... ON accounts(name)`).

### Metadata locks: the production outage trap

A long-running or forgotten open transaction holds a **metadata lock** on a table it touched. A later `ALTER TABLE` must wait for it, and while it waits, **every new query on that table queues behind the `ALTER`**, so the table appears to freeze.

**Avoid:** keep transactions short, never leave sessions open, and run schema changes in quiet periods with timeouts.

### Seeing locks

```sql
SHOW FULL PROCESSLIST;                              -- sessions and what they are doing
SHOW ENGINE INNODB STATUS\G                         -- includes the latest deadlock
SELECT * FROM performance_schema.data_locks;        -- current locks (MySQL 8)
SELECT * FROM performance_schema.data_lock_waits;   -- who waits for whom
SELECT * FROM sys.innodb_lock_waits;                -- a friendlier view
```

PostgreSQL: `pg_locks`, `pg_stat_activity`, `pg_blocking_pids(pid)`.

---

## 11. Deadlocks

### What is a deadlock

> Two (or more) transactions each hold a lock the other needs, so **neither can proceed**.

**Analogy:** two cars meet on a **one-lane bridge**; neither will reverse. Or two people each hold one chopstick and each waits for the other's.

### Example

| Time | T1 | T2 |
|---|---|---|
| 1 | `START TRANSACTION;` | `START TRANSACTION;` |
| 2 | `UPDATE accounts SET balance = balance - 100 WHERE id = 1;` (locks row 1) | |
| 3 | | `UPDATE accounts SET balance = balance - 50 WHERE id = 2;` (locks row 2) |
| 4 | `UPDATE accounts SET balance = balance + 100 WHERE id = 2;` **waits for T2** | |
| 5 | | `UPDATE accounts SET balance = balance + 50 WHERE id = 1;` **waits for T1: deadlock** |

```
T1 holds row 1, wants row 2
T2 holds row 2, wants row 1       <- a cycle: nobody can ever proceed
```

### What the database does

InnoDB **detects the cycle automatically**, picks a **victim** (usually the transaction with the least work), **rolls it back**, and lets the other continue:

```
ERROR 1213 (40001): Deadlock found when trying to get lock; try restarting transaction
```

(PostgreSQL: `ERROR: deadlock detected`, SQLSTATE `40P01`. Both advise retrying.)

> A deadlock is **a normal event in concurrent systems**, not a bug in the database. Your application must **expect it and retry**.

### Conditions

Mutual exclusion, hold-and-wait, no preemption, circular wait. Break one and it cannot occur.

### Prevention

| Strategy | How |
|---|---|
| **Consistent lock ordering** (the most effective) | Always touch rows/tables in the **same order**, for example lower `id` first |
| **Keep transactions short** | Fewer overlapping locks |
| **Lock everything up front, in order** | `SELECT ... WHERE id IN (1, 2) ORDER BY id FOR UPDATE` |
| **Use indexes** | Prevents scans from locking extra rows |
| **Avoid user interaction inside a transaction** | Long holds invite conflicts |
| **Lower the isolation level** where safe | Fewer gap and range locks |
| **Retry on failure** | Handle the remaining occurrences |

**Ordered transfer (prevents the deadlock above):**

```sql
START TRANSACTION;
SELECT * FROM accounts WHERE id IN (1, 2) ORDER BY id FOR UPDATE;    -- lock in id order
UPDATE accounts SET balance = balance - 100 WHERE id = 1;
UPDATE accounts SET balance = balance + 100 WHERE id = 2;
COMMIT;
```

Whether the transfer is 1 to 2 or 2 to 1, both transactions lock row 1 first, then row 2, so one simply waits.

### Handling a deadlock in code

```java
for (int attempt = 1; attempt <= 3; attempt++) {
    try {
        transferService.transfer(from, to, amount);     // a fresh @Transactional each time
        return;
    } catch (PessimisticLockingFailureException e) {    // deadlock or lock timeout (Spring)
        if (attempt == 3) throw e;
        Thread.sleep(50L * attempt);                    // small backoff
    }
}
```

Retry the **whole transaction** from the start, and make it **safe to repeat**.

### Related failures

| Problem | Meaning |
|---|---|
| **Lock wait timeout** (1205) | Waited too long (no cycle needed) |
| **Livelock** | Transactions keep retrying and failing without progress |
| **Starvation** | A transaction never gets a lock |

---

## 12. Preventing Lost Updates and Write Skew

### Pattern 1: atomic update in SQL (best for counters and balances)

```sql
UPDATE accounts
SET balance = balance - 200
WHERE id = 1 AND balance >= 200;          -- guard against overdraft in the same statement
-- check the affected row count: 1 = success, 0 = insufficient funds
```

The database **locks the row, reads the current value, and writes** in one step, so concurrent updates queue and none are lost. **No application-side read is needed.** This is safe at every isolation level.

### Pattern 2: pessimistic locking (`SELECT ... FOR UPDATE`)

"Lock first, then read, then write."

```sql
START TRANSACTION;
SELECT balance FROM accounts WHERE id = 1 FOR UPDATE;     -- others wait here
-- application computes the new value
UPDATE accounts SET balance = 1100 WHERE id = 1;
COMMIT;
```

**Analogy:** **checking out** a document so nobody else can edit it until you return it.

### Pattern 3: optimistic locking (version column)

"Don't lock; detect a conflict when saving."

```sql
-- table has a 'version' column
SELECT balance, version FROM accounts WHERE id = 1;                  -- balance 1000, version 5

UPDATE accounts
SET balance = 1100, version = version + 1
WHERE id = 1 AND version = 5;                                        -- succeeds only if still version 5
-- affected rows = 1: saved.   affected rows = 0: someone else changed it; reload and retry
```

**Analogy:** **Google Docs conflict check**: "someone modified this since you opened it; reload."

JPA does this with `@Version`:

```java
@Entity
public class Account {
    @Id private Long id;
    private int balance;

    @Version
    private Long version;                  // Hibernate adds "AND version = ?" to the UPDATE
}
// on conflict: ObjectOptimisticLockingFailureException (retry or tell the user)
```

### Optimistic vs pessimistic

| | Pessimistic (`FOR UPDATE`) | Optimistic (`version`) |
|---|---|---|
| Idea | Lock **before** working | Check for conflict **at save time** |
| Blocking | **Yes**, others wait | No blocking while working |
| Conflicts handled by | Waiting (or deadlock) | **Failing and retrying** |
| Best when | **High contention** (conflicts are common), short critical sections | **Low contention**, long "think time" (user edits a form) |
| Holds DB locks across user think time? | Would be harmful, so avoid | **No**, designed for it |
| Cost | Lock waits, deadlock risk | Retries when conflicts happen |

### Pattern 4: serializable isolation plus retry

```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

Catches lost updates **and write skew**; but expect **lock waits, deadlocks or serialization failures**, so you must retry.

### Fixing write skew (the on-call doctors)

```sql
START TRANSACTION;
SELECT * FROM doctors WHERE on_call = 1 FOR UPDATE;      -- lock ALL the rows this decision depends on
-- now check the count, then update
UPDATE doctors SET on_call = 0 WHERE name = 'Alice';
COMMIT;
```

The second transaction **waits** at its own `SELECT ... FOR UPDATE`, then sees the committed result (only one doctor left) and refuses to leave.

Other options: **serializable**, or a **single "gate" row** that every such transaction must lock, or an application invariant enforced by a unique/check constraint where possible.

### Choosing a pattern

| Situation | Use |
|---|---|
| Counter, balance, stock quantity | **Atomic `UPDATE`** |
| Read, complex logic, then write; contention likely | **`SELECT ... FOR UPDATE`** |
| User edits a form; conflicts are rare | **Optimistic version** |
| Rule spans multiple rows (write skew) | `FOR UPDATE` on the set, or **SERIALIZABLE** |
| Job queue with many workers | **`FOR UPDATE SKIP LOCKED`** |

---

## 13. Transactions in Java: JDBC and Spring/JPA

### Plain JDBC

```java
try (Connection con = dataSource.getConnection()) {
    con.setAutoCommit(false);                                  // begin a transaction
    con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
    try {
        debit.setInt(1, 300);  debit.setInt(2, 1);  debit.executeUpdate();
        credit.setInt(1, 300); credit.setInt(2, 2); credit.executeUpdate();
        con.commit();                                          // both succeed
    } catch (SQLException e) {
        con.rollback();                                        // undo both
        throw e;
    }
}
```

A transaction belongs to **one connection**; every statement in it must use **the same `Connection`**.

### Spring `@Transactional`

```java
@Service
public class TransferService {

    @Transactional
    public void transfer(Long fromId, Long toId, int amount) {
        Account from = accounts.findByIdForUpdate(fromId).orElseThrow();
        Account to   = accounts.findByIdForUpdate(toId).orElseThrow();

        if (from.getBalance() < amount) {
            throw new InsufficientFundsException();             // RuntimeException: rolls back
        }
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
    }                                                           // commit happens on return
}
```

Spring opens a transaction on a connection, binds it to the **current thread**, and **commits** on normal return or **rolls back** on a `RuntimeException`/`Error` (checked exceptions commit unless you set `rollbackFor`).

### `@Transactional` attributes

| Attribute | Meaning |
|---|---|
| `isolation` | `DEFAULT` (the database's default), `READ_COMMITTED`, `REPEATABLE_READ`, `SERIALIZABLE`... |
| `propagation` | How it joins an existing transaction (table below) |
| `readOnly = true` | A hint: no writes expected (optimizations) |
| `timeout` | Seconds before the transaction is rolled back |
| `rollbackFor` / `noRollbackFor` | Exception rules |

### Propagation

| Type | Behavior |
|---|---|
| **`REQUIRED`** (default) | Join the current transaction, or start one if none exists |
| **`REQUIRES_NEW`** | **Suspend** the current one and start a **brand-new, independent** transaction (commits or rolls back on its own; used for audit logging) |
| **`NESTED`** | A **savepoint** inside the current transaction (partial rollback) |
| `SUPPORTS` | Join if one exists; otherwise run without |
| `MANDATORY` | Must already be inside a transaction, or fail |
| `NOT_SUPPORTED` | Run without a transaction (suspends the current one) |
| `NEVER` | Fail if a transaction exists |

### Pessimistic locking in JPA

```java
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)                  // SELECT ... FOR UPDATE
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);
}
```

Lock timeout hint: `@QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))`.

### Exceptions to know

| Exception | Meaning |
|---|---|
| `ObjectOptimisticLockingFailureException` | `@Version` conflict |
| `PessimisticLockingFailureException` (and subclasses `CannotAcquireLockException`, `DeadlockLoserDataAccessException`) | Lock timeout or deadlock |
| `DataIntegrityViolationException` | Constraint or foreign-key violation (the Java side of errors 1451/1452) |
| `TransactionSystemException` | Commit or rollback problems |

### Pitfalls (also in the Spring/JPA notes)

- **Self-invocation:** calling a `@Transactional` method from the same class bypasses the proxy; no transaction.
- **Checked exceptions** do not roll back by default.
- **Swallowing an exception** inside the method prevents rollback.
- The transaction is **thread-bound**; new threads or `@Async` calls do not join it.
- **Retries** must wrap the transactional call from **outside** so each attempt gets a **new** transaction.
- `readOnly = true` is a hint, not a guarantee of safety.
- **Distributed transactions** across microservices cannot use one database transaction; use **sagas** with compensating actions (see the Microservices notes).

---

## 14. Transaction Best Practices

| Practice | Why |
|---|---|
| **Keep transactions short** | Locks are held until commit; long ones block others and bloat undo history |
| **Never wait for user input inside a transaction** | Holds locks across minutes of "thinking" |
| **Do not call slow external services (HTTP, email) while holding locks** | Same reason; do them after commit |
| **Access rows in a consistent order** | Prevents deadlocks |
| **Use the lowest isolation level that is safe** | Better concurrency |
| **Prefer atomic updates** (`balance = balance - x`) | Avoid read-modify-write races |
| **Lock explicitly where needed** (`FOR UPDATE`), and index the lookup columns | Precise, fast locks |
| **Expect and retry** deadlocks and serialization failures | They are normal |
| **Make operations idempotent** | Safe retries |
| **Index foreign keys and WHERE columns of writes** | Fewer rows locked |
| **Do not mix DDL into business transactions (MySQL)** | Implicit commits |
| **Batch large updates in chunks** | Smaller lock footprint |
| **Do not leave sessions open** (`idle in transaction`) | Metadata locks, blocked DDL, bloat |
| **Set timeouts** (`innodb_lock_wait_timeout`, `@Transactional(timeout=...)`) | Fail fast instead of hanging |
| **Test concurrency** with two sessions or a load tool | Races rarely show up in single-user tests |

---

## 15. Hands-On Lab: Reproduce Every Anomaly

Open **two terminals** with two MySQL sessions, **A** and **B**.

### Setup (once)

```sql
CREATE DATABASE IF NOT EXISTS lab;
USE lab;
DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (id INT PRIMARY KEY, name VARCHAR(50), balance INT) ENGINE=InnoDB;
INSERT INTO accounts VALUES (1, 'Asha', 1000), (2, 'Ravi', 500);
```

Before each experiment, in **both** sessions set the level you want to test:

```sql
SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;    -- then READ COMMITTED, REPEATABLE READ, SERIALIZABLE
```

and reset the data: `UPDATE accounts SET balance = 1000 WHERE id = 1; DELETE FROM accounts WHERE id = 3;`

### Experiment 1: dirty read

```sql
-- A:  START TRANSACTION;
-- A:  UPDATE accounts SET balance = 0 WHERE id = 1;
-- B:  START TRANSACTION;
-- B:  SELECT balance FROM accounts WHERE id = 1;       <- what does B see?
-- A:  ROLLBACK;
-- B:  COMMIT;
```

| Level | B sees |
|---|---|
| READ UNCOMMITTED | **0** (dirty read) |
| READ COMMITTED | 1000 |
| REPEATABLE READ | 1000 |
| SERIALIZABLE | B's `SELECT` **blocks** until A finishes (or times out with 1205) |

### Experiment 2: non-repeatable read

```sql
-- A:  START TRANSACTION;
-- A:  SELECT balance FROM accounts WHERE id = 1;       -- 1000
-- B:  UPDATE accounts SET balance = 500 WHERE id = 1;  (autocommit: committed immediately)
-- A:  SELECT balance FROM accounts WHERE id = 1;       <- what does A see now?
-- A:  COMMIT;
```

| Level | A's second read |
|---|---|
| READ UNCOMMITTED / READ COMMITTED | **500** (non-repeatable read) |
| REPEATABLE READ | **1000** (snapshot) |
| SERIALIZABLE | B's `UPDATE` **blocks** (A's read took a shared lock) until A commits |

### Experiment 3: phantom read

```sql
-- A:  START TRANSACTION;
-- A:  SELECT COUNT(*) FROM accounts WHERE balance > 400;      -- 2
-- B:  INSERT INTO accounts VALUES (3, 'Neha', 900);           (autocommit)
-- A:  SELECT COUNT(*) FROM accounts WHERE balance > 400;      <- ?
-- A:  SELECT COUNT(*) FROM accounts WHERE balance > 400 FOR UPDATE;   <- ? (a locking read)
-- A:  COMMIT;
```

| Level | A's plain re-read | Locking re-read (`FOR UPDATE`) |
|---|---|---|
| READ COMMITTED | **3** (phantom) | 3 |
| REPEATABLE READ | **2** (snapshot hides it) | **3** (current read sees it) |
| SERIALIZABLE | B's `INSERT` **blocks** until A commits | n/a |

To see gap locks stop an insert: at `REPEATABLE READ`, run `SELECT * FROM accounts WHERE balance BETWEEN 400 AND 2000 FOR UPDATE;` in A, then `INSERT` a row in that range from B. B **waits**.

### Experiment 4: lost update

```sql
-- A:  START TRANSACTION;   SELECT balance FROM accounts WHERE id = 1;        -- 1000
-- B:  START TRANSACTION;   SELECT balance FROM accounts WHERE id = 1;        -- 1000
-- A:  UPDATE accounts SET balance = 1100 WHERE id = 1;  COMMIT;              -- +100 computed in the "app"
-- B:  UPDATE accounts SET balance = 800  WHERE id = 1;  COMMIT;              -- -200 computed in the "app"
-- either:  SELECT balance FROM accounts WHERE id = 1;
```

Result at READ COMMITTED **and** REPEATABLE READ: **800** (A's deposit lost; the correct value is 900).

**Now repeat with atomic updates:**

```sql
-- A:  START TRANSACTION;  UPDATE accounts SET balance = balance + 100 WHERE id = 1;
-- B:  START TRANSACTION;  UPDATE accounts SET balance = balance - 200 WHERE id = 1;   <- BLOCKS
-- A:  COMMIT;                                                                          <- B proceeds
-- B:  COMMIT;
-- final: 900  (no lost update)
```

### Experiment 5: lock wait and timeout

```sql
-- A:  START TRANSACTION;  UPDATE accounts SET balance = balance + 1 WHERE id = 1;     (do not commit)
-- B:  UPDATE accounts SET balance = balance + 1 WHERE id = 1;     <- hangs ... after 50 s: ERROR 1205
-- A:  COMMIT;     (if you do this before the timeout, B continues immediately)
```

Check what is happening from a third session:

```sql
SELECT * FROM sys.innodb_lock_waits\G
SHOW FULL PROCESSLIST;
```

### Experiment 6: deadlock

```sql
-- A:  START TRANSACTION;  UPDATE accounts SET balance = balance - 1 WHERE id = 1;
-- B:  START TRANSACTION;  UPDATE accounts SET balance = balance - 1 WHERE id = 2;
-- A:  UPDATE accounts SET balance = balance + 1 WHERE id = 2;     <- waits for B
-- B:  UPDATE accounts SET balance = balance + 1 WHERE id = 1;     <- ERROR 1213: Deadlock found
```

One session is rolled back automatically. Look at the details:

```sql
SHOW ENGINE INNODB STATUS\G          -- see the section "LATEST DETECTED DEADLOCK"
```

**Fix it:** repeat, but make both sessions touch **id 1 then id 2** in the same order; no deadlock.

### Experiment 7: `SELECT ... FOR UPDATE` and `SKIP LOCKED`

```sql
CREATE TABLE jobs (id INT PRIMARY KEY, status VARCHAR(10));
INSERT INTO jobs VALUES (1,'NEW'), (2,'NEW'), (3,'NEW');

-- A:  START TRANSACTION;  SELECT * FROM jobs WHERE status='NEW' ORDER BY id LIMIT 1 FOR UPDATE SKIP LOCKED;   -- job 1
-- B:  START TRANSACTION;  SELECT * FROM jobs WHERE status='NEW' ORDER BY id LIMIT 1 FOR UPDATE SKIP LOCKED;   -- job 2 (skips locked row 1)
```

Two workers take **different** jobs with no waiting: the standard pattern for database-backed queues.

### Experiment 8: cascade behaviors

Create the `orders` / `order_items` tables from section 4 with `ON DELETE CASCADE`, delete an order, and confirm the items vanish. Then recreate the foreign key with `RESTRICT` and `SET NULL` and observe errors 1451 and the NULL-ing.

---

## 16. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| `WHERE column = NULL` | Never matches | `IS NULL` |
| `NOT IN (subquery containing NULL)` | Returns nothing | `NOT EXISTS`, or filter NULLs |
| `UPDATE`/`DELETE` without `WHERE` | Changes every row | Test with `SELECT` first; use a transaction |
| `SELECT *` and no `ORDER BY` | Undefined order | Always `ORDER BY` when order matters |
| Using `HAVING` instead of `WHERE` for plain filters | Slower, confusing | Filter rows with `WHERE` |
| Assuming `ROLLBACK` undoes autocommitted statements | They are already committed | Use `START TRANSACTION` |
| DDL inside a MySQL transaction | Implicit commit | Keep DDL separate |
| Read value, compute in app, write back | **Lost update** | Atomic `UPDATE`, `FOR UPDATE`, or `@Version` |
| Assuming `REPEATABLE READ` stops lost updates or write skew | Snapshot reads do not | Explicit locking or `SERIALIZABLE` |
| Relying on the default isolation across databases | MySQL RR vs PostgreSQL RC | Set the level or design for the weakest |
| Using `READ UNCOMMITTED` "for speed" | Dirty reads, wrong reports | Use `READ COMMITTED` |
| `SELECT ... FOR UPDATE` with autocommit on | Lock released instantly | Wrap in a transaction |
| `UPDATE ... WHERE` on an unindexed column | Locks many rows, blocks others | Add an index |
| Inconsistent lock order | **Deadlocks** | Always lock in the same order |
| Not retrying after deadlock or serialization failure | Random user-facing errors | Retry loop |
| Long transactions, or user input inside one | Lock pile-ups, undo bloat | Short, focused transactions |
| Calling HTTP or email services inside a transaction | Locks held during slow I/O | Do it after commit |
| Open "idle in transaction" sessions | Metadata locks block `ALTER TABLE` | Close or commit; timeouts |
| `CASCADE` deletes on business-critical data | Accidental mass deletion | `RESTRICT` plus soft delete |
| `SET NULL` on a `NOT NULL` column | Foreign key definition fails | Make the column nullable |
| Confusing JPA cascade with DB cascade | Surprises in raw-SQL deletes | Know where each rule lives |
| Treating `TRUNCATE` like `DELETE` | Not rollback-able in MySQL; resets auto-increment | Use `DELETE` when you need a transaction |
| `@Transactional` on a private or self-invoked method | No transaction | Public method, another bean |
| Checked exception without `rollbackFor` | Commits | Configure rollback rules |
| Retrying inside the same transaction | The transaction is already poisoned | Retry around a **new** transaction |
| Over-indexing | Slower writes, more space | Index by access pattern |
| Treating MyISAM as transactional | No transactions or foreign keys | Use InnoDB |

---

## 17. Puzzles

**Puzzle 1: snapshot vs latest** (InnoDB, `REPEATABLE READ`)

```sql
-- balance of id 1 is 1000
-- A: START TRANSACTION;
-- A: SELECT balance FROM accounts WHERE id = 1;          -- (a)
-- B: UPDATE accounts SET balance = 500 WHERE id = 1;     (autocommit)
-- A: SELECT balance FROM accounts WHERE id = 1;          -- (b)
-- A: SELECT balance FROM accounts WHERE id = 1 FOR UPDATE;   -- (c)
```

**Puzzle 2:** the same steps at `READ COMMITTED`: what are (a), (b) and (c)?

**Puzzle 3: concurrent atomic updates**

```sql
-- balance = 1000.  A and B each run, at the same time, in their own transactions:
-- UPDATE accounts SET balance = balance - 100 WHERE id = 1;
-- What is the final balance, and can an update be lost?
```

**Puzzle 4: which anomaly is this?**

```sql
-- T1: SELECT SUM(balance) FROM accounts;                          -- 1500
-- T2: INSERT INTO accounts VALUES (3, 'Neha', 900); COMMIT;
-- T1: SELECT SUM(balance) FROM accounts;                          -- 2400  (same transaction)
```

**Puzzle 5: `NOT IN` with NULL**

```sql
SELECT dept_name FROM departments
WHERE dept_id NOT IN (SELECT dept_id FROM employees);
-- employees.dept_id contains 1, 1, 2 and NULL.
```

**Puzzle 6: autocommit**

```sql
-- autocommit is ON (the default), no START TRANSACTION
UPDATE accounts SET balance = 0 WHERE id = 1;
ROLLBACK;
SELECT balance FROM accounts WHERE id = 1;
```

**Puzzle 7: DDL in a transaction (MySQL)**

```sql
START TRANSACTION;
INSERT INTO accounts VALUES (9, 'Test', 100);
ALTER TABLE accounts ADD COLUMN note VARCHAR(20);
ROLLBACK;
SELECT COUNT(*) FROM accounts WHERE id = 9;
```

**Puzzle 8: foreign key actions**

```sql
-- employees.dept_id references departments(dept_id), no ON DELETE clause
DELETE FROM departments WHERE dept_id = 1;
-- (IT has employees)    and then:
DELETE FROM departments WHERE dept_id = 3;
-- (Finance has no employees)
```

**Puzzle 9: write skew**

Two doctors are on call. Each transaction checks `COUNT(*) FROM doctors WHERE on_call = 1` (returns 2) and sets their own `on_call = 0`. Both run at `REPEATABLE READ`. How many are on call afterward, and why did no row conflict occur?

**Puzzle 10: savepoint**

```sql
START TRANSACTION;
INSERT INTO accounts VALUES (3, 'Neha', 800);
SAVEPOINT s1;
INSERT INTO accounts VALUES (4, 'Kiran', 900);
ROLLBACK TO SAVEPOINT s1;
COMMIT;
SELECT id FROM accounts WHERE id IN (3, 4);
```

**Puzzle 11: the unindexed update**

Session A runs `START TRANSACTION; UPDATE accounts SET balance = balance + 1 WHERE name = 'Asha';` on a table where `name` has no index, and keeps the transaction open. Session B runs `UPDATE accounts SET balance = balance + 1 WHERE id = 2;`. What happens, and why?

**Answers**

1. (a) **1000**; (b) **1000** (snapshot); (c) **500**. The locking read is a *current read*: it sees the latest committed data, not the snapshot.
2. (a) **1000**; (b) **500** (`READ COMMITTED` takes a new snapshot per statement, a non-repeatable read); (c) **500**.
3. **800**, with no lost update. Each `UPDATE` locks the row and reads its current value, so the second waits for the first to commit, then subtracts from 900.
4. A **phantom read** (a new row appeared in a repeated aggregate query). At InnoDB `REPEATABLE READ` the second `SUM` would still return 1500 (snapshot); at `READ COMMITTED` it would return 2400.
5. **No rows**, even though Finance has no employees. `3 NOT IN (1, 1, 2, NULL)` is unknown. Fix with `NOT EXISTS` or by excluding NULLs in the subquery.
6. **0**. With autocommit on, the `UPDATE` committed immediately; `ROLLBACK` had nothing to undo.
7. **1**. The `ALTER TABLE` caused an **implicit commit**, so the earlier `INSERT` was committed and `ROLLBACK` did nothing.
8. The first `DELETE` fails with **`ERROR 1451`** (the default is `RESTRICT`, because IT has employees). The second succeeds (Finance has no children).
9. **Zero** doctors on call. Each transaction updated a **different row**, so there was no write-write conflict, and each read an old snapshot that still showed the other doctor on call. Fix with `SELECT ... FOR UPDATE` on the on-call rows or `SERIALIZABLE`.
10. Only **3** appears. `ROLLBACK TO SAVEPOINT s1` undid Kiran's insert, and `COMMIT` saved Neha's.
11. **B blocks.** With no index on `name`, A's `UPDATE` scans the table and locks every row it examines (under `REPEATABLE READ`, effectively all rows), including `id = 2`. Adding an index on `name` lets A lock only the matching row.

---

## 18. Interview Questions

**1. What is a transaction?** A group of SQL operations executed as one unit: all succeed (commit) or none do (rollback).

**2. What are the ACID properties?** Atomicity (all or nothing), Consistency (valid state to valid state), Isolation (concurrent transactions do not interfere), Durability (committed data survives crashes).

**3. How are atomicity and durability implemented?** Atomicity via an undo log; durability via a redo/write-ahead log flushed to disk.

**4. What is autocommit?** The mode in which each statement is its own transaction, committed immediately.

**5. `COMMIT` vs `ROLLBACK` vs `SAVEPOINT`?** Commit makes changes permanent; rollback undoes them; a savepoint lets you roll back part of a transaction.

**6. Which statements cause an implicit commit in MySQL?** DDL such as `CREATE`, `ALTER`, `DROP`, `TRUNCATE`.

**7. What are transaction isolation levels?** READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE, trading anomaly protection for concurrency.

**8. What is a dirty read?** Reading another transaction's uncommitted changes, which may later be rolled back.

**9. What is a non-repeatable read?** Reading the same row twice in one transaction and getting different values because another transaction committed a change in between.

**10. What is a phantom read?** Re-running a query in one transaction and getting a different set of rows because another transaction inserted or deleted matching rows.

**11. Non-repeatable read vs phantom read?** Non-repeatable: an existing row's values changed. Phantom: rows appear or disappear from the result set.

**12. What is a lost update?** Two transactions read the same value and both write computed values; the later write overwrites the earlier one.

**13. What is write skew?** Two transactions read overlapping data, decide based on it, and update different rows, jointly violating a rule neither violated alone (the on-call doctors).

**14. Which isolation levels prevent which anomalies?** READ COMMITTED prevents dirty reads; REPEATABLE READ also prevents non-repeatable reads; SERIALIZABLE also prevents phantoms (and write skew in practice).

**15. What is the default isolation level in MySQL, PostgreSQL, Oracle and SQL Server?** MySQL InnoDB: REPEATABLE READ. The others: READ COMMITTED.

**16. Does REPEATABLE READ in InnoDB prevent phantom reads?** For plain consistent reads, yes (snapshot); for locking reads it uses next-key locks to prevent them.

**17. Does REPEATABLE READ prevent lost updates?** Not for read-modify-write patterns done in application code; use atomic updates, `FOR UPDATE` or optimistic locking.

**18. What is MVCC?** Multi-Version Concurrency Control: the database keeps multiple row versions so readers see a consistent snapshot without blocking writers.

**19. Consistent read vs locking read?** A plain `SELECT` reads a snapshot without locks; `SELECT ... FOR UPDATE/SHARE`, `UPDATE`, `DELETE` read the latest data and take locks.

**20. What is a shared lock vs an exclusive lock?** Shared: many readers may hold it. Exclusive: only one holder, for writing; it conflicts with everything.

**21. Row-level vs table-level locks?** Row-level locks affect individual rows (high concurrency, more overhead); table-level locks lock the whole table (simple, low concurrency).

**22. What are intent locks?** Table-level markers (IS, IX) showing that row locks exist or are intended, enabling fast checks for table-level lock conflicts.

**23. What are record, gap and next-key locks?** A record lock locks an index record; a gap lock locks the space between records; a next-key lock is a record plus the preceding gap, preventing phantoms.

**24. What does `SELECT ... FOR UPDATE` do?** Locks the selected rows exclusively until the transaction ends so others cannot modify (or lock) them.

**25. What do `NOWAIT` and `SKIP LOCKED` do?** `NOWAIT` errors immediately if a row is locked; `SKIP LOCKED` ignores locked rows, ideal for job queues.

**26. What is a deadlock, and how does the database handle it?** Transactions waiting for each other's locks in a cycle; the database detects it, rolls back one victim (error 1213) and lets the other proceed.

**27. How do you prevent deadlocks?** Consistent lock ordering, short transactions, indexes, lower isolation where safe, and retry logic.

**28. What is a lock wait timeout?** The error (1205) when a transaction waits longer than `innodb_lock_wait_timeout` for a lock.

**29. Why do unindexed updates cause lock problems?** InnoDB locks the index records it scans; with no index it scans and locks many rows.

**30. Optimistic vs pessimistic locking?** Pessimistic locks first (`FOR UPDATE`) and blocks others; optimistic does not lock and detects conflicts at write time using a version column, retrying on failure.

**31. When would you choose each?** Pessimistic for high contention and short critical sections; optimistic for low contention and long user think time.

**32. How does JPA implement optimistic locking?** `@Version` adds a version check to the `UPDATE`; a mismatch raises `ObjectOptimisticLockingFailureException`.

**33. How do you prevent lost updates in SQL?** An atomic `UPDATE ... SET x = x - n`, `SELECT ... FOR UPDATE`, an optimistic version column, or `SERIALIZABLE` with retry.

**34. What does `@Transactional` do, and how does it roll back?** Wraps a method in a transaction; commits on success and rolls back on unchecked exceptions by default.

**35. `REQUIRED` vs `REQUIRES_NEW`?** `REQUIRED` joins the existing transaction; `REQUIRES_NEW` suspends it and runs an independent transaction.

**36. What are cascading referential actions?** Foreign-key rules (`CASCADE`, `SET NULL`, `RESTRICT`, `NO ACTION`) for what happens to child rows when the parent is deleted or updated.

**37. `ON DELETE CASCADE` vs `SET NULL` vs `RESTRICT`?** Cascade deletes the children; `SET NULL` keeps them with a NULL foreign key; `RESTRICT` blocks the delete.

**38. What is the default if `ON DELETE` is omitted?** `RESTRICT` / `NO ACTION`.

**39. DB cascade vs JPA cascade?** DB cascade is enforced by the database for any client; JPA cascade is performed by Hibernate, only through the application.

**40. `DELETE` vs `TRUNCATE` vs `DROP`?** `DELETE` removes selected rows and is transactional; `TRUNCATE` removes all rows quickly (DDL, resets auto-increment, no rollback in MySQL); `DROP` removes the table.

**41. `WHERE` vs `HAVING`?** `WHERE` filters rows before grouping; `HAVING` filters groups after aggregation.

**42. What are the types of joins?** Inner, left, right, full outer, cross, and self joins.

**43. What is normalization, and what are 1NF, 2NF and 3NF?** Organizing data to avoid redundancy: atomic values; no partial dependencies; no transitive dependencies.

**44. What is an index, and what is a clustered index?** A structure that speeds lookups; in InnoDB the primary key is the clustered index and the table is stored in that order.

**45. What is the leftmost-prefix rule?** A composite index `(a, b)` helps queries filtering on `a` or on `a` and `b`, not on `b` alone.

**46. Why is `NOT IN` risky with NULLs?** If the subquery returns a NULL, the comparison becomes unknown and the query returns no rows.

**47. What is a metadata lock, and why can it cause outages?** A lock on a table definition; an open transaction can block an `ALTER TABLE`, which then blocks all later queries on that table.

**48. Why should transactions be short?** Locks are held until commit, so long transactions block others, raise deadlock risk and bloat undo history.

---

## 19. Cheat Sheet

**ACID**

```
Atomicity   all or nothing          (undo log)
Consistency valid state to valid     (constraints + your rules)
Isolation   no interference          (locks / MVCC; chosen level)
Durability  survives crashes         (redo log / WAL)
```

**Anomalies**

| Anomaly | One-liner | Prevented at (standard) |
|---|---|---|
| **Dirty read** | Read uncommitted data | READ COMMITTED+ |
| **Non-repeatable read** | Same row, different value | REPEATABLE READ+ |
| **Phantom read** | Same query, different rows | SERIALIZABLE (InnoDB RR for plain reads) |
| **Lost update** | Overwritten read-modify-write | Atomic update, `FOR UPDATE`, version, SERIALIZABLE |
| **Write skew** | Disjoint updates break a rule | `FOR UPDATE` on the set, SERIALIZABLE |

**Isolation levels**

| Level | Dirty | Non-repeatable | Phantom |
|---|---|---|---|
| READ UNCOMMITTED | Yes | Yes | Yes |
| READ COMMITTED | No | Yes | Yes |
| REPEATABLE READ | No | No | Yes (standard) / No (InnoDB plain reads) |
| SERIALIZABLE | No | No | No |

**Defaults:** MySQL InnoDB = REPEATABLE READ; PostgreSQL, Oracle, SQL Server = READ COMMITTED.

**Locks**

```
S (shared)    many readers            X (exclusive)   one writer
Row lock      high concurrency        Table lock      low concurrency
Record / Gap / Next-key (InnoDB)      Intent IS/IX    table-level hints
FOR UPDATE   exclusive                FOR SHARE       shared
NOWAIT       fail now                 SKIP LOCKED     skip locked rows
```

**Referential actions**

| Action | Parent deleted |
|---|---|
| `RESTRICT` / `NO ACTION` (default) | Blocked if children exist |
| `CASCADE` | Children deleted too |
| `SET NULL` | Children kept, FK becomes NULL |

**Errors to recognize**

```
1451  cannot delete/update parent row (FK)      1452  cannot add/update child row (FK)
1213  deadlock found, restart transaction       1205  lock wait timeout exceeded
```

**Golden rules**

```
1. Short transactions; no user input or slow I/O inside them.
2. Atomic UPDATEs (balance = balance - x) beat read-modify-write.
3. Lock in a consistent order; expect deadlocks; retry the whole transaction.
4. Index the columns your writes filter on, or you lock too many rows.
5. REPEATABLE READ does NOT stop lost updates or write skew by itself.
6. Same level name, different behavior per database: know yours.
7. Prefer RESTRICT (plus soft delete) for important data; CASCADE only for true children.
8. In MySQL, DDL commits implicitly; autocommit means ROLLBACK may have nothing to undo.
```

---

## 20. Practice Tasks with Solutions

**1. Basic queries.** Using the sample `employees` and `departments` tables, write queries for:

a. Employees earning more than 60000, highest first.
b. The number of employees in each department.
c. Departments with no employees.
d. The second-highest salary.
e. Employees with no department.

```sql
-- a
SELECT emp_name, salary FROM employees WHERE salary > 60000 ORDER BY salary DESC;

-- b
SELECT d.dept_name, COUNT(*) AS emp_count
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
GROUP BY d.dept_name;

-- c
SELECT d.dept_name
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
WHERE e.emp_id IS NULL;

-- d
SELECT MAX(salary) FROM employees WHERE salary < (SELECT MAX(salary) FROM employees);

-- e
SELECT emp_name FROM employees WHERE dept_id IS NULL;
```

**2. Foreign key behaviors.** Create `departments` and `employees` three times with `ON DELETE RESTRICT`, `ON DELETE SET NULL` and `ON DELETE CASCADE`. Delete the IT department each time and record what happens to the employees (error 1451, NULLs, or deleted).

**3. Bank transfer transaction.** Write a transfer of 300 from account 1 to account 2 that cannot overdraw:

```sql
START TRANSACTION;

UPDATE accounts SET balance = balance - 300 WHERE id = 1 AND balance >= 300;
-- if 0 rows were affected: ROLLBACK and report "insufficient funds"

UPDATE accounts SET balance = balance + 300 WHERE id = 2;

COMMIT;
```

**4. Reproduce all three read anomalies** with the lab in section 15, at each isolation level, and fill in a table of results from your own runs.

**5. Lost update and its fixes.** Reproduce the lost update (experiment 4). Then fix it three ways: atomic `UPDATE`, `SELECT ... FOR UPDATE`, and a `version` column.

```sql
-- fix with FOR UPDATE
START TRANSACTION;
SELECT balance FROM accounts WHERE id = 1 FOR UPDATE;
UPDATE accounts SET balance = balance + 100 WHERE id = 1;
COMMIT;

-- fix with optimistic locking
ALTER TABLE accounts ADD COLUMN version INT NOT NULL DEFAULT 0;
UPDATE accounts SET balance = 1100, version = version + 1 WHERE id = 1 AND version = 0;
-- affected rows 0 means a conflict: reload and retry
```

**6. Deadlock.** Produce a deadlock with two sessions (experiment 6), read `SHOW ENGINE INNODB STATUS`, then remove it by ordering the updates.

**7. Write skew.** Create a `doctors(name, on_call)` table with two doctors on call. Show the write skew at `REPEATABLE READ`, then fix it with `SELECT ... FOR UPDATE`.

**8. Job queue.** Build a `jobs` table and have two sessions claim jobs with `FOR UPDATE SKIP LOCKED` without blocking each other. Then update the claimed job's status to `DONE` and commit.

**9. Index and locking.** Create an `accounts` table of 1,000 rows without an index on `name`. In session A run `UPDATE ... WHERE name = 'x'` in an open transaction; test whether B can update a different row. Add `CREATE INDEX idx_name ON accounts(name)` and repeat.

**10. Spring/JPA.** Implement `transfer(fromId, toId, amount)` with `@Transactional`, a pessimistic `findByIdForUpdate`, **ordered locking by id**, and a caller-side retry for `PessimisticLockingFailureException`. Then replace pessimistic locking with `@Version` and compare behavior under 50 concurrent transfers using a thread pool; verify that the **total money is unchanged**.

### Challenge questions

1. A banking report runs at `READ COMMITTED` and sums balances across two queries; the totals sometimes disagree with the ledger. What anomaly is it, and how would you fix it?
2. A service claims "we use `REPEATABLE READ`, so lost updates cannot happen", yet two concurrent deposits sometimes lose one. What is wrong?
3. A deployment's `ALTER TABLE` brought the application to a halt for minutes, though the alter itself is fast. What is the likely cause?
4. Deleting one customer is slow and locks the application for several seconds. The schema has `ON DELETE CASCADE` across four levels. What do you check?

**Answers**

1. **Non-repeatable reads/read skew**: between the two queries another transaction committed changes, so the sums come from different moments. Fix: run both queries in one transaction at **`REPEATABLE READ`** (a stable snapshot), or compute the total in a single query.
2. `REPEATABLE READ` snapshot reads do not stop **read-modify-write** races done in application code: both transactions read the same value and write computed values. Use an atomic `UPDATE balance = balance + x`, `SELECT ... FOR UPDATE`, or an optimistic `@Version`.
3. The `ALTER` waited for a **metadata lock** held by a long-running or idle open transaction, and every new query queued **behind the waiting ALTER**. Find and close the idle transaction (`SHOW PROCESSLIST`), use short transactions, and set lock timeouts for DDL.
4. The cascade deletes a **large tree of rows in one transaction** and locks them all. Check the row counts at each level, ensure the **foreign key columns are indexed**, delete in **batches** or use **soft delete**, and consider `RESTRICT` with an explicit, controlled cleanup job instead of a deep cascade.

---

*End of notes.*
