# Java Strings: Detailed Notes

Everything about `String`: immutability, the string pool, `==` vs `equals`, creation, all the important methods, `StringBuilder` and `StringBuffer`, characters, conversions, formatting, text blocks, regex basics, performance, solved interview programs, puzzles and interview questions.

> The newer methods (`isBlank`, `strip`, `repeat`, `lines`, `formatted`, `transform`, `indent`...) are covered in the Modern Java Features notes. They appear here only briefly.

---

## Table of Contents

1. [What Is a String](#1-what-is-a-string)
2. [Creating Strings](#2-creating-strings)
3. [Immutability](#3-immutability)
4. [The String Pool and `==` vs `equals`](#4-the-string-pool-and--vs-equals)
5. [Comparing Strings](#5-comparing-strings)
6. [Essential Methods](#6-essential-methods)
7. [Concatenation](#7-concatenation)
8. [StringBuilder, StringBuffer, StringJoiner](#8-stringbuilder-stringbuffer-stringjoiner)
9. [Characters and Unicode](#9-characters-and-unicode)
10. [Conversions](#10-conversions)
11. [Formatting, Escapes and Text Blocks](#11-formatting-escapes-and-text-blocks)
12. [Regex Basics](#12-regex-basics)
13. [Performance and Security Notes](#13-performance-and-security-notes)
14. [Interview Programs, Solved](#14-interview-programs-solved)
15. [Common Mistakes](#15-common-mistakes)
16. [Output Prediction Puzzles](#16-output-prediction-puzzles)
17. [Interview Questions](#17-interview-questions)
18. [Cheat Sheet](#18-cheat-sheet)
19. [Practice Tasks with Solutions](#19-practice-tasks-with-solutions)

---

## 1. What Is a String

> A **String** is a sequence of characters, represented by the class `java.lang.String`.

```java
String name = "Asha";
```

Unlike `int` or `char`, `String` is a **class** (a reference type), but Java gives it special support: string literals in double quotes, the `+` operator, a special memory area (the pool), and `switch` support.

### Key facts

| Fact | Detail |
|---|---|
| Package | `java.lang` (no import needed) |
| Declared as | `public final class String` (**cannot be extended**) |
| Implements | `CharSequence`, `Comparable<String>`, `Serializable` |
| **Immutable** | Once created, its contents never change |
| Internally | A `byte[]` (since Java 9 "compact strings": 1 byte per character for Latin-1 text, otherwise UTF-16). Before Java 9 it was a `char[]` |
| Index | Characters are numbered from **0** |

### Real-life analogy

| Type | Analogy |
|---|---|
| `String` | A **printed certificate** or an **engraved plaque**. To "change" it, you issue a brand-new one |
| `StringBuilder` | A **whiteboard** or a **notebook written in pencil**. You can edit in place |

---

## 2. Creating Strings

```java
// 1. String literal (goes to / reuses the string pool)
String a = "hello";

// 2. new keyword (always creates a new object on the heap)
String b = new String("hello");

// 3. From a char array
char[] chars = {'h', 'e', 'l', 'l', 'o'};
String c = new String(chars);
String d = String.valueOf(chars);

// 4. From other types
String e = String.valueOf(123);        // "123"
String f = Integer.toString(45);       // "45"
String g = "" + 3.5;                   // "3.5"

// 5. From a StringBuilder
String h = new StringBuilder("hi").toString();

// 6. Text block (Java 15+), see section 11
String json = """
        {"name": "Asha"}
        """;
```

### Empty, blank and null

| Value | Meaning | Calling a method |
|---|---|---|
| `null` | **No object at all** | `NullPointerException` |
| `""` | An empty string: an object of length 0 | Works; `isEmpty()` is `true` |
| `"   "` | Only whitespace | Works; `isBlank()` is `true`, `isEmpty()` is `false` |

**Analogy:** `null` is a **missing envelope**, `""` is an **empty envelope**, `"   "` is an envelope with **blank paper** in it.

---

## 3. Immutability

> Once a `String` object is created, its **content can never change**. Every "modification" returns a **new** String.

```java
String s = "java";
s.concat(" rocks");              // returns a new String, which is thrown away
s.toUpperCase();                 // returns a new String, thrown away
System.out.println(s);           // java   (unchanged!)

s = s.concat(" rocks");          // now we KEEP the new String by reassigning
System.out.println(s);           // java rocks
```

```
Before:     s ---> "java"
After s = s.concat(" rocks"):
            s ---> "java rocks"          "java" is still in memory (if pooled), now unreferenced by s
```

**Important distinction:** the **variable** `s` can point somewhere else; the **object** it used to point to is never modified.

```java
String s1 = "abc";
String s2 = s1;          // both point to the same object
s1 = "xyz";              // s1 now points elsewhere
System.out.println(s2);  // abc   (s2 is unaffected)
```

### Why is String immutable?

| Reason | Explanation |
|---|---|
| **String pool** | Many references can safely share one object. If one could change it, every holder would see the change |
| **Security** | Strings carry file paths, database URLs, user names and class names. They cannot be altered after validation (no "check then change" attacks) |
| **Thread safety** | Immutable objects can be shared between threads without locking |
| **Hash caching** | `hashCode()` is computed once and cached, which makes `String` an excellent `HashMap` key |
| **Class loading** | Class names are strings; changing them could load the wrong class |

**Analogy:** a **printed ID card** that many offices photocopy and rely on. If anyone could scribble over it, nobody could trust it.

### The cost

Repeated modification creates **many temporary objects** (section 13). That is why `StringBuilder` exists.

---

## 4. The String Pool and `==` vs `equals`

### The string pool

> The **string pool** (string intern pool) is a special region of the heap where Java keeps **one copy of each distinct string literal**.

**Analogy:** a library with **one shared copy** of a popular book on a common shelf. Everyone who asks for "Harry Potter" is handed the same copy. Writing `new String("...")` is like buying **your own private copy**.

```java
String a = "hello";
String b = "hello";                 // reuses the same pooled object
String c = new String("hello");     // a NEW, separate object on the heap
```

```
Heap
 +------------------------------+
 |  String pool                 |
 |   "hello"  <---- a, b        |
 |                              |
 |  (normal heap)               |
 |   "hello"  <---- c           |   (a separate object, same characters)
 +------------------------------+
```

### `==` vs `equals()`

| | `==` | `equals()` |
|---|---|---|
| Compares | **References** (same object? same address?) | **Content** (same characters?) |
| Analogy | "Are these the **same house**?" | "Do these houses have the **same design**?" |
| For Strings, use it for | Almost never | **Always** |

```java
System.out.println(a == b);          // true   same pooled object
System.out.println(a == c);          // false  different objects
System.out.println(a.equals(c));     // true   same content
```

### `intern()`

Returns the **pooled** version of a string.

```java
String d = c.intern();
System.out.println(a == d);          // true
```

### Compile-time constants vs run-time concatenation

```java
String a = "hello";

String x = "hel" + "lo";             // compile-time constant: folded to "hello"
System.out.println(a == x);          // true

String p = "hel";
String y = p + "lo";                 // computed at RUN time: a new object
System.out.println(a == y);          // false

final String q = "hel";              // a constant variable
String z = q + "lo";                 // folded at compile time
System.out.println(a == z);          // true
```

### How many objects does `new String("abc")` create?

- **Up to 2:** the literal `"abc"` in the pool (if not already there), and the new object on the heap.
- **1** if `"abc"` was already in the pool.

`String s = "a" + "b" + "c";` creates **1** (folded into `"abc"`).

### Where is the pool?

In the **heap** (moved from PermGen in Java 7), so unused pooled strings can be garbage collected.

---

## 5. Comparing Strings

| Method | Purpose |
|---|---|
| `equals(Object)` | Content equality, **case-sensitive** |
| `equalsIgnoreCase(String)` | Content equality, ignoring case |
| `compareTo(String)` | **Dictionary order**, case-sensitive |
| `compareToIgnoreCase(String)` | Dictionary order, ignoring case |
| `contentEquals(CharSequence)` | Compare with a `StringBuilder` and others |
| `startsWith` / `endsWith` | Prefix and suffix tests |
| `regionMatches(...)` | Compare parts of strings |

### Null-safe equality

```java
String role = null;

role.equals("admin");                // NullPointerException
"admin".equals(role);                // false (put the constant FIRST)
Objects.equals(role, "admin");       // false (handles null on both sides)
```

### `compareTo`: how it works

Compares **character by character** (Unicode values) and returns:

- the **difference of the first differing characters**, or
- the **difference in lengths** if one is a prefix of the other, or
- `0` if equal.

```java
"apple".compareTo("banana");    // -1    'a' (97) - 'b' (98)
"banana".compareTo("apple");    //  1
"apple".compareTo("apple");     //  0
"apple".compareTo("app");       //  2    length 5 - 3 (prefix case)
"Z".compareTo("a");             // -7    'Z' (90) - 'a' (97): uppercase sorts BEFORE lowercase
```

| Result | Meaning |
|---|---|
| Negative | This string comes **before** the other |
| Zero | Equal |
| Positive | This string comes **after** the other |

Use `compareToIgnoreCase` or `String.CASE_INSENSITIVE_ORDER` to sort ignoring case.

---

## 6. Essential Methods

All of these **return new values**; they never change the original.

### Basic information

| Method | Result | Example |
|---|---|---|
| `length()` | Number of characters | `"hello".length()` is `5` |
| `charAt(i)` | Character at index | `"hello".charAt(1)` is `'e'` |
| `isEmpty()` | Length is 0? | `"".isEmpty()` is `true` |
| `isBlank()` (Java 11) | Empty or only whitespace? | `"  ".isBlank()` is `true` |

`charAt` with a bad index throws `StringIndexOutOfBoundsException`. The last valid index is `length() - 1`.

### Searching

```java
String s = "programming";

s.indexOf('g');              // 3   first occurrence (-1 if absent)
s.indexOf("gram");           // 3
s.indexOf('g', 4);           // 10  search from index 4
s.lastIndexOf('g');          // 10
s.contains("ram");           // true
s.startsWith("pro");         // true
s.endsWith("ing");           // true
```

### Extracting: `substring(begin, end)`

**`begin` is inclusive, `end` is exclusive.**

```java
String s = "hello";

s.substring(1, 3);           // "el"   (indices 1 and 2)
s.substring(2);              // "llo"  (from index 2 to the end)
s.substring(5);              // ""     (index equal to length is allowed)
s.substring(6);              // StringIndexOutOfBoundsException
```

Length of the result is `end - begin`.

### Transforming

```java
"Hello".toUpperCase();                    // HELLO
"Hello".toLowerCase();                    // hello
"  hi  ".trim();                          // "hi"      (removes characters <= space)
"  hi  ".strip();                         // "hi"      (Unicode-aware whitespace, Java 11)
"hello".replace('l', 'L');                // heLLo
"a-b-c".replace("-", "+");                // a+b+c     (literal text)
"a1b22".replaceAll("[0-9]+", "#");        // a#b#      (REGEX)
"a1b22".replaceFirst("[0-9]+", "#");      // a#b22
"ab".repeat(3);                           // ababab    (Java 11)
"hello".concat(" world");                 // hello world
```

| | `replace` | `replaceAll` |
|---|---|---|
| Pattern is | **Literal text** | A **regular expression** |
| Example trap | `"a.b".replace(".", "-")` is `a-b` | `"a.b".replaceAll(".", "-")` is `---` (the dot matches anything) |

### Splitting and joining

```java
"a,b,c".split(",");                       // ["a", "b", "c"]
"one two  three".split("\\s+");           // ["one", "two", "three"]
String.join("-", "a", "b", "c");          // a-b-c
String.join(", ", List.of("x", "y"));     // x, y
```

**`split` takes a regex.** Know these behaviors:

```java
"a,b,,c,,".split(",");           // ["a", "b", "", "c"]       trailing empty strings REMOVED -> length 4
"a,b,,c,,".split(",", -1);       // ["a", "b", "", "c", "", ""]  limit -1 keeps them -> length 6
"a.b".split(".");                // []                        "." is a regex (any char) -> every piece is empty -> length 0
"a.b".split("\\.");              // ["a", "b"]                escape the dot
"a|b".split("|");                // ["a", "|", "b"]           "|" is regex alternation of nothing -> splits every char
"a|b".split("\\|");              // ["a", "b"]
" a  b".split(" ");              // ["", "a", "", "b"]        leading empty string kept
"".split(",");                   // [""]                      length 1, not 0
```

### Converting to arrays and streams

```java
"hello".toCharArray();                          // ['h','e','l','l','o']
"hello".chars();                                // IntStream of char codes
"line1\nline2".lines();                         // Stream<String> (Java 11)
"hello".getBytes(StandardCharsets.UTF_8);       // byte[]
```

### Case and locale

```java
"TITLE".toLowerCase(Locale.ROOT);
```

`toUpperCase()` / `toLowerCase()` use the **default locale**. In a Turkish locale, `"I".toLowerCase()` gives a dotless `ı`. For keys, identifiers and protocol text, pass **`Locale.ROOT`**.

### Other useful methods

| Method | Purpose |
|---|---|
| `matches(regex)` | Does the **entire** string match? |
| `intern()` | Return the pooled instance |
| `hashCode()` | Content-based hash |
| `String.valueOf(x)` | Convert anything to a String |
| `String.format(...)` / `formatted(...)` | Formatting (section 11) |
| `isBlank`, `strip`, `lines`, `repeat`, `transform`, `indent` | See the Modern Java notes |

---

## 7. Concatenation

### Ways to join strings

```java
String a = "Hello" + " " + "World";                 // + operator
String b = "Hello".concat(" World");                 // concat
String c = String.join(" ", "Hello", "World");       // join
String d = new StringBuilder("Hello").append(" World").toString();
String e = "%s %s".formatted("Hello", "World");
```

### `+` rules (a favorite trap)

`+` is **addition** if both sides are numbers, but **concatenation** as soon as one side is a String. It is evaluated **left to right**.

```java
1 + 2 + "3";             // "33"     (1+2 = 3, then "3" + "3")
"1" + 2 + 3;             // "123"
1 + 2 + "3" + 4 + 5;     // "3345"
'a' + 'b';               // 195      (char + char = int addition)
'a' + 'b' + "c";         // "195c"
"c" + 'a' + 'b';         // "cab"
"x" + null;              // "xnull"
"x".concat(null);        // NullPointerException
```

### The loop problem

```java
String s = "";
for (int i = 0; i < 100000; i++) {
    s += i;                  // creates a NEW String each iteration, copying everything so far
}
```

Each iteration copies all previous characters, so the total work is **O(n²)**. Use `StringBuilder` (section 8). A single `a + b + c` expression is fine; the compiler optimizes it (a `StringBuilder` before Java 9, `invokedynamic` since).

---

## 8. StringBuilder, StringBuffer, StringJoiner

### StringBuilder: a mutable string

```java
StringBuilder sb = new StringBuilder("Hello");

sb.append(" World");              // Hello World
sb.insert(0, ">> ");              // >> Hello World
sb.replace(3, 8, "Howdy");        // >> Howdy World   (replaces indices 3 to 7)
sb.delete(0, 3);                  // Howdy World
sb.deleteCharAt(sb.length() - 1); // Howdy Worl
sb.setCharAt(0, 'h');             // howdy Worl
sb.reverse();                     // lroW ydwoh
sb.setLength(0);                  // (empty)
```

| Method | Purpose |
|---|---|
| `append(x)` | Add to the end (accepts any type) |
| `insert(i, x)` | Insert at an index |
| `delete(start, end)` / `deleteCharAt(i)` | Remove (end exclusive) |
| `replace(start, end, str)` | Replace a range |
| `reverse()` | Reverse in place |
| `setCharAt(i, c)` | Change one character |
| `setLength(n)` | Truncate or pad (`setLength(0)` clears) |
| `charAt`, `length`, `indexOf`, `substring` | Read-only access |
| `capacity()` / `ensureCapacity(n)` | Internal buffer size |
| `toString()` | Get an immutable `String` |

Most mutators **return `this`**, enabling **chaining**:

```java
String s = new StringBuilder().append("a").append(1).append('b').toString();   // a1b
```

**Capacity:** the internal array starts at 16 (plus the length of any initial string) and roughly **doubles** when full. If you know the final size, pass it: `new StringBuilder(1000)`.

### StringBuffer

The **thread-safe** (synchronized) twin of `StringBuilder`, with the same methods.

### String vs StringBuilder vs StringBuffer

| | `String` | `StringBuilder` | `StringBuffer` |
|---|---|---|---|
| Mutable | **No** | **Yes** | **Yes** |
| Thread-safe | Yes (immutable) | **No** | **Yes** (synchronized) |
| Speed for modifications | Slow (new object each time) | **Fastest** | Slower (locking) |
| Since | 1.0 | 1.5 | 1.0 |
| Use when | Fixed text, keys, constants | **Building text in one thread** (most cases) | Building text shared between threads (rare) |

**Analogy:** `String` is **printed paper**; `StringBuilder` is a **personal notebook** you scribble in; `StringBuffer` is a **shared notebook with a lock**: only one writer at a time, so it is slower.

In practice, even for multithreaded code, people usually build strings **locally** with `StringBuilder` and share the finished `String`.

### Gotchas

```java
StringBuilder a = new StringBuilder("hi");
StringBuilder b = new StringBuilder("hi");

a.equals(b);                          // false  (equals is NOT overridden: identity comparison)
a.toString().equals(b.toString());    // true
a.compareTo(b);                       // 0      (StringBuilder is Comparable since Java 11)
```

### Typical uses

```java
// reverse a string
String reversed = new StringBuilder("hello").reverse().toString();        // olleh

// build in a loop
StringBuilder sb = new StringBuilder();
for (int i = 1; i <= 5; i++) {
    sb.append(i);
    if (i < 5) sb.append(", ");
}
System.out.println(sb);               // 1, 2, 3, 4, 5
```

### StringJoiner and `String.join`

```java
StringJoiner sj = new StringJoiner(", ", "[", "]");    // delimiter, prefix, suffix
sj.add("A").add("B").add("C");
System.out.println(sj);                                // [A, B, C]

String.join(", ", "A", "B", "C");                      // A, B, C
list.stream().collect(Collectors.joining(", ", "[", "]"));   // from a stream
```

---

## 9. Characters and Unicode

### `char` basics

- `char` is a **16-bit unsigned** number representing a UTF-16 code unit. A `char` literal uses **single quotes**: `'a'`.
- `"a"` is a `String`; `'a'` is a `char`.

### ASCII anchors worth remembering

| Characters | Code |
|---|---|
| `'0'` to `'9'` | 48 to 57 |
| `'A'` to `'Z'` | 65 to 90 |
| `'a'` to `'z'` | 97 to 122 |

Lowercase is **32 more** than uppercase (`'a' - 'A'` is 32).

### Character arithmetic

```java
char c = 'a';
System.out.println(c + 1);               // 98        (int)
System.out.println((char) (c + 1));      // b
c++;                                     // 'b'  (increment is allowed)
c += 1;                                  // 'c'  (compound assignment narrows automatically)
// c = c + 1;                            // COMPILE ERROR: int cannot be assigned to char without a cast

int digit = '7' - '0';                   // 7        (digit char to number)
char letter = (char) ('a' + 2);          // 'c'
int idx = 'e' - 'a';                     // 4        (position in the alphabet, 0-based)
```

### `Character` helper methods

| Method | Meaning |
|---|---|
| `Character.isDigit(c)` | `0` to `9` |
| `Character.isLetter(c)` | Alphabetic |
| `Character.isLetterOrDigit(c)` | Alphanumeric |
| `Character.isUpperCase(c)` / `isLowerCase(c)` | Case tests |
| `Character.isWhitespace(c)` | Space, tab, newline... |
| `Character.toUpperCase(c)` / `toLowerCase(c)` | Convert case |
| `Character.getNumericValue(c)` | `'7'` gives `7` |

### Unicode and emoji

Some characters (emoji, rare scripts) need **two `char`s** (a surrogate pair).

```java
String s = "😀";
s.length();                         // 2   (two UTF-16 units)
s.codePointCount(0, s.length());    // 1   (one real character)
```

For text with emoji, use code-point methods (`codePoints()`, `codePointAt`). `StringBuilder.reverse()` handles surrogate pairs correctly; a naive `char[]` reverse does not.

---

## 10. Conversions

| From to | Code |
|---|---|
| String to `int` | `Integer.parseInt("42")` or `Integer.valueOf("42")` |
| String to `double` | `Double.parseDouble("3.5")` |
| String to `boolean` | `Boolean.parseBoolean("true")` (only "true", ignoring case, gives `true`) |
| number to String | `String.valueOf(42)`, `Integer.toString(42)`, `"" + 42` |
| char to String | `String.valueOf('a')`, `Character.toString('a')` |
| String to char | `s.charAt(0)` |
| String to `char[]` | `s.toCharArray()` |
| `char[]` to String | `new String(arr)`, `String.valueOf(arr)` |
| String to `byte[]` | `s.getBytes(StandardCharsets.UTF_8)` |
| `byte[]` to String | `new String(bytes, StandardCharsets.UTF_8)` |
| String to `List` | `Arrays.asList(s.split(","))` or `List.of(s.split(","))` |
| `List` to String | `String.join(",", list)` |
| String to chars as a stream | `s.chars().mapToObj(c -> (char) c)` |

```java
Integer.parseInt("42");        // 42
Integer.parseInt("+42");       // 42
Integer.parseInt(" 42");       // NumberFormatException (leading space; trim first)
Integer.parseInt("");          // NumberFormatException
Integer.parseInt("12a");       // NumberFormatException
Boolean.parseBoolean("yes");   // false
```

**Always specify the charset** when converting between `String` and `byte[]`; the platform default can differ between machines.

### `String.valueOf(null)` trap

```java
String.valueOf((Object) null);     // "null"
String.valueOf(null);              // NullPointerException: picks the char[] overload
```

---

## 11. Formatting, Escapes and Text Blocks

### `String.format`, `printf`, `formatted`

```java
String.format("%s is %d years old", "Asha", 25);   // Asha is 25 years old
System.out.printf("%.2f%n", 3.14159);              // 3.14
"Hi %s".formatted("Ravi");                          // Hi Ravi   (Java 15)
```

| Specifier | Meaning | Example | Output |
|---|---|---|---|
| `%s` | String (any object) | `"%s".formatted("hi")` | `hi` |
| `%d` | Integer | `"%d".formatted(42)` | `42` |
| `%f` | Floating point (6 decimals) | `"%f".formatted(3.5)` | `3.500000` |
| `%.2f` | 2 decimals | `"%.2f".formatted(3.14159)` | `3.14` |
| `%8.2f` | Width 8, 2 decimals | | `    3.14` |
| `%05d` | Zero-padded width 5 | `"%05d".formatted(42)` | `00042` |
| `%,d` | Thousands separator | `"%,d".formatted(1234567)` | `1,234,567` |
| `%-10s` | Left-aligned, width 10 | `"%-10s\|".formatted("Java")` | `Java      \|` |
| `%10s` | Right-aligned, width 10 | `"%10s\|".formatted("Java")` | `      Java\|` |
| `%c` | Character | `"%c".formatted('A')` | `A` |
| `%b` | Boolean | `"%b".formatted(true)` | `true` |
| `%x` | Hexadecimal | `"%x".formatted(255)` | `ff` |
| `%e` | Scientific | `"%e".formatted(12345.678)` | `1.234568e+04` |
| `%n` | Platform line break | | newline |
| `%%` | A literal percent sign | | `%` |

Use `%n` rather than `\n` for portable line endings. Mismatched types (for example `%d` with a `double`) throw `IllegalFormatConversionException`.

### Escape sequences

| Escape | Meaning |
|---|---|
| `\n` | New line |
| `\t` | Tab |
| `\\` | Backslash |
| `\"` | Double quote |
| `\'` | Single quote |
| `\r` | Carriage return |
| `\b` | Backspace |
| `\uXXXX` | Unicode character, such as `\u00A9` |

```java
System.out.println("She said \"hi\"");     // She said "hi"
System.out.println("C:\\data\\file.txt");  // C:\data\file.txt
```

### Text blocks (Java 15)

For multi-line text (JSON, SQL, HTML) without escape clutter. Delimited by `"""`.

```java
String json = """
        {
          "name": "Asha",
          "age": 25
        }
        """;
```

Result:

```
{
  "name": "Asha",
  "age": 25
}
```

(with a trailing newline)

Rules:

- The opening `"""` must be followed by a **line break**.
- **Incidental indentation is removed**: the common leading whitespace of all lines, including the closing `"""` line, is stripped. Moving the closing delimiter left keeps more indentation.
- `"` needs no escaping inside (just avoid `"""`).
- A trailing `\` **continues** the line (no newline); `\s` forces a space.

```java
String sql = """
        SELECT id, name
        FROM employees
        WHERE salary > 50000
        ORDER BY name
        """;

String oneLine = """
        Hello \
        World""";                    // "Hello World"
```

---

## 12. Regex Basics

A **regular expression** describes a text pattern. Several `String` methods accept one.

| `String` method | Regex role |
|---|---|
| `matches(regex)` | The **whole** string must match |
| `replaceAll(regex, repl)` / `replaceFirst` | Replace matches |
| `split(regex)` | Split at matches |

### Common pieces

| Pattern | Matches |
|---|---|
| `.` | Any one character (except newline) |
| `\\d` / `\\D` | Digit / non-digit |
| `\\w` / `\\W` | Word character (`a-zA-Z0-9_`) / non-word |
| `\\s` / `\\S` | Whitespace / non-whitespace |
| `[abc]` | One of `a`, `b`, `c` |
| `[a-z]`, `[A-Z0-9]` | Ranges |
| `[^abc]` | Anything **except** `a`, `b`, `c` |
| `*` | Zero or more |
| `+` | One or more |
| `?` | Zero or one |
| `{n}`, `{n,m}` | Exactly `n`, between `n` and `m` |
| `^` / `$` | Start / end of the string |
| `\|` | Or |
| `( )` | A **group** |
| `(?i)` | Ignore case |

In Java source, a backslash must be doubled: the regex `\d` is written `"\\d"`.

### Examples

```java
"9876543210".matches("[6-9]\\d{9}");          // true   (Indian mobile number pattern)
"abc123".matches("[a-zA-Z]+");                // false  (contains digits)
"asha@example.com".matches("[\\w.+-]+@[\\w-]+\\.[\\w.]+");   // true (simplified email check)

"Hello World".replaceAll("[aeiou]", "*");     // H*ll* W*rld
"a   b    c".replaceAll("\\s+", " ");          // a b c
"abc123def".replaceAll("\\D", "");             // 123      (remove non-digits)
"HELLO".replaceAll("(?i)[aeiou]", "");         // HLL
```

> `matches` requires the **entire** string to match. `"abc123".matches("\\d+")` is `false`, but `"abc123".matches(".*\\d+")` is `true`.
> An email regex like the one above is a quick sanity check, not a full RFC-compliant validator.

### `Pattern` and `Matcher`: finding and extracting

```java
Pattern p = Pattern.compile("(\\d{2})-(\\d{4})");
Matcher m = p.matcher("Order 12-3456 shipped");

if (m.find()) {
    System.out.println(m.group());     // 12-3456   (whole match)
    System.out.println(m.group(1));    // 12        (group 1)
    System.out.println(m.group(2));    // 3456      (group 2)
}
```

Compile a `Pattern` **once** and reuse it in loops; compiling is expensive.

---

## 13. Performance and Security Notes

### Performance

| Tip | Why |
|---|---|
| Use `StringBuilder` in loops | `+=` in a loop copies the whole string each time (O(n²)) |
| Pre-size it (`new StringBuilder(n)`) | Avoids repeated buffer growth |
| Prefer `equals` to `==`, and `isEmpty()` to `equals("")` | Correct and cheaper |
| Don't overuse `intern()` | Pool lookups cost time; rarely needed |
| Reuse compiled `Pattern`s | Compilation is costly |
| `String.hashCode()` is cached | Strings make excellent map keys |
| `substring` copies characters (since Java 7u6) | No hidden memory retention from the original big string |

`String.hashCode()` formula: `s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]`. Different strings can collide: `"Aa".hashCode() == "BB".hashCode()` (both 2112).

### Security: passwords

A `String` password stays in memory (possibly in the pool) until garbage collected and cannot be erased. A `char[]` can be **overwritten** after use:

```java
char[] password = console.readPassword();
try {
    authenticate(password);
} finally {
    Arrays.fill(password, '\0');         // wipe it
}
```

This is why `Console.readPassword()` and `JPasswordField.getPassword()` return `char[]`. In everyday web applications the larger risk is logging passwords; never log them.

### Switch on strings

```java
switch (command) {
    case "start" -> System.out.println("Starting");
    case "stop"  -> System.out.println("Stopping");
    default      -> System.out.println("Unknown");
}
```

Works through `hashCode` plus `equals`. A `null` selector throws `NullPointerException` (unless you use `case null` in Java 21).

---

## 14. Interview Programs, Solved

**1. Reverse a string**

```java
String reversed = new StringBuilder(s).reverse().toString();

// without StringBuilder
char[] c = s.toCharArray();
for (int i = 0, j = c.length - 1; i < j; i++, j--) {
    char t = c[i]; c[i] = c[j]; c[j] = t;
}
String r = new String(c);
```

**2. Palindrome check (ignoring case, spaces and punctuation)**

```java
static boolean isPalindrome(String s) {
    String clean = s.toLowerCase().replaceAll("[^a-z0-9]", "");
    return new StringBuilder(clean).reverse().toString().equals(clean);
}
isPalindrome("A man, a plan, a canal: Panama");   // true
```

**3. Count vowels and consonants**

```java
int vowels = 0, consonants = 0;
for (char ch : s.toLowerCase().toCharArray()) {
    if (Character.isLetter(ch)) {
        if ("aeiou".indexOf(ch) >= 0) vowels++; else consonants++;
    }
}
```

**4. Character frequency**

```java
Map<Character, Integer> freq = new LinkedHashMap<>();
for (char c : "programming".toCharArray()) {
    freq.merge(c, 1, Integer::sum);
}
// {p=1, r=2, o=1, g=2, a=1, m=2, i=1, n=1}
```

**5. First non-repeating character**

```java
static Character firstUnique(String s) {
    Map<Character, Integer> m = new LinkedHashMap<>();
    for (char c : s.toCharArray()) m.merge(c, 1, Integer::sum);
    for (var e : m.entrySet()) {
        if (e.getValue() == 1) return e.getKey();
    }
    return null;
}
firstUnique("swiss");     // 'w'
```

**6. Anagram check (lowercase letters)**

```java
static boolean isAnagram(String a, String b) {
    if (a.length() != b.length()) return false;
    int[] count = new int[26];
    for (int i = 0; i < a.length(); i++) {
        count[a.charAt(i) - 'a']++;
        count[b.charAt(i) - 'a']--;
    }
    for (int n : count) if (n != 0) return false;
    return true;
}
isAnagram("listen", "silent");   // true
```

Alternative: sort both `char[]` arrays and compare with `Arrays.equals`.

**7. Remove duplicate characters, keep order**

```java
String unique = s.chars().distinct()
                 .mapToObj(c -> String.valueOf((char) c))
                 .collect(Collectors.joining());
// "programming" -> "progamin"
```

**8. Reverse the words of a sentence**

```java
String[] words = s.trim().split("\\s+");
Collections.reverse(Arrays.asList(words));
String result = String.join(" ", words);
// "hello world java" -> "java world hello"
```

**9. Capitalize the first letter of each word**

```java
String result = Arrays.stream(s.split(" "))
    .map(w -> w.isEmpty() ? w : Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
    .collect(Collectors.joining(" "));
// "hELLO wORLD" -> "Hello World"
```

**10. Check if one string is a rotation of another**

```java
static boolean isRotation(String a, String b) {
    return a.length() == b.length() && (a + a).contains(b);
}
isRotation("abcd", "cdab");   // true
```

**11. Compress a string**

```java
static String compress(String s) {
    StringBuilder sb = new StringBuilder();
    int count = 1;
    for (int i = 1; i <= s.length(); i++) {
        if (i < s.length() && s.charAt(i) == s.charAt(i - 1)) {
            count++;
        } else {
            sb.append(s.charAt(i - 1)).append(count);
            count = 1;
        }
    }
    return sb.toString();
}
compress("aaabbc");   // a3b2c1
```

**12. Count occurrences of a substring**

```java
static int countOccurrences(String s, String sub) {
    int count = 0, idx = 0;
    while ((idx = s.indexOf(sub, idx)) != -1) {
        count++;
        idx += sub.length();
    }
    return count;
}
countOccurrences("abababa", "aba");   // 2  (non-overlapping)
```

**13. Check whether all characters are unique**

```java
boolean allUnique = s.chars().distinct().count() == s.length();
```

**14. Count words**

```java
int words = s.isBlank() ? 0 : s.trim().split("\\s+").length;
```

---

## 15. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Comparing strings with `==` | Compares references; works only by pool luck | `equals()` |
| `s.toUpperCase();` without using the result | Strings are immutable | `s = s.toUpperCase();` |
| `variable.equals("x")` when the variable may be `null` | `NullPointerException` | `"x".equals(variable)` or `Objects.equals` |
| `s += x` in a big loop | O(n²) time, lots of garbage | `StringBuilder` |
| `split(".")` or `split("|")` | Regex metacharacters | `split("\\.")`, `split("\\|")` |
| Expecting `split` to keep trailing empties | They are dropped | `split(regex, -1)` |
| `replaceAll(".", "x")` meaning "replace dots" | `.` matches every character | `replace(".", "x")` |
| `substring(begin, end)` thinking `end` is inclusive | Off-by-one | `end` is **exclusive** |
| `charAt(s.length())` | `StringIndexOutOfBoundsException` | Last index is `length() - 1` |
| `'a' + 'b'` expecting `"ab"` | Integer addition (195) | `"" + 'a' + 'b'` or `String.valueOf` |
| `c = c + 1` for a `char` | Compile error | `c += 1` or `(char) (c + 1)` |
| `a.equals(b)` on `StringBuilder` | Identity comparison | `a.toString().equals(b.toString())` |
| `Integer.parseInt(" 42")` | `NumberFormatException` | `trim()` first |
| `str.length()` vs `str.length` | Wrong syntax | Strings use `length()`, arrays use `length` |
| Mixing `String.valueOf(null)` | `NullPointerException` (char[] overload) | Cast: `(Object) null`, or avoid |
| `getBytes()` without a charset | Platform-dependent | `getBytes(StandardCharsets.UTF_8)` |
| `toLowerCase()` for identifiers in odd locales | Turkish `I` problem | `toLowerCase(Locale.ROOT)` |
| Storing passwords in `String` | Cannot be wiped from memory | `char[]` and `Arrays.fill` |
| `matches` expecting a partial match | Requires a full match | Use `find()` or add `.*` |
| Compiling a regex inside a loop | Slow | Precompile `Pattern` |

---

## 16. Output Prediction Puzzles

**Puzzle 1: pool and `==`**

```java
String a = "hello";
String b = "hello";
String c = new String("hello");
String d = c.intern();

System.out.println(a == b);
System.out.println(a == c);
System.out.println(a.equals(c));
System.out.println(a == d);
```

**Puzzle 2: concatenation and constants**

```java
String a = "hello";
String x = "hel" + "lo";
String p = "hel";
String y = p + "lo";
final String q = "hel";
String z = q + "lo";

System.out.println(a == x);
System.out.println(a == y);
System.out.println(a == z);
```

**Puzzle 3: immutability**

```java
String s = "java";
s.concat("!");
s.toUpperCase();
System.out.println(s);
```

**Puzzle 4: reference copy**

```java
String s1 = "abc";
String s2 = s1;
s1 = "xyz";
System.out.println(s2);
```

**Puzzle 5: `+` evaluation**

```java
System.out.println(1 + 2 + "3" + 4 + 5);
System.out.println('a' + 'b' + "c");
System.out.println("c" + 'a' + 'b');
```

**Puzzle 6: `split`**

```java
System.out.println("a,b,,c,,".split(",").length);
System.out.println("a,b,,c,,".split(",", -1).length);
System.out.println("a.b".split(".").length);
```

**Puzzle 7: `StringBuilder` equality**

```java
System.out.println(new StringBuilder("a").equals(new StringBuilder("a")));
System.out.println(new StringBuilder("a").toString().equals("a"));
```

**Puzzle 8: `substring`**

```java
String s = "hello";
System.out.println(s.substring(1, 3));
System.out.println("[" + s.substring(5) + "]");
System.out.println(s.indexOf("z"));
```

**Puzzle 9: `compareTo`**

```java
System.out.println("apple".compareTo("banana"));
System.out.println("apple".compareTo("app"));
System.out.println("Z".compareTo("a"));
```

**Puzzle 10: `hashCode` collision**

```java
System.out.println("Aa".hashCode() == "BB".hashCode());
System.out.println("Aa".equals("BB"));
```

**Puzzle 11: `null` handling**

```java
String s = null;
System.out.println(s + "x");
System.out.println("x".equals(s));
System.out.println(s.length());
```

**Puzzle 12: char arithmetic**

```java
char c = 'a';
c++;
System.out.println(c);
System.out.println(c + 1);
System.out.println((char) (c + 1));
```

**Answers**

1. **`true`, `false`, `true`, `true`.** `a` and `b` share the pooled object; `c` is a separate heap object; `intern()` returns the pooled one, so `d == a`.
2. **`true`, `false`, `true`.** `"hel" + "lo"` is a compile-time constant, folded to `"hello"`. `p + "lo"` is computed at run time (a new object). `q` is a `final` constant variable, so `q + "lo"` is folded too.
3. **`java`.** The calls return new strings that were discarded. The original is never modified.
4. **`abc`.** `s1` was reassigned to a different object. `s2` still points to the old one.
5. **`3345`**, **`195c`**, **`cab`**. Evaluation is left to right: `1 + 2` is `3`, then string concatenation takes over. `'a' + 'b'` is integer addition (97 + 98).
6. **`4`**, **`6`**, **`0`**. Trailing empty strings are removed by default; `-1` keeps them; `"."` is a regex matching every character.
7. **`false`** (`StringBuilder` does not override `equals`) and **`true`**.
8. **`el`**, **`[]`**, **`-1`**. `end` is exclusive; `substring(5)` on a length-5 string gives an empty string; `indexOf` returns `-1` when absent.
9. **`-1`** (`'a'` minus `'b'`), **`2`** (length difference), **`-7`** (`'Z'` is 90 and `'a'` is 97).
10. **`true`**, then **`false`**. Different strings can share a hash code; `equals` is what decides equality.
11. **`nullx`**, **`false`**, then a **`NullPointerException`** on `s.length()`.
12. **`b`**, **`99`**, **`c`**. `c++` stays a `char`; `c + 1` promotes to `int`.

---

## 17. Interview Questions

**1. What is a String in Java?** An immutable sequence of characters, represented by the `final` class `java.lang.String`.

**2. Why is String immutable?** For safe sharing through the string pool, security, thread safety, cached hash codes, and reliable use as map keys and class names.

**3. What is the string pool?** A heap region holding one copy of each distinct string literal, so identical literals share an object.

**4. `String s = "a"` vs `new String("a")`?** The literal reuses a pooled object. `new` always creates a new heap object (and may also add the literal to the pool).

**5. How many objects does `new String("abc")` create?** One or two: the heap object, plus the pooled literal if it was not already there.

**6. `==` vs `equals()` for strings?** `==` compares references; `equals()` compares content. Use `equals()`.

**7. What does `intern()` do?** Returns the canonical pooled instance of the string, adding it if needed.

**8. String vs StringBuilder vs StringBuffer?** Immutable; mutable and fast but not thread-safe; mutable and synchronized (slower).

**9. Why is `StringBuilder` faster than `+` in a loop?** `+` creates a new String and copies everything each iteration; `StringBuilder` appends into a growing buffer.

**10. Is `StringBuilder` thread-safe?** No. `StringBuffer` is.

**11. Can we extend `String`?** No; it is `final`.

**12. Why is String a good `HashMap` key?** It is immutable (the hash never changes) and its `hashCode` is cached.

**13. What is `String.hashCode()`'s formula?** `s[0]*31^(n-1) + ... + s[n-1]`, computed once and cached.

**14. What does `compareTo` return?** The difference of the first differing characters, or the length difference, or `0` when equal.

**15. `equals` vs `equalsIgnoreCase` vs `compareTo`?** Exact equality; equality ignoring case; ordering.

**16. How do you check for null-safe equality?** `"literal".equals(s)` or `Objects.equals(a, b)`.

**17. `isEmpty()` vs `isBlank()`?** `isEmpty` means length 0; `isBlank` also treats whitespace-only strings as blank (Java 11).

**18. `trim()` vs `strip()`?** `trim` removes characters up to space; `strip` is Unicode-aware.

**19. `replace` vs `replaceAll`?** `replace` is literal; `replaceAll` takes a regex.

**20. What is the exclusive/inclusive rule of `substring`?** Begin inclusive, end exclusive.

**21. How does `split` treat trailing empty strings?** They are removed unless you pass a negative limit.

**22. How do you reverse a string?** `new StringBuilder(s).reverse().toString()`.

**23. How do you convert a String to `int` and handle bad input?** `Integer.parseInt` inside try/catch for `NumberFormatException`.

**24. What is the difference between `null` and `""`?** `null` is no object; `""` is an empty string object.

**25. How are strings concatenated internally?** A `StringBuilder` before Java 9; `invokedynamic` (`StringConcatFactory`) since Java 9.

**26. What are compact strings?** A Java 9 optimization storing Latin-1 strings with 1 byte per character instead of 2.

**27. What are text blocks?** Triple-quoted multi-line string literals (Java 15) with automatic indentation stripping.

**28. How do you format a string?** `String.format`, `printf`, or `"...".formatted(...)` with specifiers like `%s`, `%d`, `%.2f`.

**29. Why prefer `char[]` for passwords?** It can be overwritten after use, whereas a `String` lingers in memory until garbage collection.

**30. Can a `switch` use strings?** Yes, since Java 7, through `hashCode` and `equals`. A `null` selector throws unless `case null` is used (Java 21).

**31. What is `String.valueOf(null)`?** A `NullPointerException`, because it resolves to the `char[]` overload; `String.valueOf((Object) null)` gives `"null"`.

**32. `charAt` on an invalid index?** `StringIndexOutOfBoundsException`.

**33. What is `CharSequence`?** An interface implemented by `String`, `StringBuilder` and `StringBuffer`, exposing `length`, `charAt`, `subSequence`.

**34. How can you check if two strings are anagrams?** Compare sorted character arrays, or count characters in an `int[26]`.

**35. How do you count occurrences of a character?** A loop with `charAt`, or `s.chars().filter(c -> c == 'a').count()`.

---

## 18. Cheat Sheet

| Task | Code |
|---|---|
| Length | `s.length()` |
| Character at index | `s.charAt(i)` |
| Substring | `s.substring(begin, endExclusive)` |
| Find | `s.indexOf("x")` (`-1` if missing), `s.contains("x")` |
| Compare content | `s.equals(t)`, `s.equalsIgnoreCase(t)` |
| Compare order | `s.compareTo(t)` |
| Null-safe equals | `"lit".equals(s)` or `Objects.equals(a, b)` |
| Case | `s.toUpperCase()`, `s.toLowerCase()` |
| Trim | `s.trim()`, `s.strip()` |
| Replace literal | `s.replace("a", "b")` |
| Replace regex | `s.replaceAll("\\d+", "#")` |
| Split | `s.split("\\s+")` (regex!) |
| Join | `String.join(", ", items)` |
| Repeat | `s.repeat(n)` |
| Char array | `s.toCharArray()` |
| Number to String | `String.valueOf(n)` |
| String to number | `Integer.parseInt(s)` |
| Build in a loop | `StringBuilder.append(...)` then `toString()` |
| Reverse | `new StringBuilder(s).reverse().toString()` |
| Format | `"%s has %d items".formatted(name, n)` |
| Multi-line text | `"""` text block `"""` |

**Core rules**

```
1. Strings are immutable: methods return NEW strings; use the result.
2. Compare content with equals(), never ==.
3. Many concatenations in a loop -> StringBuilder.
4. split / replaceAll / matches take REGEX; escape . | + * ? ( ) [ ] \ ^ $ when you mean them literally.
5. substring end is EXCLUSIVE; last index is length()-1.
6. + goes left to right: numbers add until a String appears.
```

---

## 19. Practice Tasks with Solutions

**1. Count words, ignoring extra spaces**

```java
static int wordCount(String s) {
    return s.isBlank() ? 0 : s.trim().split("\\s+").length;
}
wordCount("  hello   world  ");     // 2
```

**2. Does the string contain only digits?**

```java
boolean digitsOnly = !s.isEmpty() && s.chars().allMatch(Character::isDigit);
// or: s.matches("\\d+")
```

**3. Mask a card number (show the last 4 digits)**

```java
static String mask(String card) {
    String digits = card.replaceAll("\\D", "");
    int keep = Math.min(4, digits.length());
    return "*".repeat(digits.length() - keep) + digits.substring(digits.length() - keep);
}
mask("1234-5678-9012-3456");        // ************3456
```

**4. `snake_case` to `camelCase`**

```java
static String toCamel(String s) {
    String[] parts = s.split("_");
    StringBuilder sb = new StringBuilder(parts[0]);
    for (int i = 1; i < parts.length; i++) {
        if (!parts[i].isEmpty()) {
            sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
        }
    }
    return sb.toString();
}
toCamel("hello_world_example");     // helloWorldExample
```

**5. Longest word in a sentence**

```java
String longest = Arrays.stream(s.split("\\s+"))
    .max(Comparator.comparingInt(String::length))
    .orElse("");
```

**6. Strong password check** (at least 8 characters, an upper-case letter, a lower-case letter, a digit and a symbol)

```java
static boolean isStrong(String p) {
    return p.length() >= 8
        && p.chars().anyMatch(Character::isUpperCase)
        && p.chars().anyMatch(Character::isLowerCase)
        && p.chars().anyMatch(Character::isDigit)
        && p.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
}
```

**7. Remove all vowels**

```java
String noVowels = s.replaceAll("(?i)[aeiou]", "");
```

**8. Word frequency (using streams)**

```java
Map<String, Long> freq = Arrays.stream(text.toLowerCase().split("\\W+"))
    .filter(w -> !w.isEmpty())
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
```

**9. Swap two strings without a temporary variable**

```java
String a = "hello", b = "java";
a = a + b;                             // hellojava
b = a.substring(0, a.length() - b.length());   // hello
a = a.substring(b.length());           // java
```

**10. Is the string a valid identifier-style name?** (letter or underscore first, then letters, digits or underscores)

```java
boolean valid = name.matches("[A-Za-z_][A-Za-z0-9_]*");
```

### Challenge questions

1. After `String s = "a"; s += "b"; s += "c";`, how many String objects were created, and what happens to the intermediate ones?
2. Why is `if (s == "yes")` sometimes true and sometimes false in the same program?
3. You need to build a 1 MB report line by line from many threads. Would you use `StringBuffer`, and what is the better design?

**Answers**

1. Three String values exist over time (`"a"`, `"ab"`, `"abc"`), plus any temporary builders. The intermediate ones become unreachable and are garbage collected (the literal `"a"` may stay in the pool).
2. `==` compares references. It is `true` if both come from the pool (literal or `intern()`), and `false` if one was built at run time (user input, concatenation, `new String`). Always use `equals`.
3. `StringBuffer` would work but serializes all appends. A better design: let **each thread build its own part with a `StringBuilder`**, then combine the finished strings (or collect them in a thread-safe queue and join once).

---

*End of notes.*
