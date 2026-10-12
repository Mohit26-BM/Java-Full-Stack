# Java OOP and Inheritance: Notes

Object-oriented programming from the ground up: the four pillars, then inheritance, `super`, overriding, polymorphism, abstract classes, interfaces, the `Object` class, composition vs inheritance, tricky output questions and interview Q&A.

> Classes, objects, `static`, constructors, `this` and `this()` are covered in the earlier notes on those topics. Here they are only recapped where inheritance depends on them.

---

## Table of Contents

1. [What Is OOP and Why](#1-what-is-oop-and-why)
2. [Class and Object Recap](#2-class-and-object-recap)
3. [Encapsulation](#3-encapsulation)
4. [Abstraction](#4-abstraction)
5. [Inheritance](#5-inheritance)
6. [The `super` Keyword and Constructor Chaining](#6-the-super-keyword-and-constructor-chaining)
7. [Method Overriding](#7-method-overriding)
8. [Polymorphism](#8-polymorphism)
9. [The `final` Keyword](#9-the-final-keyword)
10. [Abstract Classes](#10-abstract-classes)
11. [Interfaces](#11-interfaces)
12. [The `Object` Class](#12-the-object-class)
13. [Association, Aggregation, Composition](#13-association-aggregation-composition)
14. [Modern OOP Tools: Records and Sealed Types](#14-modern-oop-tools-records-and-sealed-types)
15. [SOLID in One Table](#15-solid-in-one-table)
16. [Everything Together: Banking Example](#16-everything-together-banking-example)
17. [Common Mistakes](#17-common-mistakes)
18. [Output Prediction Puzzles](#18-output-prediction-puzzles)
19. [Interview Questions](#19-interview-questions)
20. [Cheat Sheet](#20-cheat-sheet)
21. [Practice Tasks](#21-practice-tasks)

---

## 1. What Is OOP and Why

### Procedural vs object-oriented

| Procedural | Object-oriented |
|---|---|
| Data and functions are **separate** | Data and the functions that use it are **bundled** into objects |
| Programs are lists of steps | Programs are **objects collaborating** |
| Changing a data structure ripples everywhere | Changes stay inside the class |
| Example: C | Example: Java |

**OOP models software on real-world things:** a `Student`, an `Account`, an `Order`, each with data (attributes) and abilities (methods).

### The four pillars

| Pillar | Idea | Real-life analogy |
|---|---|---|
| **Encapsulation** | Hide data; allow access only through controlled methods | A **medicine capsule** or a **bank locker**: you can't touch the contents directly |
| **Abstraction** | Show *what* an object does, hide *how* | A **car dashboard**: you steer and brake without knowing the engine internals |
| **Inheritance** | A class acquires the features of another | **Children inherit traits** from parents |
| **Polymorphism** | One name, many forms | One **"pay" button** that works for card, UPI or cash |

### Benefits

Reusability, maintainability, modularity, security (hidden data), extensibility, and a code structure that matches the problem domain.

---

## 2. Class and Object Recap

- A **class** is a blueprint. An **object** is a real instance built from it.
- Non-static variables: **one copy per object**. Static variables: **one shared copy**. Methods: **one copy**, shared.

```java
class Student {
    int rollNo;
    String name;

    Student(int rollNo, String name) {     // constructor initializes the object
        this.rollNo = rollNo;
        this.name = name;
    }

    void study() { System.out.println(name + " is studying"); }
}

Student s = new Student(1, "Asha");
```

Every class implicitly extends **`Object`** (section 12).

---

## 3. Encapsulation

> **Encapsulation** = make fields `private` and expose them through `public` methods that can validate access.

### Without it

```java
class BankAccount { double balance; }

BankAccount acc = new BankAccount();
acc.balance = -5000;                 // nothing stops invalid data
```

### With it

```java
public class BankAccount {
    private double balance;                       // hidden

    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) throw new IllegalStateException("Insufficient funds");
        balance -= amount;
    }
}
```

**Analogy:** an **ATM**. You cannot reach into the vault; you can only press buttons that enforce rules (PIN, limit, balance check).

### Why

- **Validation:** invalid states are impossible.
- **Flexibility:** you can change the internal representation without breaking users.
- **Control:** read-only data (getter only), or write-once data.
- **Safety:** other code can't corrupt internal state.

### Getters and setters (JavaBeans convention)

`getX()`, `isX()` (for booleans), `setX(value)`. Frameworks like Spring, JPA and Jackson rely on these names. (Records use `x()` instead.)

### Access modifiers

| Modifier | Same class | Same package | Subclass in another package | Everywhere |
|---|---|---|---|---|
| `private` | Yes | No | No | No |
| *(default)* | Yes | Yes | No | No |
| `protected` | Yes | Yes | Yes (via inheritance) | No |
| `public` | Yes | Yes | Yes | Yes |

> A subclass in a **different package** can use a `protected` member through **itself** (`this` or a subclass reference), but not through a plain parent-type reference to some other object.

### Immutable classes

An object whose state never changes after construction (`String`, `Integer`, records).

```java
public final class Money {
    private final String currency;
    private final double amount;

    public Money(String currency, double amount) { this.currency = currency; this.amount = amount; }

    public double getAmount() { return amount; }
    public Money add(double extra) { return new Money(currency, amount + extra); }   // returns a NEW object
}
```

Recipe: `final` class, `private final` fields, no setters, defensive copies of mutable fields. Immutable objects are thread-safe and safe as map keys.

---

## 4. Abstraction

> **Abstraction** = expose the essential operations, hide the implementation details.

**Analogy:** driving a car. You use the steering wheel, accelerator and brake. You do not manage fuel injection or gear timing.

In Java, abstraction is achieved with:

| Tool | Gives |
|---|---|
| **Abstract classes** | Partial abstraction: some methods implemented, some not |
| **Interfaces** | A pure contract: what must be done, not how |

```java
interface Payment {
    void pay(double amount);                 // WHAT, not HOW
}

class UpiPayment implements Payment {
    public void pay(double amount) { /* how UPI works */ }
}
```

The caller says `payment.pay(500)` and never cares how it happens. Sections 10 and 11 cover both tools in depth.

**Encapsulation vs abstraction**

| Encapsulation | Abstraction |
|---|---|
| **Hides data** | **Hides complexity** |
| Done with private fields and access modifiers | Done with abstract classes and interfaces |
| About protection | About simplification |

---

## 5. Inheritance

### What

> **Inheritance** lets a class (the **subclass**) acquire the fields and methods of another (the **superclass**) using `extends`.

Terms: superclass = parent = base class; subclass = child = derived class.

```java
class Animal {
    String name;
    void eat() { System.out.println(name + " is eating"); }
}

class Dog extends Animal {
    void bark() { System.out.println(name + " is barking"); }
}

Dog d = new Dog();
d.name = "Tommy";
d.eat();      // inherited from Animal
d.bark();     // Dog's own
```

### Why

- **Reuse:** write `eat()` once for every animal.
- **Natural modeling:** a Dog *is an* Animal.
- **Polymorphism:** treat different subclasses uniformly (section 8).

### The IS-A test

Use inheritance only when **"B is an A"** is true.

| Statement | Inheritance? |
|---|---|
| Dog **is an** Animal | Yes |
| SavingsAccount **is an** Account | Yes |
| Car **is an** Engine | No. A Car **has an** Engine, so use composition (section 13) |

### Types of inheritance

| Type | Shape | In Java |
|---|---|---|
| **Single** | `B extends A` | Yes |
| **Multilevel** | `C extends B`, `B extends A` | Yes |
| **Hierarchical** | `B` and `C` both extend `A` | Yes |
| **Multiple** | `C extends A, B` | **Not with classes**; use interfaces |
| **Hybrid** | A mix | Via interfaces |

```
Single        Multilevel     Hierarchical      Multiple (classes: NOT allowed)
  A              A              A                 A     B
  |              |             / \                 \   /
  B              B            B   C                 C
                 |
                 C
```

### Why Java forbids multiple inheritance of classes

If `A` and `B` both define `show()`, which one should `C` inherit? That ambiguity is the **diamond problem**. Java sidesteps it: a class has **one** parent class but can implement **many** interfaces.

### What is and is not inherited

| Member | Inherited? |
|---|---|
| `public` and `protected` fields and methods | Yes |
| Default (package) members | Only within the same package |
| `private` members | **Not accessible** (they exist in the object but cannot be used directly) |
| Constructors | **No** (but the parent constructor is always called via `super`) |
| `static` members | Accessible through the subclass name, but they are hidden, not overridden |

### What a subclass object looks like

A `Dog` object **contains an `Animal` part and a `Dog` part** in one object. Creating a `Dog` builds the Animal part first, then the Dog part.

### Real-life hierarchy: bank accounts

```
Account            (accNo, balance, deposit(), withdraw())
   |-- SavingsAccount   (adds: interestRate, addInterest())
   |-- CurrentAccount   (adds: overdraftLimit)
```

Shared behavior lives once in `Account`; each subtype adds only what is different.

---

## 6. The `super` Keyword and Constructor Chaining

### Three uses

| Use | Meaning |
|---|---|
| `super.field` | Access a parent field hidden by a child field |
| `super.method()` | Call the parent's version of an overridden method |
| `super(args)` | Call the parent's constructor |

```java
class Animal {
    String type = "Animal";
    Animal() { System.out.println("Animal constructor"); }
    void sound() { System.out.println("Some sound"); }
}

class Dog extends Animal {
    String type = "Dog";

    Dog() {
        super();                                   // explicit parent constructor call
        System.out.println("Dog constructor");
    }

    @Override
    void sound() {
        super.sound();                             // run the parent's version first
        System.out.println("Bark");
    }

    void show() { System.out.println(type + " / " + super.type); }
}

Dog d = new Dog();
d.sound();
d.show();
```

**Output:**

```
Animal constructor
Dog constructor
Some sound
Bark
Dog / Animal
```

### Constructor chaining rules

1. The **first statement** of every constructor is either `this(...)` or `super(...)`. If you write neither, the compiler inserts **`super();`**.
2. **Parent constructors run first**: the chain goes up to `Object`, then execution returns down.
3. If the parent has **no no-arg constructor**, the child must call `super(args)` explicitly.
4. `this(...)` and `super(...)` **cannot both** appear in one constructor (both must be first).

```java
class Parent {
    Parent(int x) { }                       // no no-arg constructor
}

class Child extends Parent {
    Child() { }                             // COMPILE ERROR: implicit super() finds no Parent()
}

class Child extends Parent {
    Child() { super(10); }                  // fix: call the existing constructor
}
```

**Analogy:** you cannot build the first floor before the foundation. The parent part of the object is built first.

### Order of execution with blocks

```java
class A {
    static { System.out.println("A static"); }
    { System.out.println("A instance block"); }
    A() { System.out.println("A constructor"); }
}

class B extends A {
    static { System.out.println("B static"); }
    { System.out.println("B instance block"); }
    B() { System.out.println("B constructor"); }
}

new B();
```

**Output:**

```
A static
B static
A instance block
A constructor
B instance block
B constructor
```

Order: **parent static, child static** (once, when the class loads), then for each object: **parent instance block and constructor, child instance block and constructor**.

---

## 7. Method Overriding

### What

A subclass provides **its own implementation** of a method inherited from the parent, with the **same signature**.

### Why

The parent defines a general behavior; each child specializes it.

```java
class Shape {
    double area() { return 0; }
}

class Circle extends Shape {
    private final double r;
    Circle(double r) { this.r = r; }

    @Override
    double area() { return Math.PI * r * r; }
}

class Rectangle extends Shape {
    private final double w, h;
    Rectangle(double w, double h) { this.w = w; this.h = h; }

    @Override
    double area() { return w * h; }
}
```

**Analogy:** every employee has a `calculatePay()` duty, but a manager, an intern and a contractor each compute it differently.

### Rules

| Rule | Detail |
|---|---|
| Signature | Same **name and parameter list** |
| Return type | Same, or a **covariant** (subtype) return for reference types |
| Access | Same or **wider**. `public` cannot become `protected` |
| `final` methods | **Cannot** be overridden |
| `static` methods | **Cannot** be overridden (they are *hidden*) |
| `private` methods | **Not visible**, so not overridden |
| Constructors | Never overridden |
| Checked exceptions | May throw the **same, narrower, or none**; **not broader or new** checked ones. Any unchecked exception is fine |
| `@Override` | Always add it: the compiler then flags typos such as `Area()` |

### Covariant return

```java
class AnimalFactory {
    Animal create() { return new Animal(); }
}
class DogFactory extends AnimalFactory {
    @Override
    Dog create() { return new Dog(); }        // Dog is a subtype of Animal
}
```

### Overloading vs overriding

| | Overloading | Overriding |
|---|---|---|
| Where | Same class (or subclass) | Parent and child |
| Signature | **Different** parameters | **Same** parameters |
| Return type | Can differ (but alone it cannot distinguish methods) | Same or covariant |
| Resolved | **Compile time** | **Run time** |
| Polymorphism type | Compile-time (static) | Runtime (dynamic) |
| Example | `add(int,int)` and `add(double,double)` | `Dog.sound()` replaces `Animal.sound()` |

### Static methods and fields are hidden, not overridden

```java
class P { static void hi() { System.out.println("P"); }  String name = "Parent"; }
class C extends P { static void hi() { System.out.println("C"); }  String name = "Child"; }

P obj = new C();
obj.hi();                         // P   (static: decided by the reference TYPE)
System.out.println(obj.name);     // Parent (fields: decided by the reference TYPE)
```

Only **instance methods** are polymorphic.

---

## 8. Polymorphism

> **Polymorphism** = "many forms": the same call behaves differently depending on the object.

### Two kinds

| | Compile-time (static) | Runtime (dynamic) |
|---|---|---|
| Mechanism | **Overloading** | **Overriding** |
| Decided by | Compiler, from argument types | JVM, from the **actual object** |
| Also called | Static binding | Dynamic binding / dynamic method dispatch |

### Compile-time polymorphism (overloading)

```java
class Calculator {
    int add(int a, int b)             { return a + b; }
    double add(double a, double b)    { return a + b; }
    int add(int a, int b, int c)      { return a + b + c; }
}
```

The compiler picks the version from the arguments. Overload resolution order: **exact match, then widening (`int` to `long`), then boxing, then varargs**.

### Runtime polymorphism (overriding)

```java
Animal a = new Dog();       // upcasting: parent reference, child object
a.sound();                  // runs Dog's sound(), chosen at RUNTIME
```

### The key rule: two different questions

| Question | Decided by | When |
|---|---|---|
| **What can I call?** | The **reference type** (`Animal`) | Compile time |
| **Which implementation runs?** | The **object type** (`Dog`) | Run time |

```java
Animal a = new Dog();
a.sound();            // OK: Animal has sound(); Dog's version runs
a.bark();             // COMPILE ERROR: Animal has no bark()
((Dog) a).bark();     // OK after downcasting
```

### Upcasting and downcasting

| | Direction | Syntax | Safety |
|---|---|---|---|
| **Upcast** | Child to parent | Implicit: `Animal a = new Dog();` | Always safe |
| **Downcast** | Parent to child | Explicit: `Dog d = (Dog) a;` | Can throw `ClassCastException` |

```java
Animal a = new Cat();
Dog d = (Dog) a;                      // ClassCastException: a Cat is not a Dog

if (a instanceof Dog dog) {           // safe check + cast (Java 16+)
    dog.bark();
}
```

### Why polymorphism is powerful: code that never changes

```java
abstract class Shape { abstract double area(); }
class Circle extends Shape { ... }
class Rectangle extends Shape { ... }

static double totalArea(List<Shape> shapes) {
    double total = 0;
    for (Shape s : shapes) {
        total += s.area();            // each shape computes its own area
    }
    return total;
}
```

Add a `Triangle` tomorrow and `totalArea` needs **no change**. This is the **Open/Closed principle**: open for extension, closed for modification.

**Analogy:** a **universal remote's "power" button** works the same for any TV brand. The remote doesn't know the brand; the TV responds in its own way.

### Polymorphism in the wild

```java
List<String> list = new ArrayList<>();      // reference: interface, object: ArrayList
Map<Integer, String> m = new HashMap<>();   // swap to TreeMap by changing one word
```

Declaring variables with the **interface or parent type** keeps your code flexible. The Spring DI notes build on exactly this idea.

---

## 9. The `final` Keyword

| Applied to | Effect |
|---|---|
| **Variable** | Value can be assigned only once (a constant) |
| **Method** | **Cannot be overridden** |
| **Class** | **Cannot be extended** (`String`, `Integer`, `Math` are final) |
| **Parameter** | Cannot be reassigned inside the method |

```java
final int MAX = 100;
MAX = 200;                                  // error

final List<String> list = new ArrayList<>();
list.add("ok");                             // allowed: the OBJECT can change
list = new ArrayList<>();                   // error: the REFERENCE cannot change
```

`final` on a reference means **the reference is fixed, not the object**.

**Why final classes and methods?** Security and design integrity: preventing a subclass from breaking a critical behavior (that is why `String` is final).

---

## 10. Abstract Classes

### What

A class declared `abstract` that **cannot be instantiated** and may contain **abstract methods** (declared without a body).

### Why

Some concepts are incomplete on their own. You can ride a car or a bike, but not a generic "vehicle". An abstract class defines the **common part** and forces subclasses to supply the **missing part**.

```java
abstract class Employee {
    protected final String name;
    protected final double baseSalary;

    Employee(String name, double baseSalary) {      // constructors are allowed
        this.name = name;
        this.baseSalary = baseSalary;
    }

    abstract double calculateSalary();              // no body: subclasses must implement

    void printPayslip() {                           // concrete: shared by all
        System.out.println(name + " earns " + calculateSalary());
    }
}

class Manager extends Employee {
    Manager(String name, double base) { super(name, base); }

    @Override
    double calculateSalary() { return baseSalary * 1.5; }
}

class Intern extends Employee {
    Intern(String name, double base) { super(name, base); }

    @Override
    double calculateSalary() { return baseSalary * 0.5; }
}

Employee e = new Manager("Asha", 50000);   // fine
// new Employee("x", 1);                   // error: cannot instantiate abstract class
e.printPayslip();                           // Asha earns 75000.0
```

### Rules

| Rule | Detail |
|---|---|
| Cannot be instantiated | `new Employee(...)` fails |
| May have constructors | Called through `super(...)` from subclasses |
| May have fields, concrete methods, and abstract methods | Or **no** abstract methods at all |
| A subclass must **implement all** abstract methods | or itself be `abstract` |
| An abstract method has **no body** | `abstract double calculateSalary();` |
| `abstract` cannot combine with `final`, `private` or `static` | They contradict "must be overridden" |

### Template Method pattern

Fix the **algorithm's skeleton** in the parent; let children fill in the steps.

```java
abstract class Report {
    final void generate() {          // final: the order cannot change
        open();
        body();                      // varies per subclass
        close();
    }
    void open()  { System.out.println("Opening"); }
    void close() { System.out.println("Closing"); }
    abstract void body();
}
```

---

## 11. Interfaces

### What

An **interface** is a **contract**: it declares *what* a class must do, without saying how.

```java
interface Payment {
    double FEE_RATE = 0.02;                    // implicitly public static final

    void pay(double amount);                   // implicitly public abstract

    default void receipt() {                   // Java 8: has a body
        System.out.println("Payment done");
    }

    static boolean isValid(double amount) {    // Java 8: called as Payment.isValid(...)
        return amount > 0;
    }

    private void audit() { }                   // Java 9: helper for default/static methods
}

class UpiPayment implements Payment {
    @Override
    public void pay(double amount) {           // must be public
        System.out.println("Paid " + amount + " via UPI");
    }
}
```

### Rules

- All interface methods are `public`, so implementations **must be declared `public`**.
- Fields are always `public static final` (constants).
- **No instance fields and no constructors.**
- A class can **implement many interfaces**; an interface can **extend many interfaces**.
- A class that does not implement every abstract method must be `abstract`.
- An interface can **extend interfaces only**, never a class.

```java
interface Flyable { void fly(); }
interface Swimmable { void swim(); }

class Duck implements Flyable, Swimmable {          // "multiple inheritance" of type
    public void fly()  { System.out.println("Flying"); }
    public void swim() { System.out.println("Swimming"); }
}
```

**Analogy:** a **USB-C port**. It defines the shape and signals. Any device that conforms (phone, laptop, charger) just works. The port does not care about the device's internals.

### The diamond problem with `default` methods

```java
interface A { default void hello() { System.out.println("A"); } }
interface B { default void hello() { System.out.println("B"); } }

class C implements A, B {
    @Override
    public void hello() {
        A.super.hello();               // must resolve the conflict explicitly
    }
}
```

Without the override, the compiler refuses. Resolution priority:

1. **Class wins:** a concrete method from a superclass beats an interface default.
2. **More specific interface wins** (a sub-interface's default beats its parent's).
3. Otherwise **you must override** and choose.

### Special kinds

| Kind | Meaning | Examples |
|---|---|---|
| **Functional interface** | Exactly one abstract method; usable with lambdas | `Runnable`, `Comparator`, `Predicate` |
| **Marker interface** | No methods; tags a class | `Serializable`, `Cloneable` |
| **Sealed interface** | Restricts which types may implement it | see section 14 |

### Abstract class vs interface

| | Abstract class | Interface |
|---|---|---|
| Relationship | **IS-A** (closely related classes) | **CAN-DO** (a capability or contract) |
| Inheritance | **One** per class | **Many** per class |
| Instance fields / state | Yes | No (constants only) |
| Constructors | Yes | No |
| Method access | Any modifier | Public (plus private helpers) |
| Method bodies | Concrete and abstract mix | `default`, `static`, `private`, plus abstract |
| Use when | Sharing **code and state** among related classes | Defining a **contract** across unrelated classes |

**Rule of thumb:** start with an interface. Add an abstract base class only when subclasses share real code or state. They combine well:

```java
interface Payment { void pay(double amt); }

abstract class BasePayment implements Payment {          // shared logic
    protected void log(String msg) { System.out.println(msg); }
}

class CardPayment extends BasePayment {
    public void pay(double amt) { log("Card " + amt); }
}
```

---

## 12. The `Object` Class

Every class extends `java.lang.Object` (directly or indirectly), so these methods exist on everything:

| Method | Purpose |
|---|---|
| `toString()` | Text form; used by `println` and string concatenation |
| `equals(Object)` | Logical equality (default: same as `==`) |
| `hashCode()` | Integer used by hash-based collections |
| `getClass()` | Runtime class information |
| `clone()` | Object copy (needs `Cloneable`) |
| `wait()`, `notify()`, `notifyAll()` | Thread coordination |

### `==` vs `equals()`

| | `==` | `equals()` |
|---|---|---|
| Compares | **References** (same object?) | **Content**, if overridden |
| For primitives | Compares values | Not applicable |

```java
String a = new String("hi");
String b = new String("hi");
System.out.println(a == b);        // false
System.out.println(a.equals(b));   // true
```

### Overriding the three that matter

```java
class Point {
    final int x, y;
    Point(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point p)) return false;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y); }

    @Override
    public String toString() { return "Point(" + x + ", " + y + ")"; }
}
```

**The contract:** if `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` **must** be true. Break it and `HashMap`/`HashSet` stop finding your objects (see the Map notes). **Always override `equals` and `hashCode` together.** Records generate all three automatically.

---

## 13. Association, Aggregation, Composition

### IS-A vs HAS-A

| Relationship | Implemented by | Example |
|---|---|---|
| **IS-A** | Inheritance (`extends`) | Dog is an Animal |
| **HAS-A** | A field holding another object | Car has an Engine |

### The HAS-A spectrum

| Type | Strength | Lifetime | Example |
|---|---|---|---|
| **Association** | Uses / knows | Independent | Teacher and Student |
| **Aggregation** | Weak ownership | Part can **exist without** the whole | Department has Professors (a professor survives if the department closes) |
| **Composition** | Strong ownership | Part **cannot exist without** the whole | House has Rooms; Order has OrderLines |

```java
class Engine { void start() { System.out.println("Engine started"); } }

class Car {
    private final Engine engine = new Engine();     // composition: Car creates and owns it

    void drive() {
        engine.start();
        System.out.println("Moving");
    }
}
```

### Favor composition over inheritance

| Inheritance | Composition |
|---|---|
| Child is **tightly coupled** to parent's internals (the "fragile base class" problem) | Parts are replaceable behind an interface |
| Fixed at compile time | Can swap at runtime |
| Deep hierarchies get confusing | Flat and simple |
| Only one parent class | Combine any number of parts |

**A real mistake in the JDK:** `Stack extends Vector`. A `Stack` should only push and pop, but inherits every `Vector` method (`insertElementAt`, ...), breaking its own rules. It would have been better as a class that **has** a list.

**Liskov Substitution test:** anywhere the parent works, the child must work too. If you wonder whether `Square extends Rectangle` is safe (setting width on a square changes the height), the contract is broken. Prefer composition or a shared interface.

**Spring tie-in:** dependency injection is composition. `Car` receives an `Engine` instead of extending something.

---

## 14. Modern OOP Tools: Records and Sealed Types

(Details are in the Modern Java Features notes.)

```java
public sealed interface Shape permits Circle, Rectangle { }

public record Circle(double radius) implements Shape { }
public record Rectangle(double w, double h) implements Shape { }

static double area(Shape s) {
    return switch (s) {
        case Circle c    -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.w() * r.h();
    };                                     // no default: the compiler knows every case
}
```

| Tool | OOP role |
|---|---|
| **Record** | Immutable data carrier; generates constructor, accessors, `equals`, `hashCode`, `toString`; cannot extend a class but can implement interfaces |
| **Sealed class/interface** | **Controlled inheritance**: only listed subtypes allowed |
| **Pattern-matching `switch`** | Type-based dispatch with compile-time exhaustiveness |

Classic polymorphism lets the **object** choose the behavior (open set of subtypes). Sealed types plus `switch` let the **function** choose by type (closed set, compiler-checked). Use whichever fits whether your set of types or your set of operations grows.

---

## 15. SOLID in One Table

| Principle | Meaning | Tie-in |
|---|---|---|
| **S**ingle Responsibility | A class has one reason to change | One service per business capability |
| **O**pen/Closed | Open for extension, closed for modification | New subclass, no change to `totalArea` |
| **L**iskov Substitution | Subtypes must be usable wherever the parent is | Don't make `Square extends Rectangle` carelessly |
| **I**nterface Segregation | Many small interfaces beat one fat one | `Flyable`, `Swimmable` |
| **D**ependency Inversion | Depend on abstractions, not concrete classes | Spring DI, `List` instead of `ArrayList` |

---

## 16. Everything Together: Banking Example

```java
import java.util.List;

abstract class Account {                                   // abstraction
    private final String accNo;                            // encapsulation
    protected double balance;

    Account(String accNo, double openingBalance) {
        this.accNo = accNo;
        this.balance = openingBalance;
    }

    void deposit(double amt) {
        if (amt <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amt;
    }

    abstract void withdraw(double amt);                    // each type has its own rule
    abstract double monthlyInterest();

    public String getAccNo()  { return accNo; }
    public double getBalance() { return balance; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + accNo + ", balance=" + balance + "]";
    }
}

class SavingsAccount extends Account {                     // inheritance
    private static final double RATE = 0.04;

    SavingsAccount(String accNo, double opening) { super(accNo, opening); }

    @Override
    void withdraw(double amt) {
        if (amt > balance) throw new IllegalStateException("Insufficient funds");
        balance -= amt;
    }

    @Override
    double monthlyInterest() { return balance * RATE / 12; }
}

class CurrentAccount extends Account {
    private final double overdraftLimit;

    CurrentAccount(String accNo, double opening, double overdraftLimit) {
        super(accNo, opening);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    void withdraw(double amt) {
        if (amt > balance + overdraftLimit) throw new IllegalStateException("Overdraft limit exceeded");
        balance -= amt;
    }

    @Override
    double monthlyInterest() { return 0; }
}

public class Bank {
    public static void main(String[] args) {
        List<Account> accounts = List.of(
            new SavingsAccount("S1", 10000),
            new CurrentAccount("C1", 5000, 2000));

        for (Account a : accounts) {                       // polymorphism
            a.withdraw(6000);
            System.out.println("%s interest=%.2f".formatted(a, a.monthlyInterest()));
        }
    }
}
```

**Output:**

```
SavingsAccount[S1, balance=4000.0] interest=13.33
CurrentAccount[C1, balance=-1000.0] interest=0.00
```

| Pillar | Where |
|---|---|
| Encapsulation | `private accNo`; validation in `deposit` |
| Abstraction | `abstract class Account` hides each type's rules |
| Inheritance | `SavingsAccount` and `CurrentAccount` extend `Account`, calling `super(...)` |
| Polymorphism | The loop calls `withdraw` on `Account` references; each subtype responds differently |

---

## 17. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Inheriting just to reuse code with no IS-A | Wrong model, fragile design | Use composition |
| Parent has only a parameterized constructor, child has none | Compile error: implicit `super()` not found | Call `super(args)` explicitly |
| Forgetting `@Override` | A typo creates a new method silently | Always annotate |
| Weaker access when overriding (`public` to `protected`) | Compile error | Keep or widen access |
| Expecting fields to be polymorphic | Fields resolve by reference type | Use methods (getters) |
| Expecting `static` methods to override | They are hidden | Use instance methods |
| Calling an overridable method from a constructor | Child override runs **before** the child's fields are initialized | Avoid it, or make the method `final` or `private` |
| Wrong downcast | `ClassCastException` | Check with `instanceof` first |
| Overriding `equals` without `hashCode` | Breaks `HashMap` and `HashSet` | Override both, or use a record |
| `==` to compare strings or objects | Compares references | `equals()` |
| `this()` and `super()` in one constructor | Both must be first | Use only one |
| Implementing an interface method without `public` | Weaker access error | Declare `public` |
| Making a class `abstract final` | Contradiction | Pick one |
| Instantiating an abstract class or interface | Compile error | Instantiate a concrete subclass |
| Deep inheritance trees | Hard to understand and change | Flatten; use composition |
| Making every field `public` | No encapsulation | `private` plus accessors |
| Overusing getters and setters for everything | Encapsulation in name only | Expose behaviors (`withdraw`), not raw state |

---

## 18. Output Prediction Puzzles

Try each before reading the answers.

**Puzzle 1: fields and upcasting**

```java
class P { String name = "Parent"; }
class C extends P { String name = "Child"; }

P obj = new C();
System.out.println(obj.name);
```

**Puzzle 2: static hiding**

```java
class P { static void hi() { System.out.println("P"); } }
class C extends P { static void hi() { System.out.println("C"); } }

P obj = new C();
obj.hi();
```

**Puzzle 3: overridden method in a constructor**

```java
class P {
    P() { show(); }
    void show() { System.out.println("P show"); }
}
class C extends P {
    int x = 10;
    C() { super(); }
    @Override void show() { System.out.println("C show " + x); }
}

new C();
```

**Puzzle 4: overload resolution**

```java
static void f(int x)     { System.out.println("int"); }
static void f(long x)    { System.out.println("long"); }
static void f(Integer x) { System.out.println("Integer"); }
static void f(Object x)  { System.out.println("Object"); }

f(5);
f(5L);
byte b = 5;
f(b);
```

**Puzzle 5: `null` argument**

```java
static void g(String s) { System.out.println("String"); }
static void g(Object o) { System.out.println("Object"); }

g(null);
```

**Puzzle 6: runtime vs compile time**

```java
class Animal { void sound() { System.out.println("Animal"); } }
class Dog extends Animal {
    @Override void sound() { System.out.println("Dog"); }
    void fetch() { System.out.println("Fetch"); }
}

Animal a = new Dog();
a.sound();
a.fetch();
```

**Answers**

1. **`Parent`**. Fields are resolved by the **reference type** (`P`), not the object.
2. **`P`**. Static methods are hidden, not overridden, so the reference type decides.
3. **`C show 0`**. The parent constructor runs first and calls the overridden `show()`, but `C`'s field `x` has not yet been initialized, so it still holds the default `0`. Lesson: never call overridable methods from constructors.
4. **`int`, `long`, `int`**. `f(5)` is an exact match. `f(5L)` is an exact match for `long`. `f(b)` with a `byte` uses **widening** to the nearest match, `int`, which wins over boxing.
5. **`String`**. Java picks the **most specific** applicable overload (`String` is more specific than `Object`). If the choices were `String` and `Integer`, `g(null)` would be a compile-time ambiguity error.
6. **`Dog`** is printed for `a.sound()` (runtime dispatch). `a.fetch()` is a **compile error** because `Animal` has no `fetch()`. You would need `((Dog) a).fetch()`.

---

## 19. Interview Questions

**1. What is OOP?** A programming paradigm that models software as objects combining data and behavior.

**2. What are the four pillars?** Encapsulation, abstraction, inheritance, polymorphism.

**3. Class vs object?** A class is a blueprint; an object is an instance of it with its own state.

**4. Encapsulation vs abstraction?** Encapsulation hides **data** (private fields plus methods). Abstraction hides **complexity** (shows only essential operations).

**5. How is abstraction achieved?** With abstract classes and interfaces.

**6. What is inheritance?** A class acquiring the members of another with `extends`.

**7. Which inheritance types does Java support with classes?** Single, multilevel and hierarchical. Multiple inheritance is achieved only through interfaces.

**8. Why no multiple inheritance of classes?** The diamond problem: ambiguity over which parent's method to inherit.

**9. IS-A vs HAS-A?** IS-A is inheritance; HAS-A is composition or aggregation (a field holding another object).

**10. Uses of `super`?** Access the parent's field, call the parent's method, call the parent's constructor.

**11. Are constructors inherited?** No, but a child constructor always calls a parent constructor first.

**12. What if the parent has no no-arg constructor?** The child must call `super(args)` explicitly, or compilation fails.

**13. Can `static`, `private` or `final` methods be overridden?** No. Static methods are hidden, private methods are invisible, and final methods are prohibited from overriding.

**14. Overloading vs overriding?** Overloading: same name, different parameters, resolved at compile time. Overriding: same signature in a subclass, resolved at runtime.

**15. Compile-time vs runtime polymorphism?** Overloading vs overriding.

**16. What is dynamic method dispatch?** The JVM chooses the overridden method based on the object's actual type at run time.

**17. Upcasting vs downcasting?** Upcasting (child to parent) is implicit and safe. Downcasting needs a cast and may throw `ClassCastException`.

**18. Are fields polymorphic?** No. Fields resolve by reference type.

**19. What is a covariant return type?** An overriding method may return a subtype of the parent method's return type.

**20. Abstract class vs interface?** Abstract class: one parent, can hold state and constructors, for related classes. Interface: many allowed, a pure contract (plus default and static methods), for capabilities across unrelated classes.

**21. Can an abstract class have a constructor?** Yes; it runs via `super()` when a subclass is created.

**22. Can an interface have method bodies?** Yes: `default`, `static` and `private` methods (Java 8 and 9).

**23. How is the diamond problem handled for default methods?** The class must override and choose with `Interface.super.method()`. A concrete superclass method wins over interface defaults.

**24. What does `final` do on a variable, method and class?** Constant, no override, no subclassing.

**25. Does `final` make an object immutable?** No, only the reference is fixed.

**26. Access modifiers?** `private`, default, `protected`, `public`.

**27. What is the `Object` class?** The root of all classes, providing `toString`, `equals`, `hashCode`, `getClass`, etc.

**28. `==` vs `equals()`?** `==` compares references; `equals()` compares logical content when overridden.

**29. Why override `hashCode` with `equals`?** Equal objects must have equal hash codes, or hash-based collections fail.

**30. Composition vs inheritance?** Composition (HAS-A) is more flexible and less coupled. Prefer it unless a true IS-A relationship exists.

**31. How do you make an immutable class?** `final` class, `private final` fields, no setters, defensive copies of mutable inputs, or use a record.

**32. Can a class be both `abstract` and `final`?** No; `abstract` needs subclasses, `final` forbids them.

**33. Can an interface extend a class?** No. It can extend only other interfaces.

**34. Can `main` be overloaded or overridden?** It can be overloaded; being static, it cannot be overridden.

**35. Order of constructor execution in inheritance?** Top-down: the parent's static blocks, then the child's, then parent instance block and constructor, then the child's.

---

## 20. Cheat Sheet

| Keyword / concept | One-line meaning |
|---|---|
| `extends` | Class inherits from a class (or interface from interfaces) |
| `implements` | Class fulfills one or more interfaces |
| `super` | Parent's constructor, method or field |
| `this` | Current object |
| `@Override` | Compiler-checked overriding |
| `abstract` | Incomplete: must be extended or implemented |
| `final` | Cannot change, override or extend |
| `static` | Belongs to the class, not objects |
| `protected` | Package plus subclasses |
| `instanceof` | Runtime type test (with pattern variable in Java 16+) |
| `sealed` / `permits` | Restrict subclasses |

**Decision guide**

```
IS-A and shared code/state                -> abstract class
A capability / contract                   -> interface
HAS-A                                     -> composition (field)
Fixed set of known subtypes               -> sealed interface + records
Simple immutable data                     -> record
Need to prevent override / subclassing    -> final
```

**Polymorphism rule:** the **reference type** decides *what you can call*; the **object type** decides *which overridden method runs*.

---

## 21. Practice Tasks

1. **Account hierarchy:** extend the banking example with a `LoanAccount` whose `withdraw` is unsupported. Notice whether it truly satisfies "IS-A Account"; if not, redesign using interfaces (Liskov).
2. **Shapes:** create an abstract `Shape` with `area()` and `perimeter()`, subclasses `Circle`, `Rectangle`, `Triangle`, and a method that sums areas of a `List<Shape>`.
3. **Constructor order:** write a three-level hierarchy (`A`, `B`, `C`) with static blocks, instance blocks and constructors, and predict the output before running it.
4. **Interfaces:** define `Payment` with `pay(double)`, implement `CardPayment` and `UpiPayment`, add a `default receipt()` method, and write `checkout(Payment p)`.
5. **Equality:** write a `Student` class with `equals`, `hashCode` and `toString`; use it as a key in a `HashMap` and verify lookup works. Then remove `hashCode` and see what breaks.
6. **Refactor:** you have `class Logger extends ArrayList<String>`. Rewrite it using composition and explain what you gained.
7. **Puzzles:** write two of your own "predict the output" snippets on field hiding and constructor-calling-override, and test them.

### Challenge questions

1. A subclass `Child` overrides `show()` and the parent constructor calls `show()`. A `Child` field `String label = "ready"` is read inside `show()`. What does it print, and why?
2. Why is `class A extends B, C` illegal but `class A implements B, C` fine?
3. You must add a new payment type without editing any existing class. Which two OOP ideas make this possible?

**Answers**

1. It prints `null`. The parent constructor runs before the child's field initializers, so `label` still has its default.
2. Two parent **classes** could supply conflicting implementations and state (the diamond problem). Interfaces mainly declare contracts, and for the rare conflicting `default` methods Java forces you to resolve them explicitly.
3. **Polymorphism** (callers depend on the `Payment` interface) and the **Open/Closed principle** (extend by adding a new implementing class instead of modifying existing code).

---

*End of notes.*
