# Java Streams API and Optional: Detailed Notes

The complete Streams guide: how pipelines work, creating streams, intermediate and terminal operations, Collectors, primitive streams, `Optional`, parallel streams, solved interview favorites, mistakes, puzzles and interview questions.

> Prerequisite: lambdas and functional interfaces (`Predicate`, `Function`, `Consumer`, `Supplier`), covered in the Lambda notes. Records are used for sample data.

**Imports used throughout**

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
```

---

## Table of Contents

1. [What Is a Stream and Why](#1-what-is-a-stream-and-why)
2. [The Pipeline: Source, Intermediate, Terminal](#2-the-pipeline-source-intermediate-terminal)
3. [Creating Streams](#3-creating-streams)
4. [Intermediate Operations](#4-intermediate-operations)
5. [Terminal Operations](#5-terminal-operations)
6. [Laziness, Ordering, Short-Circuiting](#6-laziness-ordering-short-circuiting)
7. [Collectors in Depth](#7-collectors-in-depth)
8. [Primitive Streams](#8-primitive-streams)
9. [Optional](#9-optional)
10. [Method References Recap](#10-method-references-recap)
11. [Parallel Streams](#11-parallel-streams)
12. [Rules and Best Practices](#12-rules-and-best-practices)
13. [Interview Favorites, Solved](#13-interview-favorites-solved)
14. [Common Mistakes](#14-common-mistakes)
15. [Output Prediction Puzzles](#15-output-prediction-puzzles)
16. [Interview Questions](#16-interview-questions)
17. [Cheat Sheet](#17-cheat-sheet)
18. [Practice Tasks with Solutions](#18-practice-tasks-with-solutions)

---

## 1. What Is a Stream and Why

> A **Stream** is a sequence of elements from a source that supports **aggregate operations** (filter, map, sort, reduce...) performed as a **pipeline**.

A stream is **not a data structure**. It does not store elements; it describes **what to do** with data that lives elsewhere (a list, array, file...).

### Real-life analogy: a factory conveyor belt

```
raw items  ->  [quality check]  ->  [paint]  ->  [sort]  ->  [pack into box]
(source)         filter             map          sorted       collect
```

- Items move along the belt through stations.
- **Nothing moves until you press the start button** (the terminal operation).
- Once items have gone through, the belt is empty. You cannot run the **same** items through it again.

### Imperative vs declarative

**Sample data used throughout this document**

```java
record Employee(int id, String name, String dept, double salary, int age) { }

List<Employee> emps = List.of(
    new Employee(1, "Asha",  "IT",      85000, 28),
    new Employee(2, "Ravi",  "IT",      72000, 35),
    new Employee(3, "Neha",  "HR",      55000, 30),
    new Employee(4, "Kiran", "HR",      60000, 41),
    new Employee(5, "Meera", "Finance", 90000, 38),
    new Employee(6, "Arjun", "IT",      95000, 45),
    new Employee(7, "Sara",  "Finance", 65000, 26));
```

**Task:** names of IT employees earning above 80000, sorted.

```java
// Imperative: HOW to do it, step by step
List<String> result = new ArrayList<>();
for (Employee e : emps) {
    if (e.dept().equals("IT") && e.salary() > 80000) {
        result.add(e.name());
    }
}
Collections.sort(result);

// Declarative: WHAT you want
List<String> result2 = emps.stream()
    .filter(e -> e.dept().equals("IT") && e.salary() > 80000)
    .map(Employee::name)
    .sorted()
    .toList();                       // [Arjun, Asha]
```

### Stream vs Collection

| Collection | Stream |
|---|---|
| **Stores** data | **Does not store**; computes on demand |
| Can be used many times | **Consumed once** |
| Eagerly built | **Lazily evaluated** |
| You iterate externally (loops) | **Internal iteration** (the stream loops for you) |
| Finite | Can be **infinite** (`Stream.iterate`) |
| Modified directly | **Never modifies the source**; produces new results |

### Benefits

Less boilerplate, readable data-processing pipelines, easy parallelism, and a functional style with no mutable loop variables.

---

## 2. The Pipeline: Source, Intermediate, Terminal

```
SOURCE            INTERMEDIATE OPERATIONS (lazy)           TERMINAL OPERATION (triggers)
list.stream() --> filter --> map --> sorted --> limit --> collect / forEach / count / ...
```

| Part | What it does | Returns |
|---|---|---|
| **Source** | Supplies elements | A `Stream` |
| **Intermediate operations** | Transform or filter; **lazy**; can be chained | Another `Stream` |
| **Terminal operation** | Produces the **result** or side effect; **starts the processing**; ends the stream | A value, collection, `Optional`, or `void` |

**The rule that matters most:** without a terminal operation, **nothing runs**.

```java
Stream.of(1, 2, 3).peek(n -> System.out.println("peek " + n));
// prints NOTHING: no terminal operation
```

---

## 3. Creating Streams

| From | Code |
|---|---|
| A collection | `list.stream()`, `set.stream()` |
| An array | `Arrays.stream(arr)` or `Stream.of(arr)` for object arrays |
| Values | `Stream.of(1, 2, 3)` |
| A range | `IntStream.range(1, 5)`, `IntStream.rangeClosed(1, 5)` |
| A string's characters | `"hello".chars()` (an `IntStream`) |
| A map | `map.entrySet().stream()`, `map.keySet().stream()`, `map.values().stream()` |
| Infinite, by rule | `Stream.iterate(seed, next)` |
| Infinite, by supplier | `Stream.generate(supplier)` |
| A file's lines | `Files.lines(path)` (close it with try-with-resources) |
| Empty | `Stream.empty()` |

```java
Stream<String> s1 = List.of("a", "b").stream();
IntStream s2 = Arrays.stream(new int[]{1, 2, 3});
Stream<Integer> s3 = Stream.of(1, 2, 3);

Stream.iterate(1, n -> n * 2).limit(6).toList();             // [1, 2, 4, 8, 16, 32]
Stream.iterate(1, n -> n <= 100, n -> n * 3).toList();       // [1, 3, 9, 27, 81]  (Java 9: stop condition)
Stream.generate(() -> "hi").limit(3).toList();               // [hi, hi, hi]
IntStream.range(1, 5).boxed().toList();                      // [1, 2, 3, 4]
IntStream.rangeClosed(1, 5).boxed().toList();                // [1, 2, 3, 4, 5]
```

> **Infinite streams need `limit` (or a short-circuiting terminal operation)**, or they never end.

> **Trap:** `Stream.of(new int[]{1, 2, 3})` gives a `Stream<int[]>` with **one** element. Use `Arrays.stream(intArray)` for primitives.

---

## 4. Intermediate Operations

All of these return a new stream and are **lazy**.

Test data for this section:

```java
List<Integer> nums = List.of(5, 2, 8, 2, 9, 1, 8);
```

### `filter(Predicate)`: keep matching elements

**Analogy:** a **sieve** or airport security: only items that pass go through.

```java
nums.stream().filter(n -> n % 2 == 0).toList();       // [2, 8, 2, 8]
```

### `map(Function)`: transform each element (one in, one out)

**Analogy:** a **currency converter**: each note goes in, and a converted one comes out.

```java
nums.stream().map(n -> n * n).toList();               // [25, 4, 64, 4, 81, 1, 64]
emps.stream().map(Employee::name).toList();           // [Asha, Ravi, Neha, ...]
```

### `flatMap(Function<T, Stream<R>>)`: transform and flatten (one in, many out)

**Analogy:** **unpacking cartons into one tray**. `map` would give you a tray of sealed cartons; `flatMap` opens each one and puts all items on the same tray.

```java
List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4), List.of(5));

nested.stream().map(List::size).toList();             // [2, 2, 1]      map: one per inner list
nested.stream().flatMap(List::stream).toList();       // [1, 2, 3, 4, 5]  flatMap: flattened

List<String> sentences = List.of("java is fun", "streams are powerful");
sentences.stream()
         .flatMap(s -> Arrays.stream(s.split(" ")))
         .toList();                                   // [java, is, fun, streams, are, powerful]
```

| | `map` | `flatMap` |
|---|---|---|
| Function returns | A single value | A **stream** of values |
| Result size | Same as input | Can grow or shrink |
| Use for | Converting | Flattening nested structures, splitting |

### `distinct()`: remove duplicates (uses `equals`)

```java
nums.stream().distinct().toList();                    // [5, 2, 8, 9, 1]
```

### `sorted()` / `sorted(Comparator)`

```java
nums.stream().sorted().toList();                              // [1, 2, 2, 5, 8, 8, 9]
nums.stream().sorted(Comparator.reverseOrder()).toList();     // [9, 8, 8, 5, 2, 2, 1]

emps.stream()
    .sorted(Comparator.comparingDouble(Employee::salary).reversed())
    .map(Employee::name)
    .limit(3)
    .toList();                                                // [Arjun, Meera, Asha]
```

Elements must be `Comparable` for plain `sorted()`, or you must supply a `Comparator`.

### `limit(n)` / `skip(n)`

**Analogy:** take the first 10 people from a queue, or skip the first 10.

```java
nums.stream().skip(2).limit(3).toList();              // [8, 2, 9]
```

### `takeWhile` / `dropWhile` (Java 9)

```java
nums.stream().takeWhile(n -> n > 1).toList();         // [5, 2, 8, 2, 9]   stops at the first failure (the 1)
nums.stream().dropWhile(n -> n > 1).toList();         // [1, 8]            skips until the first failure
```

Unlike `filter`, these **stop** at the first element that fails the condition (best on ordered data).

### `peek(Consumer)`: look without changing (for debugging)

```java
nums.stream().peek(n -> System.out.println("saw " + n)).map(n -> n * 2).toList();
```

Use `peek` for **debugging only**, never for real logic.

### Conversions to primitive streams

`mapToInt`, `mapToLong`, `mapToDouble`, and back with `boxed()` / `mapToObj` (section 8).

### Stateless vs stateful operations

| Stateless (look at one element) | Stateful (need other elements) |
|---|---|
| `filter`, `map`, `flatMap`, `peek`, `takeWhile`, `dropWhile` | `distinct`, `sorted`, `limit`, `skip` |

`sorted()` is a **barrier**: it must receive **all** elements before emitting the first.

---

## 5. Terminal Operations

A terminal operation **starts the pipeline** and **closes the stream**.

| Operation | Returns | Purpose |
|---|---|---|
| `forEach(Consumer)` | `void` | Do something with each element |
| `forEachOrdered(Consumer)` | `void` | Same, but respects encounter order (matters in parallel) |
| `collect(Collector)` | Varies | Gather into a collection, map, string... |
| `toList()` (Java 16) | `List` | Unmodifiable list |
| `toArray()` | `Object[]` / `T[]` | Gather into an array |
| `reduce(...)` | Value or `Optional` | Combine all elements into one |
| `count()` | `long` | Number of elements |
| `min(cmp)` / `max(cmp)` | `Optional` | Smallest or largest |
| `anyMatch` / `allMatch` / `noneMatch` | `boolean` | Condition checks (short-circuit) |
| `findFirst()` / `findAny()` | `Optional` | An element (short-circuit) |

### `reduce`: fold everything into one value

**Analogy:** adding up a **shopping bill**: start at zero and add each item.

```java
int sum = nums.stream().reduce(0, Integer::sum);          // 35   (identity, accumulator)
Optional<Integer> sum2 = nums.stream().reduce(Integer::sum);   // Optional[35]  (no identity)
Optional<Integer> max = nums.stream().reduce(Integer::max);    // Optional[9]

String joined = Stream.of("a", "b", "c").reduce("", (acc, s) -> acc + s);   // "abc"
```

| Form | Returns | Note |
|---|---|---|
| `reduce(identity, op)` | `T` | Safe on empty streams (returns the identity) |
| `reduce(op)` | `Optional<T>` | Empty stream gives an empty Optional |
| `reduce(identity, accumulator, combiner)` | `U` | For type-changing and parallel reductions |

### Match operations (short-circuit)

```java
nums.stream().anyMatch(n -> n > 8);      // true   stops at 9
nums.stream().allMatch(n -> n > 0);      // true
nums.stream().noneMatch(n -> n > 10);    // true
```

On an **empty** stream: `allMatch` and `noneMatch` return `true`; `anyMatch` returns `false`.

### find operations

```java
nums.stream().filter(n -> n > 5).findFirst();    // Optional[8]
nums.stream().filter(n -> n > 50).findFirst();   // Optional.empty
```

`findAny()` may return any element (useful in parallel streams for speed).

### `min` / `max` / `count`

```java
nums.stream().max(Integer::compare);                                  // Optional[9]
emps.stream().min(Comparator.comparingDouble(Employee::salary));      // Optional[Employee[... Neha ...]]
emps.stream().filter(e -> e.age() > 30).count();                      // 4
```

### `toList()` vs `collect(Collectors.toList())`

| | `stream.toList()` | `collect(Collectors.toList())` |
|---|---|---|
| Since | Java 16 | Java 8 |
| Mutability | **Unmodifiable** | Mutable (in practice; not guaranteed) |
| Allows `null`s | Yes | Yes |

---

## 6. Laziness, Ordering, Short-Circuiting

### Laziness: work only on demand

```java
List.of("a", "bb", "ccc", "dd").stream()
    .filter(s -> { System.out.println("filter " + s); return s.length() > 1; })
    .map(s -> { System.out.println("map " + s); return s.toUpperCase(); })
    .findFirst();
```

**Output:**

```
filter a
filter bb
map bb
```

`findFirst` needs only one element, so the pipeline **stops** as soon as `bb` passes. `ccc` and `dd` are never touched.

### Elements flow one at a time ("vertical" processing)

```java
Stream.of("a", "b")
      .filter(s -> { System.out.println("filter " + s); return true; })
      .map(s -> { System.out.println("map " + s); return s; })
      .forEach(s -> System.out.println("forEach " + s));
```

```
filter a
map a
forEach a
filter b
map b
forEach b
```

Each element travels through **all** stages before the next one starts. It is **not** "filter everything, then map everything." That is how the stream can fuse stages and stop early.

### Exception: the `sorted` barrier

```java
Stream.of("b", "a")
      .peek(s -> System.out.println("peek " + s))
      .sorted()
      .forEach(s -> System.out.println("out " + s));
```

```
peek b
peek a
out a
out b
```

`sorted` must see everything first.

### Short-circuiting operations

These can finish **without** processing the whole stream:

- Intermediate: `limit`, `takeWhile`
- Terminal: `findFirst`, `findAny`, `anyMatch`, `allMatch`, `noneMatch`

Infinite streams only work when something short-circuits:

```java
Stream.iterate(1, n -> n + 1).filter(n -> n % 7 == 0).limit(3).toList();   // [7, 14, 21]
```

### Encounter order

Streams from lists, arrays and ranges are **ordered**; `HashSet` streams are not. Operations like `findFirst`, `limit`, `skip` respect order, which costs a little in parallel streams (section 11).

---

## 7. Collectors in Depth

`collect(Collector)` is a **mutable reduction**: it accumulates elements into a container. The `Collectors` class provides the ready-made ones.

### Basic collectors

```java
emps.stream().map(Employee::name).collect(Collectors.toList());
emps.stream().map(Employee::dept).collect(Collectors.toSet());           // unique depts
emps.stream().map(Employee::name).collect(Collectors.toCollection(TreeSet::new));   // sorted set

emps.stream().map(Employee::name)
    .collect(Collectors.joining(", "));            // Asha, Ravi, Neha, Kiran, Meera, Arjun, Sara

emps.stream().map(Employee::name)
    .collect(Collectors.joining(", ", "[", "]"));  // [Asha, Ravi, ...]  (delimiter, prefix, suffix)
```

**Analogy:** `joining` strings beads onto a thread with a separator between them.

### `toMap`

```java
Map<Integer, String> idToName = emps.stream()
    .collect(Collectors.toMap(Employee::id, Employee::name));

Map<String, Double> nameToSalary = emps.stream()
    .collect(Collectors.toMap(Employee::name, Employee::salary));
```

**Duplicate keys throw `IllegalStateException`.** Provide a **merge function**:

```java
// total salary per department: two employees share a key, so merge by adding
Map<String, Double> totalByDept = emps.stream()
    .collect(Collectors.toMap(Employee::dept, Employee::salary, Double::sum));
// IT=252000.0, HR=115000.0, Finance=155000.0   (HashMap: order unspecified)

// keep insertion order with a map supplier
Map<String, Double> ordered = emps.stream()
    .collect(Collectors.toMap(Employee::dept, Employee::salary, Double::sum, LinkedHashMap::new));
```

`toMap` also throws `NullPointerException` if a **value** is `null`.

### `groupingBy`: sort into pigeonholes

**Analogy:** a **post office** sorting letters into pigeonholes by pin code. Each key gets a list of everything that belongs to it.

```java
Map<String, List<Employee>> byDept = emps.stream()
    .collect(Collectors.groupingBy(Employee::dept));
```

#### With a downstream collector (what to do with each group)

```java
// count per department
Map<String, Long> countByDept = emps.stream()
    .collect(Collectors.groupingBy(Employee::dept, Collectors.counting()));
// {Finance=2, HR=2, IT=3}

// average salary per department
Map<String, Double> avgByDept = emps.stream()
    .collect(Collectors.groupingBy(Employee::dept, Collectors.averagingDouble(Employee::salary)));
// IT=84000.0, HR=57500.0, Finance=77500.0

// total salary per department
emps.stream().collect(Collectors.groupingBy(Employee::dept, Collectors.summingDouble(Employee::salary)));

// only the names, per department
Map<String, List<String>> namesByDept = emps.stream()
    .collect(Collectors.groupingBy(Employee::dept,
             Collectors.mapping(Employee::name, Collectors.toList())));
// IT=[Asha, Ravi, Arjun], HR=[Neha, Kiran], Finance=[Meera, Sara]
```

#### Sorted keys with a map factory

```java
Map<String, Long> sorted = emps.stream()
    .collect(Collectors.groupingBy(Employee::dept, TreeMap::new, Collectors.counting()));
// {Finance=2, HR=2, IT=3}   guaranteed key order
```

#### Highest-paid employee per department

```java
Map<String, Employee> topPerDept = emps.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.collectingAndThen(
            Collectors.maxBy(Comparator.comparingDouble(Employee::salary)),
            Optional::get)));
// IT -> Arjun, HR -> Kiran, Finance -> Meera
```

`maxBy` returns an `Optional`; `collectingAndThen` post-processes the result (here, unwrapping it).

### `partitioningBy`: a pass/fail split

```java
Map<Boolean, List<Employee>> split = emps.stream()
    .collect(Collectors.partitioningBy(e -> e.salary() >= 70000));

split.get(true);     // Asha, Ravi, Meera, Arjun
split.get(false);    // Neha, Kiran, Sara
```

| | `groupingBy` | `partitioningBy` |
|---|---|---|
| Key | Any value | Always `Boolean` |
| Groups | As many as there are keys | **Exactly two** (both keys always present, even if empty) |
| Takes | A classifier function | A `Predicate` |

### Statistics

```java
DoubleSummaryStatistics stats = emps.stream()
    .collect(Collectors.summarizingDouble(Employee::salary));

stats.getMin();       // 55000.0
stats.getMax();       // 95000.0
stats.getAverage();   // 74571.428...
stats.getSum();       // 522000.0
stats.getCount();     // 7
```

### `teeing` (Java 12): two collectors, one pass

```java
String range = emps.stream().collect(Collectors.teeing(
    Collectors.minBy(Comparator.comparingDouble(Employee::salary)),
    Collectors.maxBy(Comparator.comparingDouble(Employee::salary)),
    (min, max) -> min.get().name() + " to " + max.get().name()));   // Neha to Arjun
```

### Other useful collectors

| Collector | Purpose |
|---|---|
| `counting()` | Number of elements |
| `summingInt/Long/Double` | Sum |
| `averagingInt/Long/Double` | Mean |
| `minBy` / `maxBy` | Smallest or largest, as `Optional` |
| `mapping(fn, downstream)` | Transform, then collect |
| `filtering(pred, downstream)` (Java 9) | Filter inside a group |
| `flatMapping(fn, downstream)` (Java 9) | Flatten inside a group |
| `collectingAndThen(c, fn)` | Post-process a result |
| `reducing(...)` | Reduce within a group |
| `toUnmodifiableList/Set/Map` | Immutable results |

### `collect` vs `reduce`

`reduce` is for **immutable** combining (sum, max). `collect` is for **mutable containers** (lists, maps, StringBuilders). Using `reduce` to build a list is wrong and slow.

---

## 8. Primitive Streams

`IntStream`, `LongStream`, `DoubleStream` avoid the cost of **boxing** `int` to `Integer` and add handy numeric methods.

| Need | Use |
|---|---|
| Sum, average, min, max of numbers | Primitive stream |
| Generic object processing | `Stream<T>` |

```java
IntStream.range(1, 5).sum();                  // 10   (1+2+3+4)   end EXCLUSIVE
IntStream.rangeClosed(1, 5).sum();            // 15   (1+2+3+4+5) end INCLUSIVE
IntStream.rangeClosed(1, 5).average();        // OptionalDouble[3.0]
IntStream.of(3, 1, 2).max().getAsInt();       // 3
```

### Converting between stream types

| From to | Method |
|---|---|
| `Stream<T>` to `IntStream` | `mapToInt(fn)` |
| `Stream<T>` to `DoubleStream` | `mapToDouble(fn)` |
| `IntStream` to `Stream<Integer>` | `boxed()` |
| `IntStream` to `Stream<R>` | `mapToObj(fn)` |
| `IntStream` to `DoubleStream` | `asDoubleStream()` |

```java
int total = nums.stream().mapToInt(Integer::intValue).sum();     // 35
double totalSalary = emps.stream().mapToDouble(Employee::salary).sum();   // 522000.0

List<Integer> list = IntStream.rangeClosed(1, 3).boxed().toList();         // [1, 2, 3]
```

> `Stream<Integer>` has **no** `sum()`. Use `mapToInt(...).sum()` or `reduce`.

### Statistics in one pass

```java
IntSummaryStatistics s = nums.stream().mapToInt(i -> i).summaryStatistics();
// IntSummaryStatistics{count=7, sum=35, min=1, average=5.000000, max=9}
```

### Character processing

```java
"education".chars()
           .filter(c -> "aeiou".indexOf(c) >= 0)
           .count();                                   // 5

"hello".chars().mapToObj(c -> (char) c).toList();      // [h, e, l, l, o]
```

`String.chars()` gives an `IntStream` of code values, so cast to `char` when you need characters.

---

## 9. Optional

> `Optional<T>` is a **container that may or may not hold a value**. It makes "might be absent" **explicit in the type** and avoids `NullPointerException`.

**Analogy:** a **parcel delivery**. You get either the package or a "not available" slip. You are forced to handle both cases before you open anything. A `null` is a parcel that silently turns out to be an empty air pocket when you grab it.

### Creating

```java
Optional<String> a = Optional.of("Asha");            // value must NOT be null (else NPE)
Optional<String> b = Optional.ofNullable(maybeNull); // empty if null
Optional<String> c = Optional.empty();
```

### Reading the value

| Method | Behavior |
|---|---|
| `get()` | Value, or throws `NoSuchElementException` if empty (**avoid**) |
| `orElse(x)` | Value or `x` |
| `orElseGet(supplier)` | Value or the supplier's result (**lazy**) |
| `orElseThrow()` / `orElseThrow(supplier)` | Value or throws |
| `isPresent()` / `isEmpty()` | Checks (use sparingly) |
| `ifPresent(consumer)` | Run an action if present |
| `ifPresentOrElse(consumer, runnable)` | Run one action or the other |
| `map(fn)` | Transform the value if present |
| `flatMap(fn)` | Like `map`, when the function returns an `Optional` |
| `filter(pred)` | Keep the value only if it matches |
| `or(supplier)` (Java 9) | Fallback `Optional` |
| `stream()` (Java 9) | A stream of 0 or 1 element |

```java
Optional<String> name = Optional.of("asha");

name.map(String::toUpperCase).orElse("UNKNOWN");           // ASHA
name.filter(s -> s.length() > 10).orElse("too short");     // too short
name.ifPresent(n -> System.out.println("Hello " + n));
name.ifPresentOrElse(
    n -> System.out.println("Hello " + n),
    () -> System.out.println("No name"));
```

### `orElse` vs `orElseGet`

```java
String v1 = opt.orElse(expensiveDefault());          // expensiveDefault() ALWAYS runs
String v2 = opt.orElseGet(() -> expensiveDefault()); // runs ONLY if opt is empty
```

Use **`orElseGet`** when the fallback costs something (database call, object creation, logging).

### Chaining replaces nested null checks

```java
// Before: pyramid of null checks
String city = "Unknown";
if (user != null) {
    Address address = user.getAddress();
    if (address != null && address.getCity() != null) {
        city = address.getCity();
    }
}

// After
String city2 = Optional.ofNullable(user)
                       .map(User::getAddress)
                       .map(Address::getCity)
                       .orElse("Unknown");
```

### Optional with streams

```java
Optional<Employee> top = emps.stream().max(Comparator.comparingDouble(Employee::salary));

String topName = top.map(Employee::name).orElse("nobody");     // Arjun

emps.stream()
    .filter(e -> e.dept().equals("Legal"))
    .findFirst()
    .ifPresentOrElse(
        e -> System.out.println(e.name()),
        () -> System.out.println("No one in Legal"));
```

### Rules for good use

| Do | Don't |
|---|---|
| Use as a **return type** when "no result" is normal | Use as a **field** or **method parameter** |
| Chain `map`/`filter`/`orElse...` | `if (opt.isPresent()) opt.get()` (that is just a null check in disguise) |
| Return an **empty collection**, not `Optional<List<T>>` | Call `get()` without being sure |
| `Optional.ofNullable` for possibly-null values | `Optional.of(maybeNull)` (NPE) |
| `orElseGet` for expensive defaults | Use `Optional` for performance-critical bulk data |

### Primitive versions

`OptionalInt`, `OptionalLong`, `OptionalDouble` (returned by `IntStream.max()`, `average()`, and so on). Read with `getAsInt()`, `getAsDouble()`, `orElse(...)`.

```java
double avg = IntStream.of(1, 2, 3).average().orElse(0.0);    // 2.0
```

---

## 10. Method References Recap

A method reference is shorthand for a lambda that only calls one method.

| Kind | Syntax | Equivalent lambda |
|---|---|---|
| Static method | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| Instance method of a specific object | `System.out::println` | `x -> System.out.println(x)` |
| Instance method of an arbitrary object of a type | `String::toUpperCase` | `s -> s.toUpperCase()` |
| Constructor | `ArrayList::new` | `() -> new ArrayList<>()` |

```java
List<String> names = emps.stream().map(Employee::name).toList();
Stream.of("1", "2", "3").map(Integer::parseInt).toList();       // [1, 2, 3]
emps.stream().map(Employee::name).forEach(System.out::println);
Stream.of("a", "b").collect(Collectors.toCollection(ArrayList::new));
```

Prefer a method reference when it is clearer; use a lambda when you need extra logic.

---

## 11. Parallel Streams

```java
long count = nums.parallelStream().filter(n -> n > 2).count();
// or: nums.stream().parallel()...   and  .sequential() to switch back
```

The stream splits the data, processes chunks on multiple threads (the common **ForkJoinPool**), and combines the results.

**Analogy:** instead of one cashier, several cashiers each take part of the queue. It helps if the queue is long and every customer takes real work. It hurts if the queue is short, or the cashiers keep bumping into one shared register.

### When it can help

- **Large** data sets (many thousands of elements or more).
- **CPU-heavy**, independent per-element work.
- Easily splittable sources (`ArrayList`, arrays, `IntStream.range`).

### When it hurts or breaks

| Problem | Why |
|---|---|
| Small data | Thread overhead exceeds the gain |
| **Shared mutable state** | Race conditions |
| I/O or blocking calls | Ties up the shared common pool |
| `LinkedList`, `Stream.iterate`, `limit` on ordered streams | Poor splitting or ordering cost |
| Order-dependent logic | Results may arrive unordered (use `forEachOrdered`) |

**The classic bug:**

```java
List<Integer> result = new ArrayList<>();
IntStream.range(0, 1000).parallel().forEach(result::add);   // ArrayList is not thread-safe
// missing elements, ArrayIndexOutOfBoundsException, or corrupt data
```

**Correct:**

```java
List<Integer> result = IntStream.range(0, 1000).parallel().boxed().toList();   // collect, don't mutate
```

**Rule:** do not use `parallel()` by default. **Measure first.** Make sure your lambdas are stateless and thread-safe.

---

## 12. Rules and Best Practices

### Behavioral parameters must be

- **Non-interfering:** do not modify the source while streaming (`ConcurrentModificationException`).
- **Stateless:** results must not depend on mutable state that can change during the pipeline.
- **Free of side effects** where possible (no adding to external lists inside `map` or `filter`).

```java
// Avoid: mutating external state from inside a stream
List<String> out = new ArrayList<>();
emps.stream().forEach(e -> out.add(e.name()));

// Prefer: let the stream produce the result
List<String> out2 = emps.stream().map(Employee::name).toList();
```

### A stream can be used only once

```java
Stream<Integer> s = nums.stream();
s.forEach(System.out::println);
s.forEach(System.out::println);   // IllegalStateException: stream has already been operated upon or closed
```

Need it twice? Create a new stream, or use a `Supplier<Stream<T>>`.

### Checked exceptions inside lambdas

The standard functional interfaces **do not allow checked exceptions**.

```java
paths.stream().map(p -> Files.readString(p));      // compile error: IOException

paths.stream().map(p -> {
    try { return Files.readString(p); }
    catch (IOException e) { throw new UncheckedIOException(e); }
});
```

### Streams vs plain loops

| Streams shine | Loops shine |
|---|---|
| Filtering, mapping, grouping, aggregating | Complex control flow (`break`, `continue`, multiple returns) |
| Readable pipelines | Index-based logic |
| Declarative style | Heavy checked exceptions |
| Easy parallelism | Simple tiny loops and easier debugging |

Streams are **not always faster**. Choose them for **clarity**, not speed.

### Readability

- One operation per line.
- Keep lambdas short; extract complicated ones into named methods.
- Do not nest streams deeply.

---

## 13. Interview Favorites, Solved

All use `emps` and `nums` from earlier.

**1. Second-highest salary**

```java
double second = emps.stream()
    .map(Employee::salary)
    .distinct()
    .sorted(Comparator.reverseOrder())
    .skip(1)
    .findFirst()
    .orElseThrow();                       // 90000.0
```

`distinct()` handles ties so two people at the top do not hide the second value.

**2. Top 3 highest-paid names**

```java
emps.stream()
    .sorted(Comparator.comparingDouble(Employee::salary).reversed())
    .limit(3).map(Employee::name).toList();      // [Arjun, Meera, Asha]
```

**3. Department with the highest average salary**

```java
String dept = emps.stream()
    .collect(Collectors.groupingBy(Employee::dept, Collectors.averagingDouble(Employee::salary)))
    .entrySet().stream()
    .max(Map.Entry.comparingByValue())
    .map(Map.Entry::getKey)
    .orElseThrow();                              // IT
```

**4. Word frequency**

```java
String text = "java is fun and java is powerful";
Map<String, Long> freq = Arrays.stream(text.split(" "))
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
// {java=2, is=2, fun=1, and=1, powerful=1}   (HashMap: order unspecified)
```

**5. First non-repeating character**

```java
Optional<Character> first = "swiss".chars().mapToObj(c -> (char) c)
    .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
    .entrySet().stream()
    .filter(e -> e.getValue() == 1)
    .map(Map.Entry::getKey)
    .findFirst();                                // Optional[w]
```

`LinkedHashMap` preserves the first-seen order.

**6. Find duplicates**

```java
List<Integer> dups = nums.stream()
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
    .entrySet().stream()
    .filter(e -> e.getValue() > 1)
    .map(Map.Entry::getKey)
    .toList();                                   // [2, 8]
```

A shortcut with a stateful `Set` (`filter(n -> !seen.add(n))`) works for sequential streams but breaks the "stateless" rule, so prefer the version above.

**7. Sum of squares of even numbers 1..10**

```java
IntStream.rangeClosed(1, 10).filter(n -> n % 2 == 0).map(n -> n * n).sum();   // 220
```

**8. Palindrome check**

```java
static boolean isPalindrome(String s) {
    return IntStream.range(0, s.length() / 2)
                    .allMatch(i -> s.charAt(i) == s.charAt(s.length() - 1 - i));
}
```

**9. First 8 Fibonacci numbers**

```java
Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
      .limit(8).map(f -> f[0]).toList();         // [0, 1, 1, 2, 3, 5, 8, 13]
```

**10. Sort a map by value, descending, keeping order**

```java
Map<String, Integer> marks = Map.of("Asha", 90, "Ravi", 75, "Kiran", 85);

Map<String, Integer> sorted = marks.entrySet().stream()
    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                              (a, b) -> a, LinkedHashMap::new));
// {Asha=90, Kiran=85, Ravi=75}
```

**11. Flatten nested lists and keep unique values**

```java
List<List<Integer>> nested = List.of(List.of(1, 2, 2), List.of(2, 3), List.of(4));
nested.stream().flatMap(List::stream).distinct().sorted().toList();    // [1, 2, 3, 4]
```

**12. Count employees by age bracket**

```java
Map<String, Long> brackets = emps.stream().collect(Collectors.groupingBy(
    e -> e.age() < 30 ? "<30" : e.age() < 40 ? "30-39" : "40+",
    TreeMap::new, Collectors.counting()));
// {30-39=3, 40+=2, <30=2}
```

---

## 14. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Building a pipeline with no terminal operation | Nothing runs | Add `collect`, `forEach`, `count`... |
| Reusing a stream | `IllegalStateException` | Create a new stream each time |
| `Stream<Integer>.sum()` | No such method | `mapToInt(Integer::intValue).sum()` |
| `toMap` with duplicate keys | `IllegalStateException` | Add a merge function, or use `groupingBy` |
| `toMap` with a `null` value | `NullPointerException` | Filter nulls or use another collector |
| Modifying the source list inside the pipeline | `ConcurrentModificationException` | Collect into a new list |
| Adding to an external list inside `forEach`/`map` | Side effects; breaks in parallel | Use `collect` or `toList()` |
| Relying on `peek` for logic | May not run (for example with `count()` on a sized source) | Use `peek` for debugging only |
| `sorted()` before `limit` on an infinite stream | Never ends or runs out of memory | `limit` first, or choose a different design |
| `Stream.of(intArray)` | One-element stream of the array | `Arrays.stream(intArray)` |
| `Optional.get()` without checking | `NoSuchElementException` | `orElse`, `orElseGet`, `orElseThrow` |
| `Optional.of(maybeNull)` | `NullPointerException` | `Optional.ofNullable` |
| `Optional` as a field or parameter | Clutter, serialization issues | Use plain types; `Optional` for return values |
| `orElse(expensive())` | Always evaluates the argument | `orElseGet(() -> expensive())` |
| `isPresent()` then `get()` | Verbose null-check in disguise | `map`/`ifPresent`/`orElse` |
| Default `parallel()` everywhere | Slower or incorrect results | Measure; keep lambdas stateless |
| Checked exception inside a lambda | Compile error | Wrap in try/catch and rethrow unchecked |
| Overly long lambdas | Unreadable pipelines | Extract named methods |
| Expecting `HashMap` result order from `groupingBy` | Unspecified | Use a `TreeMap` or `LinkedHashMap` factory |
| `rangeClosed` vs `range` mix-up | Off-by-one | `range` excludes the end; `rangeClosed` includes it |

---

## 15. Output Prediction Puzzles

**Puzzle 1: no terminal operation**

```java
Stream.of(1, 2, 3).peek(n -> System.out.println("peek " + n));
```

**Puzzle 2: element-by-element order**

```java
Stream.of("a", "b", "c")
      .filter(s -> { System.out.println("filter " + s); return true; })
      .map(s -> { System.out.println("map " + s); return s; })
      .forEach(s -> System.out.println("forEach " + s));
```

**Puzzle 3: the `sorted` barrier**

```java
Stream.of("b", "a")
      .peek(s -> System.out.println("peek " + s))
      .sorted()
      .forEach(s -> System.out.println("out " + s));
```

**Puzzle 4: reuse**

```java
Stream<Integer> s = Stream.of(1, 2, 3);
s.forEach(System.out::println);
System.out.println(s.count());
```

**Puzzle 5: duplicate keys**

```java
Stream.of("a", "bb", "cc")
      .collect(Collectors.toMap(String::length, x -> x));
```

**Puzzle 6: infinite plus `sorted`**

```java
Stream.iterate(1, n -> n + 1).sorted().limit(3).toList();
```

**Puzzle 7: `count()` and `peek`**

```java
long c = Stream.of(1, 2, 3).peek(System.out::println).count();
System.out.println(c);
```

**Puzzle 8: map vs flatMap sizes**

```java
List<String> words = List.of("hi there", "java");
System.out.println(words.stream().map(w -> w.split(" ")).count());
System.out.println(words.stream().flatMap(w -> Arrays.stream(w.split(" "))).count());
```

**Puzzle 9: `orElse` side effect**

```java
static String fallback() { System.out.println("fallback called"); return "x"; }

Optional.of("value").orElse(fallback());
Optional.of("value").orElseGet(() -> fallback());
```

**Answers**

1. **Prints nothing.** Intermediate operations are lazy; no terminal operation means the pipeline never runs.
2. `filter a`, `map a`, `forEach a`, `filter b`, `map b`, `forEach b`, `filter c`, `map c`, `forEach c`. Each element goes through every stage before the next one starts.
3. `peek b`, `peek a`, `out a`, `out b`. `sorted()` buffers everything first; only then do elements flow downstream.
4. Prints `1 2 3`, then the `count()` call throws **`IllegalStateException`**: the stream was already consumed.
5. **`IllegalStateException: Duplicate key 2 (attempted merging values bb and cc)`**. Both strings have length 2. Add a merge function such as `(x, y) -> x + "," + y`.
6. **Never finishes** (eventually `OutOfMemoryError`). `sorted()` must read all elements of an infinite stream before `limit` can act. Fix: `limit` first, or restructure.
7. **Prints only `3`** on Java 9 and later. `count()` can compute the size directly from a sized source without running the pipeline, so `peek` never fires. Add a `filter` and it runs. This is why `peek` must not carry logic.
8. **`2`** and **`3`**. `map` produces one array per sentence (2 items); `flatMap` flattens into words (`hi`, `there`, `java`).
9. **`fallback called` prints once**, from the `orElse` line only. `orElse` always evaluates its argument; `orElseGet` runs the supplier only when the Optional is empty.

---

## 16. Interview Questions

**1. What is a Stream?** A sequence of elements supporting aggregate operations in a lazily evaluated pipeline. It does not store data.

**2. Stream vs Collection?** A collection stores data; a stream processes it. Streams are lazy, single-use, may be infinite, and never modify their source.

**3. Intermediate vs terminal operations?** Intermediate operations are lazy and return a stream (`filter`, `map`); a terminal operation triggers execution and produces a result (`collect`, `count`).

**4. What is lazy evaluation?** Intermediate operations do nothing until a terminal operation runs; processing can also stop early.

**5. `map` vs `flatMap`?** `map` is one-to-one; `flatMap` maps each element to a stream and flattens the results.

**6. `filter` vs `takeWhile`?** `filter` tests every element; `takeWhile` stops at the first failure.

**7. What does `reduce` do?** Combines all elements into one value using an accumulator, with an optional identity.

**8. `findFirst` vs `findAny`?** `findFirst` respects encounter order; `findAny` may return any element (faster in parallel).

**9. What is `peek` for?** Debugging by observing elements mid-pipeline. Not for production logic.

**10. `forEach` vs `forEachOrdered`?** `forEachOrdered` guarantees encounter order even in parallel streams.

**11. `collect` vs `reduce`?** `collect` is a mutable reduction into containers; `reduce` is for immutable value combination.

**12. `groupingBy` vs `partitioningBy`?** `groupingBy` groups by any key; `partitioningBy` splits into exactly two groups by a boolean predicate.

**13. What happens when `toMap` hits a duplicate key?** `IllegalStateException`, unless you supply a merge function.

**14. Can a stream be reused?** No. A second terminal operation throws `IllegalStateException`.

**15. What are stateful operations?** Those that need information about other elements: `sorted`, `distinct`, `limit`, `skip`.

**16. Which operations short-circuit?** `limit`, `takeWhile`, `findFirst`, `findAny`, `anyMatch`, `allMatch`, `noneMatch`.

**17. Why have `IntStream` and friends?** To avoid boxing and provide numeric operations (`sum`, `average`, `summaryStatistics`).

**18. `range` vs `rangeClosed`?** `range` excludes the end value; `rangeClosed` includes it.

**19. How do you convert `Stream<Integer>` to a sum?** `mapToInt(Integer::intValue).sum()` or `reduce(0, Integer::sum)`.

**20. What is `Optional`?** A container that may hold a value or be empty, used to represent possible absence without `null`.

**21. `orElse` vs `orElseGet`?** `orElse` always evaluates its argument; `orElseGet` evaluates its supplier only when the Optional is empty.

**22. `Optional.of` vs `ofNullable`?** `of` throws NPE on `null`; `ofNullable` returns an empty Optional.

**23. Why not use Optional as a field or parameter?** It adds overhead and clutter, is not serializable, and is intended for return types.

**24. What are the kinds of method references?** Static, bound instance (`obj::method`), unbound instance (`Type::method`), constructor (`Type::new`).

**25. When should you use parallel streams?** For large, CPU-bound, independent work on splittable sources; measure first.

**26. Why is shared mutable state dangerous with parallel streams?** Multiple threads mutate it concurrently, causing race conditions.

**27. Does a stream modify its source?** No. It produces new results.

**28. How do you handle checked exceptions in a stream lambda?** Catch inside the lambda and rethrow an unchecked exception, or use a helper wrapper.

**29. `Stream.toList()` vs `Collectors.toList()`?** `toList()` (Java 16) returns an unmodifiable list; `Collectors.toList()` returns a mutable list in practice.

**30. How do you find the second-highest salary?** `map(salary).distinct().sorted(reverseOrder()).skip(1).findFirst()`.

**31. How do you sort by multiple fields?** `Comparator.comparing(...).thenComparing(...)`.

**32. What does `Collectors.teeing` do?** Runs two collectors on the same stream and merges their results (Java 12).

**33. Why is the output of `count()` sometimes missing `peek` output?** For sized sources, `count()` can return the size without traversing the pipeline (Java 9+).

**34. How do you stream a `Map`?** Through `entrySet().stream()`, `keySet().stream()` or `values().stream()`.

**35. When do you prefer a loop over a stream?** Complex control flow, index-based logic, heavy checked exceptions, or trivially simple iterations.

---

## 17. Cheat Sheet

| Goal | Code |
|---|---|
| Keep matching | `filter(pred)` |
| Convert each | `map(fn)` |
| Flatten | `flatMap(x -> x.stream())` |
| Unique | `distinct()` |
| Sort | `sorted()` / `sorted(comparator)` |
| First N / skip N | `limit(n)` / `skip(n)` |
| To list | `toList()` or `collect(Collectors.toList())` |
| To set | `collect(Collectors.toSet())` |
| To map | `collect(Collectors.toMap(k, v, merge))` |
| Join text | `collect(Collectors.joining(", "))` |
| Group | `collect(Collectors.groupingBy(fn, downstream))` |
| Split in two | `collect(Collectors.partitioningBy(pred))` |
| Count | `count()` or `Collectors.counting()` |
| Sum / average | `mapToInt(...).sum()` / `.average()` |
| Min / max | `min(cmp)` / `max(cmp)` |
| Combine to one | `reduce(identity, op)` |
| Any / all / none | `anyMatch` / `allMatch` / `noneMatch` |
| First element | `findFirst()` |
| Safe default | `optional.orElse(x)` / `orElseGet(...)` / `orElseThrow()` |
| Numbers 1 to n | `IntStream.rangeClosed(1, n)` |

**The mental model**

```
source  ->  lazy intermediate steps  ->  ONE terminal step (runs everything, once)
```

**Always remember:** no terminal op means nothing runs; streams are single-use; keep lambdas stateless; prefer `collect`/`toList()` over mutating external lists.

---

## 18. Practice Tasks with Solutions

Use the `emps` dataset from section 1.

**1. Names of employees earning above 60000, alphabetically**

```java
emps.stream().filter(e -> e.salary() > 60000)
    .map(Employee::name).sorted().toList();     // [Arjun, Asha, Meera, Ravi, Sara]
```

**2. Total salary of the IT department**

```java
emps.stream().filter(e -> e.dept().equals("IT"))
    .mapToDouble(Employee::salary).sum();       // 252000.0
```

**3. Oldest employee in each department**

```java
Map<String, Employee> oldest = emps.stream().collect(Collectors.groupingBy(
    Employee::dept,
    Collectors.collectingAndThen(
        Collectors.maxBy(Comparator.comparingInt(Employee::age)), Optional::get)));
// IT -> Arjun (45), HR -> Kiran (41), Finance -> Meera (38)
```

**4. Map from id to name**

```java
Map<Integer, String> idToName = emps.stream()
    .collect(Collectors.toMap(Employee::id, Employee::name));
```

**5. Do all employees earn more than 50000?**

```java
emps.stream().allMatch(e -> e.salary() > 50000);    // true
```

**6. Give IT employees a 10% raise, producing a new list**

```java
List<Employee> raised = emps.stream()
    .map(e -> e.dept().equals("IT")
        ? new Employee(e.id(), e.name(), e.dept(), e.salary() * 1.10, e.age())
        : e)
    .toList();
```

Records are immutable, so you **create new objects** instead of changing the old ones.

**7. Employees' names as one comma-separated string, sorted by salary descending**

```java
emps.stream()
    .sorted(Comparator.comparingDouble(Employee::salary).reversed())
    .map(Employee::name)
    .collect(Collectors.joining(", "));    // Arjun, Meera, Asha, Ravi, Sara, Kiran, Neha
```

**8. Unique departments, sorted**

```java
emps.stream().map(Employee::dept).distinct().sorted().toList();   // [Finance, HR, IT]
```

**9. Average age of employees earning above 70000**

```java
emps.stream().filter(e -> e.salary() > 70000)
    .mapToInt(Employee::age).average().orElse(0);     // (28+35+38+45)/4 = 36.5
```

**10. Count vowels in every name combined**

```java
emps.stream().map(Employee::name)
    .flatMapToInt(String::chars)
    .map(Character::toLowerCase)
    .filter(c -> "aeiou".indexOf(c) >= 0)
    .count();
```

### Challenge questions

1. You must print the **names of the 3 highest-paid employees in each department**. Which collector structure would you use?
2. Why can `emps.stream().map(...).forEach(...)` followed by `emps.stream()...` be fine, while reusing the same `Stream` variable is not?
3. When would you choose `Optional.ofNullable(x).map(...)` over an `if (x != null)` check, and when would you not?

**Answers**

1. `groupingBy(Employee::dept, collectingAndThen(toList(), list -> list.stream().sorted(Comparator.comparingDouble(Employee::salary).reversed()).limit(3).map(Employee::name).toList()))`. Group first, then post-process each group's list.
2. Each call to `emps.stream()` creates a **new** stream over the unchanged list. The restriction applies only to reusing one `Stream` object after a terminal operation.
3. Use `Optional` chains when you have several dependent steps that might each be absent (`user -> address -> city`) and want one readable expression. Avoid it for a single simple check, in hot loops, or for fields and parameters, where a plain `if` is clearer.

---

*End of notes.*
