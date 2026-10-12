# Java: Objects, Static vs Non-Static, Constructors and `this`

A complete set of notes built from the 20 "points to remember", each explained with the what, why and how, plus real-life examples and extra material.

---

## Table of Contents

1. [Foundation: Class and Object](#1-foundation-class-and-object)
2. [Where Things Live in Memory](#2-where-things-live-in-memory)
3. [Non-Static Variables (Points 1, 4)](#3-non-static-variables-points-1-4)
4. [Methods Have Only One Copy (Point 2)](#4-methods-have-only-one-copy-point-2)
5. [Static Variables (Point 3)](#5-static-variables-point-3)
6. [Static Methods (Points 5, 6)](#6-static-methods-points-5-6)
7. [Non-Static Methods (Point 7)](#7-non-static-methods-point-7)
8. [Access Rules at a Glance](#8-access-rules-at-a-glance)
9. [Why `main` Is Static](#9-why-main-is-static)
10. [Static Block and Instance Block](#10-static-block-and-instance-block)
11. [Constructors (Points 8, 9)](#11-constructors-points-8-9)
12. [Types of Constructors (Points 10 to 14)](#12-types-of-constructors-points-10-to-14)
13. [Constructor Overloading](#13-constructor-overloading)
14. [The `this` Keyword (Points 15, 16)](#14-the-this-keyword-points-15-16)
15. [`this()` Constructor Chaining (Points 17 to 20)](#15-this-constructor-chaining-points-17-to-20)
16. [Order of Execution](#16-order-of-execution)
17. [Extra Concepts](#17-extra-concepts)
18. [Common Mistakes](#18-common-mistakes)
19. [Cheat Sheet](#19-cheat-sheet)
20. [Practice Questions](#20-practice-questions)

---

## 1. Foundation: Class and Object

### Class

A **class** is a blueprint or template. It describes what something has (data) and what it can do (behavior), but it is not the thing itself.

### Object

An **object** is a real instance created from that blueprint. It occupies memory and has its own state.

### Real-life analogy

| Concept | Real life |
|---|---|
| Class `House` | The architect's blueprint |
| Object | A real house built from the blueprint |
| Variables | Color, number of rooms, owner name |
| Methods | `openDoor()`, `switchOnLight()` |

One blueprint can produce many houses, and each house can have a different color and owner.

```java
class Student {
    int rollNo;        // data (variable)
    String name;

    void study() {     // behavior (method)
        System.out.println(name + " is studying");
    }
}

public class Main {
    public static void main(String[] args) {
        Student s1 = new Student();   // object 1
        Student s2 = new Student();   // object 2
    }
}
```

`new Student()` creates the object. `s1` and `s2` are **reference variables** that point to those objects.

---

## 2. Where Things Live in Memory

Understanding memory makes every point below easy.

| Memory area | What goes here |
|---|---|
| **Heap** | All objects and their non-static variables |
| **Stack** | Local variables, method calls, reference variables like `s1` |
| **Method Area (Metaspace)** | Class info, static variables, method code |

```
Stack              Heap
-----              ----
s1  ------------>  [ rollNo=1, name="Asha" ]   (object 1)
s2  ------------>  [ rollNo=2, name="Ravi" ]   (object 2)

Method Area
-----------
Student.college = "LPU"      (one shared copy)
Student.study() code          (one shared copy)
```

---

## 3. Non-Static Variables (Points 1, 4)

> **Point 1:** All objects will have individual memory space for non-static variables.
> **Point 4:** A non-static variable or method can be accessed only through objects.

### What

A variable declared without `static` is an **instance variable**. Every object gets its **own private copy**.

### Why

Each object represents a different real-world entity. Two students must be able to hold different names and roll numbers.

### Real-life analogy

Every student has their **own notebook**. Writing in Asha's notebook does not change Ravi's.

```java
class Student {
    int rollNo;
    String name;
}

Student s1 = new Student();
s1.rollNo = 1;
s1.name = "Asha";

Student s2 = new Student();
s2.rollNo = 2;
s2.name = "Ravi";

System.out.println(s1.name);   // Asha
System.out.println(s2.name);   // Ravi
```

Changing `s1.name` never affects `s2.name`.

### Why only through objects (Point 4)

An instance variable does not exist until an object is created. Without an object there is no copy, so there is nothing to read.

```java
System.out.println(Student.name);   // ERROR: non-static variable cannot be referenced from a static context
System.out.println(s1.name);        // correct
```

### Default values

Instance variables get default values automatically if not assigned.

| Type | Default |
|---|---|
| `int`, `long`, `short`, `byte` | `0` |
| `double`, `float` | `0.0` |
| `boolean` | `false` |
| `char` | `'\u0000'` (null character) |
| Any object reference (`String`, etc.) | `null` |

Local variables inside methods get **no** default value and must be assigned before use.

---

## 4. Methods Have Only One Copy (Point 2)

> **Point 2:** All objects will have only one copy of methods.

### What

Even if you create 1000 objects, the method code exists **only once** in memory.

### Why

Method code does not change from object to object. Copying it 1000 times would waste memory. Only the **data** differs between objects.

### How does one method serve many objects?

Java secretly passes the object's reference to the method. This is the `this` reference (covered in section 14). So `s1.study()` runs the shared code using `s1`'s data, and `s2.study()` runs the same code using `s2`'s data.

### Real-life analogy

A **recipe book** in a restaurant kitchen. There is one recipe for pasta, but every order (object) uses its own ingredients (data).

```java
class Student {
    String name;
    void study() {
        System.out.println(name + " is studying");
    }
}

Student s1 = new Student(); s1.name = "Asha";
Student s2 = new Student(); s2.name = "Ravi";

s1.study();   // Asha is studying
s2.study();   // Ravi is studying   (same code, different data)
```

---

## 5. Static Variables (Point 3)

> **Point 3:** A static variable will have only one copy; all objects share the same copy.

### What

A variable declared with `static` belongs to the **class**, not to any object. It is created once, when the class is loaded.

### Why

Some information is common to everyone. Storing it separately in every object wastes memory and risks inconsistency.

### Real-life analogy

Every student in a college has their own roll number (non-static), but **the college name is the same for all** (static). If the college is renamed, you change it in one place.

```java
class Student {
    int rollNo;                      // one per object
    String name;                     // one per object
    static String college = "LPU";   // one for the whole class
}

Student s1 = new Student();
Student s2 = new Student();

System.out.println(s1.college);        // LPU
System.out.println(s2.college);        // LPU

Student.college = "New University";    // change once
System.out.println(s1.college);        // New University
System.out.println(s2.college);        // New University
```

### Classic use: counting objects

```java
class Student {
    static int count = 0;

    Student() {
        count++;
    }
}

new Student();
new Student();
new Student();
System.out.println(Student.count);   // 3
```

This only works because `count` is shared. If it were non-static, every object would have its own `count` starting at 0.

### Best practice

Access static members through the **class name** (`Student.college`), not through an object. It makes clear that the member is shared, not per-object.

### Real examples of static in Java

- `Math.PI`, `Math.sqrt()`
- `Integer.MAX_VALUE`, `Integer.parseInt()`
- `System.out` (here `out` is a static variable of the `System` class)

---

## 6. Static Methods (Points 5, 6)

> **Point 5:** A static method can be called directly using the class name.
> **Point 6:** A static method can access only static variables.

### What

A static method belongs to the class. You can call it without creating any object.

```java
class MathUtil {
    static int square(int x) {
        return x * x;
    }
}

System.out.println(MathUtil.square(5));   // 25, no object needed
```

### Why

Utility logic such as "square a number" does not depend on any object's data. Forcing the creation of an object just to call it would be pointless.

### Why a static method can access only static variables (Point 6)

A static method can run **before any object exists**. Instance variables live inside objects. If no object exists, there is no instance variable to read.

**Analogy:** A shop's **notice board** (static) can show the shop's name even when no customer is present. It cannot show a particular customer's name, because that customer may not have walked in yet.

```java
class Demo {
    int a = 10;              // instance
    static int b = 20;       // static

    static void show() {
        System.out.println(b);   // OK
        System.out.println(a);   // ERROR: non-static variable a cannot be referenced from a static context
    }
}
```

### The loophole (precision for Point 6)

A static method cannot use an instance variable **directly**, but it can use one if you give it an object:

```java
static void show() {
    Demo d = new Demo();
    System.out.println(d.a);   // OK: accessed through an object
}
```

So the accurate statement is: a static method can access instance members only **through an object reference**.

### Other static method rules

- A static method cannot use `this` or `super` (there is no current object).
- A static method can call other static methods directly.
- A static method cannot directly call a non-static method (same reason as variables).

---

## 7. Non-Static Methods (Point 7)

> **Point 7:** A non-static method can access both static and non-static variables.

### Why

A non-static method always runs **on an object**, so the object's own instance variables are available. The class-level static variables are always available too.

```java
class Student {
    String name;                       // instance
    static String college = "LPU";     // static

    void show() {
        System.out.println(name + " studies at " + college);   // both allowed
    }
}

Student s = new Student();
s.name = "Asha";
s.show();   // Asha studies at LPU
```

**Analogy:** A student (object) knows their own name **and** the name of the college they belong to.

A non-static method can also call static methods and other non-static methods directly.

---

## 8. Access Rules at a Glance

| From (rows) accessing (columns) | Static variable | Non-static variable | Static method | Non-static method |
|---|---|---|---|---|
| **Static method** | Directly | Only through an object | Directly | Only through an object |
| **Non-static method** | Directly | Directly | Directly | Directly |
| **Outside the class** | `ClassName.var` | `object.var` | `ClassName.method()` | `object.method()` |

**One-line memory trick:** *The static world cannot see the object world without being handed an object. The object world can see everything.*

---

## 9. Why `main` Is Static

```java
public static void main(String[] args)
```

The JVM must start the program **before any object exists**. If `main` were non-static, the JVM would first need an object of the class, which would need `main` to run first, a chicken-and-egg problem. Making it static lets the JVM call `ClassName.main()` directly.

---

## 10. Static Block and Instance Block

### Static block

Runs **once**, when the class is first loaded, before `main`.

```java
class Demo {
    static int x;
    static {
        x = 100;
        System.out.println("Static block");
    }
}
```

**Use:** one-time setup such as loading a database driver or initializing static data.

### Instance block

Runs **every time an object is created**, before the constructor body.

```java
class Demo {
    {
        System.out.println("Instance block");
    }
    Demo() {
        System.out.println("Constructor");
    }
}

new Demo();
// Instance block
// Constructor
```

---

## 11. Constructors (Points 8, 9)

> **Point 8:** Every class is created with the help of a constructor.
> **Point 9:** A constructor is a special method which is automatically called whenever an object is created.

### What

A constructor is a special block of code that **initializes a new object**.

### Why

Without constructors you would have to create an object and then set every field one by one. That is error-prone: you might forget a field and leave the object half-ready.

### Real-life analogy

A **new bank account opening process**. The moment the account is created, the bank must collect your name, ID and initial deposit. An account cannot exist in a blank state. The constructor is that "must-do setup" that runs at creation.

Another picture: a **car factory assembly line** that fits the engine, wheels and seats while the car is being built, not afterwards.

### Rules that make it special

| Rule | Detail |
|---|---|
| Same name as the class | `Student` class has constructor `Student()` |
| No return type | Not even `void`; adding `void` turns it into an ordinary method |
| Called automatically | Runs when you use `new` |
| Cannot be called like a method | You cannot write `s1.Student()` |
| Cannot be `static`, `final`, `abstract` | It works on a specific object being created |
| Can have access modifiers | `public`, `private`, `protected`, default |
| Cannot be inherited | But a subclass constructor calls the parent's using `super()` |

```java
class Student {
    String name;

    Student() {                       // constructor
        System.out.println("Object created");
        name = "Unknown";
    }
}

Student s = new Student();   // prints: Object created
```

### What `new` does, step by step

1. Allocates memory for the object on the heap.
2. Sets instance variables to default values.
3. Calls the constructor to initialize the object.
4. Returns the reference, which is stored in `s`.

---

## 12. Types of Constructors (Points 10 to 14)

> **Point 10:** A class can have either parameterized or default constructors.
> **Point 11:** A default constructor can be given either by Java or by the user.
> **Point 12:** Java assigns one empty default constructor only when the class does not have any constructor.
> **Point 13:** A parameterized constructor should be given by the user only.
> **Point 14:** We can choose which constructor to use for creating the object.

### Default (no-argument) constructor

Takes no parameters.

**Case A: written by the user**

```java
class Student {
    Student() {
        System.out.println("User-written default constructor");
    }
}
```

**Case B: provided by Java (Point 12)**

If you write **no constructor at all**, the compiler silently inserts one:

```java
class Student { }          // what you write

class Student {            // what the compiler produces
    Student() {
        super();
    }
}
```

It is empty: it does nothing except call the parent class constructor.

### The trap (Point 12 in action)

The moment you write **any** constructor, Java stops providing the free one.

```java
class Student {
    String name;
    Student(String name) {           // only a parameterized constructor
        this.name = name;
    }
}

Student s1 = new Student("Asha");    // OK
Student s2 = new Student();          // ERROR: no no-argument constructor exists
```

**Fix:** write the no-argument constructor yourself if you need it.

This is one of the most common beginner and interview mistakes.

### Parameterized constructor (Point 13)

Takes parameters to initialize the object with specific values.

```java
class Student {
    int rollNo;
    String name;

    Student(int rollNo, String name) {
        this.rollNo = rollNo;
        this.name = name;
    }
}

Student s = new Student(1, "Asha");
```

**Why only the user writes it:** Java cannot guess which values you want. It can create an empty one safely, but it cannot invent meaningful parameters.

### Choosing which constructor to use (Point 14)

The arguments you pass after `new` decide which constructor runs.

```java
new Student();               // no-argument constructor
new Student(1, "Asha");      // parameterized constructor
```

### A correction to Point 10

Point 10 says a class has "either" parameterized or default constructors. In fact a class can have **both at the same time** (and several of each kind with different parameters). This is **constructor overloading**. Java just stops providing the free default one once you write any constructor.

---

## 13. Constructor Overloading

Multiple constructors in one class, differing in the number or type of parameters. It offers flexibility when creating objects.

**Real-life analogy:** ordering a coffee. You can say just "coffee" (default), "coffee with milk" (one option), or "coffee with milk and 2 sugars" (more options). Same item, different levels of detail.

```java
class Student {
    int rollNo;
    String name;
    String city;

    Student() {
        this.rollNo = 0;
        this.name = "Unknown";
        this.city = "NA";
    }

    Student(String name) {
        this.rollNo = 0;
        this.name = name;
        this.city = "NA";
    }

    Student(int rollNo, String name, String city) {
        this.rollNo = rollNo;
        this.name = name;
        this.city = city;
    }
}

Student a = new Student();
Student b = new Student("Ravi");
Student c = new Student(5, "Neha", "Jalandhar");
```

Notice the repeated assignments. That repetition is exactly what `this()` (section 15) fixes.

---

## 14. The `this` Keyword (Points 15, 16)

> **Point 15:** `this` is a constant which will store the reference of the current object.
> **Point 16:** When a method variable name and an instance variable name are the same, to distinguish them the instance variable is referenced with `this.variableName`.

### What

`this` is a keyword that refers to **the object currently executing the code**.

Calling it a "constant" is loose: precisely, `this` is a read-only reference. You can use it but you can never assign to it (`this = something;` is a compile error).

### Real-life analogy

When you say **"my name is Mohit"**, the word "my" means "whoever is speaking right now". If Asha says "my name", it means Asha's name. `this` is the same idea: it means "the object that is running this code right now".

### Why it exists

Recall Point 2: there is only one copy of each method. So how does `name` inside `study()` know whether to use Asha's name or Ravi's? Java secretly passes the calling object into the method as `this`. Writing `name` is shorthand for `this.name`.

```java
s1.study();   // inside study(), this = s1
s2.study();   // inside study(), this = s2
```

### The naming conflict problem (Point 16)

Using the same name for a parameter and an instance variable is good style, because the name describes the data clearly. But it causes **shadowing**: the local name hides the instance variable.

```java
class Student {
    String name;

    Student(String name) {
        name = name;          // WRONG: assigns the parameter to itself, field stays null
    }
}
```

Here Java sees `name` as the parameter on both sides. The instance variable is never set, and no error is shown, which makes this a silent bug.

**Fix with `this`:**

```java
Student(String name) {
    this.name = name;   // left: instance variable, right: parameter
}
```

| Expression | Refers to |
|---|---|
| `this.name` | The object's instance variable |
| `name` | The nearest variable in scope (the parameter) |

### Other uses of `this`

**1. Call another instance method of the same object**

```java
void a() { this.b(); }
void b() { }
```

**2. Pass the current object to another method or constructor**

```java
class Order {
    void process() {
        Printer.print(this);   // sends the current object
    }
}
```

**3. Return the current object (enables method chaining)**

```java
class Builder {
    StringBuilder sb = new StringBuilder();

    Builder add(String s) {
        sb.append(s);
        return this;           // returns the same object
    }
}

new Builder().add("Hello").add(" ").add("World");   // chained calls
```

This is exactly how `StringBuilder.append()` allows `.append("a").append("b")`.

### Restrictions

- `this` cannot be used inside a static method (no current object).
- `this` cannot be reassigned.

---

## 15. `this()` Constructor Chaining (Points 17 to 20)

> **Point 17:** `this()` is used to create an object by using more than one constructor. It is used to call one constructor from another constructor.
> **Point 18:** `this()` must be called only in a constructor.
> **Point 19:** `this()` must be the first line of the constructor.
> **Point 20:** `this()` should not form a cycle.

### Do not confuse these two

| Syntax | Meaning |
|---|---|
| `this.name` | Refers to a **variable** of the current object |
| `this(...)` | **Calls another constructor** of the same class |

### What and why

In constructor overloading (section 13), the same assignments were repeated in every constructor. `this()` lets one constructor **reuse another**, so the real initialization logic lives in one place.

### Real-life analogy

A **customer care call center**. A level-1 agent handles the basic request and, if needed, transfers to level 2, who transfers to level 3 with the full details. Each level adds something and passes the work along instead of repeating everything.

### Example

```java
class Employee {
    String name;
    int age;
    String city;

    Employee() {
        this("Unknown");                 // calls Employee(String)
        System.out.println("No-arg constructor done");
    }

    Employee(String name) {
        this(name, 18);                  // calls Employee(String, int)
        System.out.println("One-arg constructor done");
    }

    Employee(String name, int age) {
        this.name = name;                // the real initialization
        this.age = age;
        this.city = "NA";
        System.out.println("Two-arg constructor done");
    }
}

new Employee();
```

### Trace

```
new Employee()
   -> Employee()           calls this("Unknown")
        -> Employee(String)     calls this("Unknown", 18)
             -> Employee(String, int)   sets fields, prints "Two-arg constructor done"
        <- prints "One-arg constructor done"
   <- prints "No-arg constructor done"
```

**Output:**

```
Two-arg constructor done
One-arg constructor done
No-arg constructor done
```

The most detailed constructor runs first because every `this()` call jumps ahead, and the rest of each constructor runs only after the call returns.

### Rule 18: only inside constructors

`this()` cannot be called from an ordinary method.

```java
void show() {
    this("Asha");   // ERROR: call to this must be first statement in constructor
}
```

**Why:** constructors build the object. Calling one on an object that already exists would "rebuild" it, which makes no sense.

### Rule 19: must be the first line

```java
Employee() {
    System.out.println("Hi");
    this("Unknown");            // ERROR: must be first statement
}
```

**Why:** the object must be fully initialized by another constructor **before** any other code in this one runs. Otherwise code could use a half-built object.

**Consequence:** because both `this()` and `super()` must be the first statement, you **cannot use both in the same constructor**.

### Rule 20: no cycles

```java
class A {
    A() {
        this(10);
    }
    A(int x) {
        this();     // calls A() again, which calls A(int) again, forever
    }
}
```

This gives the compile error **"recursive constructor invocation"**.

**Why:** a cycle would never finish building the object, like two people each waiting for the other to finish first (a deadlock). The compiler catches it at compile time rather than letting you hit a `StackOverflowError`.

**The chain must always end** at a constructor that does the real work and does not call `this()`.

---

## 16. Order of Execution

When an object is created, things happen in this order:

1. **Class loading** (first time only): static variables initialized, **static blocks** run.
2. Memory allocated; instance variables get default values.
3. Parent class constructor runs (via `super()`, implicit if not written).
4. **Instance variable initializers** and **instance blocks** run, in the order written.
5. **Constructor body** runs.

```java
class Demo {
    static { System.out.println("1. Static block"); }
    { System.out.println("2. Instance block"); }
    Demo() { System.out.println("3. Constructor"); }
}

new Demo();
new Demo();
```

**Output:**

```
1. Static block
2. Instance block
3. Constructor
2. Instance block
3. Constructor
```

The static block ran **once**; the instance block and constructor ran **per object**.

---

## 17. Extra Concepts

### 17.1 `super()` in one paragraph

Every constructor's first line is implicitly `super();`, which calls the parent class constructor so the parent part of the object is built first. If you write `this(...)`, the compiler does not add `super()` there; the constructor you chain to will do it.

| Keyword | Refers to |
|---|---|
| `this` | Current class's object |
| `super` | Parent class part of the object |

### 17.2 Private constructor and the Singleton pattern

A constructor can be `private`, so no one outside can create objects. Used when you want **only one object**.

```java
class Database {
    private static Database instance;

    private Database() { }               // blocks new Database()

    static Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }
}

Database d1 = Database.getInstance();
Database d2 = Database.getInstance();
System.out.println(d1 == d2);   // true, same object
```

**Real-life analogy:** a country has only one president at a time. Everyone talks to the same one.

Utility classes like `Math` also use private constructors to prevent object creation.

### 17.3 Copy constructor

Java has no built-in copy constructor like C++, but you can write one.

```java
class Student {
    String name;
    int rollNo;

    Student(String name, int rollNo) {
        this.name = name;
        this.rollNo = rollNo;
    }

    Student(Student other) {             // copy constructor
        this.name = other.name;
        this.rollNo = other.rollNo;
    }
}

Student s1 = new Student("Asha", 1);
Student s2 = new Student(s1);            // independent copy
```

Contrast with `Student s2 = s1;`, which does **not** copy. It makes `s2` point to the **same** object.

### 17.4 Reference copy vs object copy

```java
Student s1 = new Student("Asha", 1);
Student s2 = s1;          // two names, one object
s2.name = "Ravi";
System.out.println(s1.name);   // Ravi, because both point to the same object
```

**Analogy:** two people holding the same house key. If one repaints the house, the other sees it.

### 17.5 Static vs non-static summary

| Feature | Static | Non-static |
|---|---|---|
| Belongs to | Class | Object |
| Copies | One, shared | One per object |
| Memory created | When class loads | When object is created |
| Access | `ClassName.member` | `object.member` |
| Can use `this` | No | Yes |
| Example | `Math.PI`, object counter | `name`, `rollNo` |

### 17.6 Constructor vs method

| Constructor | Method |
|---|---|
| Same name as class | Any name |
| No return type | Must have a return type (or `void`) |
| Called automatically by `new` | Called explicitly by you |
| Initializes the object | Performs behavior |
| Runs once per object creation | Can run any number of times |
| Not inherited | Inherited |

### 17.7 Why constructors help encapsulation and validation

```java
class BankAccount {
    private double balance;

    BankAccount(double initialDeposit) {
        if (initialDeposit < 0)
            throw new IllegalArgumentException("Deposit cannot be negative");
        this.balance = initialDeposit;
    }
}
```

An invalid object can never be created, because the constructor refuses it at birth.

---

## 18. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| `name = name;` in constructor | Assigns the parameter to itself; field stays default | `this.name = name;` |
| Writing only a parameterized constructor, then `new X()` | Java does not give a free default constructor | Write the no-arg constructor yourself |
| Adding `void` to a constructor | Becomes a normal method, not a constructor | Remove the return type |
| Accessing an instance variable from a static method directly | Compile error | Create an object and use it |
| Using `this` in a static method | No current object exists | Remove `this` or make the method non-static |
| `this()` not on the first line | Compile error | Move it to the first line |
| `this()` calls forming a loop | "Recursive constructor invocation" | End the chain at a constructor with real logic |
| Using `this()` and `super()` together | Both must be first | Use only one |
| Calling a constructor like a method (`obj.Student()`) | Not allowed | Constructors run only via `new` |
| Thinking `Student s2 = s1;` copies the object | Only copies the reference | Use a copy constructor |
| Static counter written as non-static | Each object has its own counter | Declare it `static` |

---

## 19. Cheat Sheet

**Memory**
- Non-static variables: one copy per object, on the heap.
- Static variables: one shared copy per class.
- Methods: one copy, shared by all objects.

**Access**
- Non-static member: through an object.
- Static member: through the class name.
- Static method: only static members directly.
- Non-static method: everything directly.

**Constructors**
- Same name as class, no return type, runs automatically on `new`.
- Java supplies an empty default constructor only when you write none.
- Parameterized constructors are written by the user.
- Overloading lets one class have several constructors.

**this**
- `this.x`: the current object's variable (fixes name clashes).
- `this`: the current object as a value.
- `this(...)`: call another constructor; must be first line, constructors only, no cycles.

---

## 20. Practice Questions

1. A class has 100 objects. How many copies of its instance variables, static variables and methods exist?
2. Why can `main` be called without creating an object?
3. What does this print, and why?
   ```java
   class A {
       int x;
       A(int x) { x = x; }
   }
   System.out.println(new A(5).x);
   ```
4. Will this compile? Why or why not?
   ```java
   class B {
       B(int a) { }
   }
   B b = new B();
   ```
5. What is wrong here?
   ```java
   class C {
       C() { System.out.println("hi"); this(5); }
       C(int x) { }
   }
   ```
6. Write a class `Counter` that tells how many objects of it have been created.
7. What is the difference between `this.name` and `this(name)`?
8. Why can a static method not use `this`?
9. Can a constructor be `static`? Why or why not?
10. Predict the output:
    ```java
    class D {
        D() { this(1); System.out.println("A"); }
        D(int x) { this(x, 2); System.out.println("B"); }
        D(int x, int y) { System.out.println("C"); }
    }
    new D();
    ```

### Answers

1. 100 copies of instance variables, 1 copy of each static variable, 1 copy of each method.
2. `main` is static, so it belongs to the class. The JVM can call it before any object exists.
3. Prints `0`. `x = x` assigns the parameter to itself; the instance variable keeps its default `0`. Fix: `this.x = x;`.
4. No. `B` has a parameterized constructor, so Java does not supply a no-arg one. `new B()` fails.
5. `this(5)` is not the first statement. It must come first.
6. ```java
   class Counter {
       static int count = 0;
       Counter() { count++; }
   }
   ```
7. `this.name` is the object's variable. `this(name)` calls another constructor of the same class, passing `name`.
8. A static method belongs to the class and may run when no object exists, so there is no "current object" for `this` to point to.
9. No. A constructor initializes a specific object being created, but static belongs to the class and not to any object. The two ideas contradict each other.
10. `C`, then `B`, then `A` (the chain jumps to the deepest constructor first, then each one finishes on the way back).

---

*End of notes.*
