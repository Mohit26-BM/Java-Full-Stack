# Java Exception Handling: Detailed Notes

Everything about exceptions: the hierarchy, checked vs unchecked, `try`/`catch`/`finally`, `throw` and `throws`, try-with-resources, custom exceptions, chaining, best practices, exceptions in lambdas, threads and Spring Boot, plus tricky puzzles and interview questions.

---

## Table of Contents

1. [What Is an Exception and Why Handle It](#1-what-is-an-exception-and-why-handle-it)
2. [The Exception Hierarchy](#2-the-exception-hierarchy)
3. [Checked vs Unchecked](#3-checked-vs-unchecked)
4. [try, catch, finally](#4-try-catch-finally)
5. [throw vs throws and Propagation](#5-throw-vs-throws-and-propagation)
6. [try-with-resources](#6-try-with-resources)
7. [Custom Exceptions](#7-custom-exceptions)
8. [Exception Chaining and Stack Traces](#8-exception-chaining-and-stack-traces)
9. [Exceptions and Method Overriding](#9-exceptions-and-method-overriding)
10. [Common Built-in Exceptions](#10-common-built-in-exceptions)
11. [Best Practices and Anti-patterns](#11-best-practices-and-anti-patterns)
12. [Exceptions in Lambdas and Streams](#12-exceptions-in-lambdas-and-streams)
13. [Exceptions in Threads](#13-exceptions-in-threads)
14. [Exceptions in Spring Boot](#14-exceptions-in-spring-boot)
15. [Common Mistakes](#15-common-mistakes)
16. [Output Prediction Puzzles](#16-output-prediction-puzzles)
17. [Interview Questions](#17-interview-questions)
18. [Cheat Sheet](#18-cheat-sheet)
19. [Practice Tasks with Solutions](#19-practice-tasks-with-solutions)

---

## 1. What Is an Exception and Why Handle It

> An **exception** is an event that **disrupts the normal flow** of a program at run time.

```java
int[] marks = {80, 90};
System.out.println(marks[5]);          // ArrayIndexOutOfBoundsException
System.out.println("never reached");
```

Without handling, the program **terminates abruptly** and prints a **stack trace**:

```
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 2
    at Demo.main(Demo.java:4)
```

### Real-life analogies

| Situation | Programming equivalent |
|---|---|
| Your **flight is cancelled** | An exception: normal flow is broken |
| You have a **backup plan** (train, hotel) | Exception handling |
| No backup plan, you are stranded | Unhandled exception: crash |
| The ATM says "Insufficient balance" instead of exploding | A meaningful, handled error |

### Why exception handling exists

- **The program survives** unexpected situations and can recover or end gracefully.
- **Separates normal logic from error logic**: no `if (error) ... ` checks after every line.
- **Error information travels up** the call chain to whoever can deal with it.
- **Resources get cleaned up** (files closed, connections released).
- **Gives users and developers clear messages** instead of silent failure or a raw crash.

### Error vs exception vs bug

| Term | Meaning | Example |
|---|---|---|
| **Bug** | A mistake in your code | Wrong loop condition |
| **Exception** | A problem your code can reasonably **anticipate and handle** | File not found, bad input |
| **Error** | A serious **JVM-level** problem you generally cannot recover from | `OutOfMemoryError` |

---

## 2. The Exception Hierarchy

```
Throwable
  |
  +-- Error                         (JVM problems: do not catch)
  |     OutOfMemoryError, StackOverflowError, AssertionError, NoClassDefFoundError
  |
  +-- Exception
        |
        +-- RuntimeException        (UNCHECKED)
        |     NullPointerException
        |     ArithmeticException
        |     IllegalArgumentException -> NumberFormatException
        |     IllegalStateException
        |     IndexOutOfBoundsException -> ArrayIndexOutOfBoundsException, StringIndexOutOfBoundsException
        |     ClassCastException
        |     UnsupportedOperationException
        |     ConcurrentModificationException
        |
        +-- (all others)            (CHECKED)
              IOException -> FileNotFoundException
              SQLException
              ClassNotFoundException
              InterruptedException
              ParseException
```

### The three families

| Family | Superclass | Compiler forces handling? | Typical cause | Should you catch it? |
|---|---|---|---|---|
| **Checked exceptions** | `Exception` (not `RuntimeException`) | **Yes** | External, foreseeable problems (I/O, database, network) | Yes, handle or declare |
| **Unchecked exceptions** | `RuntimeException` | No | **Programming errors** (null, bad index, bad argument) | Fix the code; catch only at boundaries |
| **Errors** | `Error` | No | JVM or system failure | **No** |

`Throwable` is the root, and only `Throwable` objects can be thrown or caught.

---

## 3. Checked vs Unchecked

### Checked exceptions: "handle or declare"

The compiler **refuses to compile** code that may throw a checked exception unless you either **catch** it or **declare** it with `throws`.

```java
FileReader fr = new FileReader("data.txt");
// COMPILE ERROR: unreported exception FileNotFoundException; must be caught or declared to be thrown
```

```java
// Option 1: handle it
try {
    FileReader fr = new FileReader("data.txt");
} catch (FileNotFoundException e) {
    System.out.println("File missing");
}

// Option 2: declare it (pass the responsibility to the caller)
void load() throws FileNotFoundException {
    FileReader fr = new FileReader("data.txt");
}
```

### Unchecked exceptions: no compiler pressure

```java
String s = null;
s.length();                  // NullPointerException at run time; compiles fine
```

### The analogy

| | Real life |
|---|---|
| **Checked** | **Foreseeable external trouble**, like bad weather before a flight. You are *required* to carry a plan (ticket rules, backup). |
| **Unchecked** | **Your own mistakes**, like driving into a wall because you weren't looking. No "plan" helps; fix the driving. |

### Side-by-side

| | Checked | Unchecked |
|---|---|---|
| Extends | `Exception` | `RuntimeException` |
| Checked at | **Compile time** | Not checked |
| Must handle or declare | **Yes** | No |
| Meaning | Recoverable external condition | Programming bug or violated contract |
| Examples | `IOException`, `SQLException` | `NullPointerException`, `IllegalArgumentException` |

### Which to use in your own code?

- **Unchecked** for programming errors and violated preconditions (`IllegalArgumentException`).
- **Checked** only when the caller can **realistically recover** and you want to force them to think about it.
- **Modern practice (Spring and most libraries):** prefer **unchecked** for business and infrastructure errors. Checked exceptions create boilerplate, leak implementation details through every layer's signature, and do not mix with lambdas (section 12). Spring even wraps `SQLException` into unchecked `DataAccessException`.

> This is a real, ongoing debate. Checked exceptions make failure modes visible in the type system, while unchecked ones keep code cleaner. Pick one convention per project and apply it consistently.

---

## 4. try, catch, finally

### Syntax

```java
try {
    // code that might throw
} catch (SomeException e) {
    // handle it
} finally {
    // always runs (cleanup)
}
```

**Analogy: a picnic.**
- `try`: go on the picnic.
- `catch`: if it **rains**, move indoors.
- `finally`: **pack up and clean** either way.

### Example

```java
try {
    int result = 10 / 0;
    System.out.println(result);
} catch (ArithmeticException e) {
    System.out.println("Cannot divide by zero: " + e.getMessage());
} finally {
    System.out.println("Done");
}
System.out.println("Program continues");
```

**Output:**

```
Cannot divide by zero: / by zero
Done
Program continues
```

### Execution flow

| Scenario | `try` | `catch` | `finally` | After |
|---|---|---|---|---|
| No exception | Runs fully | Skipped | Runs | Continues |
| Exception with a **matching** catch | Stops at the throw point | Runs | Runs | Continues |
| Exception with **no** matching catch | Stops at the throw point | Skipped | Runs | **Propagates** to the caller |
| Exception thrown **inside** `catch` | Stops | Stops at the throw | Runs | New exception propagates |

Code after the line that throws, inside the same `try`, is **never executed**.

### Multiple catch blocks

```java
try {
    String s = args[0];
    int n = Integer.parseInt(s);
    System.out.println(100 / n);
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("No argument given");
} catch (NumberFormatException e) {
    System.out.println("Not a number");
} catch (ArithmeticException e) {
    System.out.println("Zero not allowed");
}
```

**Rule: order from most specific to most general.** A parent type before its child makes the child unreachable, which is a **compile error**.

```java
try { ... }
catch (Exception e) { ... }
catch (IOException e) { ... }     // COMPILE ERROR: exception IOException has already been caught
```

### Multi-catch (Java 7)

```java
try { ... }
catch (NumberFormatException | ArithmeticException e) {
    System.out.println("Bad input: " + e.getMessage());
}
```

- Alternatives **cannot be related by inheritance** (`NumberFormatException | IllegalArgumentException` is a compile error).
- The variable `e` is implicitly `final`.

### Nested try

```java
try {
    try {
        riskyStepOne();
    } catch (IOException e) {
        // handle step one
    }
    riskyStepTwo();
} catch (Exception e) {
    // handle anything that escaped
}
```

### `finally` facts

- **Always runs** whether or not an exception occurred, **even if `try` or `catch` contains `return`**.
- It does **not** run if the JVM exits (`System.exit(0)`), crashes, loses power, or the thread is killed or stuck in an infinite loop.
- Use it for **cleanup**: closing files, connections, releasing locks. (Prefer try-with-resources for resources; section 6.)
- `try` can have `finally` **without** `catch`:

```java
try {
    lock.lock();
    doWork();
} finally {
    lock.unlock();                 // exception still propagates, but unlock is guaranteed
}
```

### The `return` in `finally` trap

```java
static int f() {
    try {
        return 1;
    } finally {
        return 2;                  // overrides the return, and swallows any exception
    }
}
// f() returns 2
```

**Never `return`, `break` or `throw` from `finally`.** It discards the original outcome.

### What `finally` does to return values

```java
static int g() {
    int x = 1;
    try {
        return x;                  // value 1 is saved for return
    } finally {
        x = 99;                    // changes the variable, not the saved value
    }
}
// g() returns 1

static StringBuilder h() {
    StringBuilder sb = new StringBuilder("a");
    try {
        return sb;                 // the REFERENCE is saved
    } finally {
        sb.append("b");            // mutates the same object
    }
}
// h() returns "ab"
```

### A compile-time rule on catching checked exceptions

A `catch` for a **checked** exception that the `try` body can never throw is a compile error ("exception X is never thrown in body of corresponding try statement"). Catching `Exception`, `Throwable` or unchecked types is always allowed.

---

## 5. throw vs throws and Propagation

### `throw`: raise an exception **now**

```java
void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative: " + age);
    }
    this.age = age;
}
```

### `throws`: **declare** that a method may throw

```java
void readFile(String path) throws IOException, ParseException {
    ...
}
```

| | `throw` | `throws` |
|---|---|---|
| What | A **statement** that throws an exception object | A **declaration** in the method signature |
| Where | Inside a method body | After the parameter list |
| Followed by | One exception **object** (`new ...`) | One or more exception **class names** |
| Count | One at a time | Many, comma-separated |
| Analogy | **Reporting an incident right now** | A **warning label** ("may contain nuts") |

Notes:

- `throws` is required only for **checked** exceptions (you may list unchecked ones for documentation).
- Code after a `throw` in the same block is unreachable, so it is a compile error.
- You can throw only `Throwable` subclasses. `throw null;` produces a `NullPointerException`.

### Propagation: exceptions travel up the call stack

If a method does not handle an exception, it **propagates to its caller**, then to that caller's caller, until something catches it or it reaches `main` (and the thread dies).

```java
void a() { b(); }
void b() { c(); }
void c() { throw new IllegalStateException("boom"); }

try { a(); }
catch (IllegalStateException e) { System.out.println("caught: " + e.getMessage()); }
```

```
c()  throws  -> not handled -> unwinds to b()
b()  not handled            -> unwinds to a()
a()  not handled            -> unwinds to the caller's try/catch  (caught)
```

**Analogy: escalation in a company.** A junior can't fix it, so it goes to the manager, then the director, until someone handles it. If nobody does, the company shuts down (the thread terminates).

**Design guideline:** handle an exception **where you can do something meaningful about it**. Otherwise let it propagate (or wrap it) rather than catching it too early.

### Rethrowing

```java
try {
    process();
} catch (IOException e) {
    log.error("Processing failed", e);
    throw e;                         // rethrow the same exception
}
```

---

## 6. try-with-resources

### The problem it solves

```java
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("data.txt"));
    System.out.println(br.readLine());
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (br != null) {
        try { br.close(); } catch (IOException e) { }    // ugly, error-prone
    }
}
```

### The solution (Java 7)

```java
try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
} catch (FileNotFoundException e) {
    System.out.println("File missing: " + e.getMessage());
} catch (IOException e) {
    System.out.println("Read failed: " + e.getMessage());
}
```

`br` is **closed automatically**, even if an exception occurs. (`FileNotFoundException` comes before `IOException` because it is the subclass.)

**Analogy:** a hotel **automatic check-out**, or a library book that returns itself when you are done. You can't forget.

### Rules

- Resources must implement **`AutoCloseable`** (`close() throws Exception`) or **`Closeable`** (`close() throws IOException`, a sub-interface).
- Multiple resources: separated by `;`, closed in **reverse order** of creation (like stacking and unstacking plates).
- Resources are closed **before** the `catch` and `finally` blocks run.
- Resource variables are implicitly `final`.
- **Java 9:** you may use an existing effectively-final variable: `try (br) { ... }`.

```java
class Res implements AutoCloseable {
    private final String name;
    Res(String name) { this.name = name; System.out.println("open " + name); }
    public void close() { System.out.println("close " + name); }
}

try (Res a = new Res("A"); Res b = new Res("B")) {
    System.out.println("body");
}
```

```
open A
open B
body
close B
close A
```

### Suppressed exceptions

If the body throws **and** `close()` also throws, the **body's exception wins** and the `close()` exception is attached as **suppressed**:

```java
try (Bad b = new Bad()) {                       // Bad.close() throws "close failed"
    throw new RuntimeException("body failed");
} catch (Exception e) {
    System.out.println(e.getMessage());                          // body failed
    System.out.println(e.getSuppressed()[0].getMessage());       // close failed
}
```

With a hand-written `finally`, the `close()` exception would **replace** the original and hide the real cause. This is another reason to prefer try-with-resources.

### Typical uses

JDBC (`Connection`, `PreparedStatement`, `ResultSet`), `Scanner`, file streams, `Files.lines(...)`, sockets.

```java
try (Connection con = dataSource.getConnection();
     PreparedStatement ps = con.prepareStatement(sql)) {
    ...
}
```

---

## 7. Custom Exceptions

### Why

Built-in exceptions describe technical problems. Custom ones describe **business problems** in your domain's language, so handlers can react precisely.

**Analogy:** a bank counter slip that says "Insufficient balance: short by Rs. 500" is much more useful than "Error".

### Unchecked custom exception

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Checked custom exception with extra data

```java
public class InsufficientFundsException extends Exception {
    private final double shortfall;

    public InsufficientFundsException(String message, double shortfall) {
        super(message);
        this.shortfall = shortfall;
    }

    public double getShortfall() { return shortfall; }
}

class Account {
    private double balance;

    void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");   // bug in caller: unchecked
        }
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds", amount - balance);   // business case
        }
        balance -= amount;
    }
}

try {
    account.withdraw(5000);
} catch (InsufficientFundsException e) {
    System.out.println(e.getMessage() + ": short by " + e.getShortfall());
}
```

### Guidelines

| Guideline | Why |
|---|---|
| Name it `...Exception` and make it **specific** | Self-documenting |
| Provide constructors with **`(message)`** and **`(message, cause)`** | Supports chaining |
| Extend **`RuntimeException`** for most application errors (modern default) | Less boilerplate, lambda-friendly |
| Extend **`Exception`** only when callers must handle it | Forces a decision |
| Add fields only when handlers need the data | Keep it small |
| Create one only if it adds value | Reuse `IllegalArgumentException`, `IllegalStateException`, etc. when they fit |
| Make it **serializable-friendly** (immutable fields) | Safe across layers and remote calls |

---

## 8. Exception Chaining and Stack Traces

### Chaining: wrap, never lose the cause

Lower-level exceptions (`SQLException`) should not leak to upper layers, but you must keep the original cause.

```java
public Order load(long id) {
    try {
        return dao.find(id);
    } catch (SQLException e) {
        throw new DataAccessException("Could not load order " + id, e);   // cause preserved
    }
}
```

**Analogy:** an incident report: "Delivery delayed" **caused by** "late bus" **caused by** "flat tire". The root cause is what you need to fix.

**Anti-pattern:** `throw new X(e.getMessage())` throws the cause away and loses the stack trace.

### Reading a stack trace

```
Exception in thread "main" java.lang.IllegalStateException: Order failed
    at com.example.OrderService.place(OrderService.java:25)
    at com.example.Main.main(Main.java:10)
Caused by: java.sql.SQLException: Connection refused
    at com.example.Dao.save(Dao.java:40)
    at com.example.OrderService.place(OrderService.java:22)
    ... 1 more
```

| Part | Meaning |
|---|---|
| First line | Exception **type** and **message** |
| `at ...` lines | The **call stack**: the first is where it was **thrown**, the next ones show **who called whom** |
| `Caused by:` | The **underlying cause**. The **bottom-most** `Caused by` is usually the root cause to fix |
| `... 1 more` | Frames shared with the enclosing trace, omitted |

**Reading tip:** go to the `Caused by` at the bottom, then find the first line in **your own package** to locate the bug.

### Helpful `NullPointerException` messages (Java 14+)

```
Cannot invoke "String.length()" because "name" is null
```

Modern JDKs tell you **which variable** was null.

### Useful `Throwable` methods

| Method | Purpose |
|---|---|
| `getMessage()` | The message text |
| `getCause()` | The wrapped cause (or `null`) |
| `printStackTrace()` | Print the trace to stderr (use a logger in real code) |
| `getStackTrace()` | Trace as an array |
| `toString()` | Type plus message |
| `getSuppressed()` / `addSuppressed(t)` | Suppressed exceptions |
| `initCause(t)` | Set the cause after construction |

---

## 9. Exceptions and Method Overriding

(Overriding rules appear in the OOP notes; exceptions add constraints.)

| Overriding method may throw | Allowed? |
|---|---|
| The **same** checked exception | Yes |
| A **narrower** (subclass) checked exception | Yes |
| **No** checked exception | Yes |
| A **broader** or **new** checked exception | **No** (compile error) |
| Any **unchecked** exception | Yes |

```java
class Parent {
    void read() throws IOException { }
}

class Child extends Parent {
    @Override
    void read() throws FileNotFoundException { }     // narrower: OK
    // void read() throws Exception { }              // broader: compile error
}
```

**Why:** code written against `Parent` expects at most `IOException`. A subclass that suddenly throws something broader would break callers who handled only `IOException`.

Other rules:

- A **constructor** may throw exceptions; a subclass constructor must declare any checked exception the parent constructor throws.
- **Static initializer blocks** cannot throw checked exceptions; an uncaught unchecked one becomes `ExceptionInInitializerError`.

---

## 10. Common Built-in Exceptions

### Unchecked

| Exception | Typical cause | Fix |
|---|---|---|
| `NullPointerException` | Using a `null` reference | Null checks, `Objects.requireNonNull`, `Optional`, initialize fields |
| `ArrayIndexOutOfBoundsException` | Bad array index | Check `index < length` |
| `StringIndexOutOfBoundsException` | Bad index in `charAt` / `substring` | Check length |
| `IndexOutOfBoundsException` | Bad `List` index | Check `size()` |
| `ArithmeticException` | **Integer** division or modulo by zero | Check divisor |
| `ClassCastException` | Invalid downcast | Check `instanceof` first |
| `NumberFormatException` | `Integer.parseInt("12a")` | Validate or catch and report |
| `IllegalArgumentException` | A bad argument passed in | Validate arguments early |
| `IllegalStateException` | Method called at the wrong time or state | Check state |
| `UnsupportedOperationException` | Modifying `List.of(...)`, `Arrays.asList(...).add` | Use a mutable copy |
| `ConcurrentModificationException` | Changing a collection while iterating | `removeIf` or `Iterator.remove()` |
| `NoSuchElementException` | `Optional.get()` on empty, `iterator.next()` at end | Check first or use `orElse...` |
| `InputMismatchException` | `Scanner.nextInt()` on non-numeric input | Validate with `hasNextInt()` |
| `ArrayStoreException` | Wrong type stored in an array | Use generics and collections |
| `NegativeArraySizeException` | `new int[-1]` | Validate size |

> `ArithmeticException` applies to **integers** only. `1.0 / 0` gives `Infinity`, `0.0 / 0` gives `NaN`, and `1 / 0` throws.

### Checked

| Exception | Typical cause |
|---|---|
| `IOException`, `FileNotFoundException` | File and network I/O |
| `SQLException` | Database access (JDBC) |
| `ClassNotFoundException` | Loading a class by name that is missing |
| `InterruptedException` | A thread was interrupted while sleeping or waiting |
| `ParseException` | Parsing text (dates, formats) |
| `TimeoutException`, `ExecutionException` | Concurrency (futures) |

### Errors

| Error | Cause |
|---|---|
| `StackOverflowError` | Runaway or infinitely deep recursion |
| `OutOfMemoryError` | Heap exhausted |
| `NoClassDefFoundError` | A class that was present at compile time is missing at run time |
| `ExceptionInInitializerError` | Exception inside a static initializer |
| `AssertionError` | A failed `assert` |

`ClassNotFoundException` (checked; a **dynamic load** failed, for example `Class.forName`) differs from `NoClassDefFoundError` (an **Error**; the JVM could not find a class it needed at run time).

---

## 11. Best Practices and Anti-patterns

### Do

| Practice | Example |
|---|---|
| **Catch specific exceptions** | `catch (FileNotFoundException e)`, not `catch (Exception e)` |
| **Fail fast**: validate inputs at the start | `Objects.requireNonNull(x, "x must not be null")` |
| **Use the standard exceptions** when they fit | `IllegalArgumentException`, `IllegalStateException` |
| **Preserve the cause** when wrapping | `new X("message", e)` |
| **Clean up** with try-with-resources | Files, connections |
| **Give useful messages** (what, which value, why) | `"Employee 42 not found"` |
| **Handle at the right layer** | Catch where you can act or translate |
| **Log once** (at the boundary), with the exception | `log.error("Could not place order {}", id, e)` |
| **Document** with `@throws` | Javadoc |
| **Return empty collections or `Optional`** for normal "no result" | Not `null`, not exceptions |
| **Restore the interrupt flag** after catching `InterruptedException` | `Thread.currentThread().interrupt();` |

### Don't (anti-patterns)

| Anti-pattern | Why it is bad |
|---|---|
| `catch (Exception e) { }` (**empty catch**) | **Swallows errors silently**; bugs become invisible |
| `catch (Exception e) { e.printStackTrace(); }` as the only handling | Not logging; the program continues in a broken state |
| Catching **`Throwable`** or **`Error`** | Hides `OutOfMemoryError` and similar failures |
| **Log and rethrow** at every layer | Duplicate log entries for one failure |
| Using exceptions for **normal control flow** | Slow and confusing (for example looping until `ArrayIndexOutOfBoundsException`) |
| `throw new X(e.getMessage())` | Loses the cause and stack trace |
| `return` / `throw` in `finally` | Overrides the real outcome |
| Overly broad `throws Exception` | Forces every caller to handle everything |
| Exposing stack traces or internals to API clients | Security risk |
| Returning `null` to signal failure | Leads to `NullPointerException` downstream |

### Return null vs Optional vs exception

| Situation | Use |
|---|---|
| Absence is **normal** (search with no match) | `Optional<T>` or an empty collection |
| Failure is **exceptional** (database down, invalid state) | Throw an exception |
| The caller violated the contract | `IllegalArgumentException` / `IllegalStateException` |

### Performance note

Creating an exception **captures a stack trace**, which is relatively costly. That is fine for genuinely exceptional events and wasteful in hot loops used for control flow.

### Assertions (brief)

```java
assert age >= 0 : "age must be non-negative";
```

Disabled by default (enable with `-ea`). For **internal sanity checks**, not for validating user input.

---

## 12. Exceptions in Lambdas and Streams

The standard functional interfaces (`Function`, `Predicate`, `Consumer`, ...) **do not declare checked exceptions**, so this fails:

```java
paths.stream()
     .map(p -> Files.readString(p))      // COMPILE ERROR: unreported exception IOException
     .toList();
```

### Option 1: handle inside the lambda

```java
paths.stream()
     .map(p -> {
         try {
             return Files.readString(p);
         } catch (IOException e) {
             throw new UncheckedIOException(e);
         }
     })
     .toList();
```

### Option 2: a reusable wrapper

```java
@FunctionalInterface
interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}

static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> f) {
    return t -> {
        try {
            return f.apply(t);
        } catch (RuntimeException e) {
            throw e;                              // do not double-wrap unchecked exceptions
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}

paths.stream().map(unchecked(Files::readString)).toList();
```

### Option 3: return a result instead of throwing

For "process everything, report failures later" batches, map to `Optional` or a result record and collect failures separately.

> This friction is one of the main reasons modern code prefers **unchecked** exceptions.

---

## 13. Exceptions in Threads

- An uncaught exception **terminates only that thread**, not the whole program (unless it is the last non-daemon thread).

```java
Thread t = new Thread(() -> { throw new RuntimeException("boom"); });
t.start();
// prints "Exception in thread "Thread-0" java.lang.RuntimeException: boom"; main continues
```

- Provide a global safety net:

```java
Thread.setDefaultUncaughtExceptionHandler((thread, ex) ->
    System.err.println("Unhandled in " + thread.getName() + ": " + ex));
```

- With **`ExecutorService`**, an exception inside a task is **captured in the `Future`** and rethrown when you call `future.get()`, wrapped in an **`ExecutionException`**. Use `getCause()` to see the real problem.

```java
Future<Integer> f = executor.submit(() -> 10 / 0);
try {
    f.get();
} catch (ExecutionException e) {
    System.out.println(e.getCause());       // java.lang.ArithmeticException: / by zero
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();     // restore the flag, then stop
}
```

---

## 14. Exceptions in Spring Boot

### The pattern

1. **Services** throw meaningful **unchecked** exceptions.
2. **One global handler** turns them into consistent HTTP responses.
3. **Controllers** stay clean, with no try/catch.

### Throwing from the service layer

```java
public Employee get(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Employee " + id + " not found"));
}
```

### A uniform error body

```java
public record ErrorResponse(int status, String message, LocalDateTime timestamp) { }
```

### Global handler with `@RestControllerAdvice`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(404, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)          // @Valid failures
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)                                // last-resort catch-all
    public ResponseEntity<ErrorResponse> handleAny(Exception ex) {
        // log the real exception here; do NOT leak internals to the client
        ErrorResponse body = new ErrorResponse(500, "Something went wrong", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
```

Spring picks the **most specific** matching `@ExceptionHandler`.

### Quicker alternatives

```java
// annotate the exception class
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException { ... }

// or throw directly
throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
```

### Mapping exceptions to HTTP status codes

| Situation | Status |
|---|---|
| Invalid input, failed validation | **400** Bad Request |
| Not authenticated | **401** Unauthorized |
| Authenticated but not allowed | **403** Forbidden |
| Resource does not exist | **404** Not Found |
| Duplicate or state conflict | **409** Conflict |
| Unexpected server failure | **500** Internal Server Error |

### Transactions and exceptions (important)

`@Transactional` **rolls back on unchecked exceptions (`RuntimeException`, `Error`) but commits on checked ones** by default.

```java
@Transactional                                   // checked IOException => COMMIT, not rollback
void process() throws IOException { ... }

@Transactional(rollbackFor = Exception.class)    // roll back on checked ones too
void process() throws IOException { ... }
```

**Swallowing an exception inside a `@Transactional` method** (catch and ignore) also prevents rollback, because Spring never sees the failure.

### Microservices tie-in

- A failing Feign call throws `FeignException` (unchecked). Translate it into your own exception at the boundary.
- A **circuit breaker fallback** (Resilience4j) is exception handling at the architecture level: failure of a dependency becomes a controlled response.

---

## 15. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Empty `catch` block | Errors vanish silently | Log, handle or rethrow |
| `catch (Exception e)` everywhere | Hides real problems; catches bugs too | Catch specific types |
| Catching a parent before a child | Compile error (unreachable catch) | Most specific first |
| `return` or `throw` in `finally` | Overrides the original result or exception | Keep `finally` for cleanup only |
| Closing resources manually in `finally` | Verbose; the close error hides the original | try-with-resources |
| `throw new X(e.getMessage())` | Loses the cause | `new X("msg", e)` |
| Logging **and** rethrowing at each layer | Duplicate logs | Log once at the boundary |
| Using exceptions for control flow | Slow, unclear | Validate with `if` first |
| Catching `Throwable`/`Error` | Masks JVM failures | Do not |
| `throws Exception` on everything | Pushes the burden onto every caller | Declare specific types, or use unchecked |
| Checked exception in a lambda | Compile error | Wrap or handle inside |
| Forgetting `Thread.currentThread().interrupt()` | Interrupt signal lost | Restore the flag |
| Returning `null` on failure | `NullPointerException` later | `Optional` or throw |
| `@Transactional` with checked exceptions and no `rollbackFor` | No rollback | Add `rollbackFor` |
| Multi-catch with related types | Compile error | Keep only the parent, or separate blocks |
| Exposing stack traces to API users | Security leak | Global handler returns safe messages |
| `printStackTrace()` in production code | No levels, no context | Use a logging framework |
| Using `System.exit()` inside library code | Skips `finally`, kills the host app | Throw an exception instead |

---

## 16. Output Prediction Puzzles

**Puzzle 1: flow order**

```java
try {
    System.out.println("A");
    int x = 10 / 0;
    System.out.println("B");
} catch (ArithmeticException e) {
    System.out.println("C");
} finally {
    System.out.println("D");
}
System.out.println("E");
```

**Puzzle 2: `return` in `finally`**

```java
static int f() {
    try { return 1; }
    finally { return 2; }
}
System.out.println(f());
```

**Puzzle 3: `finally` changes a variable**

```java
static int g() {
    int x = 1;
    try { return x; }
    finally { x = 99; }
}
System.out.println(g());
```

**Puzzle 4: `finally` changes an object**

```java
static StringBuilder h() {
    StringBuilder sb = new StringBuilder("a");
    try { return sb; }
    finally { sb.append("b"); }
}
System.out.println(h());
```

**Puzzle 5: exception inside `finally`**

```java
try {
    throw new RuntimeException("try");
} finally {
    throw new RuntimeException("finally");
}
```

**Puzzle 6: `System.exit`**

```java
try {
    System.exit(0);
} finally {
    System.out.println("finally");
}
```

**Puzzle 7: unmatched catch**

```java
static void m() {
    try {
        throw new IllegalStateException("inner");
    } catch (IllegalArgumentException e) {
        System.out.println("caught IAE");
    } finally {
        System.out.println("finally m");
    }
}

public static void main(String[] args) {
    try { m(); }
    catch (RuntimeException e) { System.out.println("main caught " + e.getMessage()); }
}
```

**Puzzle 8: try-with-resources order**

```java
class R implements AutoCloseable {
    String n;
    R(String n) { this.n = n; System.out.println("open " + n); }
    public void close() { System.out.println("close " + n); }
}

try (R a = new R("A"); R b = new R("B")) {
    System.out.println("body");
}
```

**Puzzle 9: division**

```java
System.out.println(1.0 / 0);
System.out.println(0.0 / 0);
System.out.println(1 / 0);
```

**Puzzle 10: does this compile?**

```java
try {
    Integer.parseInt("12a");
} catch (NumberFormatException | IllegalArgumentException e) {
    System.out.println("bad");
}
```

**Answers**

1. **`A`, `C`, `D`, `E`.** `B` is skipped because the exception interrupts the `try` block.
2. **`2`.** The `return` in `finally` overrides the one in `try`.
3. **`1`.** The return value is saved before `finally` runs; changing `x` afterwards does not affect it.
4. **`ab`.** The saved value is a reference to the same `StringBuilder`, which `finally` mutates.
5. A **`RuntimeException` with message `"finally"`** propagates. The original `"try"` exception is lost, which is why throwing from `finally` is dangerous.
6. **Nothing is printed.** `System.exit` terminates the JVM, so `finally` never runs.
7. **`finally m`** then **`main caught inner`.** The `IllegalStateException` does not match `IllegalArgumentException`, so `finally` runs and the exception propagates to `main`.
8. **`open A`, `open B`, `body`, `close B`, `close A`.** Resources close in reverse order.
9. **`Infinity`**, **`NaN`**, then an **`ArithmeticException: / by zero`**. Only integer division throws.
10. **No, compile error.** `NumberFormatException` is a subclass of `IllegalArgumentException`, and multi-catch alternatives cannot be related by inheritance.

---

## 17. Interview Questions

**1. What is an exception?** An event that disrupts the normal flow of a program at run time.

**2. What is exception handling?** A mechanism (try/catch/finally, throw, throws) to detect, report and recover from runtime problems without crashing.

**3. Exception hierarchy?** `Throwable` splits into `Error` and `Exception`; `Exception` splits into `RuntimeException` (unchecked) and everything else (checked).

**4. Checked vs unchecked?** Checked exceptions are verified at compile time and must be handled or declared; unchecked (`RuntimeException` subclasses) are not.

**5. Error vs Exception?** Errors are serious JVM problems you should not handle; exceptions are conditions your code can handle.

**6. Name some checked and unchecked exceptions.** Checked: `IOException`, `SQLException`. Unchecked: `NullPointerException`, `ArithmeticException`.

**7. `throw` vs `throws`?** `throw` raises an exception object; `throws` declares exceptions a method may throw.

**8. `final` vs `finally` vs `finalize`?** `final` is a modifier; `finally` is the always-run block; `finalize()` was an object-cleanup hook, now deprecated for removal.

**9. Can `try` exist without `catch`?** Yes, with `finally` or with resources (try-with-resources).

**10. Does `finally` always execute?** Almost always. Not after `System.exit()`, a JVM crash or an infinite loop or killed thread in `try`.

**11. What if both `try` and `finally` have `return`?** The `finally` return wins.

**12. Can you have multiple catch blocks?** Yes; order from most specific to most general, or compilation fails.

**13. What is multi-catch?** `catch (A | B e)` handles several unrelated exception types in one block; the types cannot be related by inheritance.

**14. What is try-with-resources?** A `try` that declares `AutoCloseable` resources and closes them automatically in reverse order.

**15. `AutoCloseable` vs `Closeable`?** `AutoCloseable.close()` throws `Exception`; `Closeable` extends it and throws `IOException`.

**16. What are suppressed exceptions?** Exceptions from `close()` that are attached to the primary exception rather than replacing it; read with `getSuppressed()`.

**17. How do you create a custom exception?** Extend `Exception` (checked) or `RuntimeException` (unchecked) and provide message and cause constructors.

**18. When to choose checked vs unchecked for custom exceptions?** Checked when callers can realistically recover and must be forced to decide; unchecked for programming errors and most application errors (modern default).

**19. What is exception chaining?** Wrapping a low-level exception as the cause of a higher-level one to keep the root cause.

**20. How do you read a stack trace?** Top line is the type and message; first `at` line is where it was thrown; `Caused by` at the bottom is usually the root cause.

**21. What is exception propagation?** An unhandled exception moves up the call stack to each caller until it is caught or the thread ends.

**22. Rules for overriding with exceptions?** The overriding method can throw the same, narrower or no checked exceptions, but not broader or new ones; unchecked are unrestricted.

**23. Can a constructor throw an exception?** Yes. Static initializer blocks cannot throw checked exceptions.

**24. What happens if an exception occurs in a `catch` block?** It propagates (after `finally` runs) unless caught by an outer `try`.

**25. What causes `StackOverflowError`?** Infinite or very deep recursion exhausting the thread stack.

**26. `ClassNotFoundException` vs `NoClassDefFoundError`?** The first is a checked exception from dynamic loading (`Class.forName`); the second is an `Error` when a class present at compile time is missing at run time.

**27. How do you avoid `NullPointerException`?** Validate inputs, use `Objects.requireNonNull`, `Optional`, initialize fields, and return empty collections instead of `null`.

**28. Why are empty catch blocks bad?** They hide failures, so bugs go unnoticed.

**29. Should you catch `Exception` or `Throwable`?** Rarely `Exception` (at boundaries); never `Throwable`/`Error` in normal code.

**30. What is a global exception handler in Spring?** A `@RestControllerAdvice` class with `@ExceptionHandler` methods that converts exceptions into consistent HTTP responses.

**31. How does `@Transactional` treat exceptions?** Rolls back on unchecked exceptions and errors; commits on checked ones unless `rollbackFor` is set.

**32. How do you handle checked exceptions in lambdas?** Catch inside the lambda and rethrow unchecked, or use a wrapper helper.

**33. What happens to an uncaught exception in a thread?** That thread terminates; others continue. An `UncaughtExceptionHandler` can intercept it.

**34. How do exceptions in `ExecutorService` tasks surface?** Wrapped in `ExecutionException` when calling `Future.get()`.

**35. Why restore the interrupt flag after `InterruptedException`?** Catching it clears the flag; calling `Thread.currentThread().interrupt()` preserves the signal for callers.

**36. What does "fail fast" mean?** Detect and report invalid state or input as early as possible instead of letting corrupt data travel.

---

## 18. Cheat Sheet

| Concept | Key point |
|---|---|
| Root | `Throwable` splits into `Error` and `Exception` |
| Checked | Extends `Exception`; **compiler forces handle or declare** |
| Unchecked | Extends `RuntimeException`; programming errors |
| Error | JVM-level; do not catch |
| `try` | Code that may fail |
| `catch` | Handle a type; **specific before general** |
| `finally` | Cleanup; runs except on `System.exit` or crash |
| `throw` | Raise an exception object |
| `throws` | Declare possible checked exceptions |
| try-with-resources | Auto-close `AutoCloseable`s in **reverse order** |
| Custom exception | `extends RuntimeException` (default) or `Exception`; constructors `(msg)` and `(msg, cause)` |
| Chaining | `new X("message", cause)`: never lose the cause |
| Overriding | Same, narrower or no checked exceptions |
| Spring | `@RestControllerAdvice` plus `@ExceptionHandler`; rollback only on unchecked by default |

**Golden rules**

```
1. Catch specific exceptions, as late as you can do something useful.
2. Never swallow an exception silently.
3. Always preserve the cause when wrapping.
4. Clean up with try-with-resources, not manual finally.
5. Do not return/throw from finally.
6. Log once, at the boundary; do not leak internals to clients.
7. Fail fast: validate early with standard exceptions.
```

---

## 19. Practice Tasks with Solutions

**Task 1: safe integer parsing**

Write a method that returns an `Optional<Integer>` for any string, never throwing.

```java
static Optional<Integer> parse(String s) {
    try {
        return Optional.of(Integer.parseInt(s.trim()));
    } catch (NumberFormatException | NullPointerException e) {
        return Optional.empty();
    }
}

parse("42");     // Optional[42]
parse("12a");    // Optional.empty
parse(null);     // Optional.empty
```

**Task 2: custom exception and handler**

Create `InsufficientFundsException` (checked) with a `shortfall` field, throw it from `withdraw`, and print a friendly message in the caller. (See section 7 for the full code.)

**Task 3: try-with-resources**

Write a class `Connection implements AutoCloseable` that prints "open" and "close". Use two of them in one try-with-resources and confirm they close in reverse order. (See section 6.)

**Task 4: read a file safely**

```java
static List<String> readLines(Path path) {
    try (Stream<String> lines = Files.lines(path)) {
        return lines.toList();
    } catch (NoSuchFileException e) {
        throw new ResourceNotFoundException("File not found: " + path);
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
}
```

`Files.lines` returns a stream that holds an open file, so it must be closed with try-with-resources.

**Task 5: retry with limited attempts**

```java
static <T> T retry(int attempts, Callable<T> action) throws Exception {
    Exception last = null;
    for (int i = 1; i <= attempts; i++) {
        try {
            return action.call();
        } catch (Exception e) {
            last = e;
            System.out.println("Attempt " + i + " failed: " + e.getMessage());
        }
    }
    throw last;
}
```

(Only retry **idempotent** operations; add a delay or backoff in real use.)

**Task 6: global handler**

Build a Spring Boot controller `GET /employees/{id}` that throws `ResourceNotFoundException` when the id is missing, and a `@RestControllerAdvice` that returns a 404 JSON body. Test that a missing id returns `404` with your `ErrorResponse`.

**Task 7: exception translation**

Write `findOrder(long id)` that calls a method throwing `SQLException` and rethrows it as an unchecked `DataAccessException` **with the cause**. Print `e.getCause()` in the caller to verify.

**Task 8: spot the bugs**

```java
try {
    process();
} catch (Exception e) {
}

try {
    FileReader fr = new FileReader("x.txt");
    // ... use fr ...
} catch (Exception e) {
    throw new RuntimeException(e.getMessage());
}
```

### Challenge questions

1. A `@Transactional` service method catches a `RuntimeException` from a repository call, logs it, and returns normally. Will the transaction roll back?
2. A method declares `throws IOException`. Can its override throw `Exception`? Can it throw `FileNotFoundException`?
3. Why does `try (Resource r = new Resource()) { ... }` hide **less** information than a hand-written `finally { r.close(); }` when both the body and `close()` fail?

**Answers**

1. **No.** The exception never reaches Spring's transaction interceptor, so Spring thinks the method succeeded and **commits**. Rethrow it, or call `TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()`.
2. Not `Exception` (broader than the parent's `IOException`). Yes for `FileNotFoundException` (a narrower subclass).
3. With try-with-resources, the **body exception stays primary** and the `close()` failure is attached as a **suppressed** exception, so both are visible. In a manual `finally`, an exception from `close()` **replaces** the original, so the real cause is lost.

**Spot-the-bugs answers (Task 8):** the first block swallows every exception silently. The second never closes `fr` (use try-with-resources), catches the over-broad `Exception`, and loses the cause and stack trace by passing only `e.getMessage()` (use `new RuntimeException("message", e)`).

---

*End of notes.*
