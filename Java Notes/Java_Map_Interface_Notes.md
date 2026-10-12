# Java Map Interface: Complete Notes

A full guide to `java.util.Map`: what it is, why it exists, how `HashMap` works internally, every important method with real-life examples, the different implementations, common patterns, mistakes and interview questions.

---

## Table of Contents

1. [What Is a Map and Why Do We Need One](#1-what-is-a-map-and-why-do-we-need-one)
2. [Map Hierarchy and Key Facts](#2-map-hierarchy-and-key-facts)
3. [How a HashMap Works Internally](#3-how-a-hashmap-works-internally)
4. [Creating a Map and Basic Operations](#4-creating-a-map-and-basic-operations)
5. [Views and Iteration: keySet, values, entrySet](#5-views-and-iteration-keyset-values-entryset)
6. [Replacing and Conditional Methods](#6-replacing-and-conditional-methods)
7. [compute, computeIfAbsent, computeIfPresent, merge](#7-compute-computeifabsent-computeifpresent-merge)
8. [forEach, equals, hashCode, clone](#8-foreach-equals-hashcode-clone)
9. [Immutable Maps: Map.of, ofEntries, copyOf](#9-immutable-maps-mapof-ofentries-copyof)
10. [Null Handling in Each Implementation](#10-null-handling-in-each-implementation)
11. [The Implementations](#11-the-implementations)
12. [Sorting a Map](#12-sorting-a-map)
13. [Common Patterns](#13-common-patterns)
14. [Map vs Collection, List and Set](#14-map-vs-collection-list-and-set)
15. [Method Summary Table](#15-method-summary-table)
16. [Common Mistakes and Gotchas](#16-common-mistakes-and-gotchas)
17. [Interview Questions and Answers](#17-interview-questions-and-answers)
18. [Practice Exercises with Solutions](#18-practice-exercises-with-solutions)
19. [Key Takeaways](#19-key-takeaways)

---

## 1. What Is a Map and Why Do We Need One

### What

A **Map** stores data as **key-value pairs**. The key identifies; the value is the data you want.

```
101 -> "Praveen"
102 -> "Ravi"
103 -> "Kiran"
104 -> "Ravi"
```

Here `101`, `102`, `103`, `104` are **keys**; `"Praveen"`, `"Ravi"`, `"Kiran"`, `"Ravi"` are **values**. One key-value pair together is called an **entry**. A map is, in effect, a **collection of entries**.

### Why

A `List` finds things by **position** (`list.get(3)`). In real life we rarely know the position; we know an **identity**: a roll number, a PAN, an email, a product code. A Map lets you jump straight to the data by that identity, without scanning.

### Real-life analogies

| Real life | Key | Value |
|---|---|---|
| Phone contacts | Name | Phone number |
| Dictionary | Word | Meaning |
| Cloakroom | Token number | Your bag |
| Student register | Roll number | Student record |
| Pin code directory | PIN code | Area name |
| Bus route board | Route number | Destination |

### A way to think about it

A `List` or `Set` is **one-dimensional**: just a row of values. A `Map` is **two-dimensional** in the sense that every item has two parts: *how to find it* (key) and *what it is* (value).

### Why a Map is fast

Finding "student 101" in a `List` means scanning from the start: **O(n)**. A `HashMap` goes almost directly to the answer: **O(1) on average**. With a million records, that is the difference between a million checks and about one.

### Rules about keys and values

| Rule | Detail |
|---|---|
| Keys must be **unique** | Putting the same key again replaces the value |
| One key maps to **at most one** value | |
| Many keys can share the **same value** | `102 -> "Ravi"` and `104 -> "Ravi"` is fine |
| Keys and values must be **objects** | Primitives are not allowed |
| Values can be anything | Including `List`, `Set`, another `Map`, any class |

### Primitives are not allowed (autoboxing helps)

```java
Map<int, String> m;          // compile error
Map<Integer, String> m;      // correct: use the wrapper class

m.put(101, "Java");          // 101 is automatically boxed to Integer
```

---

## 2. Map Hierarchy and Key Facts

```
                    Map<K,V>
                       |
        +--------------+--------------+----------------+
        |              |              |                |
     HashMap       Hashtable      SortedMap        (others)
        |                             |
  LinkedHashMap                  NavigableMap
                                      |
                                   TreeMap
```

Other implementations: `ConcurrentHashMap`, `EnumMap`, `WeakHashMap`, `IdentityHashMap`.

### Key facts

- `Map` is an **interface**, not a class. You always create an implementation: `new HashMap<>()`.
- `Map` is in `java.util`: `import java.util.Map;`
- **`Map` does NOT extend `Collection`.** It has its own hierarchy.
- `Map` is **not `Iterable`**, so you cannot write `for (x : map)` directly (see section 5).
- Generic form: `Map<K, V>`, where `K` is the key type and `V` is the value type.

```java
Map<Integer, String> employees = new HashMap<>();
Map<String, Double> products = new HashMap<>();
```

### Program to the interface

Declare the variable as `Map` and choose the implementation on the right:

```java
Map<Integer, String> map = new HashMap<>();     // good: easy to switch to TreeMap later
HashMap<Integer, String> map2 = new HashMap<>(); // tied to HashMap
```

### Basic example

```java
import java.util.*;

public class MapDemo {
    public static void main(String[] args) {
        Map<Integer, String> students = new HashMap<>();

        students.put(101, "Praveen");
        students.put(102, "Ravi");
        students.put(103, "Kiran");

        System.out.println(students);   // {101=Praveen, 102=Ravi, 103=Kiran}
    }
}
```

> **Never rely on `HashMap` order.** Small `Integer` keys happen to print in ascending order because of how their hash codes map to buckets. That is an implementation detail, not a promise.

### Version timeline

| Feature | Java |
|---|---|
| `Map`, `HashMap`, `Hashtable` | 1.0 to 1.2 |
| `ConcurrentHashMap` | 5 |
| Default methods (`getOrDefault`, `merge`, `compute...`, `forEach`...) | 8 |
| `Map.of`, `Map.ofEntries`, `Map.entry` | 9 |
| `Map.copyOf` | 10 |
| `SequencedMap` (`firstEntry`, `reversed`...) | 21 |

---

## 3. How a HashMap Works Internally

This section explains *why* keys must be unique, *why* lookups are fast, and *why* some bugs happen. It is the most common interview topic.

### Real-life analogy: a library with numbered shelves

1. You want the book titled "Java". The librarian runs a formula on the title that gives a **shelf number** (this is the `hashCode`).
2. You go straight to that shelf. A few books may share the shelf (**collision**).
3. You compare titles one by one on that shelf using the **exact-match rule** (`equals`) until you find the right one.

You never scan the whole library. That is the speed of a HashMap.

### The steps for `put(key, value)`

```
1. h = key.hashCode()
2. bucket index = spread(h) & (capacity - 1)
3. if the bucket is empty        -> store the entry there
4. else compare with each entry there using equals()
      same key found            -> replace the value
      no match                  -> add a new entry to that bucket
```

`get(key)` does steps 1 to 2, then walks that one bucket using `equals`.

### Important numbers

| Detail | Value |
|---|---|
| Default capacity (number of buckets) | 16 |
| Load factor | 0.75 |
| Resize trigger | When entries exceed capacity x load factor (12 for 16 buckets) |
| Resize | Capacity doubles, entries are redistributed |
| Collision handling | Linked list in the bucket; converted to a **red-black tree** in Java 8+ when a bucket gets long (8+ entries and capacity at least 64) |
| Lookup cost | O(1) average; O(log n) worst case with trees |

### The `equals()` and `hashCode()` contract for keys

If two keys are `equals()`, they **must** have the same `hashCode()`. Otherwise they land on different shelves and the map cannot recognize them as the same key.

**A class without these overridden uses object identity:**

```java
class Point {
    int x, y;
    Point(int x, int y) { this.x = x; this.y = y; }
}

Map<Point, String> m = new HashMap<>();
m.put(new Point(1, 2), "A");
System.out.println(m.get(new Point(1, 2)));   // null: different objects, different identity
```

**Fix: override both, or use a record (records generate both correctly):**

```java
record Point(int x, int y) { }

Map<Point, String> m = new HashMap<>();
m.put(new Point(1, 2), "A");
System.out.println(m.get(new Point(1, 2)));   // A
```

`String`, `Integer`, `Long`, and the other wrapper classes already have correct `equals`/`hashCode`.

### Mutable keys are dangerous

```java
List<Integer> key = new ArrayList<>(List.of(1, 2));
Map<List<Integer>, String> m = new HashMap<>();
m.put(key, "A");

key.add(3);                     // the key's hashCode changes
System.out.println(m.get(key)); // null: the entry is on the old shelf and effectively lost
```

**Rule:** use **immutable keys** (`String`, `Integer`, records, enums).

### Why `TreeMap` is different

`TreeMap` does not use hashing. It keeps keys in sorted order in a **red-black tree** and decides uniqueness with `compareTo` (or a `Comparator`), **not** `equals`.

---

## 4. Creating a Map and Basic Operations

### 4.1 `put(K key, V value)`

Adds a pair. If the key already exists, the **old value is replaced**.

```java
Map<Integer, String> map = new HashMap<>();

map.put(101, "Java");
map.put(102, "Python");
System.out.println(map);        // {101=Java, 102=Python}

String old = map.put(101, "Spring");
System.out.println(old);        // Java   (previous value returned)
System.out.println(map);        // {101=Spring, 102=Python}
```

**Return value:** the previous value, or `null` if there was none.

> **Nuance:** a `null` return does not prove the key was absent. The key may have existed mapped to `null`.

**Analogy:** saving a contact. If the name already exists, the number is overwritten.

### 4.2 `putAll(Map m)`

Copies all entries from another map. Existing keys get their values replaced.

```java
Map<Integer, String> map1 = new HashMap<>();
map1.put(101, "Java");
map1.put(102, "Python");

Map<Integer, String> map2 = new HashMap<>();
map2.put(103, "Spring");
map2.put(104, "Docker");

map1.putAll(map2);
System.out.println(map1);   // {101=Java, 102=Python, 103=Spring, 104=Docker}
```

Think of it as "merge map2 into map1". There is **no `+` operator** for maps; `map1 = map1 + map2` is only a way of describing the idea.

### 4.3 `get(Object key)`

Returns the value for the key, or `null` if there is no mapping.

```java
map.put(999, null);

System.out.println(map.get(101));    // Spring
System.out.println(map.get(555));    // null: key absent
System.out.println(map.get(999));    // null: key present, value is null
```

The last two lines are identical in output but mean different things. To tell them apart, use `containsKey()`.

**Auto-unboxing trap:**

```java
Map<String, Integer> marks = new HashMap<>();
int m = marks.get("Asha");      // NullPointerException: get returned null, unboxing null fails
```

### 4.4 `getOrDefault(Object key, V defaultValue)`

Returns the value, or your default when the key is absent.

```java
map.put(101, "Java");

System.out.println(map.getOrDefault(101, "Unknown"));   // Java
System.out.println(map.getOrDefault(999, "Unknown"));   // Unknown
```

**Analogy:** asking directory enquiry for a number. If not found, you get "Unknown" instead of silence.

> The default is used only when the **key is absent**. If the key is present with a `null` value, you get `null`.

### 4.5 `remove(Object key)`

Removes the mapping and returns the old value (or `null` if none).

```java
map.put(101, "Java");
map.put(102, "Python");

String removed = map.remove(101);
System.out.println(removed);   // Java
System.out.println(map);       // {102=Python}
```

### 4.6 `remove(Object key, Object value)`

Removes the entry **only if the key is currently mapped to that value**. Returns `boolean`.

```java
map.put(101, "Java");

System.out.println(map.remove(101, "Python"));  // false: value does not match, nothing removed
System.out.println(map.remove(101, "Java"));    // true: removed
```

**Analogy:** cancelling a ticket booking **only if it is still in your name**.

### 4.7 `containsKey(Object key)`

```java
map.put(101, "Java");
System.out.println(map.containsKey(101));   // true
System.out.println(map.containsKey(999));   // false
```

Fast: O(1) average in a `HashMap`.

### 4.8 `containsValue(Object value)`

```java
map.put(101, "Java");
map.put(102, "Python");

System.out.println(map.containsValue("Java"));   // true
System.out.println(map.containsValue("101"));    // false (101 is a key, not a value)
```

**Slow:** it scans every entry, **O(n)**. Maps are indexed by key, not by value.

**Analogy:** finding a person by name in your contacts (instant) versus finding whose number is 98765xxxxx (read every contact).

### 4.9 `size()`, `isEmpty()`, `clear()`

```java
Map<Integer, String> map = new HashMap<>();
System.out.println(map.isEmpty());   // true

map.put(101, "Java");
map.put(102, "Python");
map.put(103, "Spring");
System.out.println(map.size());      // 3

map.clear();
System.out.println(map);             // {}
System.out.println(map.isEmpty());   // true
```

---

## 5. Views and Iteration: keySet, values, entrySet

A Map has three **views**: its keys, its values, and its entries.

**Analogy:** a school register. You can read only the roll-number column (keys), only the names column (values), or full rows (entries).

| Method | Returns | Contains |
|---|---|---|
| `keySet()` | `Set<K>` | All keys (unique, so a Set) |
| `values()` | `Collection<V>` | All values (duplicates possible, so not a Set) |
| `entrySet()` | `Set<Map.Entry<K,V>>` | All key-value pairs |

### `keySet()`

```java
Set<Integer> keys = map.keySet();
for (Integer key : map.keySet()) {
    System.out.println(key);
}
```

### `values()`

```java
Collection<String> values = map.values();
for (String v : values) {
    System.out.println(v);
}
```

### `entrySet()` and `Map.Entry`

A `Map.Entry` represents **one key-value pair**.

```java
for (Map.Entry<Integer, String> entry : map.entrySet()) {
    System.out.println(entry.getKey() + " -> " + entry.getValue());
}
```

| Method | Purpose |
|---|---|
| `getKey()` | The key |
| `getValue()` | The value |
| `setValue(V v)` | Replaces the value in the map |
| `equals()`, `hashCode()` | Entry comparison |
| `Map.Entry.comparingByKey()` | **Static** comparator on keys |
| `Map.Entry.comparingByValue()` | **Static** comparator on values |

### Why you cannot write `for (x : map)`

A `for-each` loop needs something **`Iterable`** that hands over one item at a time. A `Map` has two things per item (key and value) and is not `Iterable`. The array version is simple:

```java
int[] a = {10, 20, 30, 40};
for (int x : a) System.out.println(x);
```

`entrySet()` solves it by **packing each key-value pair into a single object** (`Map.Entry`) inside a `Set`, which is `Iterable`.

```
{ (101,Java), (102,Python) }   <- each pair is one Map.Entry<Integer,String>
```

### `entry.setValue(V)`: update while iterating

```java
for (Map.Entry<Integer, String> entry : map.entrySet()) {
    if (entry.getKey() == 101) {
        entry.setValue("Spring");     // writes through to the map
    }
}
```

> `setValue` works on entries from a `HashMap`/`TreeMap`. Entries from `Map.of(...)` or `Map.entry(...)` are immutable and throw `UnsupportedOperationException`.

### Three ways to iterate

```java
// 1. keySet + get   (works, but does an extra lookup per key)
for (Integer key : map.keySet()) {
    System.out.println(key + " -> " + map.get(key));
}

// 2. entrySet       (clearest when you need both key and value)
for (Map.Entry<Integer, String> e : map.entrySet()) {
    System.out.println(e.getKey() + " -> " + e.getValue());
}

// 3. forEach with a lambda (Java 8+)
map.forEach((key, value) -> System.out.println(key + " -> " + value));
```

Use `entrySet()` or `forEach()` when you need both. Approach 1 does a second hash lookup for every key.

### The views are backed by the map

Removing from a view removes from the map:

```java
map.keySet().remove(101);                    // entry 101 is gone from the map
map.values().removeIf(v -> v.equals("Java"));
map.entrySet().removeIf(e -> e.getKey() > 150);
```

You **cannot add** through `keySet()` or `values()` (`UnsupportedOperationException`).

### Modifying a map while iterating: `ConcurrentModificationException`

```java
for (Integer key : map.keySet()) {
    if (key == 101) {
        map.remove(key);       // ConcurrentModificationException (typically)
    }
}
```

**Correct options:**

```java
map.entrySet().removeIf(e -> e.getKey() == 101);     // cleanest

Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
while (it.hasNext()) {
    if (it.next().getKey() == 101) {
        it.remove();                                  // safe removal
    }
}
```

Replacing the value of an **existing** key during iteration (`put` or `setValue`) is fine. Adding or removing keys is the problem.

---

## 6. Replacing and Conditional Methods

### 6.1 `replace(K key, V value)`

Replaces the value **only if the key exists**. It never creates a new entry.

```java
map.put(101, "Java");

map.replace(101, "Spring");   // {101=Spring}
map.replace(555, "X");        // nothing happens, key absent
```

Returns the previous value, or `null`.

**Contrast with `put`:** `put` would create key 555. `replace` refuses.

**Analogy:** editing an existing contact versus saving a new one.

### 6.2 `replace(K key, V oldValue, V newValue)`

Replaces **only when the current value equals `oldValue`**. Returns `boolean`.

```java
map.put(101, "Java");

boolean r = map.replace(101, "Java", "Spring");
System.out.println(r);      // true
System.out.println(map);    // {101=Spring}
```

### 6.3 `replaceAll(BiFunction)`

Applies a function to **every entry's value**.

```java
Map<Integer, Integer> marks = new HashMap<>();
marks.put(101, 80);
marks.put(102, 70);
marks.put(103, 90);

marks.replaceAll((key, value) -> value + 5);
// {101=85, 102=75, 103=95}
```

```java
Map<String, String> names = new HashMap<>();
names.put("a", "java");
names.put("b", "python");

names.replaceAll((key, value) -> value.toUpperCase());
// {a=JAVA, b=PYTHON}
```

**Real life:** giving every employee a Rs. 5000 raise:

```java
salaries.replaceAll((name, salary) -> salary + 5000);
```

### 6.4 `putIfAbsent(K key, V value)`

Adds the entry **only if the key is absent (or mapped to `null`)**. Returns the existing value, or `null` if it inserted.

```java
map.put(101, "Java");

map.putIfAbsent(101, "Python");   // ignored: 101 already has a value
map.putIfAbsent(102, "Spring");   // inserted
// {101=Java, 102=Spring}
```

**Analogy:** reserving a seat **only if nobody has taken it**.

### put vs putIfAbsent vs replace

| Situation | `put` | `putIfAbsent` | `replace` |
|---|---|---|---|
| Key absent | Inserts | Inserts | Does nothing |
| Key present | Overwrites | Does nothing | Overwrites |

---

## 7. compute, computeIfAbsent, computeIfPresent, merge

These Java 8 methods do **"read, calculate, write" in one call**. They replace verbose `if (map.containsKey(...))` blocks.

### Quick comparison

| Method | When the function runs | Function receives | Function returns `null` means |
|---|---|---|---|
| `compute(k, fn)` | Always | `(key, oldValueOrNull)` | Remove the mapping |
| `computeIfAbsent(k, fn)` | Key absent (or null value) | `(key)` | No mapping is created |
| `computeIfPresent(k, fn)` | Key present with non-null value | `(key, oldValue)` | Remove the mapping |
| `merge(k, v, fn)` | Key present; if absent, `v` is simply stored | `(oldValue, v)` | Remove the mapping |

### 7.1 `compute(K key, BiFunction)`

```java
Map<String, Integer> map = new HashMap<>();
map.put("Java", 10);

map.compute("Java", (key, value) -> value + 5);
// Java -> 15

map.compute("Spring", (key, value) -> 20);
// Spring -> 20  (the old value was null and the function ignored it)
```

**Pitfall: `value` is `null` when the key is absent.**

```java
map.compute("Docker", (k, v) -> v + 1);               // NullPointerException
map.compute("Docker", (k, v) -> v == null ? 1 : v + 1);   // safe
```

### 7.2 `computeIfAbsent(K key, Function)`

Calculates and stores a value **only when missing**. Returns the value now in the map.

```java
Map<String, Integer> wordLength = new HashMap<>();
wordLength.computeIfAbsent("Java", key -> key.length());
// Java -> 4
```

**The grouping pattern (very common):**

```java
Map<String, List<String>> groups = new HashMap<>();

groups.computeIfAbsent("Java", key -> new ArrayList<>()).add("Spring");
```

It means: "if there is no list for this key, create one; then add to it." **Analogy:** sorting letters into pigeonholes by department. If the pigeonhole for "HR" does not exist, create it first.

Also excellent for **caching**: compute an expensive value once, reuse it after.

> **Do not modify the same map inside the function.** With `HashMap`, that can throw `ConcurrentModificationException`.

### 7.3 `computeIfPresent(K key, BiFunction)`

Updates **only if the key exists** with a non-null value.

```java
map.put("Java", 10);

map.computeIfPresent("Java", (key, value) -> value + 10);   // Java -> 20
map.computeIfPresent("Go",   (key, value) -> value + 10);   // nothing happens
```

### 7.4 `merge(K key, V value, BiFunction)`

"If the key is absent, store `value`. If present, **combine** old and new using the function."

```java
Map<String, Integer> counts = new HashMap<>();
counts.put("Java", 1);

counts.merge("Java", 1, (oldValue, newValue) -> oldValue + newValue);   // Java -> 2
counts.merge("Python", 1, Integer::sum);                                // Python -> 1
```

**Analogy:** counting votes. Each ballot says "add one to this candidate's tally," and a brand-new candidate starts at 1.

Useful for counting, summing, aggregation and frequency maps.

**Rules:**
- The `value` argument must **not be `null`** (NPE).
- If the function returns `null`, the entry is **removed**.

```java
// decrement stock, and remove the item when it reaches zero
stock.merge("pen", -1, (oldQty, delta) -> oldQty + delta == 0 ? null : oldQty + delta);
```

---

## 8. forEach, equals, hashCode, clone

### `forEach(BiConsumer)`

```java
map.forEach((key, value) -> System.out.println(key + " -> " + value));
```

### `equals(Object)`

Two maps are equal if they contain the **same mappings**, regardless of implementation.

```java
Map<Integer, String> m1 = new HashMap<>();
Map<Integer, String> m2 = new TreeMap<>();
m1.put(101, "Java");
m2.put(101, "Java");

System.out.println(m1.equals(m2));   // true
```

### `hashCode()`

The sum of the hash codes of all entries. Do not use the exact number as business data.

### `clone()`

`Map` does **not** define `clone()`. `HashMap` provides it (a shallow copy), and it returns `Object`, so a cast is needed:

```java
HashMap<Integer, String> original = new HashMap<>();
original.put(101, "Java");

@SuppressWarnings("unchecked")
HashMap<Integer, String> copy = (HashMap<Integer, String>) original.clone();
```

**A simpler, general way to copy any map:**

```java
Map<Integer, String> copy = new HashMap<>(original);
```

Both copies are **shallow**: the keys and values themselves are shared, not duplicated.

---

## 9. Immutable Maps: Map.of, ofEntries, copyOf

### `Map.of(...)` (Java 9)

```java
Map<Integer, String> map = Map.of(
    101, "Java",
    102, "Python",
    103, "Spring"
);

map.put(104, "Docker");   // UnsupportedOperationException
```

**Analogy:** a **printed price list on the wall**. You can read it, but you cannot edit it.

### Rules for `Map.of`

| Rule | Behavior |
|---|---|
| Immutable | `put`, `remove`, `clear` throw `UnsupportedOperationException` |
| No `null` keys or values | `NullPointerException` |
| Duplicate keys | `IllegalArgumentException` at creation |
| Maximum 10 pairs | Use `ofEntries` for more |
| Iteration order | **Unspecified**, and can differ between JVM runs |

### `Map.ofEntries(...)` and `Map.entry(...)`

```java
Map<Integer, String> map = Map.ofEntries(
    Map.entry(101, "Java"),
    Map.entry(102, "Python"),
    Map.entry(103, "Spring")
);
```

The result is immutable, with no null keys or values.

### `Map.copyOf(Map)` (Java 10)

```java
Map<Integer, String> source = new HashMap<>();
source.put(101, "Java");

Map<Integer, String> copy = Map.copyOf(source);
source.put(102, "Python");      // does NOT affect the copy
System.out.println(copy);       // {101=Java}
```

It is an **independent snapshot**. **Analogy:** a photocopy of a notice.

### `Map.copyOf` vs `Collections.unmodifiableMap`

| `Map.copyOf(m)` | `Collections.unmodifiableMap(m)` |
|---|---|
| Independent **copy** | Read-only **view** of the original |
| Later changes to `m` are not seen | Later changes to `m` **show through** |
| Rejects null keys/values | Allows whatever `m` allows |

### Getting a mutable map from an immutable one

```java
Map<Integer, String> editable = new HashMap<>(Map.of(1, "A", 2, "B"));
editable.put(3, "C");     // fine
```

---

## 10. Null Handling in Each Implementation

| Implementation | Null key | Null values |
|---|---|---|
| `HashMap` | **One** null key | Yes, many |
| `LinkedHashMap` | One null key | Yes |
| `TreeMap` | **No** (NPE with natural ordering) | Yes |
| `Hashtable` | No | No |
| `ConcurrentHashMap` | No | No |
| `EnumMap` | No | Yes |
| `Map.of`, `Map.copyOf` | No | No |
| `WeakHashMap`, `IdentityHashMap` | Yes | Yes |

```java
Map<Integer, String> map = new HashMap<>();
map.put(null, "Unknown");   // allowed
map.put(101, null);         // allowed
```

### Why does `ConcurrentHashMap` ban nulls?

In a concurrent map, `get(k)` returning `null` must clearly mean "absent." If nulls were allowed, you could not safely tell absent from null-valued, because another thread may change the map between `get` and `containsKey`.

> Always check the implementation's contract before relying on null support.

---

## 11. The Implementations

### 11.1 HashMap

The default, general-purpose choice.

```java
Map<Integer, String> map = new HashMap<>();
```

- One null key, many null values.
- **No ordering guarantee.**
- O(1) average `get` and `put`.
- **Not thread-safe.**

### 11.2 LinkedHashMap

A `HashMap` that also keeps a **linked list** through its entries, so iteration order is predictable (insertion order by default).

```java
Map<Integer, String> map = new LinkedHashMap<>();
map.put(103, "Spring");
map.put(101, "Java");
map.put(102, "Python");

System.out.println(map);   // {103=Spring, 101=Java, 102=Python}  (insertion order)
```

**Analogy:** a **guest book**: names stay in the order people arrived.

#### Access-order mode and an LRU cache

```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    LRUCache(int capacity) {
        super(16, 0.75f, true);        // true = access order
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;      // evict the least recently used
    }
}

LRUCache<Integer, String> cache = new LRUCache<>(2);
cache.put(1, "A");
cache.put(2, "B");
cache.get(1);             // 1 becomes the most recently used
cache.put(3, "C");        // evicts 2, the least recently used
System.out.println(cache);   // {1=A, 3=C}
```

**Analogy:** your phone's "recent apps" list. The oldest unused app drops off when it fills up.

### 11.3 TreeMap

Keeps entries **sorted by key**, using a red-black tree. Implements `SortedMap` and `NavigableMap`.

```java
Map<Integer, String> map = new TreeMap<>();
map.put(103, "Spring");
map.put(101, "Java");
map.put(102, "Python");

System.out.println(map);   // {101=Java, 102=Python, 103=Spring}
```

- O(log n) for `get`, `put`, `remove`.
- Keys must be comparable (`Comparable`) or you must supply a `Comparator`.
- No null keys with natural ordering.
- Uniqueness is decided by `compareTo`, **not** `equals`.

**Analogy:** a **dictionary** where words are always in alphabetical order.

#### Custom ordering

```java
Map<String, Integer> reverse = new TreeMap<>(Comparator.reverseOrder());
Map<String, Integer> ignoreCase = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
```

#### Navigation methods (the real strength of `TreeMap`)

```java
TreeMap<Integer, String> t = new TreeMap<>();
t.put(10, "A"); t.put(20, "B"); t.put(30, "C"); t.put(40, "D");

t.firstKey();          // 10
t.lastKey();           // 40
t.floorKey(25);        // 20  (greatest key <= 25)
t.ceilingKey(25);      // 30  (least key >= 25)
t.lowerKey(20);        // 10  (strictly less than 20)
t.higherKey(20);       // 30  (strictly greater than 20)
t.headMap(30);         // {10=A, 20=B}        keys < 30
t.tailMap(30);         // {30=C, 40=D}        keys >= 30
t.descendingMap();     // {40=D, 30=C, 20=B, 10=A}
```

**Real-life use: marks to grade slabs.**

```java
TreeMap<Integer, String> grades = new TreeMap<>();
grades.put(0, "F");
grades.put(40, "D");
grades.put(50, "C");
grades.put(60, "B");
grades.put(75, "A");

System.out.println(grades.floorEntry(68).getValue());   // B
```

Income tax slabs, delivery-charge slabs and discount tiers all work this way.

### 11.4 Hashtable (legacy)

- Every method is **synchronized** (slow under contention).
- No null keys or values.
- Legacy API; avoid in new code.

### 11.5 ConcurrentHashMap

Built for **many threads at once**, without locking the whole map.

```java
Map<String, Integer> counts = new ConcurrentHashMap<>();
counts.merge("java", 1, Integer::sum);     // atomic: safe from many threads
```

- No null keys or values.
- `merge`, `compute...`, `putIfAbsent` are **atomic**.
- Prefer it over `Hashtable` or `Collections.synchronizedMap` for concurrent code.

**Analogy:** `Hashtable` is a bank with **one counter** open (everyone waits). `ConcurrentHashMap` is a bank with **many counters**; people only wait if they need the same account.

**The race it prevents:**

```java
// NOT safe even on a thread-safe map: two threads can both read the same count
Integer c = map.get("java");
map.put("java", (c == null ? 0 : c) + 1);

// Safe: one atomic operation
map.merge("java", 1, Integer::sum);
```

### 11.6 Other implementations

| Class | Purpose |
|---|---|
| `EnumMap` | Keys are enum constants. Very compact and fast. |
| `WeakHashMap` | Keys held weakly; entries vanish when the key is no longer referenced elsewhere (caches). |
| `IdentityHashMap` | Compares keys with `==` instead of `equals`. |

```java
enum Day { MON, TUE, WED }
Map<Day, String> plan = new EnumMap<>(Day.class);
```

### 11.7 Comparison

| Feature | HashMap | LinkedHashMap | TreeMap | Hashtable | ConcurrentHashMap |
|---|---|---|---|---|---|
| Order | None | Insertion (or access) | Sorted by key | None | None |
| `get`/`put` | O(1) avg | O(1) avg | O(log n) | O(1) avg | O(1) avg |
| Null key | 1 | 1 | No | No | No |
| Null values | Yes | Yes | Yes | No | No |
| Thread-safe | No | No | No | Yes (slow) | Yes (fast) |
| Use when | General purpose | Order matters / LRU | Sorted or range queries | Legacy only | Multi-threaded |

### 11.8 Which one should I use?

```
Need thread safety?              -> ConcurrentHashMap
Need keys in sorted order?       -> TreeMap
Need insertion order or an LRU?  -> LinkedHashMap
Keys are enum constants?         -> EnumMap
Otherwise                        -> HashMap
```

### 11.9 SequencedMap (Java 21)

`LinkedHashMap` and `TreeMap` implement `SequencedMap`, which gives direct access to both ends:

```java
LinkedHashMap<String, Integer> m = new LinkedHashMap<>();
m.put("a", 1); m.put("b", 2); m.put("c", 3);

m.firstEntry();      // a=1
m.lastEntry();       // c=3
m.reversed();        // view in order: c=3, b=2, a=1
```

On `TreeMap`, positional inserts such as `putFirst` throw `UnsupportedOperationException`, because order is decided by the keys.

---

## 12. Sorting a Map

A `HashMap` cannot be sorted in place. You either use a sorted map, or sort a **view** and produce something new.

### Sort by key

**Option 1: `TreeMap`**

```java
Map<Integer, String> sorted = new TreeMap<>(map);
```

**Option 2: stream the entries**

```java
map.entrySet().stream()
   .sorted(Map.Entry.comparingByKey())
   .forEach(System.out::println);
```

### Sort by value

```java
Map<String, Integer> marks = new HashMap<>();
marks.put("Praveen", 90);
marks.put("Ravi", 75);
marks.put("Kiran", 85);

marks.entrySet().stream()
     .sorted(Map.Entry.comparingByValue())
     .forEach(System.out::println);
// Ravi=75
// Kiran=85
// Praveen=90
```

Descending:

```java
.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
```

### Keep the sorted result as a map

Collect into a `LinkedHashMap` so the order survives:

```java
Map<String, Integer> sortedByMarks = marks.entrySet().stream()
    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
    .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        (a, b) -> a,              // merge function for duplicate keys
        LinkedHashMap::new));     // keeps the order
```

Collecting into a plain `HashMap` would **lose** the order.

---

## 13. Common Patterns

### 13.1 Objects as values (lookup by ID)

```java
record Employee(int id, String name) { }

Map<Integer, Employee> employees = new HashMap<>();
employees.put(101, new Employee(101, "Praveen"));
employees.put(102, new Employee(102, "Ravi"));

Employee e = employees.get(101);
System.out.println(e.name());      // Praveen
```

**Why a map beats a list here:** with a list you scan every employee to find ID 101. With a map you go straight to it, like pulling a file from a labelled pigeonhole rather than flipping through a stack.

Always handle the missing case:

```java
Employee found = employees.get(999);
if (found == null) {
    System.out.println("Not found");
}
```

### 13.2 Frequency counter

```java
String[] words = {"java", "python", "java", "spring", "java", "python"};
Map<String, Integer> frequency = new HashMap<>();

for (String word : words) {
    frequency.merge(word, 1, Integer::sum);
}
// {java=3, python=2, spring=1}
```

**Older equivalent (to understand `merge`):**

```java
frequency.put(word, frequency.getOrDefault(word, 0) + 1);
```

**Stream version:**

```java
Map<String, Long> freq = Arrays.stream(words)
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
```

### 13.3 Grouping

```java
Map<String, List<String>> employees = new HashMap<>();

employees.computeIfAbsent("IT", k -> new ArrayList<>()).add("Praveen");
employees.computeIfAbsent("IT", k -> new ArrayList<>()).add("Ravi");
employees.computeIfAbsent("HR", k -> new ArrayList<>()).add("Kiran");

// IT -> [Praveen, Ravi]
// HR -> [Kiran]
```

**Stream version:**

```java
record Emp(String name, String dept) { }

Map<String, List<Emp>> byDept = emps.stream()
    .collect(Collectors.groupingBy(Emp::dept));
```

### 13.4 Records as keys

Records generate correct `equals` and `hashCode`, so they make safe composite keys:

```java
record Seat(int row, int number) { }

Map<Seat, String> bookings = new HashMap<>();
bookings.put(new Seat(3, 12), "Asha");
System.out.println(bookings.get(new Seat(3, 12)));   // Asha
```

### 13.5 Caching with `computeIfAbsent`

```java
Map<Integer, Double> cache = new HashMap<>();
double value = cache.computeIfAbsent(42, id -> expensiveLookup(id));
```

The expensive call runs once per key.

---

## 14. Map vs Collection, List and Set

| | Stores | Access by | Duplicates |
|---|---|---|---|
| `Collection<E>` (List, Set, Queue) | Single elements | Depends on type | Depends |
| `List<E>` | Ordered sequence | **Index** | Allowed |
| `Set<E>` | Unique elements | Membership test | **Not allowed** |
| `Map<K,V>` | Key-value associations | **Key** | Keys no, values yes |

- `Map` is **not** part of the `Collection` hierarchy. Its methods do not match (`put(k, v)` vs `add(e)`).
- `HashSet` is actually **backed by a `HashMap`**: the set's elements are the map's keys, and every value is a dummy placeholder object. Likewise `TreeSet` uses `TreeMap` and `LinkedHashSet` uses `LinkedHashMap`. That is why a Set rejects duplicates: a Map rejects duplicate keys.

---

## 15. Method Summary Table

| Method | Purpose | Returns |
|---|---|---|
| `put(k, v)` | Add or overwrite | Previous value or `null` |
| `putAll(m)` | Copy all entries | `void` |
| `putIfAbsent(k, v)` | Add only if absent | Existing value or `null` |
| `get(k)` | Retrieve value | Value or `null` |
| `getOrDefault(k, d)` | Retrieve or default | Value or `d` |
| `remove(k)` | Remove by key | Removed value or `null` |
| `remove(k, v)` | Remove if value matches | `boolean` |
| `containsKey(k)` | Key exists? | `boolean` |
| `containsValue(v)` | Value exists? (O(n)) | `boolean` |
| `size()` | Number of entries | `int` |
| `isEmpty()` | Is it empty? | `boolean` |
| `clear()` | Remove everything | `void` |
| `keySet()` | All keys | `Set<K>` |
| `values()` | All values | `Collection<V>` |
| `entrySet()` | All pairs | `Set<Map.Entry<K,V>>` |
| `replace(k, v)` | Overwrite if key exists | Previous value or `null` |
| `replace(k, old, new)` | Overwrite if value matches | `boolean` |
| `replaceAll(fn)` | Transform every value | `void` |
| `compute(k, fn)` | Recalculate value | New value |
| `computeIfAbsent(k, fn)` | Calculate if missing | Value in map |
| `computeIfPresent(k, fn)` | Calculate if present | New value or `null` |
| `merge(k, v, fn)` | Combine old and new | New value or `null` |
| `forEach(fn)` | Action on each entry | `void` |
| `equals(o)`, `hashCode()` | Comparison and hash | `boolean`, `int` |

---

## 16. Common Mistakes and Gotchas

| Mistake | Problem | Fix |
|---|---|---|
| Relying on `HashMap` order | Order is unspecified | Use `LinkedHashMap` or `TreeMap` |
| `int x = map.get("k");` with missing key | NPE from unboxing `null` | `getOrDefault("k", 0)` |
| Treating `get()` returning `null` as "absent" | May be a null value | Use `containsKey()` |
| Wrong key type: `Map<Long,String>`, then `map.get(101)` | `Integer(101)` is not `Long(101)`, returns `null` | Use `map.get(101L)` |
| Custom class as key without `equals`/`hashCode` | Lookups fail | Override both, or use a record |
| Mutating a key after inserting | Entry becomes unreachable | Use immutable keys |
| Removing during a `for-each` over the map | `ConcurrentModificationException` | `removeIf` or an `Iterator` |
| Writing to `Map.of(...)` | `UnsupportedOperationException` | Copy into `new HashMap<>(...)` |
| Null in `Map.of` or `ConcurrentHashMap` | NPE | Use `HashMap`, or avoid nulls |
| `compute` on a missing key using `v + 1` | `v` is `null`, so NPE | `v == null ? 1 : v + 1` |
| Using `containsValue` in a hot loop | O(n) every time | Keep a reverse map |
| `keySet()` then `get(key)` for both parts | Extra lookup per key | `entrySet()` or `forEach` |
| Expecting `TreeMap` to use `equals` | It uses `compareTo`/`Comparator` | Make comparison consistent with equality |
| Collecting a sorted stream into `HashMap` | Order lost | Collect into `LinkedHashMap` |
| Thinking `ConcurrentHashMap` makes get-then-put safe | Two separate steps can race | Use `merge` or `compute` |
| Shallow copy surprises | Copy shares the same value objects | Deep-copy values when needed |

---

## 17. Interview Questions and Answers

**1. What is the Map interface?**
An interface in `java.util` that stores unique keys mapped to values.

**2. Does Map extend Collection?**
No. It has its own hierarchy.

**3. Key vs value?**
The key identifies and must be unique. The value is the data and can repeat.

**4. Can a Map contain duplicate keys?**
No. Putting an existing key replaces its value.

**5. Can a Map contain duplicate values?**
Yes.

**6. What happens when `put()` is called with an existing key?**
The old value is replaced and returned.

**7. `get()` vs `getOrDefault()`?**
`get` returns `null` for an absent key. `getOrDefault` returns the supplied fallback.

**8. `remove(key)` vs `remove(key, value)`?**
The first removes unconditionally. The second removes only if the key is currently mapped to that value.

**9. `put()` vs `putIfAbsent()`?**
`put` always writes. `putIfAbsent` writes only if the key is missing or null-valued.

**10. `replace()` vs `put()`?**
`replace` only updates existing keys. `put` also creates new ones.

**11. `keySet()` vs `values()` vs `entrySet()`?**
Keys (as a Set), values (as a Collection), and key-value pairs (as a Set of `Map.Entry`).

**12. Why is `entrySet()` commonly used for iteration?**
It gives both key and value in one step without a second lookup.

**13. What is `Map.Entry`?**
An object representing one key-value pair.

**14. HashMap vs LinkedHashMap?**
`LinkedHashMap` adds a linked list to keep insertion (or access) order, with slightly more memory.

**15. HashMap vs TreeMap?**
`HashMap` is unordered with O(1) average operations. `TreeMap` keeps keys sorted with O(log n) operations and supports range and navigation queries.

**16. Does HashMap allow null keys?**
Yes, one null key and many null values.

**17. Does TreeMap allow null keys?**
No, not with natural ordering. It throws `NullPointerException`.

**18. Does ConcurrentHashMap allow null keys?**
No, neither null keys nor null values.

**19. What is `computeIfAbsent()`?**
Computes and stores a value only if the key is missing; widely used for grouping and caching.

**20. What is `computeIfPresent()`?**
Recomputes the value only if the key exists with a non-null value.

**21. Purpose of `merge()`?**
Insert a value if absent, otherwise combine old and new values. Ideal for counting and summing.

**22. How is a Map used as a frequency counter?**
`map.merge(item, 1, Integer::sum)` for each item.

**23. How is a Map used for grouping?**
`map.computeIfAbsent(key, k -> new ArrayList<>()).add(item)`.

**24. `Map.of()` vs `HashMap`?**
`Map.of` creates a small immutable map with no nulls and unspecified order. `HashMap` is mutable.

**25. Is `Map.of()` mutable?**
No. Modifying it throws `UnsupportedOperationException`.

**26. Purpose of `Map.copyOf()`?**
Creates an unmodifiable, independent snapshot of another map.

**27. How do you sort a Map by key?**
Use a `TreeMap`, or stream the entries with `Map.Entry.comparingByKey()`.

**28. How do you sort a Map by value?**
Stream the entries with `Map.Entry.comparingByValue()` and collect into a `LinkedHashMap`.

**29. How do you iterate with lambdas?**
`map.forEach((k, v) -> ...)`.

**30. Which implementation for sorted keys?**
`TreeMap`.

**31. How does HashMap work internally?**
It hashes the key to a bucket, then uses `equals` to find the exact entry in that bucket. It resizes at 75% load, and converts long buckets to red-black trees.

**32. What is the `equals`/`hashCode` contract?**
Equal objects must have equal hash codes. Without it, hash-based maps cannot find keys.

**33. Why are immutable keys recommended?**
A changed key changes its hash code, so the entry can no longer be found.

**34. HashMap vs Hashtable?**
`HashMap` is unsynchronized and allows nulls. `Hashtable` is legacy, synchronized and disallows nulls.

**35. HashMap vs ConcurrentHashMap?**
`ConcurrentHashMap` is thread-safe with fine-grained concurrency and atomic compound operations; no nulls.

**36. What is a fail-fast iterator?**
One that throws `ConcurrentModificationException` when the map is structurally modified during iteration.

**37. How is HashSet related to HashMap?**
A `HashSet` stores its elements as keys of an internal `HashMap`.

**38. `Map.copyOf` vs `Collections.unmodifiableMap`?**
`copyOf` is an independent snapshot; `unmodifiableMap` is a read-only view that reflects later changes.

---

## 18. Practice Exercises with Solutions

### Exercise 1: Students map

```java
Map<Integer, String> students = new HashMap<>();
students.put(1, "Asha");
students.put(2, "Ravi");
students.put(3, "Neha");
students.put(4, "Kiran");
students.put(5, "Meera");

students.forEach((id, name) -> System.out.println(id + " -> " + name));
```

### Exercise 2: 10% raise with `replaceAll`

```java
Map<String, Integer> salaries = new HashMap<>();
salaries.put("Asha", 50000);
salaries.put("Ravi", 42000);

salaries.replaceAll((name, sal) -> (int) Math.round(sal * 1.10));
System.out.println(salaries);   // {Asha=55000, Ravi=46200}
```

### Exercise 3: Word frequency

```java
String sentence = "java is fun and java is powerful";
Map<String, Integer> freq = new HashMap<>();

for (String w : sentence.split(" ")) {
    freq.merge(w, 1, Integer::sum);
}
System.out.println(freq);   // java=2, is=2, fun=1, and=1, powerful=1 (order not guaranteed)
```

### Exercise 4: Core operations

```java
Map<Integer, String> map = new HashMap<>();
map.put(101, "Java");
map.put(102, "Python");

System.out.println(map.get(101));               // Java
System.out.println(map.remove(102));            // Python
System.out.println(map.containsKey(101));       // true
System.out.println(map.containsValue("Java"));  // true
System.out.println(map.size());                 // 1
```

### Exercise 5: `putIfAbsent`, `replace`, `getOrDefault`

```java
Map<String, Integer> m = new HashMap<>();
m.put("A", 1);

m.putIfAbsent("A", 99);                  // ignored
m.putIfAbsent("B", 2);                   // inserted
m.replace("A", 10);                      // A -> 10
m.replace("Z", 5);                       // ignored, Z absent
System.out.println(m.getOrDefault("C", 0));   // 0
```

### Exercise 6: Product quantities with `merge`

```java
Map<String, Integer> stock = new HashMap<>();
stock.merge("pen", 10, Integer::sum);
stock.merge("pen", 5, Integer::sum);     // pen -> 15
stock.merge("book", 3, Integer::sum);    // book -> 3
```

### Exercise 7: Departments with `computeIfAbsent`

```java
Map<String, List<String>> dept = new HashMap<>();
dept.computeIfAbsent("IT", k -> new ArrayList<>()).add("Asha");
dept.computeIfAbsent("IT", k -> new ArrayList<>()).add("Ravi");
dept.computeIfAbsent("HR", k -> new ArrayList<>()).add("Neha");
```

### Exercise 8: Sorted IDs with `TreeMap`

```java
Map<Integer, Integer> marks = new TreeMap<>();
marks.put(103, 78);
marks.put(101, 90);
marks.put(102, 85);
System.out.println(marks);   // {101=90, 102=85, 103=78}
```

### Exercise 9: Insertion order with `LinkedHashMap`

```java
Map<String, Integer> m = new LinkedHashMap<>();
m.put("zeta", 1);
m.put("alpha", 2);
m.put("mid", 3);
System.out.println(m);   // {zeta=1, alpha=2, mid=3}
```

### Exercise 10: Employee lookup

```java
record Employee(int id, String name) { }

Map<Integer, Employee> map = new HashMap<>();
map.put(101, new Employee(101, "Praveen"));
map.put(102, new Employee(102, "Ravi"));

Employee e = map.get(102);
System.out.println(e != null ? e.name() : "Not found");   // Ravi
```

### Challenge questions (try before checking)

1. What does this print, and why?
   ```java
   Map<Long, String> m = new HashMap<>();
   m.put(5L, "five");
   System.out.println(m.get(5));
   ```
2. Why does this throw, and how do you fix it?
   ```java
   for (String k : map.keySet()) {
       if (k.startsWith("x")) map.remove(k);
   }
   ```
3. What is the difference in output between `HashMap` and `LinkedHashMap` for keys inserted as `"b", "a", "c"`?
4. Write one line that counts how many times each character appears in a string.

**Answers**

1. `null`. `5` is boxed to `Integer`, which is never equal to a `Long`. Use `m.get(5L)`.
2. Structural modification while iterating gives `ConcurrentModificationException`. Fix: `map.keySet().removeIf(k -> k.startsWith("x"));`.
3. `HashMap`: unspecified order (do not rely on it). `LinkedHashMap`: `{b=..., a=..., c=...}`.
4. `for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);` with `Map<Character, Integer> freq`.

---

## 19. Key Takeaways

1. A `Map` stores **unique keys** mapped to values; values may repeat.
2. `Map` is **not** a `Collection` and is not `Iterable`. Iterate with `entrySet()`, `keySet()`, `values()` or `forEach`.
3. `HashMap` works by **hashing to a bucket, then matching with `equals`**, so keys need a correct `equals`/`hashCode` and should be immutable.
4. `HashMap` is the default choice; `LinkedHashMap` for order (and LRU caches); `TreeMap` for sorted keys and range queries; `ConcurrentHashMap` for threads.
5. `get()` returning `null` is ambiguous. Use `containsKey()` or `getOrDefault()`.
6. `put` always writes, `putIfAbsent` only when missing, `replace` only when present.
7. `merge` and `computeIfAbsent` handle counting and grouping in one line.
8. `Map.of`, `ofEntries` and `copyOf` give **immutable** maps that reject nulls.
9. Do not modify a map while iterating it; use `removeIf` or an `Iterator`.
10. Match the key type exactly (`Long` vs `Integer`), because `get(Object)` will not warn you.

---

*End of notes.*
