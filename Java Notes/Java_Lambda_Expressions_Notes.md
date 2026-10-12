# Java Lambda Expressions and Functional Interfaces

A complete set of notes: what lambdas are, why they exist, how they work, and how to use the predefined utility functional interfaces.

---

## Table of Contents

1. [The Problem Lambdas Solve](#1-the-problem-lambdas-solve)
2. [What Is a Lambda Expression?](#2-what-is-a-lambda-expression)
3. [From Method to Lambda, Step by Step](#3-from-method-to-lambda-step-by-step)
4. [Simplification Rules](#4-simplification-rules)
5. [More Conversion Examples](#5-more-conversion-examples)
6. [Functional Interfaces](#6-functional-interfaces)
7. [Using a Lambda with a Functional Interface](#7-using-a-lambda-with-a-functional-interface)
8. [Why Predefined Utility Functional Interfaces Exist](#8-why-predefined-utility-functional-interfaces-exist)
9. [Predicate](#9-predicate)
10. [Function](#10-function)
11. [Consumer](#11-consumer)
12. [Supplier](#12-supplier)
13. [Bi-Versions: BiPredicate, BiFunction, BiConsumer](#13-bi-versions-bipredicate-bifunction-biconsumer)
14. [Quick Comparison Table](#14-quick-comparison-table)
15. [Common Mistakes](#15-common-mistakes)
16. [Cheat Sheet](#16-cheat-sheet)
17. [Practice Questions](#17-practice-questions)

---

## 1. The Problem Lambdas Solve

### Real-life analogy

Imagine you run a small shop and need to tell an employee: "Check if a number is even."

- **Old way:** You write a formal instruction manual with a title, a department, and a job description, just to say one small thing.
- **Lambda way:** You just say "number divisible by 2 means yes." A short, direct instruction.

### In Java

Before Java 8, passing even a tiny piece of behavior (like "check if even") required:

- creating a full class, or
- writing a bulky anonymous inner class,

plus a method with a name, a return type, and an access specifier. That is a lot of ceremony for one line of logic.

**Lambda expressions (Java 8)** let you write just the useful part: the parameters and the logic.

### Why it matters

| Benefit | Explanation |
|---|---|
| Less code | No boilerplate class or method declaration |
| Readability | The logic is visible right where it is used |
| Functional style | Behavior can be passed around like data |
| Foundation for Streams | Streams, `forEach`, `filter`, `map` all depend on lambdas |

---

## 2. What Is a Lambda Expression?

> A **lambda expression** is an **anonymous method**: a method without a name, return type, or access specifier.

A normal method has four parts:

```java
public int add(int a, int b) {
    return a + b;
}
```

| Part | In this method |
|---|---|
| Access specifier | `public` |
| Return type | `int` |
| Name | `add` |
| Parameters and body | `(int a, int b) { return a + b; }` |

A lambda **removes the first three** and keeps only the parameters and body.

### What "anonymous" means

Think of a **disposable cup** versus **your personal mug with your name on it**.

- A named method is your personal mug: reusable, called by name.
- A lambda is a disposable cup: used once, right where you need it, no name.

### Why drop the name, return type, and access specifier?

- **Name:** you never call a lambda by name. It is attached to a functional interface (covered later).
- **Return type:** the compiler works it out from the code.
- **Access specifier:** the lambda lives inside the place where it is used, so it does not need its own visibility rule.

---

## 3. From Method to Lambda, Step by Step

Start with this normal method:

```java
public int add(int a, int b) {
    return a + b;
}
```

### Step 1: remove access specifier, return type, and name

```java
(int a, int b) -> {
    return a + b;
}
```

- `(int a, int b)` is the parameter list.
- `->` is the **arrow operator**. Read it as "goes to" or "produces".
- `{ ... }` is the body.

### Lambda syntax

```
(parameters) -> { body }
```

| Piece | Meaning |
|---|---|
| `(parameters)` | Inputs to the logic |
| `->` | Separates inputs from logic |
| `{ body }` | What to do with the inputs |

---

## 4. Simplification Rules

Java lets you shorten lambdas in four steps. Each rule is applied only when its condition is met.

### Rule 1: Parameter data types are not required

The compiler already knows the types from the functional interface the lambda is assigned to (this is called **type inference**).

```java
// Before
(int a, int b) -> {
    return a + b;
}

// After
(a, b) -> {
    return a + b;
}
```

**Why?** If the interface says `add(int a, int b)`, writing `int` again is redundant, like repeating your own name every time you speak.

### Rule 2: Braces are not required if the body is a single statement

```java
(a, b) -> return a + b;     // see note below
```

> **Important note:** In real Java, you cannot combine "no braces" with the `return` keyword. If you remove the braces, you must also remove `return` (Rule 3). The original notes show this as an intermediate step to explain the idea. The legal forms are:
>
> - with braces and `return`: `(a, b) -> { return a + b; }`
> - without braces and without `return`: `(a, b) -> a + b`
>
> Mixing "no braces + `return`" gives a **compile error**.

### Rule 3: If the body is a single expression, `return` is not required

When you drop the braces, the value of the expression is returned automatically.

```java
(a, b) -> a + b;
```

**Why?** With only one expression there is nothing else the result could be, so `return` is implied.

### Rule 4: If there is only one parameter, parentheses are not required

```java
// Before
(n) -> n % 2 == 0;

// After
n -> n % 2 == 0;
```

**Careful:** parentheses are still **required** when there are:

- zero parameters: `() -> System.out.println("Hi")`
- two or more parameters: `(a, b) -> a + b`

### Summary of the shrinking process

```
(int a, int b) -> { return a + b; }     full form
(a, b)         -> { return a + b; }     Rule 1: no types
(a, b)         -> a + b                 Rules 2 and 3: no braces, no return
```

For a single parameter:

```
(int n) -> { return n % 2 == 0; }
(n)     -> { return n % 2 == 0; }
(n)     -> n % 2 == 0
n       -> n % 2 == 0                   Rule 4: no parentheses
```

---

## 5. More Conversion Examples

### Example A: Check even number

**Normal method**

```java
public Boolean check(int n) {
    if (n % 2 == 0)
        return true;
    else
        return false;
}
```

**Lambda with a block body**

```java
(n) -> {
    if (n % 2 == 0)
        return true;
    else
        return false;
}
```

**Why the block is needed here:** there are multiple statements (`if` and `else` with separate returns), so braces and `return` stay.

**Shortened:** the whole `if/else` just returns the result of a condition, so use the condition directly.

```java
(n) -> { n % 2 == 0 }    // INVALID: needs return or no braces
(n) -> n % 2 == 0        // valid
n -> n % 2 == 0          // valid and shortest
```

> **Note:** `(n) -> { n % 2 == 0 }` as written in the original notes is not valid Java. Inside braces you must write `return n % 2 == 0;`. Either keep the braces and `return`, or drop both.

**Real-life picture:** a doorman checks "is your ticket number even?" and only says yes or no.

### Example B: Factorial

**Normal method**

```java
public int fact(int n) {
    int f = 1;
    for (int i = 1; i <= n; i++)
        f = f * i;
    return f;
}
```

**Lambda**

```java
n -> {
    int f = 1;
    for (int i = 1; i <= n; i++)
        f = f * i;
    return f;
}
```

**Why the braces stay:** the body has multiple statements (a variable, a loop, a return). The shortened form is only possible when the body is a single expression.

**Real-life picture:** a factorial is like counting the number of ways to arrange `n` books on a shelf. The loop multiplies as it goes.

### Rule of thumb

| Body | Braces and `return` |
|---|---|
| Single expression | Not needed |
| Multiple statements | Needed |

---

## 6. Functional Interfaces

### Definition

> A **functional interface** is an interface that has **exactly one abstract method**.

Lambda expressions are used to **implement functional interfaces**.

### Example

```java
interface Maths {
    int add(int a, int b);
}
```

`Maths` has one abstract method (`add`), so it is a functional interface.

### Why exactly one?

A lambda has no name. If the interface had two abstract methods, Java could not tell which method the lambda is supposed to implement.

**Analogy:** a TV remote with a single button labeled "Power". You press it and it is obvious what happens. A remote with five buttons would need labels to tell them apart; a lambda has no label.

### The `@FunctionalInterface` annotation

```java
@FunctionalInterface
public interface Maths {
    public int add(int a, int b);
}
```

- Optional, but strongly recommended.
- Tells the compiler: "If someone adds a second abstract method, show an error."
- Documents your intent for other developers.

> The original notes spell it `@FunctionInterface`; the correct spelling is **`@FunctionalInterface`**.

### What is allowed in a functional interface

| Allowed | Not allowed (would break the rule) |
|---|---|
| Exactly one abstract method | Two or more abstract methods |
| Any number of `default` methods | |
| Any number of `static` methods | |
| Methods inherited from `Object` such as `equals` | |

### Known examples from the notes

- **`Runnable`**: one abstract method `run()`.
- **`Cloneable`**: a marker interface with no methods, so it is **not** a functional interface (the notes list it as a well-known interface, spelled `Clonenable`).
- **`Comparable`**: one abstract method `compareTo()`.

---

## 7. Using a Lambda with a Functional Interface

```java
@FunctionalInterface
public interface Maths {
    public int add(int a, int b);
}

Maths m = (a, b) -> a + b;
System.out.println(m.add(10, 20));   // 30
```

### What happens step by step

1. `Maths m` declares a reference of the functional interface type.
2. `(a, b) -> a + b` provides the implementation of the single abstract method `add`.
3. Java infers that `a` and `b` are `int` (from the interface) and the result is `int`.
4. `m.add(10, 20)` runs the lambda body and returns `30`.

### Without lambda (anonymous class), for comparison

```java
Maths m = new Maths() {
    public int add(int a, int b) {
        return a + b;
    }
};
```

Six lines of ceremony versus one line.

### Same interface, different behavior

```java
Maths addition = (a, b) -> a + b;
Maths multiply = (a, b) -> a * b;

System.out.println(addition.add(4, 5));   // 9
System.out.println(multiply.add(4, 5));   // 20 (the name is just a label)
```

**Real-life analogy:** a vending machine slot is the interface (insert coin, get item). What actually comes out depends on what is plugged in behind it. A lambda is the thing you plug in.

---

## 8. Why Predefined Utility Functional Interfaces Exist

### The situation in the notes

You often write small helper methods in a utility class:

```java
class Util {
    public Boolean isEven(int n) {
        if (n % 2 == 0)
            return true;
        else
            return false;
    }

    public Boolean isPrime(int n) {
        // logic for checking prime
        return true;   // if prime, else false
    }

    public int fact(int x) {
        // write code for factorial
        return f;
    }

    public int countVowels(String word) {
        // write code to count vowels
        return count;
    }
}
```

Usage:

```java
Util utils = new Util();
System.out.println(utils.isEven(10));
```

> The notes write `class Util` but create `Utils utils = new Utils();`. The class name must match in real code.

### The pain points

- A whole class is needed just to host tiny methods.
- Methods are tied to the class and cannot be passed around easily.
- Every project invents its own tiny interface (`Checker`, `Calculator`, `Printer`...) for the same ideas.

### The solution

Java ships ready-made functional interfaces in the package `java.util.function`, covering the most common shapes of logic. You do not invent your own; you just pick the one matching the shape.

### The shapes, in plain words

| Question | Interface |
|---|---|
| "Is this true or false?" | **Predicate** |
| "Convert this input into something else" | **Function** |
| "Do something with this, return nothing" | **Consumer** |
| "Give me something without any input" | **Supplier** |

Plus the "Bi" versions that take **two** inputs.

### Import needed

```java
import java.util.function.*;
```

---

## 9. Predicate

### Definition

A **Predicate** is a functional interface with exactly one abstract method, `test`. It takes **one argument of any type** and returns a **boolean**.

```java
@FunctionalInterface
public interface Predicate<T> {
    public boolean test(T t);
}
```

> The notes write `Boolean` (the wrapper class). The real JDK signature returns the primitive `boolean`.

### Understanding `<T>`

`T` is a **generic type placeholder**. It means "whatever type you choose".

- `Predicate<Integer>` tests an `Integer`.
- `Predicate<String>` tests a `String`.

> Generics require **wrapper classes**, not primitives. Write `Predicate<Integer>`, never `Predicate<int>`.

### Real-life analogy

A **security guard at a gate** has one job: look at what you show and answer yes or no.

- Guard 1: "Is the ticket number even?"
- Guard 2: "Is your age 18 or above?"

The guard never gives you anything; he only says allow or deny. That is a Predicate.

### Example from the notes

```java
Predicate<Integer> p1 = x -> x % 2 == 0;           // is the number even?
Predicate<Integer> p2 = y -> y >= 20;              // is the number at least 20?
Predicate<String>  p3 = str -> str.length() >= 10; // is the string 10+ chars?

System.out.println(p1.test(10));                    // true
System.out.println(p1.test(15));                    // false
System.out.println(p2.test(20));                    // true
System.out.println(p2.test(10));                    // false
System.out.println(p3.test("Hello"));               // false (length 5)
System.out.println(p3.test("Programming in java")); // true  (length 19)
```

### Tracing each line

| Call | Calculation | Result |
|---|---|---|
| `p1.test(10)` | `10 % 2 == 0` gives `0 == 0` | `true` |
| `p1.test(15)` | `15 % 2 == 0` gives `1 == 0` | `false` |
| `p2.test(20)` | `20 >= 20` | `true` |
| `p2.test(10)` | `10 >= 20` | `false` |
| `p3.test("Hello")` | length 5, `5 >= 10` | `false` |
| `p3.test("Programming in java")` | length 19, `19 >= 10` | `true` |

### Replacing the Util class

Instead of a `Util` class with `isEven`, you just write:

```java
Predicate<Integer> isEven = n -> n % 2 == 0;
System.out.println(isEven.test(10));   // true
```

No class, no object creation, and the logic can be passed into other methods.

### Why it is useful: filtering

```java
List<Integer> numbers = Arrays.asList(5, 10, 15, 20, 25, 30);
Predicate<Integer> isEven = n -> n % 2 == 0;

numbers.stream()
       .filter(isEven)
       .forEach(System.out::println);   // 10, 20, 30
```

This is how a Predicate is used in practice: "keep only items where the test passes".

### Bonus: combining Predicates

Predicate has built-in default methods:

```java
Predicate<Integer> even = x -> x % 2 == 0;
Predicate<Integer> big  = x -> x >= 20;

even.and(big).test(30);   // true  (even AND big)
even.or(big).test(25);    // true  (25 is big, even though odd)
even.negate().test(10);   // false (NOT even)
```

**Real-life picture:** a club entry rule "age 18+ **and** has ticket". Combining two guards.

---

## 10. Function

### Definition

A **Function** takes **one input** of type `T` and **returns a result** of type `R` (possibly a different type). Its single abstract method is `apply`.

```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
```

### Real-life analogy

A **juicer**: you put in an orange (input) and get juice (output). The input and output are different things.

### Examples

```java
Function<Integer, Integer> square = x -> x * x;
System.out.println(square.apply(5));        // 25

Function<String, Integer> length = s -> s.length();
System.out.println(length.apply("Hello")); // 5
```

### Replacing the Util class methods

The `fact` and `countVowels` methods from the notes fit Function:

```java
Function<Integer, Integer> fact = n -> {
    int f = 1;
    for (int i = 1; i <= n; i++)
        f = f * i;
    return f;
};
System.out.println(fact.apply(5));   // 120

Function<String, Integer> countVowels = word -> {
    int count = 0;
    for (char c : word.toLowerCase().toCharArray())
        if ("aeiou".indexOf(c) != -1)
            count++;
    return count;
};
System.out.println(countVowels.apply("Education"));   // 5
```

### Chaining with `andThen`

```java
Function<Integer, Integer> doubleIt = x -> x * 2;
Function<Integer, Integer> addTen   = x -> x + 10;

System.out.println(doubleIt.andThen(addTen).apply(5));   // (5*2)+10 = 20
System.out.println(doubleIt.compose(addTen).apply(5));   // (5+10)*2 = 30
```

- `andThen`: run this first, then the other.
- `compose`: run the other first, then this.

**Real-life picture:** an assembly line. Raw material goes to the cutter, then the painter, and each machine is a Function.

---

## 11. Consumer

### Definition

A **Consumer** takes **one input** and **returns nothing** (`void`). It "consumes" the value to do some action. Its method is `accept`.

```java
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
```

### Real-life analogy

A **shredder**: you feed in a paper and it does something with it, but nothing comes back. Or a **printer**: you send a document and it prints, with no value returned to you.

### Examples

```java
Consumer<String> printer = s -> System.out.println("Hello, " + s);
printer.accept("Mohit");   // Hello, Mohit

List<String> names = Arrays.asList("Asha", "Ravi", "Neha");
names.forEach(n -> System.out.println(n));   // forEach takes a Consumer
```

### Chaining with `andThen`

```java
Consumer<String> c1 = s -> System.out.println("Log: " + s);
Consumer<String> c2 = s -> System.out.println("Length: " + s.length());

c1.andThen(c2).accept("Java");
// Log: Java
// Length: 4
```

---

## 12. Supplier

### Definition

A **Supplier** takes **no input** and **returns a value**. Its method is `get`.

```java
@FunctionalInterface
public interface Supplier<T> {
    T get();
}
```

### Real-life analogy

A **water dispenser** or **ATM with a fixed amount**: you press the button (no input) and get something out.

### Examples

```java
Supplier<Double> randomValue = () -> Math.random();
System.out.println(randomValue.get());

Supplier<String> greeting = () -> "Welcome to Java";
System.out.println(greeting.get());

Supplier<LocalDate> today = () -> LocalDate.now();
```

### Why it is useful

- Generating values on demand (random numbers, timestamps, IDs).
- **Lazy evaluation**: the value is created only when `get()` is called, not before.

Note the empty parentheses `()`: the zero-parameter case where parentheses are mandatory.

---

## 13. Bi-Versions: BiPredicate, BiFunction, BiConsumer

The "Bi" interfaces are the same as their single-input versions, but take **two inputs**.

| Interface | Method | Takes | Returns |
|---|---|---|---|
| `BiPredicate<T, U>` | `test(T t, U u)` | two inputs | `boolean` |
| `BiFunction<T, U, R>` | `apply(T t, U u)` | two inputs | result of type `R` |
| `BiConsumer<T, U>` | `accept(T t, U u)` | two inputs | nothing |

### BiPredicate

```java
BiPredicate<Integer, Integer> isGreater = (a, b) -> a > b;
System.out.println(isGreater.test(10, 5));    // true
System.out.println(isGreater.test(3, 8));     // false
```

**Real-life:** "Is the student's marks (first input) above the pass mark (second input)?"

### BiFunction

```java
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
System.out.println(add.apply(10, 20));        // 30

BiFunction<String, String, String> join = (a, b) -> a + " " + b;
System.out.println(join.apply("Hello", "World"));   // Hello World
```

**Real-life:** a calculator: two numbers in, one result out. This is exactly the earlier `Maths` interface, already built in.

### BiConsumer

```java
BiConsumer<String, Integer> show = (name, age) ->
        System.out.println(name + " is " + age + " years old");
show.accept("Mohit", 21);

Map<String, Integer> marks = new HashMap<>();
marks.put("Asha", 90);
marks.put("Ravi", 80);
marks.forEach((k, v) -> System.out.println(k + " -> " + v));   // Map.forEach takes a BiConsumer
```

**Real-life:** printing a receipt line with item name and price.

---

## 14. Quick Comparison Table

| Interface | Method | Input | Output | Think of it as |
|---|---|---|---|---|
| Predicate | `test` | 1 | boolean | Gatekeeper (yes/no) |
| Function | `apply` | 1 | any type | Converter or machine |
| Consumer | `accept` | 1 | nothing | Shredder or printer |
| Supplier | `get` | 0 | any type | Dispenser |
| BiPredicate | `test` | 2 | boolean | Gatekeeper comparing two things |
| BiFunction | `apply` | 2 | any type | Calculator |
| BiConsumer | `accept` | 2 | nothing | Receipt printer |

### How to choose

```
Need a true/false answer?     -> Predicate (or BiPredicate)
Need to transform a value?    -> Function (or BiFunction)
Need to just perform action?  -> Consumer (or BiConsumer)
Need to produce a value?      -> Supplier
```

---

## 15. Common Mistakes

| Mistake | Why it is wrong | Fix |
|---|---|---|
| `Predicate<int>` | Generics cannot use primitives | `Predicate<Integer>` |
| `(a, b) -> return a + b;` | `return` is not allowed without braces | `(a, b) -> a + b` or `(a, b) -> { return a + b; }` |
| `n -> { n % 2 == 0 }` | Braces need a `return` statement | `n -> n % 2 == 0` |
| Interface with 2 abstract methods used for a lambda | Not a functional interface | Keep exactly one abstract method |
| `() -> ...` written as `-> ...` | Zero parameters need `()` | Always write `()` |
| Calling `p.apply(x)` on a Predicate | Predicate's method is `test` | Use `test` |
| Writing `@FunctionInterface` | Misspelled, compile error | `@FunctionalInterface` |
| Modifying a local variable inside a lambda | Captured locals must be effectively final | Use a final or unchanged variable |

### The "effectively final" rule

```java
int count = 0;
Runnable r = () -> count++;   // ERROR: count is changed inside lambda
```

A lambda can read local variables from the surrounding method, but only if they never change after being assigned.

---

## 16. Cheat Sheet

**Syntax**

```
(params) -> expression
(params) -> { statements; return value; }
```

**Shrinking rules**

1. Drop parameter types.
2. Drop braces if the body is one statement.
3. Drop `return` too when braces are dropped.
4. Drop parentheses if there is exactly one parameter.

**Functional interface:** exactly one abstract method; mark it with `@FunctionalInterface`.

**The utility interfaces (package `java.util.function`)**

```java
Predicate<T>        boolean test(T t)
Function<T,R>       R       apply(T t)
Consumer<T>         void    accept(T t)
Supplier<T>         T       get()
BiPredicate<T,U>    boolean test(T t, U u)
BiFunction<T,U,R>   R       apply(T t, U u)
BiConsumer<T,U>     void    accept(T t, U u)
```

---

## 17. Practice Questions

1. Convert this method into a lambda and shorten it as much as possible:
   ```java
   public int square(int x) { return x * x; }
   ```
2. Why can't `Cloneable` be used with a lambda?
3. Write a `Predicate<String>` that checks if a string starts with `"A"`.
4. Write a `Function<String, Integer>` that returns the number of words in a sentence.
5. Which interface would you use to print every element of a list? Which to generate a random OTP?
6. Why does this fail, and how do you fix it?
   ```java
   Predicate<int> p = n -> n > 0;
   ```
7. Rewrite the `Util` class methods (`isEven`, `isPrime`, `fact`, `countVowels`) as lambdas using the correct utility interface for each.

### Answers (short)

1. `x -> x * x`
2. It has no abstract method, so there is nothing for a lambda to implement.
3. `Predicate<String> p = s -> s.startsWith("A");`
4. `Function<String, Integer> f = s -> s.trim().split("\\s+").length;`
5. `Consumer` for printing, `Supplier` for generating.
6. Primitives are not allowed in generics. Use `Predicate<Integer>`.
7. `isEven` and `isPrime` use `Predicate<Integer>`; `fact` uses `Function<Integer, Integer>`; `countVowels` uses `Function<String, Integer>`.

---

*End of notes.*
