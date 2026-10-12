# JDBC, JPA and Spring Data JPA: Notes

Short but detailed. Covers how Java talks to a database, from raw JDBC to Spring Data JPA, with the important annotations in quick-scan tables.

---

## Table of Contents

1. [The Big Picture](#1-the-big-picture)
2. [JDBC](#2-jdbc)
3. [ORM, JPA and Hibernate](#3-orm-jpa-and-hibernate)
4. [Entity Mapping Annotations](#4-entity-mapping-annotations)
5. [Relationships](#5-relationships)
6. [Entity Lifecycle and Persistence Context](#6-entity-lifecycle-and-persistence-context)
7. [Spring Data JPA](#7-spring-data-jpa)
8. [Queries: Derived, JPQL, Native](#8-queries-derived-jpql-native)
9. [Transactions](#9-transactions)
10. [Spring Boot Configuration](#10-spring-boot-configuration)
11. [End-to-End Example](#11-end-to-end-example)
12. [Annotation Cheat Sheet](#12-annotation-cheat-sheet)
13. [Common Mistakes](#13-common-mistakes)
14. [Interview Questions](#14-interview-questions)
15. [Practice Tasks](#15-practice-tasks)

---

## 1. The Big Picture

Java objects live in memory; data lives in tables. Something must translate between them. There are layers, each hiding the one below:

```
Controller  ->  Service  ->  Repository (Spring Data JPA)
                                  |
                                JPA (specification)
                                  |
                              Hibernate (implementation)
                                  |
                                JDBC  (always at the bottom)
                                  |
                           DB driver -> Database
```

| Layer | What it is | You write |
|---|---|---|
| **JDBC** | Low-level Java API for SQL | SQL, connections, result-set loops |
| **Spring JDBC** (`JdbcTemplate`) | JDBC without the boilerplate | SQL only |
| **JPA** | A **specification** (interfaces and annotations) for ORM | Entities and annotations |
| **Hibernate** | The most popular JPA **implementation** | Usually nothing directly |
| **Spring Data JPA** | Generates repository implementations on top of JPA | Just interfaces |

**Analogy: sending a parcel.** JDBC is packing, addressing and carrying it yourself. JPA/Hibernate is a courier service. Spring Data JPA is the app where you tap "send".

Even with Spring Data JPA, JDBC still runs underneath. Understanding it explains the errors.

---

## 2. JDBC

**JDBC (Java Database Connectivity)** is the standard API (`java.sql`) for running SQL from Java. The driver (e.g. MySQL Connector/J) translates calls for each database.

### Core types

| Type | Role |
|---|---|
| `DriverManager` | Creates connections |
| `Connection` | A session with the database |
| `Statement` | Runs a fixed SQL string |
| `PreparedStatement` | Runs **precompiled SQL with `?` parameters** |
| `CallableStatement` | Calls stored procedures |
| `ResultSet` | Rows returned by a SELECT |

### Steps

1. Add the driver dependency (loaded automatically in modern JDBC).
2. Open a `Connection`.
3. Create a `PreparedStatement`.
4. Execute it.
5. Process the `ResultSet` (for SELECT).
6. Close everything (use try-with-resources).

### SELECT

```java
String url = "jdbc:mysql://localhost:3306/company";
String sql = "SELECT id, name, salary FROM employees WHERE salary > ?";

try (Connection con = DriverManager.getConnection(url, "root", "password");
     PreparedStatement ps = con.prepareStatement(sql)) {

    ps.setDouble(1, 50000);                       // parameters are 1-indexed

    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getInt("id") + " "
                + rs.getString("name") + " " + rs.getDouble("salary"));
        }
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

### INSERT / UPDATE / DELETE

```java
String sql = "INSERT INTO employees(name, salary) VALUES (?, ?)";
try (Connection con = DriverManager.getConnection(url, "root", "password");
     PreparedStatement ps = con.prepareStatement(sql)) {

    ps.setString(1, "Asha");
    ps.setDouble(2, 55000);
    int rows = ps.executeUpdate();                // number of affected rows
}
```

### Which execute method?

| Method | Use for | Returns |
|---|---|---|
| `executeQuery()` | SELECT | `ResultSet` |
| `executeUpdate()` | INSERT, UPDATE, DELETE, DDL | `int` (rows affected) |
| `execute()` | Unknown or mixed | `boolean` |

### Statement vs PreparedStatement

| `Statement` | `PreparedStatement` |
|---|---|
| SQL built by string concatenation | SQL with `?` placeholders |
| **Vulnerable to SQL injection** | **Safe**: values are never parsed as SQL |
| Compiled every time | Precompiled and reusable (faster) |

```java
// DANGEROUS: name = "x' OR '1'='1" returns every row
"SELECT * FROM users WHERE name = '" + name + "'"
```

**Always use `PreparedStatement`.**

### Transactions in JDBC

A transaction is **all or nothing**. Bank transfer: debit and credit must both happen or neither.

```java
con.setAutoCommit(false);              // default is true (each statement commits)
try {
    debit.executeUpdate();
    credit.executeUpdate();
    con.commit();
} catch (SQLException e) {
    con.rollback();
}
```

### Why JDBC alone is painful

- Repetitive boilerplate: open, close, loop, map columns to fields.
- Manual object mapping (`rs.getString(...)` into setters).
- Checked `SQLException` everywhere.
- Hard-coded, database-specific SQL.

### Spring JDBC (`JdbcTemplate`)

Removes connection handling and exception noise, but you still write SQL.

```java
@Autowired JdbcTemplate jdbc;

int rows = jdbc.update("INSERT INTO employees(name, salary) VALUES (?, ?)", "Asha", 55000);

List<String> names = jdbc.query(
    "SELECT name FROM employees WHERE salary > ?",
    (rs, rowNum) -> rs.getString("name"),      // RowMapper
    50000);
```

---

## 3. ORM, JPA and Hibernate

**ORM (Object-Relational Mapping)** maps classes to tables so you work with objects, not SQL rows.

| Object world | Database world |
|---|---|
| Class | Table |
| Object | Row |
| Field | Column |
| Reference to another object | Foreign key |
| Collection | Child rows |

**Why:** the object model and relational model differ (inheritance, references vs foreign keys, collections). ORM bridges that gap and generates the SQL for you.

### JPA vs Hibernate vs Spring Data JPA

| | What |
|---|---|
| **JPA** | The standard (`jakarta.persistence.*`): annotations, `EntityManager`, JPQL |
| **Hibernate** | A JPA implementation that actually does the work (EclipseLink is another) |
| **Spring Data JPA** | Auto-implements repositories using JPA |

> Spring Boot 3 uses `jakarta.persistence`. Older code uses `javax.persistence`; that won't compile on Boot 3.

### Entity rules

- Annotated with `@Entity`.
- Has an `@Id` field.
- Has a **no-arg constructor** (`public` or `protected`).
- Class is **not `final`**.
- **Records cannot be entities** (use them for DTOs).

---

## 4. Entity Mapping Annotations

```java
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String name;

    @Column(unique = true)
    private String email;

    private double salary;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDate joiningDate;

    @Transient
    private double annualSalary;          // not stored

    @Version
    private Long version;                 // optimistic locking

    protected Employee() { }              // required by JPA
    // constructor, getters, setters
}
```

| Annotation | Purpose |
|---|---|
| `@Entity` | Marks a class as a table-backed entity |
| `@Table(name, schema, uniqueConstraints)` | Table name and constraints (default: class name) |
| `@Id` | Primary key |
| `@GeneratedValue(strategy)` | Auto-generated key |
| `@Column(name, nullable, unique, length, precision, scale)` | Column details |
| `@Transient` | Field is **not** persisted |
| `@Enumerated(EnumType.STRING)` | Store enums by name |
| `@Lob` | Large text or binary |
| `@Version` | Optimistic-lock version counter |
| `@Embeddable` / `@Embedded` | Reusable value object whose fields become columns of the owner table |
| `@MappedSuperclass` | Base class whose fields are inherited by entities (not itself a table) |
| `@Temporal` | Only for old `java.util.Date`; not needed with `java.time` types |

### `@GeneratedValue` strategies

| Strategy | Meaning |
|---|---|
| `IDENTITY` | Database auto-increment (MySQL). Simple; disables JDBC insert batching |
| `SEQUENCE` | Database sequence (PostgreSQL, Oracle) |
| `AUTO` | Provider chooses |
| `TABLE` | Separate table simulates a sequence (rarely used) |

### Tips

- Prefer `EnumType.STRING`. `ORDINAL` (the default) breaks if you reorder the enum.
- JPA's `@Transient` is not Java's `transient` keyword (though Hibernate respects both).
- Default naming: camelCase field `joiningDate` becomes column `joining_date` in Spring Boot.

---

## 5. Relationships

### The four types

| Annotation | Example | Default fetch |
|---|---|---|
| `@OneToOne` | User and Profile | **EAGER** |
| `@ManyToOne` | Many Employees to one Department | **EAGER** |
| `@OneToMany` | One Department, many Employees | **LAZY** |
| `@ManyToMany` | Students and Courses | **LAZY** |

### Owning side vs inverse side

- The **owning side** holds the **foreign key** and uses `@JoinColumn`. In One-to-Many it is always the **"many" side** (`@ManyToOne`).
- The **inverse side** uses `mappedBy = "fieldNameOnOwningSide"` and holds no column.

**Analogy:** the employee carries the ID card (foreign key) naming their department. The department doesn't carry a list of employee IDs; it just refers to the employees' cards.

### Bidirectional One-to-Many / Many-to-One

```java
@Entity
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee e) {       // keep both sides in sync
        employees.add(e);
        e.setDepartment(this);
    }
}

@Entity
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")               // foreign key column
    private Department department;
}
```

### Many-to-Many with a join table

```java
@Entity
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();
}

@Entity
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
```

### Relationship attributes

| Attribute | Meaning |
|---|---|
| `mappedBy` | Marks the inverse side |
| `cascade` | Pass operations to children: `PERSIST`, `MERGE`, `REMOVE`, `REFRESH`, `DETACH`, `ALL` |
| `fetch` | `LAZY` (load when accessed) or `EAGER` (load immediately) |
| `orphanRemoval = true` | Delete a child when removed from the parent's collection |
| `@JoinColumn` | Foreign key column name |
| `@JoinTable` | Join table for many-to-many |

**Cascade analogy:** deleting a department with `CascadeType.REMOVE` is like closing a branch and automatically closing its counters.

### Fetch strategy: lazy vs eager

- **LAZY:** like a library catalog: you load the book list only when asked. Efficient.
- **EAGER:** like carrying every book home in advance. Convenient but wasteful.

**Prefer LAZY everywhere** and fetch what you need explicitly.

### The two classic problems

**1. `LazyInitializationException`**
Accessing a lazy collection after the transaction or session closed.
*Fix:* access it inside `@Transactional`, use `JOIN FETCH` or `@EntityGraph`, or return a DTO.

**2. N+1 queries**
Loading 1 list of departments (1 query), then touching each department's employees (N more queries).
*Fix:*

```java
@Query("SELECT d FROM Department d JOIN FETCH d.employees")
List<Department> findAllWithEmployees();

// or
@EntityGraph(attributePaths = "employees")
List<Department> findAll();
```

### Bidirectional gotchas

- `toString()` or JSON serialization loops forever (Department prints Employees, which print Department...). Use **DTOs**, or `@JsonIgnore` / `@JsonManagedReference` / `@JsonBackReference`, and keep relationships out of `toString()`.
- Always update **both sides** (use a helper like `addEmployee`).

---

## 6. Entity Lifecycle and Persistence Context

The **persistence context** is the "working area" where JPA tracks entities (it acts as a first-level cache). `EntityManager` is the API to it; Spring Data uses it for you.

### States

```
new Employee()  --persist/save-->  MANAGED  --remove/delete-->  REMOVED
 (TRANSIENT)                          |
                                      | transaction ends / detach / close
                                      v
                                  DETACHED  --merge/save-->  MANAGED
```

| State | Meaning |
|---|---|
| **Transient** | New object, not known to JPA |
| **Managed** | Tracked; changes are saved automatically |
| **Detached** | Was managed, no longer tracked |
| **Removed** | Scheduled for deletion |

### Dirty checking

Changes to a **managed** entity are written to the database at commit, with no `save()` needed:

```java
@Transactional
public void raise(Long id) {
    Employee e = repo.findById(id).orElseThrow();
    e.setSalary(e.getSalary() + 5000);      // UPDATE runs automatically at commit
}
```

**Analogy:** a teacher marking the register as the class goes on. At the end of the day, everything noted is filed without anyone asking again.

---

## 7. Spring Data JPA

You declare an interface; Spring generates the implementation at runtime.

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
<!-- or H2 for quick demos: com.h2database:h2 -->
```

### Repository

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> { }
//                                                    entity type ^  ^ id type
```

No `@Repository` annotation needed; Spring Data finds it.

### Methods you get free

| Method | Purpose |
|---|---|
| `save(e)` | Insert if new, otherwise update |
| `saveAll(list)` | Save many |
| `findById(id)` | Returns `Optional<T>` |
| `findAll()` | All rows |
| `findAll(Pageable)` / `findAll(Sort)` | Paging and sorting |
| `existsById(id)` | Boolean check |
| `count()` | Number of rows |
| `deleteById(id)` / `delete(e)` / `deleteAll()` | Delete |
| `saveAndFlush(e)` / `flush()` | Force SQL to the database now |
| `getReferenceById(id)` | Lazy proxy; no query until used |

```java
Employee saved = repo.save(new Employee("Asha", 55000));         // INSERT
Employee e = repo.findById(1L).orElseThrow();
e.setSalary(60000);
repo.save(e);                                                     // UPDATE
```

### Repository hierarchy

```
Repository -> CrudRepository -> PagingAndSortingRepository -> JpaRepository
```

`JpaRepository` includes everything above plus JPA-specific methods (`flush`, batch deletes). It is the usual choice.

### Pagination and sorting

```java
Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());   // page 0, 10 per page
Page<Employee> page = repo.findAll(pageable);

page.getContent();         // the rows
page.getTotalPages();
page.getTotalElements();
```

---

## 8. Queries: Derived, JPQL, Native

### 8.1 Derived queries (from the method name)

```java
List<Employee> findByName(String name);
List<Employee> findByNameAndCity(String name, String city);
List<Employee> findBySalaryGreaterThan(double salary);
List<Employee> findByNameContaining(String text);          // LIKE %text%
List<Employee> findByNameIgnoreCase(String name);
List<Employee> findBySalaryBetween(double min, double max);
List<Employee> findByCityIn(List<String> cities);
List<Employee> findByDepartmentName(String deptName);      // follows relationships
List<Employee> findTop3ByOrderBySalaryDesc();
Optional<Employee> findByEmail(String email);
boolean existsByEmail(String email);
long countByCity(String city);
```

| Keyword | Meaning |
|---|---|
| `And`, `Or` | Combine conditions |
| `GreaterThan`, `LessThan`, `Between` | Comparisons |
| `Like`, `Containing`, `StartingWith`, `EndingWith` | Text matching |
| `In`, `NotIn` | Membership |
| `IsNull`, `IsNotNull`, `True`, `False` | Null and boolean |
| `OrderBy...Asc/Desc` | Sorting |
| `Top N`, `First` | Limit |
| `existsBy`, `countBy`, `deleteBy` | Existence, count, delete |

The method name must match **field names of the entity**, or the app fails at startup. This is a useful early warning.

### 8.2 JPQL (`@Query`)

JPQL queries **entities and fields**, not tables and columns.

```java
@Query("SELECT e FROM Employee e WHERE e.salary BETWEEN :min AND :max")
List<Employee> findInRange(@Param("min") double min, @Param("max") double max);

@Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.name = ?1")
List<Employee> findWithDept(String name);
```

| SQL | JPQL |
|---|---|
| `SELECT * FROM employees` | `SELECT e FROM Employee e` |
| Table and column names | Class and field names |
| Database-specific | Database-independent |

### 8.3 Native SQL

```java
@Query(value = "SELECT * FROM employees WHERE salary > ?1", nativeQuery = true)
List<Employee> richerThan(double salary);
```

Use only when JPQL cannot do it (database-specific features).

### 8.4 Update and delete queries

```java
@Modifying
@Transactional
@Query("UPDATE Employee e SET e.salary = e.salary + :amt WHERE e.department.id = :deptId")
int giveRaise(@Param("amt") double amt, @Param("deptId") Long deptId);
```

### When to use which

```
Simple lookups               -> derived query methods
Joins, custom conditions     -> @Query (JPQL)
Database-specific SQL        -> native query
Many parameters/optional     -> Specifications or Querydsl (beyond these notes)
```

---

## 9. Transactions

A **transaction** groups operations so they all succeed or all roll back (ACID). In Spring, use `@Transactional`.

```java
@Service
public class TransferService {

    @Transactional
    public void transfer(Long from, Long to, double amount) {
        Account a = accounts.findById(from).orElseThrow();
        Account b = accounts.findById(to).orElseThrow();
        a.setBalance(a.getBalance() - amount);
        b.setBalance(b.getBalance() + amount);
        // if any exception occurs, BOTH changes roll back
    }
}
```

### Attributes

| Attribute | Meaning |
|---|---|
| `readOnly = true` | Hint for read-only work (small optimization) |
| `rollbackFor = X.class` | Also roll back on this exception |
| `propagation` | How it joins an existing transaction (default `REQUIRED`: join if present, else start one) |
| `isolation` | Visibility between concurrent transactions |

### Rules

- Rolls back by default on **unchecked** exceptions (`RuntimeException`, `Error`), **not on checked** ones. Add `rollbackFor = Exception.class` if needed.
- Put `@Transactional` on **service** methods, not controllers or repositories. A service method is the natural "unit of work".
- It works through a **proxy**, so:
  - Calling a `@Transactional` method from **another method in the same class** bypasses the proxy and the annotation does nothing.
  - `private` methods are not intercepted.
- Repository methods are transactional by default (`save`, `delete`...), but a **service-level transaction** is what makes several calls atomic.

---

## 10. Spring Boot Configuration

`application.properties`:

```properties
# Datasource (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/company
spring.datasource.username=root
spring.datasource.password=password

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

H2 (in-memory, for quick demos):

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true            # browse at /h2-console
```

### `ddl-auto` values

| Value | Effect | Use |
|---|---|---|
| `none` | Does nothing | Production |
| `validate` | Checks schema matches entities; fails if not | Production |
| `update` | Adds missing tables/columns (never drops) | Development |
| `create` | Drops and recreates at startup | Throwaway demos |
| `create-drop` | Create at start, drop at shutdown | Tests |

> **Never use `create` or `update` against a production database.** Use migration tools (Flyway or Liquibase) for schema changes.

Hibernate 6 detects the SQL dialect automatically, so you don't need `spring.jpa.database-platform`.

---

## 11. End-to-End Example

**Entity**

```java
@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private double price;

    protected Product() { }
    public Product(String name, double price) { this.name = name; this.price = price; }
    // getters and setters
}
```

**Repository**

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByPriceLessThan(double price);
}
```

**Service**

```java
@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) { this.repo = repo; }

    @Transactional
    public Product add(Product p) { return repo.save(p); }

    @Transactional(readOnly = true)
    public Product get(Long id) {
        return repo.findById(id)
                   .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public List<Product> cheaperThan(double price) { return repo.findByPriceLessThan(price); }
}
```

**Controller**

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) { this.service = service; }

    @PostMapping
    public Product add(@RequestBody Product p) { return service.add(p); }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) { return service.get(id); }

    @GetMapping("/cheap")
    public List<Product> cheap(@RequestParam double max) { return service.cheaperThan(max); }
}
```

**Flow of `POST /api/products`:**

```
JSON -> Controller (@RequestBody -> Product)
     -> Service (@Transactional)
     -> Repository.save()
     -> Hibernate builds INSERT
     -> JDBC executes it
     -> row in the database
```

---

## 12. Annotation Cheat Sheet

### JPA mapping

| Annotation | Purpose |
|---|---|
| `@Entity` | Class is a table-backed entity |
| `@Table` | Table name and constraints |
| `@Id` | Primary key |
| `@GeneratedValue` | Auto-generate the key |
| `@Column` | Column settings |
| `@Transient` | Do not persist |
| `@Enumerated` | Enum storage mode |
| `@Lob` | Large object |
| `@Version` | Optimistic locking |
| `@Embeddable`, `@Embedded` | Value object inside an entity |
| `@MappedSuperclass` | Shared base fields |

### Relationships

| Annotation | Purpose |
|---|---|
| `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany` | Relationship type |
| `@JoinColumn` | Foreign key column |
| `@JoinTable` | Join table |
| `mappedBy` | Inverse side |
| `cascade`, `fetch`, `orphanRemoval` | Behavior |

### Spring Data and transactions

| Annotation | Purpose |
|---|---|
| `@Repository` | Data-access bean (also translates exceptions) |
| `@Query` | Custom JPQL or native query |
| `@Param` | Bind a named query parameter |
| `@Modifying` | Query changes data |
| `@EntityGraph` | Declare what to fetch eagerly |
| `@Transactional` | Transaction boundary |

### Spring and web (used alongside)

| Annotation | Purpose |
|---|---|
| `@Service` | Business logic bean |
| `@RestController` | Controller returning JSON |
| `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` | Map URLs and methods |
| `@RequestBody` | JSON body into an object |
| `@PathVariable`, `@RequestParam` | URL path and query values |
| `@Autowired` | Inject a dependency (optional for a single constructor) |

### Validation (optional starter: `spring-boot-starter-validation`)

| Annotation | Purpose |
|---|---|
| `@NotNull`, `@NotBlank`, `@NotEmpty` | Required values |
| `@Size`, `@Min`, `@Max` | Limits |
| `@Email`, `@Pattern` | Formats |
| `@Valid` | Trigger validation on a method parameter |

---

## 13. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Entity without a no-arg constructor | Hibernate cannot instantiate it | Add a `protected` no-arg constructor |
| Making an entity a `record` or `final` class | Proxies and instantiation fail | Use a normal class; records for DTOs |
| `javax.persistence` imports on Boot 3 | Does not compile | Use `jakarta.persistence` |
| `EnumType.ORDINAL` (default) | Reordering enum corrupts data | `EnumType.STRING` |
| Derived-query name does not match a field | Startup error | Match the entity field names exactly |
| Using table and column names in JPQL | Query error | Use entity and field names |
| `LazyInitializationException` | Lazy field used outside a transaction | `JOIN FETCH`, `@EntityGraph`, DTO, or `@Transactional` |
| N+1 queries | Slow pages | `JOIN FETCH` or `@EntityGraph` |
| Updating only one side of a bidirectional link | Database not updated | Update both sides via a helper |
| Missing `mappedBy`, or `mappedBy` on both sides | Extra join table or errors | `mappedBy` only on the inverse side |
| Returning entities from controllers with bidirectional links | Infinite JSON recursion | Return DTOs |
| `@Transactional` on a private method or self-called method | Silently ignored | Move to a public method called from another bean |
| Checked exception and no `rollbackFor` | No rollback | `rollbackFor = Exception.class` |
| `@Modifying` query without `@Transactional` | `TransactionRequiredException` | Add `@Transactional` |
| `ddl-auto=create` on a real database | Data wiped on startup | `validate` or `none` in production |
| `findById(id).get()` without checking | `NoSuchElementException` | `orElseThrow(...)` |
| String concatenation in JDBC SQL | SQL injection | `PreparedStatement` or bind parameters |
| Not closing JDBC resources | Connection leaks | try-with-resources |
| `@Data` (Lombok) on entities | Bad `equals`, `hashCode`, `toString` with relations | Use `@Getter` and `@Setter` only |

---

## 14. Interview Questions

**1. What is JDBC?** The standard Java API for executing SQL against a database via a driver.

**2. Statement vs PreparedStatement?** `PreparedStatement` uses precompiled SQL with `?` parameters. It is faster and prevents SQL injection.

**3. `executeQuery` vs `executeUpdate`?** `executeQuery` runs SELECT and returns a `ResultSet`. `executeUpdate` runs INSERT/UPDATE/DELETE and returns the affected row count.

**4. What is ORM?** Mapping objects to relational tables so you work with objects instead of SQL.

**5. JPA vs Hibernate?** JPA is the specification; Hibernate is an implementation of it.

**6. JPA vs Spring Data JPA?** Spring Data JPA builds on JPA and generates repository implementations from interfaces.

**7. What is an entity?** A class mapped to a table, annotated `@Entity`, with an `@Id` and a no-arg constructor.

**8. `@GeneratedValue` strategies?** `IDENTITY`, `SEQUENCE`, `AUTO`, `TABLE`.

**9. What is `mappedBy`?** It marks the inverse side of a bidirectional relationship; the other side owns the foreign key.

**10. Lazy vs eager fetching?** Lazy loads related data when first accessed; eager loads it immediately. Defaults: `@ManyToOne` and `@OneToOne` eager; `@OneToMany` and `@ManyToMany` lazy.

**11. What is the N+1 problem?** One query for the parents plus one per parent for children. Fix with `JOIN FETCH` or `@EntityGraph`.

**12. What is `LazyInitializationException`?** Accessing a lazy association after the session or transaction is closed.

**13. What does `CascadeType.ALL` do?** Propagates persist, merge, remove, refresh and detach from parent to children.

**14. What is `orphanRemoval`?** Deletes a child that is removed from its parent's collection.

**15. JPQL vs SQL?** JPQL works on entities and fields and is database-independent; SQL works on tables and columns.

**16. What does `save()` do?** Inserts if the entity is new, otherwise merges (updates).

**17. What is dirty checking?** Hibernate detects changes to managed entities and updates them at commit without an explicit `save()`.

**18. What is `@Transactional`?** Wraps a method in a transaction; commits on success and rolls back on unchecked exceptions by default.

**19. Why does `@Transactional` sometimes not work?** Self-invocation within the same class or a `private` method bypasses the Spring proxy.

**20. What does `ddl-auto` do?** Controls automatic schema generation. Use `validate` or `none` in production.

**21. What is `@Version` for?** Optimistic locking: detects concurrent updates to the same row.

**22. Why not return entities directly from REST controllers?** Lazy-loading issues, infinite recursion with bidirectional links, and exposing internal structure. Use DTOs.

---

## 15. Practice Tasks

1. **JDBC:** write a Java program that inserts three employees and prints those earning above 50,000, using `PreparedStatement` and try-with-resources.
2. **JDBC transaction:** transfer money between two accounts with `setAutoCommit(false)`, then force an exception after the debit and confirm the rollback.
3. **JPA:** create `Employee` and `Department` entities (many-to-one), run the app with `show-sql=true`, and read the generated SQL.
4. **Spring Data:** add `findBySalaryGreaterThan`, `findByDepartmentName`, and a JPQL `@Query` for a salary range.
5. **Performance:** load all departments and print their employees, count the queries (N+1), then fix with `JOIN FETCH`.
6. **Transactions:** write a `@Transactional` service method that saves two entities and throws a `RuntimeException` after the first; verify neither is saved.

### Challenge questions

1. A `@Transactional` method calls another `@Transactional` method in the **same class**. Does the second get its own transaction settings?
2. Why can `findByDeptName` fail at startup in `EmployeeRepository`?
3. Where is the foreign key in a bidirectional Department/Employee mapping?

**Answers**

1. No. The call bypasses the proxy, so only the outer method's transaction applies.
2. There is no field named `deptName` on `Employee`. The derived-query name must match fields (for a nested field it would be `findByDepartmentName`).
3. In the **employees** table (the `@ManyToOne` side with `@JoinColumn`). The `mappedBy` side has no column.

---

*End of notes.*
