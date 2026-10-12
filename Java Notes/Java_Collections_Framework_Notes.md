# Java Collections Framework: Detailed Notes

The complete `java.util` collections framework: hierarchy, List, Set, Queue and Deque, iterators, sorting with Comparable and Comparator, the utility classes, immutable collections, complexity, choosing the right collection, tricky puzzles and interview questions.

> **Map** has its own dedicated notes (Map interface, HashMap internals, compute/merge, and so on). This document recaps it briefly in section 14 and does not repeat the detail.

---

## Table of Contents

1. [Why Collections](#1-why-collections)
2. [The Framework Hierarchy](#2-the-framework-hierarchy)
3. [Generics and Wrapper Classes](#3-generics-and-wrapper-classes)
4. [The Collection Interface](#4-the-collection-interface)
5. [Iterating: Iterator, ListIterator, Fail-Fast](#5-iterating-iterator-listiterator-fail-fast)
6. [List](#6-list)
7. [Set](#7-set)
8. [Queue, Deque, Stack](#8-queue-deque-stack)
9. [Comparable and Comparator](#9-comparable-and-comparator)
10. [The Collections and Arrays Utility Classes](#10-the-collections-and-arrays-utility-classes)
11. [Immutable and Unmodifiable Collections](#11-immutable-and-unmodifiable-collections)
12. [Choosing the Right Collection](#12-choosing-the-right-collection)
13. [Thread Safety](#13-thread-safety)
14. [Map Recap and Streams Preview](#14-map-recap-and-streams-preview)
15. [Common Mistakes](#15-common-mistakes)
16. [Output Prediction Puzzles](#16-output-prediction-puzzles)
17. [Interview Questions](#17-interview-questions)
18. [Cheat Sheet](#18-cheat-sheet)
19. [Practice Tasks with Solutions](#19-practice-tasks-with-solutions)

---

## 1. Why Collections

### Arrays and their limits

```java
int[] marks = new int[5];       // size fixed forever
marks[5] = 10;                  // ArrayIndexOutOfBoundsException
```

| Array | Collection |
|---|---|
| **Fixed size** | **Grows and shrinks** dynamically |
| Holds primitives or objects | Holds **objects only** |
| No built-in operations (search, sort, remove) | Rich methods: `add`, `remove`, `contains`, `sort`... |
| One structure | Many structures: list, set, queue, map |
| Type-safe | Type-safe with **generics** |

**Analogy:** an array is an **egg tray** with a fixed number of slots. A collection is a **shopping basket** that holds whatever you put in and stretches as needed.

### What the framework gives you

- **Interfaces** (what a structure does): `List`, `Set`, `Queue`, `Map`.
- **Implementations** (how it does it): `ArrayList`, `HashSet`, `PriorityQueue`...
- **Algorithms** (ready-made operations): `Collections.sort`, `max`, `shuffle`...

The same code can work with different implementations because you program to the **interface**:

```java
List<String> names = new ArrayList<>();     // later switch to LinkedList by changing one word
```

---

## 2. The Framework Hierarchy

```
Iterable
  +-- Collection
        +-- List        ordered, index access, duplicates allowed
        |     ArrayList, LinkedList, Vector (-> Stack)
        |
        +-- Set         no duplicates
        |     HashSet
        |     LinkedHashSet
        |     SortedSet -> NavigableSet : TreeSet
        |
        +-- Queue       processed in order (usually FIFO)
              PriorityQueue
              Deque     double-ended
                 ArrayDeque, LinkedList

Map  (SEPARATE hierarchy; does NOT extend Collection)
  HashMap, LinkedHashMap, TreeMap, Hashtable, ConcurrentHashMap
```

### The key differences at a glance

| Interface | Order | Duplicates | Access by | Real-life picture |
|---|---|---|---|---|
| **List** | Insertion order, indexed | Allowed | Index | A **playlist** or numbered seats in a cinema row |
| **Set** | Depends on implementation | **Not allowed** | Membership | A **stamp collection**: no two the same |
| **Queue** | By arrival or priority | Allowed | Head | A **ticket counter line** |
| **Map** | Depends | Keys unique | Key | A **phone book** |

### Iterable vs Iterator vs Collection vs Collections

| Name | What it is |
|---|---|
| `Iterable` | Interface: anything usable in a for-each loop |
| `Iterator` | Object that walks through a collection |
| `Collection` | **Interface**: root of List, Set, Queue |
| `Collections` | **Utility class** with static helpers (`sort`, `reverse`...) |

### Java 21: sequenced collections

`List`, `Deque`, `LinkedHashSet` and `SortedSet` gained a shared interface, `SequencedCollection`, with:

```java
list.getFirst();   list.getLast();
list.addFirst(x);  list.addLast(x);
list.removeFirst(); list.removeLast();
list.reversed();           // a reversed view
```

Before Java 21 you wrote `list.get(list.size() - 1)` for the last element.

---

## 3. Generics and Wrapper Classes

### Generics

```java
List<String> names = new ArrayList<>();
names.add("Asha");
names.add(42);                      // compile error: only Strings allowed
String s = names.get(0);            // no cast needed
```

- **Type safety at compile time**, so no surprise `ClassCastException`.
- No casting needed when reading.
- `<>` (the diamond) lets the compiler infer the type on the right.

**Avoid raw types** (`List list = new ArrayList();`). They disable type checks.

### Primitives are not allowed; use wrappers

```java
List<int> bad;                  // error
List<Integer> ok = new ArrayList<>();
ok.add(10);                     // autoboxing: int -> Integer
int x = ok.get(0);              // auto-unboxing: Integer -> int
```

| Primitive | Wrapper |
|---|---|
| `int` | `Integer` |
| `double` | `Double` |
| `char` | `Character` |
| `boolean` | `Boolean` |
| `long` | `Long` |

Cautions with boxing:

- **Unboxing `null` throws `NullPointerException`.**
- **Compare wrappers with `equals()`, not `==`** (small values `-128..127` are cached, larger ones are not).

### Wildcards (brief)

| Syntax | Meaning | Use |
|---|---|---|
| `List<?>` | List of anything | Read-only generic handling |
| `List<? extends Number>` | List of `Number` or a subtype | **Reading** values (producer) |
| `List<? super Integer>` | List of `Integer` or a supertype | **Writing** values (consumer) |

Rule of thumb, **PECS**: *Producer Extends, Consumer Super.*

---

## 4. The Collection Interface

Methods shared by List, Set and Queue:

| Method | Purpose |
|---|---|
| `add(e)` | Add an element (returns `boolean`) |
| `addAll(c)` | Add all elements of another collection |
| `remove(o)` | Remove one occurrence |
| `removeAll(c)` | Remove all elements that appear in `c` (difference) |
| `retainAll(c)` | Keep only elements that appear in `c` (intersection) |
| `removeIf(predicate)` | Remove elements matching a condition |
| `contains(o)` / `containsAll(c)` | Membership tests |
| `size()` / `isEmpty()` / `clear()` | Size and emptiness |
| `iterator()` | Get an iterator |
| `forEach(consumer)` | Run an action on each element |
| `toArray()` | Convert to an array |
| `stream()` | Start a stream pipeline |

```java
Collection<String> c = new ArrayList<>();
c.add("A");
c.add("B");
c.addAll(List.of("C", "D"));      // [A, B, C, D]
c.remove("B");                    // [A, C, D]
c.removeIf(s -> s.equals("D"));   // [A, C]
System.out.println(c.contains("A"));   // true
System.out.println(c.size());          // 2
```

`contains`, `remove`, `indexOf` and similar methods use **`equals()`**. Objects you store must have a correct `equals` (and `hashCode` for hash-based collections).

---

## 5. Iterating: Iterator, ListIterator, Fail-Fast

### Four ways to loop

```java
List<String> list = List.of("A", "B", "C");

for (String s : list) { }                         // for-each (cleanest)
list.forEach(s -> System.out.println(s));         // lambda
for (int i = 0; i < list.size(); i++) { }         // index (List only)

Iterator<String> it = list.iterator();            // explicit iterator
while (it.hasNext()) {
    String s = it.next();
}
```

**Analogy:** an iterator is a **bookmark**. `hasNext()` asks "are there more pages?", `next()` turns to the next page.

### Iterator methods

| Method | Meaning |
|---|---|
| `hasNext()` | More elements? |
| `next()` | Return the next element and advance |
| `remove()` | Remove the **last returned** element (the only safe removal during iteration) |

### ListIterator (Lists only): both directions

```java
ListIterator<String> li = list.listIterator();
while (li.hasNext()) {
    String s = li.next();
    if (s.equals("B")) li.set("BB");              // replace current
}
while (li.hasPrevious()) {
    System.out.println(li.previous());            // walk backwards
}
```

Extra methods: `hasPrevious`, `previous`, `add`, `set`, `nextIndex`, `previousIndex`.

### Fail-fast and `ConcurrentModificationException`

Most collections are **fail-fast**: they keep a modification counter, and if the collection is structurally changed during iteration (except through the iterator itself), the next `next()` throws `ConcurrentModificationException`.

**Analogy:** a library walkthrough where an alarm sounds if anyone rearranges shelves while you're mid-tour.

```java
List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4));

for (Integer n : nums) {
    if (n % 2 == 0) nums.remove(n);        // ConcurrentModificationException (usually)
}
```

**Safe ways to remove while looping**

```java
nums.removeIf(n -> n % 2 == 0);            // best

Iterator<Integer> it = nums.iterator();
while (it.hasNext()) {
    if (it.next() % 2 == 0) it.remove();   // safe
}
```

### Fail-fast vs fail-safe

| | Fail-fast | Fail-safe (weakly consistent) |
|---|---|---|
| Behavior on modification during iteration | Throws `ConcurrentModificationException` | No exception |
| How | Checks a modification counter | Iterates over a **snapshot** or a concurrent structure |
| Examples | `ArrayList`, `HashMap`, `HashSet` | `CopyOnWriteArrayList`, `ConcurrentHashMap` |

Fail-fast is **best-effort** (a debugging aid), not a guarantee. See puzzle 1.

---

## 6. List

> A **List** is an **ordered** collection with **index access** that **allows duplicates** and `null`s (implementation-dependent).

### Common List methods

| Method | Purpose |
|---|---|
| `add(e)` / `add(index, e)` | Append / insert |
| `get(index)` | Read by position |
| `set(index, e)` | Replace by position |
| `remove(index)` / `remove(Object)` | Remove by position / by value |
| `indexOf(o)` / `lastIndexOf(o)` | First / last position (or `-1`) |
| `contains(o)` | Membership |
| `subList(from, to)` | A **view** of a range (`to` exclusive) |
| `sort(comparator)` | Sort in place |
| `replaceAll(op)` | Transform every element |
| `List.of(...)` | Immutable list |

```java
List<String> list = new ArrayList<>();
list.add("Java");
list.add("Python");
list.add(1, "Spring");                  // [Java, Spring, Python]
list.set(0, "Kotlin");                  // [Kotlin, Spring, Python]
list.remove("Spring");                  // [Kotlin, Python]
list.indexOf("Python");                 // 1
list.replaceAll(String::toUpperCase);   // [KOTLIN, PYTHON]
```

### The `remove` trap

```java
List<Integer> list = new ArrayList<>(List.of(10, 20, 30, 1));

list.remove(1);                      // removes the element AT INDEX 1  -> [10, 30, 1]
list.remove(Integer.valueOf(1));     // removes the VALUE 1             -> [10, 30]
```

For `List<Integer>`, `remove(int)` (index) and `remove(Object)` (value) are different overloads. A plain `int` argument picks the **index** version.

### 6.1 ArrayList

A **resizable array**.

```java
List<String> list = new ArrayList<>();
```

**How it works**

- Backed by an `Object[]` array.
- Default capacity **10**, allocated on the first `add`.
- When full, creates a new array about **1.5 times larger** and copies elements.

**Analogy:** a row of **numbered seats in a cinema**. Finding seat 7 is instant. Inserting a person in the middle makes everyone after them shift one seat. When the row is full, you move the whole group to a bigger hall.

| Operation | Cost |
|---|---|
| `get(i)`, `set(i, e)` | **O(1)** |
| `add(e)` at end | **O(1)** amortized |
| `add(i, e)` / `remove(i)` in the middle | **O(n)** (shifting) |
| `contains`, `indexOf` | O(n) |

Tips: `new ArrayList<>(expectedSize)` avoids repeated resizing; not thread-safe; allows `null`.

### 6.2 LinkedList

A **doubly linked list**: each node stores the element and links to the previous and next nodes. It implements both `List` and `Deque`.

**Analogy:** **train coaches** linked together. Adding or removing a coach at the ends is easy, but finding "coach number 50" means walking along the train.

| Operation | Cost |
|---|---|
| `addFirst`, `addLast`, `removeFirst`, `removeLast` | **O(1)** |
| `get(i)` | **O(n)** (walks from the nearer end) |
| Insert or remove in the middle | O(n) to **find** the position, O(1) to relink |

Uses more memory (two links per element) and is **cache-unfriendly**.

### ArrayList vs LinkedList

| | ArrayList | LinkedList |
|---|---|---|
| Structure | Dynamic array | Doubly linked list |
| `get(i)` | O(1) | O(n) |
| Add at end | O(1) amortized | O(1) |
| Add/remove at front | O(n) | O(1) |
| Memory | Compact | Extra per element |
| Real-world speed | **Faster in most cases** | Rarely wins |

**Practical advice:** use **`ArrayList`** by default. For a queue or stack use **`ArrayDeque`**. `LinkedList` is seldom the best choice even for frequent insertions, because finding the position costs O(n) anyway.

### 6.3 Vector and Stack (legacy)

- `Vector`: like `ArrayList`, but every method is `synchronized` (slow), from Java 1.0.
- `Stack extends Vector`: push/pop/peek. **A design mistake** (it exposes `Vector` methods like `insertElementAt`).
- Use `ArrayList` and `ArrayDeque` instead; for thread safety use `CopyOnWriteArrayList` or `Collections.synchronizedList`.

### 6.4 Arrays and Lists

```java
// Array -> List
List<String> fixed = Arrays.asList("A", "B", "C");     // FIXED-SIZE list backed by the array
List<String> mutable = new ArrayList<>(Arrays.asList("A", "B", "C"));
List<String> immutable = List.of("A", "B", "C");

// List -> Array
String[] arr = mutable.toArray(new String[0]);
```

| Creation | `add`/`remove` | `set` | Notes |
|---|---|---|---|
| `new ArrayList<>(...)` | Yes | Yes | Normal, mutable |
| `Arrays.asList(...)` | **No** (`UnsupportedOperationException`) | Yes (writes through to the array) | Fixed-size view |
| `List.of(...)` | **No** | **No** | Immutable, no `null`s |

`Arrays.asList(new int[]{1, 2, 3})` gives a `List<int[]>` of size **1**, because a primitive array is a single object. Use `Integer[]`.

### 6.5 `subList` is a view

```java
List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5));
List<Integer> part = nums.subList(1, 4);     // [2, 3, 4]
part.set(0, 99);                             // nums becomes [1, 99, 3, 4, 5]
```

Changes show through both ways. If you structurally modify the original afterward, the sublist breaks. Copy it with `new ArrayList<>(nums.subList(...))` if you need independence.

---

## 7. Set

> A **Set** contains **no duplicate elements**.

**Analogy:** a **stamp collection** or a **guest list**. Adding something you already have changes nothing.

`add()` returns `false` when the element was already present:

```java
Set<String> set = new HashSet<>();
set.add("Java");      // true
set.add("Java");      // false (duplicate ignored)
```

### Three main implementations

| | HashSet | LinkedHashSet | TreeSet |
|---|---|---|---|
| Order | **None** | **Insertion order** | **Sorted** |
| Speed | O(1) average | O(1) average | O(log n) |
| Duplicates decided by | `equals` + `hashCode` | `equals` + `hashCode` | `compareTo` / `Comparator` |
| `null` allowed | One | One | **No** (NPE) |
| Internally | `HashMap` | `LinkedHashMap` | `TreeMap` (red-black tree) |
| Analogy | A bag of unique items | A guest book in arrival order | An alphabetized dictionary |

### 7.1 HashSet

```java
Set<String> set = new HashSet<>();
set.add("Banana"); set.add("Apple"); set.add("Cherry");
System.out.println(set);                 // order not guaranteed
System.out.println(set.contains("Apple"));   // true, in O(1)
```

**Internally**, a `HashSet` is a `HashMap` where your elements are the **keys** and every value is a dummy constant. That is why a set rejects duplicates the way a map rejects duplicate keys.

### Custom objects in a HashSet

You **must** override `equals` and `hashCode`, or duplicates will not be detected:

```java
record Student(int id, String name) { }      // records generate both

Set<Student> students = new HashSet<>();
students.add(new Student(1, "Asha"));
students.add(new Student(1, "Asha"));
System.out.println(students.size());          // 1
```

With a plain class and no overrides, size would be **2**.

### 7.2 LinkedHashSet

A `HashSet` with a linked list through its entries to preserve insertion order.

**Classic use: remove duplicates but keep order**

```java
List<Integer> withDuplicates = List.of(3, 1, 3, 2, 1);
List<Integer> unique = new ArrayList<>(new LinkedHashSet<>(withDuplicates));
System.out.println(unique);       // [3, 1, 2]
```

### 7.3 TreeSet

Keeps elements **sorted**, using a red-black tree.

```java
Set<Integer> set = new TreeSet<>(List.of(30, 10, 20));
System.out.println(set);          // [10, 20, 30]
```

- Elements must be `Comparable`, or you must pass a `Comparator`.
- Uniqueness is decided by **comparison**, not `equals`.

```java
Set<String> ci = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
ci.add("Java"); ci.add("java"); ci.add("JAVA");
System.out.println(ci);           // [Java]   all three compare as equal
```

**Navigation methods (`NavigableSet`)**

```java
TreeSet<Integer> t = new TreeSet<>(List.of(10, 20, 30, 40));

t.first();            // 10
t.last();             // 40
t.floor(25);          // 20  (greatest <= 25)
t.ceiling(25);        // 30  (least >= 25)
t.lower(20);          // 10  (strictly less)
t.higher(20);         // 30  (strictly greater)
t.headSet(30);        // [10, 20]      elements < 30
t.tailSet(30);        // [30, 40]      elements >= 30
t.descendingSet();    // [40, 30, 20, 10]
t.pollFirst();        // removes and returns 10
```

### Set operations (maths on sets)

```java
Set<String> java = new HashSet<>(List.of("Asha", "Ravi", "Neha"));
Set<String> python = new HashSet<>(List.of("Ravi", "Kiran"));

Set<String> union = new HashSet<>(java);
union.addAll(python);                     // [Asha, Ravi, Neha, Kiran]

Set<String> both = new HashSet<>(java);
both.retainAll(python);                   // [Ravi]         intersection

Set<String> onlyJava = new HashSet<>(java);
onlyJava.removeAll(python);               // [Asha, Neha]   difference
```

**Real life:** students enrolled in both courses, in either course, or in one only.

### Other sets

- `Set.of(...)`: immutable; no `null`s; **duplicates throw `IllegalArgumentException`**.
- `EnumSet`: very compact and fast set of enum constants.
- `CopyOnWriteArraySet`, `ConcurrentSkipListSet`: concurrent versions.

---

## 8. Queue, Deque, Stack

### 8.1 Queue (FIFO: first in, first out)

**Analogy:** a **ticket counter line**. The first person to arrive is served first.

Each operation exists in two flavors:

| Operation | Throws exception | Returns special value |
|---|---|---|
| Insert | `add(e)` | `offer(e)` (returns `false` if full) |
| Remove head | `remove()` | `poll()` (returns `null` if empty) |
| Examine head | `element()` | `peek()` (returns `null` if empty) |

```java
Queue<String> q = new LinkedList<>();       // or ArrayDeque
q.offer("A");
q.offer("B");
q.offer("C");

System.out.println(q.peek());    // A  (look at the head)
System.out.println(q.poll());    // A  (remove the head)
System.out.println(q);           // [B, C]
```

Prefer `offer`/`poll`/`peek`; they signal "empty" without exceptions.

### 8.2 PriorityQueue

A queue ordered by **priority**, not arrival. `poll()` always returns the **smallest** element (natural order) or the first by your `Comparator`.

**Analogy:** a hospital **emergency room**. The most critical patient is seen first, regardless of arrival time.

```java
Queue<Integer> pq = new PriorityQueue<>();
pq.add(5); pq.add(1); pq.add(3);

System.out.println(pq.poll());   // 1
System.out.println(pq.poll());   // 3
System.out.println(pq.poll());   // 5
```

**Max-heap (largest first):**

```java
Queue<Integer> max = new PriorityQueue<>(Comparator.reverseOrder());
```

**Custom priority:**

```java
record Task(String name, int priority) { }

Queue<Task> tasks = new PriorityQueue<>(Comparator.comparingInt(Task::priority));
tasks.add(new Task("Report", 3));
tasks.add(new Task("Bug fix", 1));
System.out.println(tasks.poll().name());     // Bug fix
```

**Facts**

- Internally a **binary heap** (stored in an array).
- `offer` and `poll`: **O(log n)**; `peek`: O(1); `contains`/`remove(Object)`: O(n).
- Only the **head** is guaranteed to be the smallest. **Printing or iterating does not give sorted order** (see puzzle 3).
- No `null`s; not thread-safe (`PriorityBlockingQueue` is).

**Classic use: top-3 largest numbers with a size-limited min-heap**

```java
int[] nums = {7, 2, 9, 4, 11, 5};
PriorityQueue<Integer> top = new PriorityQueue<>();
for (int n : nums) {
    top.offer(n);
    if (top.size() > 3) top.poll();       // drop the smallest
}
System.out.println(top);                   // contains 7, 9, 11 (heap order)
```

### 8.3 Deque (double-ended queue)

Add and remove at **both ends**. Pronounced "deck".

**Analogy:** a line you can join or leave from either end.

| Operation | Front | Back |
|---|---|---|
| Insert | `addFirst`, `offerFirst` | `addLast`, `offerLast` |
| Remove | `removeFirst`, `pollFirst` | `removeLast`, `pollLast` |
| Examine | `getFirst`, `peekFirst` | `getLast`, `peekLast` |

**Implementations:** `ArrayDeque` (**preferred**) and `LinkedList`.

### 8.4 Stack behavior (LIFO: last in, first out)

**Analogy:** a **pile of plates**: you add to the top and take from the top. The **browser back button** and **undo** work the same way.

Use `Deque`, not the legacy `Stack` class:

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(10);                 // addFirst
stack.push(20);
stack.push(30);

System.out.println(stack.peek());   // 30  (top, not removed)
System.out.println(stack.pop());    // 30  (removed)
System.out.println(stack);          // [20, 10]
```

| Stack method | Deque equivalent |
|---|---|
| `push(e)` | `addFirst(e)` |
| `pop()` | `removeFirst()` |
| `peek()` | `peekFirst()` |

### Example: balanced brackets with a stack

```java
static boolean balanced(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char ch : s.toCharArray()) {
        if ("({[".indexOf(ch) >= 0) {
            stack.push(ch);
        } else if (")}]".indexOf(ch) >= 0) {
            if (stack.isEmpty()) return false;
            char open = stack.pop();
            if ("({[".indexOf(open) != ")}]".indexOf(ch)) return false;
        }
    }
    return stack.isEmpty();
}

balanced("{[()]}");   // true
balanced("{[(])}");   // false
```

### `ArrayDeque` vs others

- Faster than `Stack` and `LinkedList` for stack and queue use; no per-node overhead.
- **No `null` elements.** Not thread-safe.
- `BlockingQueue` implementations (`LinkedBlockingQueue`, `ArrayBlockingQueue`) support producer-consumer threading.

---

## 9. Comparable and Comparator

Sorting and ordered collections (`TreeSet`, `TreeMap`, `PriorityQueue`, `Collections.sort`) need to know **how to compare** two objects.

| | Comparable | Comparator |
|---|---|---|
| Package | `java.lang` | `java.util` |
| Method | `compareTo(T other)` | `compare(T a, T b)` |
| Where defined | **Inside** the class | **Outside** the class |
| Orderings | **One** natural ordering | **Many** different ones |
| Analogy | A student's natural **roll-number order** | An external judge who ranks by **marks**, **age** or **name** |

### Return value meaning

| Result | Meaning |
|---|---|
| Negative | First is **before** second |
| Zero | Equal in ordering |
| Positive | First is **after** second |

### Comparable: natural ordering

```java
record Student(int id, String name, int marks) implements Comparable<Student> {
    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.id, other.id);       // order by id
    }
}

List<Student> list = new ArrayList<>(List.of(
    new Student(3, "Ravi", 80),
    new Student(1, "Asha", 90),
    new Student(2, "Neha", 80)));

Collections.sort(list);      // uses compareTo -> ids 1, 2, 3
```

### Comparator: custom orderings

```java
// by marks, highest first
list.sort(Comparator.comparingInt(Student::marks).reversed());

// by marks descending, then by name ascending
list.sort(Comparator.comparingInt(Student::marks).reversed()
                    .thenComparing(Student::name));
// Asha(90), Neha(80), Ravi(80)
```

### Useful `Comparator` helpers

| Method | Purpose |
|---|---|
| `Comparator.comparing(keyExtractor)` | Compare by a key |
| `comparingInt`, `comparingDouble` | Avoid boxing for numeric keys |
| `.reversed()` | Flip the order |
| `.thenComparing(...)` | Tie-breaker |
| `Comparator.naturalOrder()` / `reverseOrder()` | Natural ordering and its reverse |
| `Comparator.nullsFirst(...)` / `nullsLast(...)` | Handle `null`s |
| `String.CASE_INSENSITIVE_ORDER` | Ignore case |

### Pitfalls

- **Do not subtract** to compare numbers (`a.id - b.id`). It can **overflow**. Use `Integer.compare(a, b)`.
- A comparator **must be consistent** (if `a < b` and `b < c`, then `a < c`), or sorting can throw `IllegalArgumentException: Comparison method violates its general contract`.
- In `TreeSet`/`TreeMap`, two elements that **compare as equal are treated as duplicates**, even if `equals()` says otherwise. Make ordering consistent with `equals` when you can.

---

## 10. The Collections and Arrays Utility Classes

### `java.util.Collections`: static helpers

```java
List<Integer> nums = new ArrayList<>(List.of(5, 2, 9, 1));

Collections.sort(nums);                         // [1, 2, 5, 9]
Collections.sort(nums, Collections.reverseOrder());   // [9, 5, 2, 1]
Collections.reverse(nums);                      // [1, 2, 5, 9]
Collections.shuffle(nums);                      // random order
Collections.swap(nums, 0, 1);
Collections.max(nums);                          // 9
Collections.min(nums);                          // 1
Collections.frequency(nums, 2);                 // how many 2s
Collections.binarySearch(sortedList, 5);        // list MUST be sorted
```

| Method | Purpose |
|---|---|
| `sort`, `reverse`, `shuffle`, `swap`, `rotate`, `fill` | Reorder or fill a list |
| `max`, `min`, `frequency` | Statistics |
| `binarySearch` | Fast search (requires a **sorted** list) |
| `unmodifiableList/Set/Map` | Read-only **view** |
| `synchronizedList/Set/Map` | Thread-safe wrapper |
| `emptyList`, `singletonList`, `nCopies(n, x)` | Quick small lists |
| `addAll(c, a, b, c)` | Add many elements |
| `disjoint(a, b)` | No common elements? |

### `java.util.Arrays`

```java
int[] a = {5, 2, 9, 1};

Arrays.sort(a);                              // [1, 2, 5, 9]
System.out.println(Arrays.toString(a));      // [1, 2, 5, 9]
int[] copy = Arrays.copyOf(a, 6);            // [1, 2, 5, 9, 0, 0]
int[] range = Arrays.copyOfRange(a, 1, 3);   // [2, 5]
Arrays.fill(a, 0);
Arrays.equals(a, copy);
Arrays.binarySearch(sortedArray, 5);
Arrays.deepToString(matrix);                 // for 2D arrays
Arrays.stream(a).sum();
```

---

## 11. Immutable and Unmodifiable Collections

### Three different things

| Kind | Example | Behavior |
|---|---|---|
| **Immutable factory** | `List.of(...)`, `Set.of(...)`, `Map.of(...)`, `List.copyOf(...)` | Cannot change at all; **no `null`s** |
| **Unmodifiable view** | `Collections.unmodifiableList(list)` | You cannot change it through the view, but **changes to the original show through** |
| **Fixed-size** | `Arrays.asList(...)` | `set` allowed; `add`/`remove` not |

**Analogy:** an immutable list is a **printed photograph**. An unmodifiable view is a **glass display case**: you can't touch the items, but if the owner rearranges them inside, you see the change.

```java
List<String> original = new ArrayList<>(List.of("A", "B"));

List<String> view = Collections.unmodifiableList(original);
List<String> snapshot = List.copyOf(original);

original.add("C");
System.out.println(view);        // [A, B, C]   (view reflects the change)
System.out.println(snapshot);    // [A, B]      (independent copy)
view.add("D");                   // UnsupportedOperationException
```

### Other points

- `stream.toList()` (Java 16) returns an **unmodifiable** list; `Collectors.toList()` returns a mutable one (not guaranteed by the spec).
- `List.of`, `Set.of`, `Map.of` reject `null` elements with `NullPointerException`; `Set.of` and `Map.of` reject **duplicates** with `IllegalArgumentException`.
- **Why use immutable collections:** safe sharing, thread-safe, no accidental modification, good for constants and return values.

### Getting a mutable copy

```java
List<String> editable = new ArrayList<>(List.of("A", "B"));
```

---

## 12. Choosing the Right Collection

### Decision guide

```
Need key -> value lookup?                     -> Map (see Map notes)

Need unique elements?
     insertion order matters?                 -> LinkedHashSet
     sorted order matters?                    -> TreeSet
     just uniqueness, fastest                 -> HashSet

Need ordered elements with duplicates?        -> List
     default choice                           -> ArrayList

Need first-in-first-out processing?           -> ArrayDeque (as Queue)
Need last-in-first-out (stack, undo)?         -> ArrayDeque (as Deque)
Need "most important first"?                  -> PriorityQueue
```

### Real-life scenarios

| Scenario | Collection |
|---|---|
| Shopping cart items, duplicates allowed | `ArrayList` |
| Unique email IDs that signed up | `HashSet` |
| Unique usernames shown alphabetically | `TreeSet` |
| Recently viewed products, unique, in order | `LinkedHashSet` |
| Print job queue | `ArrayDeque` (as Queue) |
| Emergency-room triage, task scheduler | `PriorityQueue` |
| Undo history, browser back, bracket matching | `ArrayDeque` (as Stack) |
| Phone book, ID to record | `HashMap` |

### Complexity summary

| Structure | Access / `get` | Search / `contains` | Insert | Remove |
|---|---|---|---|---|
| **ArrayList** | O(1) | O(n) | O(1) end, O(n) middle | O(n) |
| **LinkedList** | O(n) | O(n) | O(1) at ends | O(1) at ends, O(n) middle |
| **HashSet / HashMap** | n/a | O(1) avg | O(1) avg | O(1) avg |
| **LinkedHashSet** | n/a | O(1) avg | O(1) avg | O(1) avg |
| **TreeSet / TreeMap** | n/a | O(log n) | O(log n) | O(log n) |
| **PriorityQueue** | peek O(1) | O(n) | O(log n) | poll O(log n) |
| **ArrayDeque** | ends O(1) | O(n) | O(1) at ends | O(1) at ends |

Hash-based "O(1)" is **average**; it depends on a good `hashCode`.

---

## 13. Thread Safety

Most collections (`ArrayList`, `HashMap`, `HashSet`...) are **not thread-safe**. Options:

| Option | Notes |
|---|---|
| `Collections.synchronizedList(...)` | Wraps with a lock; you must still synchronize manually while iterating |
| `Vector`, `Hashtable` | Legacy; coarse locking |
| `CopyOnWriteArrayList` / `CopyOnWriteArraySet` | Copies on every write; excellent for **read-heavy** use |
| `ConcurrentHashMap` | Fine-grained concurrency; atomic `merge`/`compute` |
| `ConcurrentLinkedQueue`, `BlockingQueue` family | Thread-safe queues; blocking queues suit producer-consumer |

Even with thread-safe collections, compound actions like "check then add" must be **atomic** (use `putIfAbsent`, `merge`, `computeIfAbsent`).

---

## 14. Map Recap and Streams Preview

### Map in one table (details are in the Map notes)

| | HashMap | LinkedHashMap | TreeMap |
|---|---|---|---|
| Order | None | Insertion (or access) | Sorted by key |
| `get`/`put` | O(1) avg | O(1) avg | O(log n) |
| `null` key | One | One | No |

`Map` is **not** a `Collection`, but `keySet()` and `entrySet()` return Sets and `values()` returns a Collection.

### Streams with collections

```java
List<Student> students = ...;

List<String> topNames = students.stream()
    .filter(s -> s.marks() >= 80)
    .sorted(Comparator.comparingInt(Student::marks).reversed())
    .map(Student::name)
    .toList();

Map<Integer, List<Student>> byMarks = students.stream()
    .collect(Collectors.groupingBy(Student::marks));
```

Streams **do not store data**; they describe a pipeline over a collection, and the original collection is not modified.

---

## 15. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| `list.remove(1)` on `List<Integer>` expecting value removal | Removes the element **at index 1** | `list.remove(Integer.valueOf(1))` |
| Modifying a list inside a for-each | `ConcurrentModificationException` | `removeIf` or `Iterator.remove()` |
| Calling `add` on `Arrays.asList(...)` or `List.of(...)` | `UnsupportedOperationException` | `new ArrayList<>(...)` |
| `Arrays.asList(intArray)` | List of one `int[]`, not of ints | Use `Integer[]` or `Arrays.stream(...).boxed().toList()` |
| Custom class in `HashSet`/`HashMap` without `equals` and `hashCode` | Duplicates not detected, lookups fail | Override both, or use a record |
| Mutating an object after adding it to a hash-based collection | Element becomes unfindable | Use immutable keys or elements |
| Using `LinkedList` for random access | O(n) `get` | `ArrayList` |
| Using `Stack` or `Vector` in new code | Legacy, synchronized | `ArrayDeque` / `ArrayList` |
| Relying on `HashSet` or `HashMap` order | Unspecified | `LinkedHash...` or `Tree...` |
| Expecting `PriorityQueue` iteration to be sorted | Only the head is guaranteed | Poll repeatedly, or sort a copy |
| `TreeSet` of a class that is not `Comparable` | `ClassCastException` | Implement `Comparable` or pass a `Comparator` |
| Comparing with `a.id - b.id` | Integer overflow | `Integer.compare(a.id, b.id)` |
| `binarySearch` on an unsorted list | Meaningless result | Sort first |
| Comparing `Integer`s with `==` | Works only in the cached range | `equals()` |
| Unboxing a `null` (`int x = map.get(k)`) | `NullPointerException` | Check for null or use `getOrDefault` |
| `null` in `List.of`, `Set.of`, `ArrayDeque`, `TreeSet` | `NullPointerException` | Avoid nulls or use another class |
| Sharing a mutable list from a getter | Callers can change internal state | Return `List.copyOf(list)` or an unmodifiable view |
| Using raw types | No compile-time safety | Always parameterize |
| `subList` then changing the original | `ConcurrentModificationException` | Copy the sublist |

---

## 16. Output Prediction Puzzles

**Puzzle 1: the second-to-last quirk**

```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
for (Integer n : list) {
    if (n == 2) list.remove(n);
}
System.out.println(list);
```

**Puzzle 2: two kinds of `remove`**

```java
List<Integer> list = new ArrayList<>(List.of(10, 20, 30, 1));
list.remove(1);
System.out.println(list);

List<Integer> list2 = new ArrayList<>(List.of(10, 20, 30, 1));
list2.remove(Integer.valueOf(1));
System.out.println(list2);
```

**Puzzle 3: printing a PriorityQueue**

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.add(5); pq.add(3); pq.add(1); pq.add(4);
System.out.println(pq);
System.out.println(pq.poll() + " " + pq.poll() + " " + pq.poll());
```

**Puzzle 4: the primitive array**

```java
int[] arr = {1, 2, 3};
System.out.println(Arrays.asList(arr).size());
```

**Puzzle 5: case-insensitive TreeSet**

```java
Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
set.add("Java"); set.add("java"); set.add("JAVA");
System.out.println(set + " " + set.size());
```

**Puzzle 6: which lines throw?**

```java
List<Integer> a = List.of(1, 2, 3);
List<Integer> b = Arrays.asList(1, 2, 3);
List<Integer> c = new ArrayList<>(a);

a.set(0, 9);     // line A
b.set(0, 9);     // line B
b.add(4);        // line C
c.add(4);        // line D
```

**Puzzle 7: boxed comparison**

```java
List<Integer> small = List.of(127, 127);
List<Integer> big = List.of(1000, 1000);
System.out.println(small.get(0) == small.get(1));
System.out.println(big.get(0) == big.get(1));
System.out.println(big.get(0).equals(big.get(1)));
```

**Answers**

1. Prints **`[1, 3]` with no exception**. After removing the second-to-last element, `size` becomes 2 and the iterator's cursor is 2, so `hasNext()` returns `false` and the loop ends before the fail-fast check. Removing the **first** element would throw instead. Fail-fast is best-effort, so never rely on this.
2. **`[10, 30, 1]`** (removed the element at index 1, which was `20`) and **`[10, 20, 30]`** (removed the value `1`).
3. **`[1, 4, 3, 5]`** then **`1 3 4`**. The printed order is the internal heap array, not sorted order. Polling returns elements in priority order.
4. **`1`**. A primitive `int[]` is a single object, so you get a `List<int[]>` with one element.
5. **`[Java] 1`**. All three strings compare as equal, so only the first is kept.
6. **Line A throws** (`List.of` is immutable). **Line B works** (`set` writes through on a fixed-size list). **Line C throws** (`Arrays.asList` cannot grow). **Line D works**.
7. **`true`, `false`, `true`**. Values from -128 to 127 are cached `Integer` objects, but `1000` is boxed into separate objects each time, so `==` fails. `equals` compares values.

---

## 17. Interview Questions

**1. What is the Collections Framework?** A unified architecture of interfaces, implementations and algorithms for storing and manipulating groups of objects.

**2. Array vs collection?** Arrays are fixed-size and can hold primitives. Collections are dynamic, hold objects, and provide rich operations.

**3. `Collection` vs `Collections`?** `Collection` is the root interface; `Collections` is a utility class of static methods.

**4. Does `Map` extend `Collection`?** No.

**5. List vs Set vs Queue vs Map?** List: ordered, indexed, duplicates. Set: unique. Queue: processing order, FIFO or priority. Map: key-value pairs.

**6. ArrayList vs LinkedList?** ArrayList uses a dynamic array (O(1) `get`). LinkedList uses a doubly linked list (O(1) at the ends, O(n) `get`). Prefer ArrayList.

**7. How does ArrayList grow?** When full, it allocates a new array about 1.5 times larger and copies the elements.

**8. ArrayList vs Vector?** Vector is synchronized and legacy; ArrayList is not synchronized and is faster.

**9. HashSet vs LinkedHashSet vs TreeSet?** Unordered O(1); insertion-ordered O(1); sorted O(log n).

**10. How does HashSet work internally?** It is backed by a `HashMap`; elements are keys and all values are a dummy object.

**11. How does a Set detect duplicates?** HashSet uses `hashCode` and `equals`; TreeSet uses `compareTo`/`Comparator`.

**12. Why override `equals` and `hashCode` for objects in a HashSet?** Without them, equal-looking objects are treated as different.

**13. Comparable vs Comparator?** Comparable defines natural ordering inside the class (`compareTo`). Comparator defines external, multiple orderings (`compare`).

**14. Can TreeSet store `null`?** No; it throws `NullPointerException` with natural ordering.

**15. What is the difference between `poll()` and `remove()` on a queue?** `poll` returns `null` when empty; `remove` throws `NoSuchElementException`.

**16. How does PriorityQueue work?** A binary heap; `poll` returns the smallest element or the first by the comparator.

**17. Does iterating a PriorityQueue give sorted order?** No; only the head is guaranteed.

**18. Why prefer `ArrayDeque` over `Stack`?** `Stack` is legacy, synchronized and extends `Vector`. `ArrayDeque` is faster and cleaner.

**19. `Iterator` vs `ListIterator`?** `ListIterator` works only on Lists and can move backward and add or set elements.

**20. What is fail-fast?** Iterators throw `ConcurrentModificationException` if the collection is structurally modified during iteration.

**21. How do you remove elements safely while iterating?** `Iterator.remove()` or `removeIf`.

**22. Fail-fast vs fail-safe?** Fail-fast throws on modification; fail-safe iterates a snapshot or concurrent structure without throwing.

**23. What is `CopyOnWriteArrayList`?** A thread-safe list that copies the array on every write, with snapshot iterators.

**24. `Arrays.asList` vs `List.of`?** `asList` is a fixed-size, writable view over the array; `List.of` is fully immutable and rejects nulls.

**25. Unmodifiable view vs immutable copy?** A view reflects changes to the original; `List.copyOf` is an independent snapshot.

**26. What does `subList` return?** A view of part of the original list, not a copy.

**27. How do you remove duplicates from a list preserving order?** `new ArrayList<>(new LinkedHashSet<>(list))`.

**28. How do you sort a list of objects by two fields?** `Comparator.comparing(...).thenComparing(...)`.

**29. What does `Collections.binarySearch` require?** A sorted list.

**30. What are wrapper classes and autoboxing?** Object versions of primitives (`Integer`), with automatic conversion between them.

**31. What is the PECS rule?** Producer Extends, Consumer Super, for choosing wildcard bounds.

**32. How do you make a collection thread-safe?** `Collections.synchronized...`, concurrent collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`, blocking queues).

**33. Which collection for LRU or ordered caching?** `LinkedHashMap` in access-order mode.

**34. What are sequenced collections (Java 21)?** An interface giving a defined encounter order with `getFirst`, `getLast`, `reversed`, and so on on lists, deques and ordered sets.

**35. How do you convert between array and list?** `Arrays.asList` / `new ArrayList<>(...)` and `list.toArray(new T[0])`.

---

## 18. Cheat Sheet

| Need | Use | Key detail |
|---|---|---|
| Ordered, indexed, duplicates | `ArrayList` | O(1) `get`; default capacity 10; grows 1.5x |
| Frequent add/remove at both ends | `ArrayDeque` | No nulls; use as queue or stack |
| Unique elements, fastest | `HashSet` | Needs `equals` and `hashCode` |
| Unique, insertion order | `LinkedHashSet` | Dedupe while keeping order |
| Unique, sorted | `TreeSet` | `Comparable` or `Comparator`; no null |
| FIFO | `Queue` (`ArrayDeque`) | `offer`, `poll`, `peek` |
| LIFO | `Deque` (`ArrayDeque`) | `push`, `pop`, `peek` |
| By priority | `PriorityQueue` | Heap; only head is ordered |
| Key to value | `HashMap` / `TreeMap` | See the Map notes |
| Read-only | `List.of`, `List.copyOf` | No nulls |

**Remember**

```
Programming to interfaces:   List<String> list = new ArrayList<>();
Remove while looping:        removeIf  or  Iterator.remove()
Sorting:                     Comparable (natural)  vs  Comparator (custom)
Utilities:                   Collections.* and Arrays.*
Legacy to avoid:             Vector, Stack, Hashtable
```

---

## 19. Practice Tasks with Solutions

### Task 1: Remove duplicates, keep order

```java
List<Integer> nums = List.of(4, 2, 4, 7, 2, 9);
List<Integer> unique = new ArrayList<>(new LinkedHashSet<>(nums));
System.out.println(unique);        // [4, 2, 7, 9]
```

### Task 2: Common elements of two lists

```java
List<Integer> a = new ArrayList<>(List.of(1, 2, 3, 4));
List<Integer> b = List.of(3, 4, 5);
a.retainAll(b);
System.out.println(a);             // [3, 4]
```

### Task 3: Sort students by marks (high to low), then name

```java
record Student(String name, int marks) { }

List<Student> list = new ArrayList<>(List.of(
    new Student("Ravi", 80), new Student("Asha", 90), new Student("Neha", 80)));

list.sort(Comparator.comparingInt(Student::marks).reversed()
                    .thenComparing(Student::name));
list.forEach(System.out::println);
// Student[name=Asha, marks=90]
// Student[name=Neha, marks=80]
// Student[name=Ravi, marks=80]
```

### Task 4: Word frequency (List plus Map)

```java
String text = "java is fun and java is powerful";
Map<String, Integer> freq = new HashMap<>();
for (String w : text.split(" ")) {
    freq.merge(w, 1, Integer::sum);
}
System.out.println(freq);          // {java=2, is=2, ...}
```

### Task 5: Process tasks by priority

```java
record Task(String name, int priority) { }

PriorityQueue<Task> pq = new PriorityQueue<>(Comparator.comparingInt(Task::priority));
pq.add(new Task("Deploy", 2));
pq.add(new Task("Fix outage", 1));
pq.add(new Task("Write docs", 3));

while (!pq.isEmpty()) System.out.println(pq.poll().name());
// Fix outage, Deploy, Write docs
```

### Task 6: Balanced brackets

Use the `balanced(String)` method from section 8.4 and test `"{[()]}"` and `"{[(])}"`.

### Task 7: Safe removal of even numbers

```java
List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
nums.removeIf(n -> n % 2 == 0);
System.out.println(nums);          // [1, 3, 5]
```

### Task 8: Prove the contract for custom objects

Create a plain `Point` class with `x` and `y`. Add `new Point(1, 2)` twice to a `HashSet` and print the size. Then add `equals`/`hashCode` (or convert to a record) and print it again.

### Challenge questions

1. You need the 10 largest scores from a stream of a million numbers. Which collection keeps memory small, and why?
2. Why does `new TreeSet<>(List.of(...))` fail if the element type is a class without `Comparable`, and what are two ways to fix it?
3. A getter returns your internal `ArrayList`. What can go wrong, and what is the fix?

**Answers**

1. A **min-heap `PriorityQueue` of size 10**: offer each score and poll when the size exceeds 10. Memory stays at 10 elements regardless of input size, and each step costs O(log 10).
2. The tree has no way to order the elements, so it throws `ClassCastException`. Fix by making the class implement `Comparable` or by passing a `Comparator` to the `TreeSet` constructor.
3. Callers can add, remove or clear elements and corrupt your object's state. Return `List.copyOf(list)` (an independent immutable copy) or `Collections.unmodifiableList(list)` (a read-only view).

---

*End of notes.*
