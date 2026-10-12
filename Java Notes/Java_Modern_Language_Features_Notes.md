# Modern Java Language Features

Records, sealed types, pattern matching, `null` in `switch`, private interface methods, `var`, modern `String` methods, and `var` in lambda parameters. Each topic explains what it is, why it was added, and how to use it, with real-life analogies and extras.

> **Recommended JDK: 21 or later.** Pattern matching for `switch` and `case null` are standard features from Java 21.

---

## Table of Contents

0. [Big Picture and Version Timeline](#0-big-picture-and-version-timeline)
1. [Records](#1-records)
2. [Sealed Classes and Interfaces](#2-sealed-classes-and-interfaces)
3. [Pattern Matching](#3-pattern-matching)
4. [`null` in `switch`](#4-null-in-switch)
5. [Private Methods in Interfaces](#5-private-methods-in-interfaces)
6. [The `var` Keyword](#6-the-var-keyword)
7. [Modern String Methods](#7-modern-string-methods)
8. [`var` in Lambda Parameters](#8-var-in-lambda-parameters)
9. [Everything Together (Corrected Examples)](#9-everything-together-corrected-examples)
10. [Common Mistakes and Gotchas](#10-common-mistakes-and-gotchas)
11. [Interview Questions and Answers](#11-interview-questions-and-answers)
12. [Quick Comparison Table](#12-quick-comparison-table)
13. [Exercises with Solutions](#13-exercises-with-solutions)
14. [Key Takeaways](#14-key-takeaways)

---

## 0. Big Picture and Version Timeline

### The theme

For years Java was criticized as verbose: too much boilerplate, too many casts, too many repeated type names. Since Java 9, the language has steadily removed ceremony while keeping **static typing and safety**.

| Pain in old Java | Modern fix |
|---|---|
| Writing getters, constructor, `equals`, `hashCode`, `toString` for a data class | **Records** |
| Anyone can extend your class | **Sealed types** |
| `instanceof` followed by a manual cast | **Pattern matching** |
| `switch` crashes on `null`; `switch` only handles constants | **`case null`, pattern `switch`** |
| Duplicate code inside interface default methods | **Private interface methods** |
| `Map<String, List<Integer>> m = new HashMap<String, List<Integer>>();` | **`var`** |
| Hand-written whitespace and text utilities | **New `String` methods** |

### When each feature became standard

| Feature | Standard in |
|---|---|
| Private interface methods | Java 9 |
| `var` for local variables | Java 10 |
| `var` in lambda parameters | Java 11 |
| `isBlank`, `strip`, `stripLeading`, `stripTrailing`, `lines`, `repeat` | Java 11 |
| `indent`, `transform` | Java 12 |
| `stripIndent`, `translateEscapes`, `formatted` (with text blocks) | Java 15 |
| Pattern matching for `instanceof` | Java 16 |
| Records | Java 16 |
| Sealed classes and interfaces | Java 17 |
| Pattern matching for `switch`, `case null`, record patterns | Java 21 |

---

## 1. Records

### What

A **record** is a special kind of class designed to **carry data**. You declare the data fields once, and the compiler generates:

- a canonical constructor
- accessor methods
- `equals()`
- `hashCode()`
- `toString()`

### Why

Look at how much code a simple "customer" data holder needs in traditional Java:

```java
public final class Customer {
    private final int id;
    private final String name;

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId()      { return id; }
    public String getName() { return name; }

    // plus equals(), hashCode(), toString() if you want them correct
}
```

Around 15 to 30 lines of code that all say one thing: "a customer has an id and a name." Equivalent record:

```java
public record Customer(int id, String name) { }
```

### Real-life analogy

A **printed visiting card** or **ID card**. The details are decided at the time of printing (construction), you can read them anytime, and two cards with identical details are considered the same card. You don't edit a printed card; you print a new one. That is exactly a record: **fixed data, readable, compared by content**.

### How to use it

```java
public record Employee(int id, String name, double salary) { }

Employee e = new Employee(101, "Praveen", 85000);

System.out.println(e.id());       // 101
System.out.println(e.name());     // Praveen
System.out.println(e.salary());   // 85000.0
System.out.println(e);            // Employee[id=101, name=Praveen, salary=85000.0]
```

> **Accessors are `id()` and `name()`, not `getId()` and `getName()`.** Beginners lose marks on this.

### What the compiler generates

| Generated | Behavior |
|---|---|
| Private final fields | One per component |
| Canonical constructor | Takes all components in order |
| Accessors | `component()` for each field |
| `equals()` | Two records are equal if **all components are equal** |
| `hashCode()` | Computed from all components |
| `toString()` | `RecordName[field=value, ...]` |

### Value-based equality

```java
Employee a = new Employee(101, "Praveen", 85000);
Employee b = new Employee(101, "Praveen", 85000);

System.out.println(a == b);        // false  (different objects)
System.out.println(a.equals(b));   // true   (same content)
```

This is why records work correctly as keys in a `HashMap` or items in a `HashSet` without extra code.

### Rules about records

- Records are **implicitly `final`**: no one can extend them.
- A record **cannot extend another class** (it implicitly extends `java.lang.Record`).
- A record **can implement interfaces**.
- Fields are **`private final`**; there are no setters.
- A record **cannot declare extra instance fields** beyond its components.
- A record **can** have static fields, static methods, instance methods, and nested types.

### Compact constructor (validation)

A **compact constructor** has no parameter list. It runs **before** the fields are assigned, which makes it ideal for validation.

```java
public record Employee(int id, String name, double salary) {

    public Employee {                                   // no (...) here
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }
}
```

The compiler adds the field assignments **at the end automatically**. Do not write `this.id = id;` inside a compact constructor; that is a compile error.

**Analogy:** the security check at the ID-card office. Details are verified **before** the card is printed. If the check fails, no card is produced at all, so an invalid `Employee` can never exist.

### Normalizing data in the compact constructor

You may reassign the **parameter** (not the field) to clean data:

```java
public record Customer(String name, String email) {
    public Customer {
        name = name.strip();
        email = email.toLowerCase();
    }
}
```

### Methods inside records

```java
public record Employee(int id, String name, double salary) {

    public double annualBonus() {
        return salary * 0.10;
    }

    public boolean highEarner() {
        return salary >= 100000;
    }
}
```

### Extra constructors must delegate

A record can have additional constructors, but each must call the canonical one using `this(...)` (the same constructor chaining from your `this()` notes):

```java
public record Point(int x, int y) {
    public Point() {
        this(0, 0);
    }
}
```

### Static members

```java
public record Point(int x, int y) {
    public static final Point ORIGIN = new Point(0, 0);

    public static Point of(int x, int y) {
        return new Point(x, y);
    }
}
```

### Overriding an accessor

```java
public record Person(String name) {
    @Override
    public String name() {
        return name.toUpperCase();
    }
}
```

### Generic and local records

```java
record Pair<A, B>(A first, B second) { }

void demo() {
    record Temp(int a, int b) { }          // local record, handy inside a method
    var t = new Temp(1, 2);
}
```

### Records implementing interfaces

```java
interface Printable { void print(); }

record Invoice(int no, double amount) implements Printable {
    public void print() {
        System.out.println("Invoice " + no + ": " + amount);
    }
}
```

### Banking example

```java
public record Transaction(
        String transactionId,
        String accountNumber,
        double amount,
        String type) { }

Transaction t = new Transaction("TX1001", "ACC101", 5000, "DEPOSIT");
System.out.println(t.transactionId());   // TX1001
System.out.println(t.amount());          // 5000.0
```

A transaction, once recorded, should **never change**. Records match that business rule exactly.

### Immutability is shallow (important)

The **fields** are final, but if a field refers to a **mutable object**, that object can still change.

```java
record Team(String name, List<String> members) { }

var list = new ArrayList<>(List.of("Asha", "Ravi"));
var team = new Team("Dev", list);

list.add("Neha");                    // the record's data changed!
System.out.println(team.members());  // [Asha, Ravi, Neha]
```

**Fix with a defensive copy in the compact constructor:**

```java
record Team(String name, List<String> members) {
    public Team {
        members = List.copyOf(members);   // unmodifiable snapshot
    }
}
```

**Analogy:** the ID card is fixed, but if it lists "contact: see the shared notebook," anyone can edit the notebook. Copy the notebook's contents onto the card instead.

### Where to use and where not to use

| Good for | Not good for |
|---|---|
| DTOs (data transfer objects) | Objects whose fields must change over time |
| API request and response bodies | JPA/Hibernate entities (they need a no-arg constructor and non-final class) |
| Transactions, events, messages | Classes that need inheritance |
| Map keys, small domain values | Classes with lots of behavior and hidden state |
| Returning multiple values from a method | |

### Record vs Lombok `@Data`

| Record | Lombok `@Data` |
|---|---|
| Built into the language | Third-party library, annotation processing |
| Always immutable | Mutable by default |
| Cannot be extended | Can be extended |
| Accessors `name()` | Getters `getName()` |

---

## 2. Sealed Classes and Interfaces

### What

A **sealed** class or interface declares **exactly which classes are allowed** to extend or implement it.

### Why

Normally, any class anywhere can extend your class. Sometimes the business domain has a **fixed, known set of possibilities**, and you want the compiler to know and enforce that set.

### Real-life analogy

A bank **offers only a few account types: Savings and Current**. A branch manager cannot just invent "Loan Account" and start issuing it. Only authorized types exist, and every employee knows the complete list. A sealed class is that "authorized list" written in code.

Another picture: a **franchise**. Only approved outlets may use the brand name.

### Syntax

```java
public sealed class Account permits SavingsAccount, CurrentAccount { }

public final class SavingsAccount extends Account { }
public final class CurrentAccount extends Account { }
```

This is **illegal**, because `LoanAccount` is not in the permits list:

```java
public class LoanAccount extends Account { }   // compile error
```

### Why use sealed types

- **Controlled inheritance:** you decide who can extend.
- **Clearer modeling:** the code documents "these are all the cases."
- **Safer APIs:** outsiders cannot add surprising subtypes.
- **Compiler knows the complete hierarchy:** this unlocks exhaustive `switch` (section 3).

### Rule: every permitted subclass must choose one of three modifiers

| Modifier | Meaning | Franchise analogy |
|---|---|---|
| `final` | No one may extend me further | Outlet cannot open sub-outlets |
| `sealed` | I may be extended, but only by my own permitted list | Outlet can open only approved sub-outlets |
| `non-sealed` | I reopen the hierarchy; anyone may extend me | Outlet can open sub-outlets freely |

```java
public sealed class Payment
        permits CardPayment, CashPayment, DigitalPayment { }

public final class CardPayment extends Payment { }

public sealed class CashPayment extends Payment
        permits PhysicalCashPayment { }

public final class PhysicalCashPayment extends CashPayment { }

public non-sealed class DigitalPayment extends Payment { }
```

Why force a choice? Without it, a subclass would silently become an open door, defeating the point of sealing.

### Sealed interfaces

```java
public sealed interface Account permits SavingsAccount, CurrentAccount { }

public final class SavingsAccount implements Account { }
public final class CurrentAccount implements Account { }
```

### Records and enums are natural fits

Records are implicitly `final`, so they need no extra modifier:

```java
public sealed interface Shape permits Circle, Rectangle { }

public record Circle(double radius) implements Shape { }
public record Rectangle(double w, double h) implements Shape { }
```

### Extra rules

- Permitted subclasses must **directly** extend or implement the sealed type.
- They must be in the **same module**, or the **same package** if you are not using modules.
- If all permitted subclasses are in the **same source file**, you may **omit `permits`**; the compiler infers them.

```java
sealed interface Vehicle { }                // permits inferred from this file
record Car(String model) implements Vehicle { }
record Bike(String model) implements Vehicle { }
```

### Sealed vs enum vs final

| Tool | Use when |
|---|---|
| `enum` | A fixed set of **constant instances** (e.g. `MONDAY`, `TUESDAY`) |
| `final` class | **No** subtypes at all |
| `sealed` | A fixed set of **types**, each of which can hold different data |

An enum has several values of one type. A sealed hierarchy has several **types**, each with its own shape of data (`CardPayment` might carry a card number, `UpiPayment` a UPI id).

---

## 3. Pattern Matching

### What

**Pattern matching** combines **testing a type** and **extracting a typed variable** in one step.

### Why

Old code checked a type and then repeated the same type in a cast. That is noisy and a classic source of `ClassCastException` bugs when the check and the cast drift apart.

### Real-life analogy

A **courier sorting office**. A parcel arrives; the worker checks "is this a fragile parcel?" and, if yes, immediately gets it into the fragile tray with the right handling label. The old way was "check it's fragile, then go back and manually re-label it as fragile." Pattern matching does the check and the label in one move.

### Before pattern matching

```java
Object obj = "Hello";

if (obj instanceof String) {
    String value = (String) obj;     // repeated type, manual cast
    System.out.println(value.length());
}
```

### Modern `instanceof` pattern

```java
Object obj = "Hello";

if (obj instanceof String value) {
    System.out.println(value.length());
}
```

`value` is a **pattern variable**. It exists only where the compiler can prove the test succeeded.

### With `&&`

```java
if (obj instanceof String value && value.length() > 5) {
    System.out.println(value);
}
```

This works because `&&` only evaluates the right side when the left side is true, so `value` is definitely a `String` there.

**This will not compile:**

```java
if (obj instanceof String value || value.length() > 5) { }   // value may not exist
```

### Scope that follows the logic

```java
static int lengthOf(Object obj) {
    if (!(obj instanceof String s)) {
        return -1;                 // leaves the method
    }
    return s.length();             // s is in scope here, because we only get here if it IS a String
}
```

### Pattern matching for `switch` (Java 21)

```java
static String describe(Object obj) {
    return switch (obj) {
        case String s  -> "String with length " + s.length();
        case Integer i -> "Integer with value " + i;
        case Double d  -> "Double with value " + d;
        default        -> "Unknown type";
    };
}
```

Compare with the old chain of `if (x instanceof ...) else if (...)` plus casts: shorter, clearer, and the compiler checks it.

### Guards with `when`

A **guard** adds an extra condition to a pattern:

```java
static String classify(Object obj) {
    return switch (obj) {
        case Integer i when i > 100   -> "Big integer " + i;
        case Integer i                -> "Integer " + i;
        case String s when s.isBlank() -> "Blank string";
        case String s                 -> "String " + s;
        default                       -> "Other";
    };
}
```

### Dominance rule: order matters

If a general case comes before a specific one, the specific one is unreachable, and the compiler rejects it:

```java
switch (obj) {
    case Integer i -> ...
    case Integer i when i > 100 -> ...   // compile error: dominated by the case above
    default -> ...
}
```

**Always put the more specific case first.** Analogy: a gatekeeper who says "all adults enter" will never reach the later rule "VIP adults enter by the side door."

### Pattern matching with sealed types: exhaustiveness

```java
public sealed interface Payment
        permits CardPayment, CashPayment, UpiPayment { }

public record CardPayment(double amount) implements Payment { }
public record CashPayment(double amount) implements Payment { }
public record UpiPayment(double amount)  implements Payment { }

static String process(Payment payment) {
    return switch (payment) {
        case CardPayment c -> "Processing card: " + c.amount();
        case CashPayment c -> "Processing cash: " + c.amount();
        case UpiPayment u  -> "Processing UPI: " + u.amount();
    };   // no default needed
}
```

Because `Payment` is sealed and every permitted subtype is covered, the compiler knows the `switch` is **exhaustive**. No `default` is required.

### Why this is the real payoff

Suppose next month you add `NetBankingPayment` to the `permits` list. Every `switch` over `Payment` immediately **fails to compile** until you handle the new case.

**Analogy:** a UPI app's payment-mode screen. If the bank adds a new payment mode, the app refuses to build until every screen that lists modes is updated. Compare this with a `default -> "Unknown"` branch, which would silently swallow the new type and fail at runtime.

> **Design tip:** avoid `default` on a switch over a sealed type. A `default` hides the exact safety you sealed the hierarchy to get.

### Record patterns (Java 21): deconstruction

A pattern can **take a record apart** and bind its components:

```java
record Point(int x, int y) { }

static String where(Object o) {
    if (o instanceof Point(int x, int y)) {
        return "x=" + x + ", y=" + y;
    }
    return "not a point";
}
```

Inside `switch`, with sealed types:

```java
sealed interface Shape permits Circle, Rectangle { }
record Circle(double radius) implements Shape { }
record Rectangle(double w, double h) implements Shape { }

static double area(Shape s) {
    return switch (s) {
        case Circle(double r)            -> Math.PI * r * r;
        case Rectangle(double w, double h) -> w * h;
    };
}
```

No accessor calls, no casts. Records, sealed types, and patterns are designed to work as a set.

---

## 4. `null` in `switch`

### What

Modern Java lets a `switch` have an explicit **`case null`**.

### Why

Historically, switching on a `null` selector threw `NullPointerException`, forcing a separate `if (x == null)` before every `switch`.

### Real-life analogy

A **receptionist** who is asked to route visitors by department. If a visitor arrives with no department written down, a bad system crashes. A good system has an explicit rule: "no department given, send to the help desk." `case null` is that rule.

### Old style (Java 21 syntax allows it with colons)

```java
String value = null;

switch (value) {
    case null:
        System.out.println("Value is null");
        break;
    case "A":
        System.out.println("Value is A");
        break;
    default:
        System.out.println("Other value");
}
```

### Switch expression

```java
String result = switch (value) {
    case null -> "NULL";
    case "A"  -> "VALUE A";
    case "B"  -> "VALUE B";
    default   -> "OTHER";
};
```

### With patterns

```java
static String describe(Object obj) {
    return switch (obj) {
        case null       -> "Object is null";
        case String s   -> "String: " + s;
        case Integer i  -> "Integer: " + i;
        default         -> "Other object";
    };
}
```

Null handling and type handling live in **one** construct instead of an `if` wrapped around a `switch`.

### The critical gotcha: `default` does NOT match `null`

```java
String v = null;
switch (v) {
    case "A" -> System.out.println("A");
    default  -> System.out.println("other");
}
// throws NullPointerException; default does not catch null
```

If there is **no `case null`**, a `null` selector still throws NPE, even when `default` exists.

### Combine null and default

```java
String label = switch (v) {
    case "A" -> "Value A";
    case null, default -> "Missing or unknown";
};
```

### Use `case null` only when null is a legitimate input

If `null` indicates a bug, letting the NPE fire may be better than hiding it. `case null` is for inputs where "no value" is a real, expected case (an optional form field, a missing config).

---

## 5. Private Methods in Interfaces

### What

Since **Java 9**, interfaces can contain `private` methods (instance and static). Interfaces may now contain:

- abstract methods
- `default` methods
- `static` methods
- `private` methods
- constants (implicitly `public static final`)

### Why

Java 8 added `default` and `static` methods to interfaces. Soon, several of those methods contained **the same repeated code**, and the only way to share it was to make a helper method `public`, which would **expose it to every implementing class** and clutter the API.

### Real-life analogy

A **restaurant**. The menu (public interface) lists dishes. Many dishes need the same prep: chopping onions, heating oil. That shared prep happens **in the kitchen**, not on the menu, and customers never ask for it by name. A private interface method is that hidden kitchen step.

### Without a private method (duplication)

```java
public interface Report {

    default void generateDailyReport() {
        System.out.println("Opening report");
        System.out.println("Validating data");
        System.out.println("Daily report");
    }

    default void generateMonthlyReport() {
        System.out.println("Opening report");
        System.out.println("Validating data");
        System.out.println("Monthly report");
    }
}
```

### With a private method

```java
public interface Report {

    default void generateDailyReport() {
        startReport();
        System.out.println("Daily report");
    }

    default void generateMonthlyReport() {
        startReport();
        System.out.println("Monthly report");
    }

    private void startReport() {
        System.out.println("Opening report");
        System.out.println("Validating data");
    }
}
```

The private method:

- is accessible **only inside the interface**
- is **not inherited** by implementing classes
- can be called by the interface's `default`, `static`, and other private methods

### Private static methods

A `static` method in an interface can only call **static** helpers, so use `private static`:

```java
public interface Util {

    static String shout(String s) {
        return decorate(s.toUpperCase());
    }

    private static String decorate(String s) {
        return "** " + s + " **";
    }
}
```

### Rules

| Rule | Detail |
|---|---|
| Must have a body | `private` + abstract is meaningless |
| Cannot be `default` | `private default` is invalid |
| Not visible to implementing classes | Not inherited, not overridable |
| Private instance methods | Callable from default and other private instance methods |
| Private static methods | Callable from static and instance methods of the interface |

### Banking example

```java
public interface BankReport {

    default void dailyReport() {
        prepare();
        System.out.println("Daily banking report");
    }

    default void monthlyReport() {
        prepare();
        System.out.println("Monthly banking report");
    }

    private void prepare() {
        System.out.println("Loading account data");
        System.out.println("Validating transactions");
    }
}

class MyBank implements BankReport { }

new MyBank().dailyReport();
// Loading account data
// Validating transactions
// Daily banking report
```

`new MyBank().prepare()` would not compile. The helper is invisible outside the interface.

---

## 6. The `var` Keyword

### What

From **Java 10**, you can declare a local variable with `var` and let the compiler **infer the type from the initializer**.

```java
var name = "Praveen";     // String
var age = 46;             // int
var salary = 85000.50;    // double
```

### Why

To remove **repeated, obvious type names**:

```java
// Old
HashMap<String, List<Integer>> marks = new HashMap<String, List<Integer>>();

// Modern
var marks = new HashMap<String, List<Integer>>();
```

### Real-life analogy

A **label on a jar** that the compiler writes for you. When you pour sugar into a jar, you don't need to also announce "this jar contains sugar" twice. But the jar **still has a label** (the type). It is just written by the compiler instead of you.

### `var` is NOT dynamic typing

```java
var x = 100;
x = "Hello";       // compile-time error: x is an int forever
```

Java remains **statically typed**. The type is fixed at compile time. It is unlike JavaScript's `var`, or Python, where a variable can change type.

### Where `var` can be used

```java
var name = "Praveen";
var employees = new ArrayList<String>();
var customer = new Customer(101, "Ravi", "CHENNAI");
```

Enhanced `for`:

```java
var names = List.of("Ravi", "Sita", "John");
for (var n : names) {
    System.out.println(n);
}
```

Classic `for`:

```java
for (var i = 0; i < 5; i++) { }
```

Try-with-resources:

```java
try (var scanner = new Scanner(System.in)) {
    // use scanner
}
```

### Where `var` CANNOT be used

| Place | Example | Why not |
|---|---|---|
| Fields | `class E { var name; }` | No initializer-based inference at class level |
| Method return type | `public var getName()` | Callers need to know the type from the signature |
| Method parameter | `void test(var v)` | Same reason |
| No initializer | `var x;` | Nothing to infer from |
| `null` initializer | `var v = null;` | `null` has no type |
| Array initializer shortcut | `var a = {1, 2};` | Needs a target type |
| Lambda or method reference alone | `var f = x -> x * 2;` | Needs a target functional interface |
| Multiple declarations | `var a = 1, b = 2;` | Not allowed |

**Fix for the `null` case:**

```java
String value = null;
```

### Gotchas

**1. Diamond with `var` loses information**

```java
var list = new ArrayList<>();      // ArrayList<Object>, not what you probably wanted
var list2 = new ArrayList<String>(); // ArrayList<String>
```

Without a target type, `<>` has nothing to infer from, so it falls back to `Object`.

**2. Inference uses the expression's type, not your intention**

```java
var n = 10;        // int, not long
var d = 10 / 4;    // int 2, not 2.5
```

**3. The reserved name**

`var` is a *reserved type name*, not a keyword. `var var = 5;` compiles, but never do it.

### When to use `var`

**Good (type is obvious from the right-hand side):**

```java
var customer = new Customer();
var names = new ArrayList<String>();
```

**Less readable (type is hidden):**

```java
var data = getData();      // what is data?
var result = process(x);   // List? Map? int?
```

**Rule of thumb:** use `var` to reduce *repetition*, not to hide *information*. If a reader has to hunt for the type, write it explicitly.

---

## 7. Modern String Methods

### Overview

| Method | Since | Purpose |
|---|---|---|
| `isBlank()` | 11 | True if empty or only whitespace |
| `strip()` | 11 | Remove leading and trailing whitespace (Unicode-aware) |
| `stripLeading()` | 11 | Remove leading whitespace |
| `stripTrailing()` | 11 | Remove trailing whitespace |
| `lines()` | 11 | Split into a `Stream<String>` of lines |
| `repeat(n)` | 11 | Repeat the string `n` times |
| `indent(n)` | 12 | Add or remove indentation per line |
| `transform(fn)` | 12 | Apply a function to the string |
| `stripIndent()` | 15 | Remove incidental indentation |
| `translateEscapes()` | 15 | Turn `\n`-style text into real escape characters |
| `formatted(args)` | 15 | Instance version of `String.format` |

### `isBlank()`

```java
"".isBlank();          // true
"   ".isBlank();       // true
"  Hello  ".isBlank(); // false
```

- `isEmpty()` is true **only** when length is 0.
- `isBlank()` is true for empty **and** whitespace-only strings.

**Real life:** a "Name" field in a form where someone typed three spaces. `isEmpty()` would accept it as valid; `isBlank()` correctly rejects it.

**Null-safe check:**

```java
if (s == null || s.isBlank()) { /* treat as missing */ }
```

Both methods throw `NullPointerException` when `s` is `null`.

### `strip()`, `stripLeading()`, `stripTrailing()`

```java
String value = "   Hello Java   ";

value.strip();           // "Hello Java"
value.stripLeading();    // "Hello Java   "
value.stripTrailing();   // "   Hello Java"
```

Strings are immutable, so these return **new** strings; the original is unchanged.

### `strip()` vs `trim()`

`trim()` removes characters up to `\u0020` (the ASCII space and control characters). `strip()` uses `Character.isWhitespace`, which understands Unicode spaces.

```java
String s = "\u2003Hello\u2003";    // em-space around Hello

System.out.println(s.trim().length());    // 7  (trim cannot remove them)
System.out.println(s.strip().length());   // 5
```

> **Note:** neither removes the non-breaking space `\u00A0`. For new code, prefer `strip()`.

### `repeat()`

```java
"-".repeat(10);     // "----------"
"=".repeat(50);     // handy console divider
"ab".repeat(3);     // "ababab"
"x".repeat(0);      // ""
"x".repeat(-1);     // IllegalArgumentException
```

**Real life:** printing a line of dashes under a report heading, or building a simple progress bar.

### `lines()`

```java
String text = "Java\nSpring\nMicroservices\nDocker";

text.lines().forEach(System.out::println);
```

- Returns a `Stream<String>`, so you can chain `filter`, `map`, `count`.
- Handles `\n`, `\r`, and `\r\n` line endings.
- Does not include the line terminators.

```java
long nonBlank = text.lines().filter(l -> !l.isBlank()).count();
```

### `transform()`

Applies a function to the string and returns the result. It can return **any type**.

```java
String name = "  praveen  ";

String result = name
        .transform(String::strip)
        .transform(String::toUpperCase);

System.out.println(result);   // PRAVEEN

int number = "42".transform(Integer::parseInt);   // changes type to int
```

It lets you build **left-to-right pipelines** instead of nesting calls inside each other.

### `formatted()`

```java
String name = "Praveen";
int age = 46;

String msg = "Name: %s, Age: %d".formatted(name, age);
// Name: Praveen, Age: 46
```

Same as `String.format("Name: %s, Age: %d", name, age)`, but reads in natural order.

### `indent()`

```java
String text = "Java\nSpring\nDocker";

System.out.println(text.indent(4));
//     Java
//     Spring
//     Docker
```

- Positive `n` adds `n` spaces to every line; negative `n` removes up to `n` leading spaces.
- It normalizes line endings and **appends a trailing newline**, so `println` will show an extra blank line.

### `stripIndent()`

Removes the **common leading whitespace** shared by all lines. It is what text blocks use internally.

> **The sample in the trainer reference has no visible effect.** For `"Java\n  Spring\n    Docker"`, the first line has **zero** indentation, so the common indent is zero and nothing is removed. A working example:

```java
String text = "  Java\n    Spring\n      Docker";

System.out.println(text.stripIndent());
// Java
//   Spring
//     Docker
```

The smallest indent (2 spaces) is removed from every line, keeping the **relative** indentation.

### `translateEscapes()`

Converts escape sequences that are written as **plain characters** into the real characters.

```java
String value = "Hello\\nWorld";     // contains a backslash and an n, not a newline

System.out.println(value);                      // Hello\nWorld
System.out.println(value.translateEscapes());   // Hello
                                                // World
```

Useful when text comes from a file, JSON, or user input, where `\n` arrives as two literal characters.

### Putting several together

```java
String raw = "   java programming   ";

String result = raw
        .strip()
        .transform(String::toUpperCase);

System.out.println("=".repeat(30));
System.out.println("Course: %s".formatted(result));
System.out.println("=".repeat(30));
```

---

## 8. `var` in Lambda Parameters

### What

From **Java 11**, lambda parameters can be declared with `var`.

```java
(var x) -> x * 2
(var a, var b) -> a + b
```

### The three ways to write lambda parameters

```java
(x) -> x * 2          // inferred, no type shown
(int x) -> x * 2      // explicit type
(var x) -> x * 2      // inferred, but with var
```

### Why would anyone write `(var x)` if `(x)` already works?

The main reason: **annotations**. You cannot attach an annotation to a bare inferred parameter. Annotations need a type declaration after them, and `var` provides that.

```java
(@Deprecated var value) -> value                     // legal
(@NonNull var name) -> name.toUpperCase()            // needs a library such as Lombok or Checker Framework
```

**Real-life analogy:** a conference **name badge**. A plain badge shows only your name. To add a "Speaker" sticker you need a badge slot for it. `var` gives the parameter a slot where annotations can attach, while still letting the compiler figure out the type.

### Consistency rule

If you use `var`, you must use it for **every** parameter, and you must not mix with explicit types or bare names.

| Lambda | Valid? |
|---|---|
| `(var a, var b) -> a + b` | Valid |
| `(a, b) -> a + b` | Valid (no var) |
| `(int a, int b) -> a + b` | Valid (explicit types) |
| `(var a, b) -> a + b` | **Invalid** (mixed) |
| `(var a, int b) -> a + b` | **Invalid** (mixed) |

**Why:** the language treats a parameter list as one unit: all inferred, all explicit, or all `var`. Mixed styles would be confusing to read and complicate the compiler's rules.

### Parentheses are required with `var`

```java
var x -> x * 2        // invalid
(var x) -> x * 2      // valid
```

This differs from the simple one-parameter shortcut where `x -> x * 2` is allowed.

### Example

```java
List<String> names = List.of("Ravi", "Sita", "John");

names.forEach((var n) -> System.out.println(n.toUpperCase()));
```

---

## 9. Everything Together (Corrected Examples)

### 9.1 Customer example

> **Source file issue:** the trainer's version puts `public class ModernJavaDemo` and `public record Customer` in **one file**. Java allows **only one public top-level type per file**, and it must match the file name, so that fails to compile. Fix: drop `public` from the record, or put it in its own file `Customer.java`.

**File: `ModernJavaDemo.java`**

```java
import java.util.List;

public class ModernJavaDemo {

    public static void main(String[] args) {

        List<Customer> customers = List.of(
            new Customer(101, "Praveen", "HYDERABAD"),
            new Customer(102, "Ravi", "BANGALORE"),
            new Customer(103, "Sita", "CHENNAI")
        );

        customers.forEach(
            (var customer) ->
                System.out.println(customer.id() + " - " + customer.name())
        );

        Object value = "Hello Java";

        String result = switch (value) {
            case null     -> "NULL VALUE";
            case String s -> "String length: " + s.length();
            default       -> "Other type";
        };

        System.out.println(result);
    }
}

record Customer(int id, String name, String city) { }   // no 'public'
```

**Output:**

```
101 - Praveen
102 - Ravi
103 - Sita
String length: 10
```

**What it demonstrates:** a record, `var` in a lambda, pattern `switch`, explicit `case null`.

### 9.2 Banking example

```java
import java.util.List;

public class BankingModernJavaDemo {

    public static void main(String[] args) {

        var payments = List.of(
            new CardPayment(5000),
            new UpiPayment(2500),
            new CashPayment(1000)
        );

        payments.forEach(
            (var payment) -> System.out.println(processPayment(payment))
        );

        String customerName = "   praveen kumar   ";
        String formattedName = customerName.strip().toUpperCase();
        System.out.println(formattedName);

        String value = null;
        System.out.println(describe(value));
    }

    static String processPayment(Payment payment) {
        return switch (payment) {
            case CardPayment c -> "Card payment: Rs." + c.amount();
            case UpiPayment u  -> "UPI payment: Rs." + u.amount();
            case CashPayment c -> "Cash payment: Rs." + c.amount();
        };
    }

    static String describe(String value) {
        return switch (value) {
            case null     -> "Customer name is null";
            case String s -> "Customer name: " + s;
        };
    }
}

sealed interface Payment permits CardPayment, UpiPayment, CashPayment { }

record CardPayment(double amount) implements Payment { }
record UpiPayment(double amount)  implements Payment { }
record CashPayment(double amount) implements Payment { }
```

**Output:**

```
Card payment: Rs.5000.0
UPI payment: Rs.2500.0
Cash payment: Rs.1000.0
PRAVEEN KUMAR
Customer name is null
```

### Why this example is well designed

1. `Payment` is **sealed**, so the compiler knows all three cases.
2. Each payment is a **record**: tiny, immutable, comparable by value.
3. `processPayment` is an **exhaustive pattern `switch`** with no `default`. Add a fourth payment type and this code stops compiling until handled.
4. `describe` uses `case String s` after `case null`; together they cover every possible `String` value, including `null`, so no `default` is needed.
5. `var` in the lambda and `strip()` show the smaller conveniences.

---

## 10. Common Mistakes and Gotchas

| Mistake | Why it fails | Fix |
|---|---|---|
| `e.getName()` on a record | Accessor is `name()` | Use `e.name()` |
| `this.id = id;` in a compact constructor | Fields are assigned automatically | Just validate; reassign the **parameter** if needed |
| Expecting a record to be deeply immutable | Only the references are final | `List.copyOf(...)` in the constructor |
| Making a JPA entity a record | JPA needs a mutable class with a no-arg constructor | Use a normal class for entities, records for DTOs |
| `class X extends SomeRecord` | Records are `final` | Use composition or an interface |
| Subclass of a sealed type without `final`/`sealed`/`non-sealed` | Compiler requires one | Pick one |
| Subclass not listed in `permits` | Not allowed to extend | Add it to the permits clause |
| Adding `default` to a switch over a sealed type | Hides missing-case compile errors | Remove it |
| `case Integer i` placed before `case Integer i when ...` | Dominance error | Put the specific case first |
| `default` assumed to catch `null` | It does not; NPE is thrown | Add `case null` or `case null, default` |
| `var x;` or `var x = null;` | Nothing to infer | Give a typed initializer |
| `var list = new ArrayList<>();` | Infers `ArrayList<Object>` | `new ArrayList<String>()` |
| `var` for a field, parameter, or return type | Not permitted | Use an explicit type |
| `(var a, b) -> ...` | Mixed lambda styles | Use `var` everywhere or nowhere |
| `text.stripIndent()` where the first line has no indent | Common indent is zero, nothing changes | Test with a consistently indented string |
| `List.of("a", null)` | `List.of` rejects `null` elements with NPE | Use `Arrays.asList(...)` when you need nulls |
| Two `public` top-level types in one file | Only one allowed, and it must match the file name | Remove `public` or split files |
| Using `isBlank()` on a possibly-null string | NPE | `s == null \|\| s.isBlank()` |
| `indent()` followed by `println` | `indent` already adds a trailing newline | Use `print` or accept the blank line |

---

## 11. Interview Questions and Answers

**Q1. What is a record?**
A concise data-carrier class. The compiler generates the canonical constructor, accessors, `equals()`, `hashCode()`, and `toString()`.

**Q2. Are records completely immutable?**
No. Fields are `final`, but immutability is **shallow**. A referenced mutable object (like an `ArrayList`) can still be modified. Use defensive copies.

**Q3. Can a record extend another class?**
No. It implicitly extends `java.lang.Record`, and Java has single inheritance.

**Q4. Can a record implement an interface?**
Yes.

**Q5. Can a record have instance fields beyond its components?**
No. It can have static fields only.

**Q6. What is a compact constructor?**
A constructor with no parameter list, used mainly for validation or normalization. The compiler assigns the fields at the end.

**Q7. What is a sealed class?**
A class or interface that restricts which types may directly extend or implement it.

**Q8. What modifiers can a permitted subclass have?**
`final`, `sealed`, or `non-sealed`.

**Q9. When can `permits` be omitted?**
When all permitted subclasses are declared in the same source file.

**Q10. Sealed class vs enum?**
An enum has a fixed set of constant instances of one type. A sealed hierarchy has a fixed set of types, each of which can carry different data.

**Q11. What is pattern matching?**
Testing a type and binding a typed variable in one step, in `instanceof` and in `switch`.

**Q12. What does "exhaustive switch" mean?**
Every possible input is covered. With sealed types, the compiler proves it, so no `default` is needed and adding a new subtype forces you to update the switch.

**Q13. What is a guard in a pattern switch?**
A `when` condition added to a case label: `case Integer i when i > 100 -> ...`.

**Q14. Can modern `switch` handle `null`?**
Yes, with `case null`. Without it, a `null` selector throws `NullPointerException`.

**Q15. Does `default` match `null`?**
No. Use `case null, default` to cover both.

**Q16. Why are private methods allowed in interfaces?**
So `default` and `static` methods can share helper logic without exposing the helper to implementing classes.

**Q17. Is `var` dynamic typing?**
No. The type is inferred at compile time and cannot change.

**Q18. Where can `var` be used?**
Local variables, enhanced and classic `for` variables, and try-with-resources. Not fields, parameters, or return types.

**Q19. Can you write `var x = null;`?**
No, `null` has no type to infer.

**Q20. `isEmpty()` vs `isBlank()`?**
`isEmpty()` means length zero. `isBlank()` also returns true for whitespace-only strings.

**Q21. `trim()` vs `strip()`?**
`trim()` removes characters up to `\u0020`. `strip()` is Unicode-aware via `Character.isWhitespace`. Prefer `strip()` in new code.

**Q22. Why use `var` in lambda parameters?**
It keeps type inference while allowing annotations on the parameters.

**Q23. Can lambda parameters mix `var` and explicit types?**
No. All must use `var`, or none.

**Q24. Records vs Lombok `@Data`?**
Records are a language feature, always immutable, and non-extensible. Lombok is a library, mutable by default, with getter-style names.

**Q25. Why shouldn't a switch over a sealed type have `default`?**
`default` silently absorbs future subtypes and removes the compile-time safety that sealed types provide.

---

## 12. Quick Comparison Table

| Feature | Purpose | Key detail |
|---|---|---|
| Record | Concise data carrier | `final`, accessors are `name()` not `getName()` |
| Compact constructor | Validate or normalize | No parameter list; no `this.x = x` |
| Sealed class/interface | Restrict inheritance | `permits`; subclasses must be `final`/`sealed`/`non-sealed` |
| `instanceof` pattern | Type test + binding | `obj instanceof String s` |
| Pattern `switch` | Type-based switch | Order matters; guards via `when` |
| `case null` | Explicit null handling | `default` does not catch null |
| Private interface method | Hidden shared helper | Not inherited, body required |
| `var` | Local type inference | Still statically typed |
| `isBlank()` | Empty or whitespace check | Throws NPE on null |
| `strip()` | Unicode-aware trimming | Prefer over `trim()` |
| `lines()` | Text to `Stream<String>` | Handles all line endings |
| `repeat()` | Repeat a string | Negative count throws |
| `formatted()` | Instance `String.format` | `"%s".formatted(x)` |
| `transform()` | Apply a function | Can change the type |
| `indent()` | Indent every line | Adds a trailing newline |
| `stripIndent()` | Remove common indent | Based on the smallest indent |
| `translateEscapes()` | Interpret `\n` text | For text from files or input |
| `var` in lambda | Inferred params with annotations | All-or-nothing, parentheses required |

---

## 13. Exercises with Solutions

### Exercise 1: Record

Create an `Employee` record with `id`, `name`, `department`, `salary`. Add `annualSalary()` and reject negative salary.

```java
public record Employee(int id, String name, String department, double salary) {

    public Employee {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }

    public double annualSalary() {
        return salary * 12;        // assumes salary is monthly
    }
}

var e1 = new Employee(1, "Asha", "IT", 50000);
var e2 = new Employee(2, "Ravi", "HR", 42000);
var e3 = new Employee(3, "Neha", "IT", 61000);
System.out.println(e1);
System.out.println(e1.annualSalary());
```

### Exercise 2: Sealed types

```java
public sealed interface Notification
        permits EmailNotification, SMSNotification, PushNotification { }

public record EmailNotification(String to, String subject) implements Notification { }
public record SMSNotification(String phone, String text)   implements Notification { }
public record PushNotification(String deviceId, String text) implements Notification { }
```

### Exercise 3: Pattern matching with `null`

> **Trap in the exercise wording:** `List.of("Java", 42, 3.14, null)` throws `NullPointerException`, because `List.of` forbids null elements. Use `Arrays.asList`.

```java
List<Object> items = Arrays.asList("Java", 42, 3.14, null);

for (var item : items) {
    String msg = switch (item) {
        case null       -> "null value";
        case String s   -> "String: " + s;
        case Integer i  -> "Integer: " + i;
        case Double d   -> "Double: " + d;
        default         -> "Other: " + item;
    };
    System.out.println(msg);
}
```

### Exercise 4: Payment fees

```java
static double fee(Payment p) {
    return switch (p) {
        case CardPayment c -> c.amount() * 0.02;
        case UpiPayment u  -> u.amount() * 0.01;
        case CashPayment c -> 0;
    };
}
```

No `default`: the sealed hierarchy makes the switch exhaustive.

### Exercise 5: `case null`

```java
static String getAccountType(String type) {
    return switch (type) {
        case null        -> "No account type provided";
        case "SAVINGS"   -> "Savings Account";
        case "CURRENT"   -> "Current Account";
        default          -> "Invalid account type: " + type;
    };
}
```

### Exercise 6: Private interface method

```java
public interface BankReport {

    default void dailyReport() {
        validate();
        System.out.println("Daily report");
    }

    default void monthlyReport() {
        validate();
        System.out.println("Monthly report");
    }

    private void validate() {
        System.out.println("Validating accounts and transactions");
    }
}
```

### Exercise 7: Rewriting with `var`

```java
// Before
Map<String, List<Integer>> marks = new HashMap<String, List<Integer>>();
String name = "Praveen";
int count = 0;
Customer c = new Customer(1, "Asha", "Pune");

// After (type obvious from the right-hand side)
var marks = new HashMap<String, List<Integer>>();
var name = "Praveen";
var count = 0;
var c = new Customer(1, "Asha", "Pune");
```

Do **not** use `var` for fields, method parameters, or return types.

### Exercise 8: String methods

```java
String input = "   java programming   ";

System.out.println(input.isBlank());                  // false
String result = input.strip().toUpperCase();          // JAVA PROGRAMMING
System.out.println("%s | %s".formatted(result, result.repeat(2)));
// JAVA PROGRAMMING | JAVA PROGRAMMINGJAVA PROGRAMMING
```

### Exercise 9: Lambda `var`

```java
var employees = List.of(
    new Employee(1, "Asha", "IT", 50000),
    new Employee(2, "Ravi", "HR", 42000));

employees.forEach((var employee) -> System.out.println(employee.name()));
```

Why these are invalid:

```java
(var a, b) -> a + b          // mixes var with a bare inferred parameter
(var a, int b) -> a + b      // mixes var with an explicit type
```

A lambda parameter list must be uniform: all `var`, all explicit, or all bare.

---

## 14. Key Takeaways

1. **Records** remove boilerplate for data-centric classes, but remember the immutability is shallow.
2. **Sealed types** give controlled inheritance; every subclass must be `final`, `sealed`, or `non-sealed`.
3. **Pattern matching** replaces check-then-cast with one step; in `switch`, order and guards matter.
4. **Sealed + records + pattern `switch`** is the combination to remember: exhaustive, no `default`, and new subtypes become compile errors rather than runtime surprises.
5. **`case null`** handles nulls explicitly; `default` does not catch `null`.
6. **Private interface methods** keep shared helpers hidden inside the interface.
7. **`var`** is compile-time inference, not dynamic typing. Use it when the right side makes the type obvious.
8. **Modern `String` methods** (`isBlank`, `strip`, `lines`, `repeat`, `formatted`, `transform`) replace a lot of hand-written utility code.
9. **`var` in lambdas** exists mainly to allow annotations; it must be used for all parameters or none.
10. Two practical file rules: **one public top-level type per file**, and **`List.of` rejects `null`**.

---

*End of notes.*
