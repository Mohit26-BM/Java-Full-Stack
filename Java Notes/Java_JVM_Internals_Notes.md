# JVM Internals: Detailed Notes

How Java actually runs: JDK vs JRE vs JVM, compilation and bytecode, class loading, the runtime memory areas (heap, stack, metaspace), the JIT compiler, garbage collection and the collectors (Serial, Parallel, G1, ZGC), reference types, memory leaks and `OutOfMemoryError`, JVM options and tuning, diagnostic tools, and running the JVM in containers and Spring Boot.

> Examples target **modern HotSpot / OpenJDK (Java 17 and 21)**. Flag names and defaults can differ across versions and vendors; check `java -XX:+PrintFlagsFinal -version` on your JDK.

---

## Table of Contents

1. [The Big Picture: JDK, JRE, JVM](#1-the-big-picture-jdk-jre-jvm)
2. [From Source Code to Execution](#2-from-source-code-to-execution)
3. [Class Loading](#3-class-loading)
4. [Runtime Data Areas](#4-runtime-data-areas)
5. [The Execution Engine and the JIT Compiler](#5-the-execution-engine-and-the-jit-compiler)
6. [Garbage Collection Fundamentals](#6-garbage-collection-fundamentals)
7. [The Generational Heap and the GC Cycle](#7-the-generational-heap-and-the-gc-cycle)
8. [Garbage Collectors](#8-garbage-collectors)
9. [Reference Types](#9-reference-types)
10. [Memory Leaks and OutOfMemoryError](#10-memory-leaks-and-outofmemoryerror)
11. [JVM Options and Tuning](#11-jvm-options-and-tuning)
12. [Monitoring and Troubleshooting Tools](#12-monitoring-and-troubleshooting-tools)
13. [The JVM in Containers and Spring Boot](#13-the-jvm-in-containers-and-spring-boot)
14. [Common Mistakes](#14-common-mistakes)
15. [Output Prediction Puzzles](#15-output-prediction-puzzles)
16. [Interview Questions](#16-interview-questions)
17. [Cheat Sheet](#17-cheat-sheet)
18. [Practice Tasks with Solutions](#18-practice-tasks-with-solutions)

---

## 1. The Big Picture: JDK, JRE, JVM

### The three layers

```
JDK (Java Development Kit)
 +-- Development tools: javac, jar, javadoc, jdb, jlink, jshell, jcmd, jstack ...
 +-- JRE (Java Runtime Environment)
      +-- Class libraries (java.lang, java.util, java.io, ...)
      +-- JVM (Java Virtual Machine)
           +-- Class loader, memory areas, execution engine (interpreter + JIT), garbage collector
```

| | What it is | Who needs it |
|---|---|---|
| **JVM** | The engine that **loads and runs bytecode** | Everyone running Java |
| **JRE** | JVM plus the **standard libraries** | Anyone *running* Java programs |
| **JDK** | JRE plus **compiler and tools** | Developers (to *build* programs) |

**Analogy:** the **JVM** is the *engine*, the **JRE** is the *whole car* (engine plus the parts needed to drive), and the **JDK** is the *car plus a full workshop* (tools to build and repair cars).

Since Java 11, distributions ship mainly as a JDK; you can build a trimmed-down runtime with `jlink`.

### "Write once, run anywhere"

You compile once to **bytecode** (platform-independent). Each operating system has its **own JVM** that translates bytecode to that machine's instructions.

**Analogy:** a **travel adapter**. Your device's plug (bytecode) stays the same; the adapter (JVM) fits the local socket (Windows, Linux, macOS).

### JVM specification vs implementation

The **JVM Specification** defines the behavior; vendors implement it.

| Implementation | Notes |
|---|---|
| **HotSpot** (OpenJDK, Oracle, Temurin, Corretto...) | The standard, most widely used |
| **OpenJ9** (Eclipse/IBM) | Lower memory footprint, fast startup |
| **GraalVM** | Polyglot VM, Graal JIT, and **native image** (ahead-of-time) |

Other languages (Kotlin, Scala, Groovy, Clojure) also compile to JVM bytecode.

---

## 2. From Source Code to Execution

### The pipeline

```
Calc.java  --javac-->  Calc.class (bytecode)  --JVM-->  class loader
                                                          |
                                                          v
                                          bytecode verifier (safety checks)
                                                          |
                                                          v
                                   interpreter  +  JIT compiler (hot code)
                                                          |
                                                          v
                                               native machine code on the CPU
```

### Bytecode

**Bytecode** is a compact, stack-based instruction set for the JVM, stored in `.class` files.

```java
public class Calc {
    int add(int a, int b) {
        return a + b;
    }
}
```

Inspect it with the disassembler:

```
javap -c -p Calc
```

```
int add(int, int);
  Code:
     0: iload_1        // push local variable 1 (a) onto the operand stack
     1: iload_2        // push local variable 2 (b)
     2: iadd           // pop two ints, add, push the result
     3: ireturn        // return the int on top of the stack
```

(Local variable 0 is `this` in an instance method.)

### Common bytecode instruction families

| Family | Examples | Purpose |
|---|---|---|
| Load / store | `iload`, `aload`, `istore` | Move values between locals and the operand stack |
| Arithmetic | `iadd`, `imul`, `dsub` | Compute |
| Object | `new`, `getfield`, `putfield`, `getstatic` | Create objects, access fields |
| Invoke | `invokevirtual`, `invokestatic`, `invokespecial`, `invokeinterface`, `invokedynamic` | Call methods (`invokedynamic` powers lambdas and modern string concatenation) |
| Control | `if_icmpge`, `goto`, `tableswitch` | Branches and loops |
| Return | `ireturn`, `areturn`, `return` | Leave a method |

### The `.class` file

| Part | Contents |
|---|---|
| **Magic number** | `0xCAFEBABE` identifies a class file |
| **Version** | The Java version it targets |
| **Constant pool** | Literals, names, references |
| **Access flags** | `public`, `final`, `abstract`... |
| **Fields and methods** | Declarations and bytecode |
| **Attributes** | Debug info, annotations... |

| Java version | Class file version |
|---|---|
| 8 | 52 |
| 11 | 55 |
| 17 | 61 |
| 21 | 65 |

### Interpreted vs compiled

Java is **both**: bytecode starts **interpreted**, and frequently used ("hot") code is **JIT-compiled** to native machine code at run time (section 5).

---

## 3. Class Loading

### Lazy, on demand

The JVM does **not** load every class at startup. A class is loaded **the first time it is needed**.

**Analogy:** a library fetches a book from the stacks **only when someone asks for it**.

### The lifecycle of a class

```
Loading -> Linking ( Verification -> Preparation -> Resolution ) -> Initialization
```

| Phase | What happens |
|---|---|
| **Loading** | A class loader reads the `.class` bytes and creates the `Class` object |
| **Verification** | Checks the bytecode is valid and safe (correct format, no stack misuse, type rules) |
| **Preparation** | Allocates static fields and gives them **default values** (`0`, `null`, `false`) |
| **Resolution** | Replaces symbolic references in the constant pool with direct references (may happen lazily) |
| **Initialization** | Runs **static initializers and static blocks**, assigning real values (the class's `<clinit>`) |

```java
class Config {
    static int timeout = 30;                  // preparation: 0.   initialization: 30
    static { System.out.println("Config loaded"); }
}
```

### What triggers class initialization

| Triggers it | Does NOT trigger it |
|---|---|
| `new Foo()` | Reading a **compile-time constant** (`static final int X = 10;`, inlined by the compiler) |
| Calling a **static method** | Declaring a variable or array of the type (`Foo f;` / `Foo[] a = new Foo[3];`) |
| Reading or writing a **static field** (non-constant) | Using the class literal `Foo.class` |
| `Class.forName("Foo")` | |
| Initializing a **subclass** (the parent is initialized first) | |
| The class containing `main` | |

A class is initialized **only once**, in a thread-safe way.

### Class loaders

| Loader | Loads | Notes |
|---|---|---|
| **Bootstrap** | Core platform classes (`java.base`: `String`, `Object`...) | Written in native code; `String.class.getClassLoader()` returns **`null`** |
| **Platform** (called *Extension* before Java 9) | Other platform modules (`java.sql`...) | |
| **Application (System)** | **Your classes** from the classpath / module path | Default for application code |
| **Custom** | Anything you define | Application servers, plugins, hot reload |

```java
System.out.println(String.class.getClassLoader());            // null  (bootstrap)
System.out.println(Main.class.getClassLoader());              // ...ClassLoaders$AppClassLoader@...
System.out.println(Main.class.getClassLoader().getParent());  // ...ClassLoaders$PlatformClassLoader@...
```

### The delegation model (parent first)

```
Application loader   "Can you load com.example.Foo?"
        |  asks parent first
        v
Platform loader      asks its parent first
        |
        v
Bootstrap loader     can it load it?  no
        |
        v   (back down: each tries in turn)
Platform: no  ->  Application: yes, loads it
```

**Analogy:** a junior employee **asks their manager first** before doing a task themselves; the request escalates to the top, and only if nobody above can handle it does the junior do it.

**Why delegation:**

- **Security:** you cannot replace `java.lang.String` with a malicious version; the bootstrap loader always supplies the real one.
- **Uniqueness:** core classes are loaded once and shared.

### Class identity

> A class is identified by its **fully qualified name plus the class loader that loaded it.**

The same `.class` loaded by two different loaders produces **two different types**. Casting between them gives the notorious:

```
ClassCastException: class com.example.User cannot be cast to class com.example.User
```

This happens in application servers (each web app has its own loader) and hot-reload tools such as Spring Boot DevTools' restart loader.

### Custom class loader (skeleton)

```java
class MyLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = readBytesFromSomewhere(name);        // file, network, database...
        return defineClass(name, bytes, 0, bytes.length);
    }
}
```

Uses: **isolating web applications** (Tomcat), plugin systems, loading from encrypted or remote sources, hot reloading.

### Class loading errors

| Error | Meaning |
|---|---|
| **`ClassNotFoundException`** (checked) | A **dynamic** load (`Class.forName`, `loadClass`) could not find the class |
| **`NoClassDefFoundError`** (Error) | The class was there at **compile time** but is missing at **run time** (or its static initialization failed earlier) |
| **`ExceptionInInitializerError`** | An exception occurred in a static initializer |
| **`UnsupportedClassVersionError`** | Compiled with a **newer Java than the runtime**: "class file version 65.0, this version of the JVM only recognizes up to 61.0" |
| **`NoSuchMethodError`**, **`NoSuchFieldError`**, **`AbstractMethodError`** | Compiled against one library version, running with another ("**jar hell**" / dependency conflicts) |
| **`ClassCastException`** with identical names | The same class loaded by two loaders |

**Spring Boot tip:** `NoSuchMethodError` almost always means **conflicting dependency versions**; check `mvn dependency:tree`.

---

## 4. Runtime Data Areas

### The map

```
JVM process
+-------------------------------------------------------------------+
| SHARED BY ALL THREADS                                             |
|   Heap        objects, arrays, string pool, static field storage  |
|   Metaspace   class metadata, method bytecode, constant pool      |
|   Code cache  JIT-compiled native code                            |
+-------------------------------------------------------------------+
| PER THREAD                                                        |
|   JVM stack (frames)  |  PC register  |  Native method stack      |
+-------------------------------------------------------------------+
| OUTSIDE THE HEAP (native memory)                                  |
|   direct buffers, thread stacks, GC data structures, JIT data     |
+-------------------------------------------------------------------+
```

### 4.1 The heap

The area where **all objects and arrays** are allocated, shared by all threads, and managed by the **garbage collector**.

**Analogy:** a big **warehouse**. Anyone can store and fetch items, and a cleaning crew (GC) removes items nobody needs anymore.

- Controlled by `-Xms` (initial) and `-Xmx` (maximum).
- Out of space gives `OutOfMemoryError: Java heap space`.
- Divided into generations (section 7).
- The **string pool** lives here (since Java 7).

### 4.2 Metaspace (formerly PermGen)

Holds **class metadata**: class structures, method bytecode, the runtime constant pool.

| | PermGen (Java 7 and earlier) | Metaspace (Java 8+) |
|---|---|---|
| Location | Inside the JVM heap | **Native memory** (outside the heap) |
| Size | Fixed maximum (`-XX:MaxPermSize`) | Grows automatically; cap with `-XX:MaxMetaspaceSize` |
| Error | `OutOfMemoryError: PermGen space` | `OutOfMemoryError: Metaspace` |

Metaspace usage grows with the number of loaded classes (large applications, dynamic proxies, repeated redeploys that leak class loaders).

> **Static variables:** conceptually part of the "method area". In HotSpot (Java 8+), static field **values** live in the `Class` object on the **heap**, while the class metadata lives in Metaspace.

### 4.3 The JVM stack (one per thread)

Each thread has a private stack made of **frames**. Every method call **pushes** a frame; returning **pops** it.

```
Stack (grows upward)
+---------------------+
| frame: c()          |  <- top: currently running
+---------------------+
| frame: b()          |
+---------------------+
| frame: a()          |
+---------------------+
| frame: main()       |
+---------------------+
```

**Analogy:** a **pile of papers**. Each method call puts a new sheet on top for its notes; finishing a method removes its sheet. Every thread has its **own desk**.

A frame contains:

| Part | Holds |
|---|---|
| **Local variable array** | Parameters and local variables (primitives and **references**) |
| **Operand stack** | Working area for bytecode operations |
| **Frame data** | Reference to the constant pool, return address, exception handling info |

- **`StackOverflowError`** occurs when the stack is exhausted, typically from infinite or very deep **recursion**.
- Stack size per thread: `-Xss` (default around 1 MB on 64-bit platforms).
- Stack memory is reclaimed **instantly** when a method returns (no GC involved).

### 4.4 PC register and native method stack

- **Program Counter (PC) register:** per thread; holds the address of the **current bytecode instruction**.
- **Native method stack:** used when a thread calls **native (C/C++) code** through JNI.

### 4.5 Code cache

Stores **native code produced by the JIT compiler**. If it fills up, compilation stops and performance drops (`-XX:ReservedCodeCacheSize`).

### 4.6 Direct memory

`ByteBuffer.allocateDirect(...)` and NIO/Netty allocate **native memory outside the heap**, which the GC does not directly manage. Limit with `-XX:MaxDirectMemorySize`; running out gives `OutOfMemoryError: Direct buffer memory`.

### Where does each thing live?

| Item | Location |
|---|---|
| Local primitive (`int x = 5;`) | **Stack** frame |
| Local reference variable (`Student s`) | **Stack** frame (the reference) |
| The object `new Student()` | **Heap** |
| Instance fields | Inside the object, on the **heap** |
| Arrays | **Heap** |
| Static fields | `Class` object (**heap**) in HotSpot (conceptually the method area) |
| Class metadata, method bytecode, constant pool | **Metaspace** |
| String literals (interned) | **Heap** (string pool) |
| JIT-compiled code | **Code cache** |
| Direct buffers, thread stacks | **Native memory** |

```java
void demo() {
    int count = 5;                       // stack
    Student s = new Student("Asha");     // reference 's' on the stack; the Student object on the heap
}
```

When `demo()` returns, the frame disappears; the `Student` object becomes **eligible for GC** if nothing else references it.

> **Escape analysis** (section 5) can avoid allocating some objects on the heap at all, so "every object lives on the heap" is the *model*, with JIT optimizations as exceptions.

### Object memory layout

```
+---------------------+------------------------+------------------+
| Object header       | Instance fields        | Padding          |
| mark word + class   |                        | (to 8-byte       |
| pointer (12 bytes   |                        |  alignment)      |
| with compressed     |                        |                  |
| oops)               |                        |                  |
+---------------------+------------------------+------------------+
```

| Object | Approximate size (64-bit, compressed oops) |
|---|---|
| `new Object()` | **16 bytes** |
| `Integer` | 16 bytes |
| `Long` | 24 bytes (12 header + 8 value + 4 padding) |
| `int[10]` | 56 bytes (16 header with length + 40) |

**Insight:** boxing is expensive. A `List<Integer>` holding 1 million numbers uses about 20 MB (16 MB of `Integer` objects plus 4 MB of references), while an `int[]` uses 4 MB.

- The **mark word** stores the identity hash code, GC age and lock state.
- **Compressed oops** (default for heaps up to about 32 GB) use 32-bit references to save memory. Crossing roughly 32 GB disables it and increases memory use; so a 31 GB heap can hold more than a 33 GB one.
- Use the **JOL** (Java Object Layout) tool to inspect layouts.

---

## 5. The Execution Engine and the JIT Compiler

### Components

| Part | Role |
|---|---|
| **Interpreter** | Executes bytecode instruction by instruction (starts immediately, slow) |
| **JIT compiler** | Compiles hot bytecode to optimized native code |
| **Garbage collector** | Reclaims unused memory |
| **JNI** | Bridge to native libraries |

### Why both interpretation and compilation

| | Interpreter | JIT-compiled |
|---|---|---|
| Startup | **Instant** | Compile time cost |
| Speed | Slow | **Fast** |

**Analogy:** a live **translator** who translates sentence by sentence. When they hear the **same phrase repeatedly**, they write the translation down once and reuse it instantly. The JVM does this for "hot" methods and loops.

### Tiered compilation

```
Interpreter  --(counts calls and loop iterations)-->  C1 compiler  --(very hot)-->  C2 compiler
   level 0           "warm" code: fast compile,           "hot" code: slow compile,
                      light optimization, profiling        aggressive optimization
```

| Compiler | Character |
|---|---|
| **C1** (client) | Compiles quickly, modest optimization, gathers profiling data |
| **C2** (server) | Compiles slowly, **aggressive** optimization using the profile |

Tiered compilation is the **default**. HotSpot is named for hunting "hot spots".

### Key optimizations

| Optimization | What it does |
|---|---|
| **Inlining** | Replaces a small method call with its body (removes call overhead; enables further optimization) |
| **Escape analysis** | Detects objects that **never leave** a method: avoids heap allocation (scalar replacement) and **removes unnecessary locks** (lock elision) |
| **Dead-code elimination** | Removes code with no effect |
| **Loop unrolling** | Reduces loop overhead |
| **Devirtualization** | Turns a virtual call into a direct one when only one implementation is loaded |
| **Branch prediction / profile-guided layout** | Optimizes for the common path |

### Deoptimization

The JIT optimizes using **assumptions** (for example "only one subclass exists"). If an assumption later breaks (a new subclass is loaded), the JVM **discards** the compiled code and falls back to the interpreter, then recompiles.

### Warm-up and benchmarking

- A freshly started JVM is **slower** until hot code is compiled (**warm-up**).
- Naive timing loops (`System.nanoTime` around one run) give misleading numbers. Use **JMH**, which handles warm-up and prevents the JIT from removing the code under test.
- Watch with `-XX:+PrintCompilation`.

### Ahead-of-time compilation and faster startup

| Technique | Idea |
|---|---|
| **GraalVM Native Image** | Compile the whole app to a **native executable** ahead of time: very fast startup, low memory, but no JIT and build-time restrictions |
| **Class Data Sharing (CDS / AppCDS)** | Pre-process class metadata into a shared archive to **start faster** |
| **Project Leyden / CRaC** | Ongoing efforts for faster startup and warm-up (checkpoint and restore) |

---

## 6. Garbage Collection Fundamentals

### What is GC

> **Garbage collection** automatically finds objects that the program can **no longer reach** and reclaims their memory, so you do not call `free()`.

**Analogy:** a **cleaning crew** in an office. They don't ask who is finished with what; they check which items are still **connected to someone's desk**, and remove everything else.

### Reachability, not reference counting

An object is **alive** if it can be reached from a **GC root** through a chain of references. Anything else is garbage, **even if objects reference each other in a cycle**.

**GC roots:**

- Local variables and parameters on **thread stacks**
- **Static fields**
- Active **threads**
- JNI references
- Loaded **classes** and class loaders

```
Roots --> A --> B --> C         A, B, C are alive
         D <--> E               D and E reference each other, but no root reaches them -> garbage
```

### Basic steps

| Step | Description |
|---|---|
| **Mark** | Traverse from the roots and mark everything reachable |
| **Sweep** | Reclaim unmarked objects (leaves **fragmentation**) |
| **Compact** | Move live objects together to remove gaps |
| **Copy** | Copy live objects to a fresh region; the old region is cleared wholesale |

**Analogy:** after marking what to keep, **sweep** = throw away the rest; **compact** = rearrange furniture to remove gaps; **copy** = move the keepers to a new room and demolish the old one.

### The generational hypothesis

> **Most objects die young.** (Temporary strings, loop variables, request objects.)

So the heap is split into a **young** area, collected **often and cheaply**, and an **old** area for long-lived objects, collected **rarely**. This is far more efficient than scanning everything every time.

**Analogy:** your **inbox**: junk arrives and is deleted within minutes, so you check the *new mail tray* constantly. The *old archive* you rarely sift through.

### Stop-the-world (STW)

At certain moments the GC **pauses all application threads** (at **safepoints**) so it can work safely. Modern collectors minimize or overlap this with the application (**concurrent** collection).

| Term | Meaning |
|---|---|
| **Stop-the-world** | Application threads paused |
| **Concurrent** | GC works while the application runs |
| **Parallel** | GC uses several threads |
| **Throughput** | Share of time spent running application code (not GC) |
| **Latency / pause time** | How long the application freezes |
| **Footprint** | Memory used |

You cannot maximize all three: **throughput, latency and footprint trade off against each other.**

### Is an object eligible for GC?

```java
String s = new String("a");
s = null;                        // the object is now unreachable -> eligible

A a = new A(); B b = new B();
a.other = b; b.other = a;        // a cycle
a = null; b = null;              // both unreachable -> BOTH eligible (an "island of isolation")
```

Eligible does not mean "collected immediately"; the GC decides when to run.

---

## 7. The Generational Heap and the GC Cycle

### Heap layout

```
Heap
+---------------------------------------------+-------------------------+
|              Young generation               |     Old generation      |
|   Eden        |  S0 (from)  |  S1 (to)      |       (Tenured)         |
+---------------------------------------------+-------------------------+
```

| Area | Purpose |
|---|---|
| **Eden** | Where **new objects** are allocated |
| **Survivor spaces (S0, S1)** | Objects that survived at least one young collection; one is always empty |
| **Old generation** | **Long-lived** objects promoted from the young area |

(G1, ZGC and Shenandoah divide the heap into **regions** rather than fixed contiguous areas, but the same ideas apply.)

### Fast allocation: TLABs

Each thread gets a private chunk of Eden (a **Thread-Local Allocation Buffer**) and allocates by simply **bumping a pointer**, with no locking. Allocation is therefore very cheap.

### Walkthrough

```
1. new objects are allocated in EDEN.
2. Eden fills -> MINOR GC (stop-the-world, but quick):
      live objects in Eden + S0  --copied-->  S1,   their AGE increases
      Eden and S0 are wiped in one go.
3. Next minor GC: live objects in Eden + S1 are copied to S0 (roles swap).
4. An object whose age reaches the TENURING THRESHOLD (up to 15, adaptive)
   is PROMOTED to the Old generation.
5. The Old generation fills -> MAJOR / FULL GC (much slower).
```

| Event | Scope | Typical cost |
|---|---|---|
| **Minor / Young GC** | Young generation only | Short pause, frequent |
| **Major / Old GC** | Old generation (collector-dependent) | Longer |
| **Full GC** | Whole heap (and metaspace) | **Longest pause**; a warning sign if frequent |

**Analogy:** the *new mail tray* (Eden) is emptied constantly. Mail that survives a few rounds gets filed into the *archive* (Old). Cleaning the archive is a big job done rarely.

### What triggers GC

- Eden (or a region) is full.
- The old generation is nearly full, or promotion fails.
- Metaspace threshold reached.
- `System.gc()` (a **hint**, usually honored; avoid it in application code).
- Concurrent cycles starting at an occupancy threshold (G1/ZGC).

### Cross-generation references

Old objects can point to young ones. To avoid scanning the entire old generation during a minor GC, the JVM tracks such references with a **card table** (write barrier marks "dirty" memory cards).

### Reading a GC log

```
[0.512s][info][gc] GC(3) Pause Young (Normal) (G1 Evacuation Pause) 24M->8M(64M) 3.215ms
[3.870s][info][gc] GC(9) Pause Full (Allocation Failure) 120M->118M(128M) 450.123ms
```

Format: `heap before -> heap after (total committed heap)` and **pause duration**.

- First line: healthy. A young collection reclaimed 16 MB in 3 ms.
- Second line: **a red flag**. A Full GC reclaimed almost nothing (120M to 118M) and paused for 450 ms. That means a **memory leak** or a heap that is too small.

**Healthy vs leaking memory graph**

```
Healthy:   /\  /\  /\  /\      sawtooth returning to the same baseline
          /  \/  \/  \/  \
Leaking:     /\    /\    /\    the baseline keeps rising until OutOfMemoryError
           /\/  \/\/  \/\/
```

---

## 8. Garbage Collectors

| Collector | Flag | Character | Best for |
|---|---|---|---|
| **Serial** | `-XX:+UseSerialGC` | Single thread, stop-the-world | Tiny heaps, single-CPU or small containers |
| **Parallel** | `-XX:+UseParallelGC` | **Multi-threaded** STW; maximizes **throughput** | Batch jobs where pauses are acceptable (the Java 8 default) |
| **G1** (Garbage First) | `-XX:+UseG1GC` | Region-based, mostly concurrent, **predictable pauses** | **General purpose; default since Java 9** |
| **ZGC** | `-XX:+UseZGC` | Concurrent; pauses typically **under 1 ms** regardless of heap size | Latency-sensitive services, very large heaps |
| **Shenandoah** | `-XX:+UseShenandoahGC` | Concurrent compaction, low pauses (OpenJDK builds) | Low-latency alternative to ZGC |
| **Epsilon** | `-XX:+UseEpsilonGC` | **Does not collect** | Testing and extremely short-lived programs |
| ~~CMS~~ | | Deprecated in 9, **removed in 14** | (use G1) |

### G1 in more detail

- Divides the heap into many equal **regions** (1 to 32 MB). Each region plays a role: Eden, Survivor, Old, or **Humongous** (for objects larger than half a region).
- Collects the regions with **the most garbage first** ("garbage first"), within a **pause-time goal**: `-XX:MaxGCPauseMillis` (default 200 ms).
- Cycle: young collections, **concurrent marking** of the old regions, then **mixed collections** (young plus some old regions).
- A **Full GC** is a fallback when it cannot keep up.

### ZGC in more detail

- Marking, relocation and compaction happen **concurrently** with the application, using **colored pointers** and **load barriers**.
- Pause times stay in the sub-millisecond to low-millisecond range even for multi-terabyte heaps.
- Costs somewhat higher CPU and memory overhead.
- **Generational ZGC** arrived in Java 21 (`-XX:+UseZGC -XX:+ZGenerational`); later releases made generational mode the default.

### The default collector choice

The JVM picks **G1** by default, unless it considers the machine "small" (**fewer than 2 CPUs or less than about 1.8 GB of RAM**), in which case it picks **Serial**. So a **1-CPU container may silently run Serial GC**.

### Choosing

| Goal | Choice |
|---|---|
| Don't know yet / general web service | **G1** (default) |
| Maximum throughput, batch processing | **Parallel** |
| Strict latency (low p99), large heap | **ZGC** (or Shenandoah) |
| Tiny app, 1 CPU, small container | **Serial** |
| Measuring allocation behavior | Epsilon |

### Why `System.gc()` and `finalize()` are discouraged

- `System.gc()` can trigger an expensive Full GC at a bad moment (`-XX:+DisableExplicitGC` ignores it).
- **`finalize()`** is unpredictable, slow, can resurrect objects, and is **deprecated for removal** (Java 18+). Use **try-with-resources** or `java.lang.ref.Cleaner`.

---

## 9. Reference Types

Not every reference prevents collection.

| Type | Class | Collected when | Typical use |
|---|---|---|---|
| **Strong** | ordinary references | **Never** while reachable | Everything normal |
| **Soft** | `SoftReference` | Only when memory is **running low** (before `OutOfMemoryError`) | Memory-sensitive caches |
| **Weak** | `WeakReference` | At the **next GC** if only weakly reachable | `WeakHashMap`, canonicalizing maps, listeners |
| **Phantom** | `PhantomReference` | After the object is finalized; `get()` always returns `null` | Cleanup actions (with a `ReferenceQueue`) |

**Analogy:**

- **Strong:** a **leash**; the object is held no matter what.
- **Soft:** a **rubber band** that snaps only when things get tight.
- **Weak:** a **thread** that breaks at the next cleaning.
- **Phantom:** a **death notice** delivered after the object is gone.

```java
Object obj = new Object();
WeakReference<Object> weak = new WeakReference<>(obj);

System.out.println(weak.get() != null);   // true: obj is still strongly referenced

obj = null;                               // remove the only strong reference
System.gc();                              // a hint
System.out.println(weak.get());           // almost always null now
```

### `WeakHashMap`

Entries disappear when the **key** is no longer strongly referenced elsewhere. Good for metadata attached to objects without keeping them alive.

> A cache keyed by `WeakHashMap` loses entries aggressively. For real caches, use a library such as **Caffeine** with size and time-based eviction.

---

## 10. Memory Leaks and OutOfMemoryError

### What is a leak in Java?

The GC removes **unreachable** objects, so Java has no classic leaks of the "forgot to free" type. A Java **leak** is an object that is **still reachable but no longer needed**.

**Analogy:** a **hoarder's closet**. The cleaners cannot throw things away because you technically still "own" them, even though you never use them.

### Common causes

| Cause | Example | Fix |
|---|---|---|
| **Static collection that only grows** | `static List<Order> ALL = new ArrayList<>();` | Bound it, evict, or avoid static state |
| **Cache without eviction** | `HashMap` used as a cache forever | LRU/`LinkedHashMap`, Caffeine, soft/weak refs |
| **Unremoved listeners/callbacks** | Registered but never unregistered | `removeListener`, weak listeners |
| **Unclosed resources** | Streams, connections, `Statement`s | try-with-resources |
| **`ThreadLocal` in thread pools** | Value never `remove()`d | `finally { local.remove(); }` |
| **Inner class holding the outer instance** | Non-static inner or anonymous class | `static` nested class, or weak reference |
| **Class loader leaks** | Redeploying a web app keeps old classes (Metaspace grows) | Clean up threads, drivers, `ThreadLocal`s on shutdown |
| **Mutable keys in hash maps** | Key changes after insertion | Immutable keys |
| **Huge result sets in memory** | Loading a million rows with `findAll()` | Pagination, streaming |
| **Sessions or buffers retained** | HTTP sessions pile up | Timeouts, size limits |

```java
public class LeakDemo {
    static final List<byte[]> LEAK = new ArrayList<>();           // grows forever

    public static void main(String[] args) throws Exception {
        while (true) {
            LEAK.add(new byte[1024 * 1024]);                       // 1 MB, never released
            Thread.sleep(50);
        }
    }
}
// java -Xmx128m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./dump.hprof LeakDemo
```

### OutOfMemoryError variants

| Message | Cause | Typical fix |
|---|---|---|
| **`Java heap space`** | Heap full of live objects | Fix the leak; reduce data; raise `-Xmx` if truly needed |
| **`GC overhead limit exceeded`** | GC spends nearly all time reclaiming almost nothing | Same as above; leak or too-small heap |
| **`Metaspace`** | Too many classes or class loader leak | Fix redeploy leaks; raise `-XX:MaxMetaspaceSize` |
| **`unable to create native thread`** | Too many threads, OS limits, or not enough native memory | Pool threads; reduce `-Xss`; check `ulimit`; virtual threads |
| **`Direct buffer memory`** | NIO direct buffers exhausted | Release buffers; raise `-XX:MaxDirectMemorySize` |
| **`Requested array size exceeds VM limit`** | Trying to allocate an enormous array (such as `new int[Integer.MAX_VALUE]`) | Fix the size logic |
| **`Compressed class space`** | Class metadata area exhausted | Reduce class count; raise the limit |

### Diagnosing a leak, step by step

1. **Watch the pattern:** is the post-GC baseline **rising**? (GC logs, Actuator metrics `jvm.memory.used`, VisualVM.)
2. **Get a heap dump:** `-XX:+HeapDumpOnOutOfMemoryError` (set it in production), or `jcmd <pid> GC.heap_dump file.hprof`.
3. **Analyze** with **Eclipse MAT** or VisualVM: use the **histogram** (which classes dominate) and the **dominator tree** and **"path to GC roots"** (what holds them).
4. **Fix** the holder (the static map, the cache, the listener), and verify the baseline stays flat.
5. Take **two dumps** a few minutes apart and compare the growth.

### `StackOverflowError`

```java
static void recurse() { recurse(); }          // no base case: deep recursion exhausts the stack
```

Fix the **recursion** (add a base case, or convert to a loop). Raising `-Xss` only delays it.

### OS "OOM killer" vs Java OOM

If a container or machine runs out of **total** memory, the **operating system** may kill the process (exit code **137**, "OOMKilled" in Kubernetes) **without any Java exception**. This is different from `OutOfMemoryError`; see section 13.

---

## 11. JVM Options and Tuning

### Option types

| Prefix | Meaning |
|---|---|
| `-X...` | Standard non-portable options (`-Xmx`, `-Xms`, `-Xss`) |
| `-XX:+Flag` / `-XX:-Flag` | Enable / disable a boolean flag |
| `-XX:Name=value` | Set a value |
| `-D name=value` | A system property (`-Dspring.profiles.active=prod`) |

### Essential flags

| Flag | Purpose |
|---|---|
| `-Xms512m` / `-Xmx2g` | Initial / maximum **heap** |
| `-Xss512k` | **Thread stack** size |
| `-XX:MaxMetaspaceSize=256m` | Cap Metaspace |
| `-XX:MaxRAMPercentage=75` | Heap as a percentage of the container/system memory (default 25) |
| `-XX:+UseG1GC`, `-XX:+UseZGC`, `-XX:+UseParallelGC`, `-XX:+UseSerialGC` | Choose the collector |
| `-XX:MaxGCPauseMillis=200` | G1 pause-time goal |
| `-Xlog:gc*:file=gc.log` | **GC logging** (Java 9+; older: `-XX:+PrintGCDetails`) |
| `-XX:+HeapDumpOnOutOfMemoryError` `-XX:HeapDumpPath=/tmp` | Dump the heap on OOM |
| `-XX:+ExitOnOutOfMemoryError` | Exit so an orchestrator can restart a healthy instance |
| `-XX:+PrintCompilation` | Show JIT compilations |
| `-Xlog:class+load` / `-verbose:class` | Show class loading |
| `-XX:NativeMemoryTracking=summary` | Track native memory (then `jcmd <pid> VM.native_memory`) |
| `-XX:ReservedCodeCacheSize=256m` | JIT code cache size |

### Heap sizing guidelines

- Set **`-Xms` = `-Xmx`** on servers to avoid resizing pauses (not always necessary in containers).
- Size the heap from the **live data set** (the amount remaining after a full GC): roughly **3 to 4 times** the live set is a common starting point.
- **A larger heap is not automatically faster**: bigger heap means fewer collections but potentially longer pauses (depending on the collector).
- Stay under about **32 GB** to keep compressed object pointers.

### Total JVM memory is more than `-Xmx`

```
Process memory = heap (-Xmx)
               + Metaspace
               + thread stacks (threads x -Xss)
               + code cache
               + direct buffers
               + GC and JIT internal structures
```

A container limit of 1 GB with `-Xmx1g` **will be OOM-killed**. Leave room: with `MaxRAMPercentage`, typically **60 to 75%**.

### Tuning approach

1. **Measure first** (GC logs, metrics, JFR). Don't tune by folklore.
2. Define the goal: **latency** (p99), **throughput**, or **footprint**.
3. Pick the collector for the goal; change **one thing at a time**.
4. **Reduce allocation** in hot paths (avoid needless boxing, string concatenation in loops, large temporary collections). It is usually better than GC tuning.
5. Keep default settings unless you have evidence. Modern JVMs adapt well.
6. **Object pooling** is rarely beneficial for ordinary objects (allocation is cheap and the GC is good at short-lived garbage), except for expensive resources such as connections.

### A sensible production start for a Spring Boot service

```
java -XX:MaxRAMPercentage=70 \
     -XX:+UseG1GC \
     -Xlog:gc*:file=/var/log/app/gc.log:time,uptime:filecount=5,filesize=20m \
     -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/app \
     -XX:+ExitOnOutOfMemoryError \
     -jar app.jar
```

---

## 12. Monitoring and Troubleshooting Tools

### Command-line tools (in the JDK `bin`)

| Tool | Purpose | Example |
|---|---|---|
| **`jps`** | List Java processes | `jps -l` |
| **`jcmd`** | The Swiss Army knife: heap info, dumps, thread dumps, flags, JFR | `jcmd <pid> GC.heap_info` |
| **`jstack`** | Thread dump (and deadlock detection) | `jstack <pid>` |
| **`jmap`** | Heap histogram and dump | `jmap -histo:live <pid>` |
| **`jstat`** | Live GC and memory statistics | `jstat -gcutil <pid> 1000` |
| **`jinfo`** | Show or change JVM flags | `jinfo -flags <pid>` |
| **`javap`** | Disassemble bytecode | `javap -c -p Calc` |

```bash
jps -l                                              # find the PID
jcmd 12345 Thread.print                             # thread dump
jcmd 12345 GC.heap_dump /tmp/heap.hprof             # heap dump
jstat -gcutil 12345 1000                            # GC utilization every second
jmap -histo:live 12345 | head -20                   # top classes by instance count
```

### `jstat -gcutil` columns

| Column | Meaning |
|---|---|
| `S0`, `S1` | Survivor space utilization (%) |
| `E` | Eden utilization (%) |
| `O` | Old generation utilization (%) |
| `M` | Metaspace utilization (%) |
| `YGC`, `YGCT` | Young GC count and total time |
| `FGC`, `FGCT` | **Full GC count and total time** (a growing number is a warning) |
| `GCT` | Total GC time |

### GUI and profiling tools

| Tool | Use |
|---|---|
| **VisualVM** | Live monitoring, sampling, heap dumps, threads |
| **JConsole** | MBean-based monitoring |
| **JDK Flight Recorder (JFR) + JDK Mission Control** | Low-overhead production profiling and event recording |
| **Eclipse MAT** | Heap dump analysis (dominator tree, leak suspects) |
| **async-profiler** | CPU and allocation flame graphs |
| **GC log analyzers** (GCeasy and others) | Visualize GC logs |
| **Spring Boot Actuator + Micrometer** | `jvm.memory.used`, `jvm.gc.pause`, `jvm.threads.live`, with Prometheus and Grafana |

```bash
java -XX:StartFlightRecording=duration=60s,filename=rec.jfr -jar app.jar
jcmd <pid> JFR.start duration=60s filename=rec.jfr        # on a running JVM
```

### Which tool for which symptom

| Symptom | Start with |
|---|---|
| App is slow or CPU is high | Thread dumps (`jstack`), JFR, async-profiler |
| Application hangs | `jstack` (deadlock section), thread dump comparison |
| Memory keeps growing | GC logs, `jstat`, heap dump plus MAT |
| Long pauses | GC logs; consider G1 tuning or ZGC |
| `OutOfMemoryError` | The heap dump written on OOM; MAT |
| Slow startup | `-Xlog:class+load`, CDS, native image, reduce component scanning |

---

## 13. The JVM in Containers and Spring Boot

### Container awareness

Since Java 10 (and backported to 8u191), the JVM reads **container CPU and memory limits** (`-XX:+UseContainerSupport`, on by default).

| Fact | Consequence |
|---|---|
| Default max heap is **25%** of container memory | Often too low; set `-XX:MaxRAMPercentage=60` to `75` |
| `availableProcessors()` follows the **CPU limit** | A 1-CPU limit means the JVM sizes GC and JIT threads accordingly |
| **Fewer than 2 CPUs or under about 1.8 GB** means **Serial GC** is auto-selected | Set the collector explicitly if you want G1 |
| Heap is only part of memory use | Set the container limit **above** the heap (section 11) |
| Kubernetes **OOMKilled** (exit 137) is the OS, not Java | Look at total process memory, not just heap |

### Dockerfile sketch

```dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar /app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

`JAVA_TOOL_OPTIONS` lets you set flags without changing the command.

### Spring Boot specifics

| Topic | Notes |
|---|---|
| **Fat JAR** | Spring Boot's launcher loads nested JARs with its own class loader, so startup includes class scanning |
| **Startup time** | Reduce component scanning, lazy initialization (`spring.main.lazy-initialization=true`), AppCDS, native image |
| **Memory** | Many beans and proxies mean more classes in Metaspace |
| **Thread count** | Tomcat (200 threads by default) plus pools plus the connection pool; stack memory adds up |
| **Virtual threads (Java 21)** | `spring.threads.virtual.enabled=true`: cheap blocking threads |
| **Actuator** | `/actuator/metrics/jvm.memory.used`, `/actuator/heapdump`, `/actuator/threaddump` (secure these endpoints) |
| **Graceful shutdown** | `server.shutdown=graceful`; JVM **shutdown hooks** run on SIGTERM |

### JVM (JIT) vs GraalVM native image

| | JVM + JIT | Native image (AOT) |
|---|---|---|
| Startup | Slower (seconds) | **Very fast** (milliseconds) |
| Memory | Higher | **Lower** |
| Peak throughput | **Higher** after warm-up | Usually lower |
| Build | Fast | **Slow and heavy** |
| Reflection, proxies, dynamic features | Free | Need configuration (Spring Boot 3 helps with AOT processing) |
| Debugging and profiling | Rich tooling | More limited |
| Best for | Long-running services | Serverless, CLI tools, scale-to-zero |

### JVM startup and shutdown (briefly)

- **Startup:** parse options, reserve heap, load bootstrap classes, initialize, load the main class, call `main`.
- **Shutdown** occurs when the last **non-daemon** thread ends, `System.exit()` is called, or the process receives a termination signal. **Shutdown hooks** (`Runtime.getRuntime().addShutdownHook(thread)`) run for cleanup.

---

## 14. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Believing Java programs cannot leak memory | Retained references still leak | Look for static holders, caches, listeners |
| Calling `System.gc()` "to free memory" | Triggers costly Full GCs; only a hint | Don't |
| Setting `-Xmx` equal to the container limit | Total process memory exceeds the limit, so OOMKilled | `MaxRAMPercentage` of 60 to 75 |
| Ignoring that 1-CPU containers get Serial GC | Surprising pause behavior | Choose the GC explicitly |
| Tuning GC flags without measuring | Worse or no improvement | GC logs, metrics, JFR first |
| Huge heaps "just in case" | Longer GC work, wasted memory | Size from the live set |
| Heap above 32 GB without need | Loses compressed oops | Stay under, or accept the cost knowingly |
| Using `finalize()` | Unpredictable, deprecated | try-with-resources, `Cleaner` |
| Benchmarking with a naive loop | Warm-up and JIT effects distort results | JMH |
| Expecting objects to be collected immediately after `= null` | GC runs when it decides | Don't rely on timing |
| Using `WeakHashMap` as a cache | Entries vanish too eagerly | Caffeine or similar |
| Unbounded caches and static maps | Slow leak, then OOM | Eviction and limits |
| Leaving `ThreadLocal` values in pools | Leaks and cross-request contamination | `remove()` in `finally` |
| Raising `-Xss` to cure `StackOverflowError` | Hides runaway recursion | Fix the recursion |
| Conflicting dependency versions | `NoSuchMethodError` | `mvn dependency:tree`, align versions |
| Compiling with a newer JDK than the runtime | `UnsupportedClassVersionError` | Match versions or use `--release` |
| Boxing in hot loops | Millions of temporary objects | Primitive types, primitive streams |
| Not capturing a heap dump on OOM | No evidence after a production crash | `-XX:+HeapDumpOnOutOfMemoryError` |
| Exposing `/actuator/heapdump` publicly | Leaks secrets in memory | Secure or disable |
| Confusing `ClassNotFoundException` and `NoClassDefFoundError` | Wrong diagnosis | Dynamic load failure vs missing at run time |
| Assuming all objects are on the heap, or all `static` data in Metaspace | Incorrect mental model | See section 4 |

---

## 15. Output Prediction Puzzles

**Puzzle 1: class initialization triggers**

```java
class A {
    static { System.out.println("A initialized"); }
    static final int CONST = 10;
    static int value = 20;
}

public class Main {
    public static void main(String[] args) {
        System.out.println(A.CONST);
        System.out.println(A.value);
    }
}
```

**Puzzle 2: class loaders**

```java
System.out.println(String.class.getClassLoader());
System.out.println(Main.class.getClassLoader() != null);
```

**Puzzle 3: weak reference**

```java
Object obj = new Object();
WeakReference<Object> w = new WeakReference<>(obj);
System.out.println(w.get() != null);
obj = null;
System.gc();
System.out.println(w.get());
```

**Puzzle 4: eligible for GC?**

```java
A a = new A();
B b = new B();
a.other = b;
b.other = a;
a = null;
b = null;
// Are the two objects eligible for garbage collection?
```

**Puzzle 5: catching `StackOverflowError`**

```java
static int depth = 0;
static void recurse() { depth++; recurse(); }

try { recurse(); }
catch (StackOverflowError e) { System.out.println("caught, depth > 1000: " + (depth > 1000)); }
```

**Puzzle 6: where does it live?** For each, name the memory area:

```java
class Student { int age; static String school = "LPU"; }

void demo() {
    int count = 5;
    Student s = new Student();
    String name = "Asha";
}
```

**Puzzle 7: boxing overhead**

Roughly how much more memory does a `List<Integer>` of 1,000,000 numbers need than an `int[]` of 1,000,000?

**Puzzle 8: which OutOfMemoryError?**

```java
int[] a = new int[Integer.MAX_VALUE];
```

**Puzzle 9: the second line of this GC log**

```
[3.870s][info][gc] GC(9) Pause Full (Allocation Failure) 120M->118M(128M) 450.123ms
```

**Puzzle 10: duplicated class**

An application server loads `com.example.User` through two different web-app class loaders and passes an instance between them. What happens, and why?

**Answers**

1. **`10`, then `A initialized`, then `20`.** `CONST` is a compile-time constant, so the compiler inlines `10` and the class is not initialized. Reading the non-constant `A.value` triggers initialization (static block runs first, then the value is read).
2. **`null`**, then **`true`**. Core classes come from the bootstrap loader (represented as `null`); your class comes from the application loader.
3. **`true`**, then **almost always `null`**. After the only strong reference is removed, the object is weakly reachable and is collected at the next GC (`System.gc()` is a hint, but HotSpot typically honors it).
4. **Yes, both.** The JVM uses reachability from GC roots, not reference counting, so the cycle (an "island of isolation") is collectable.
5. **`caught, depth > 1000: true`** (typical depth is in the thousands). `StackOverflowError` is an `Error` and can be caught, but recovery is usually not meaningful; fix the recursion.
6. `count`: **stack** frame. `s` (the reference): **stack**; the `Student` object: **heap**. `age`: inside the object, on the **heap**. `school`: static storage (conceptually the method area; the value lives in the `Class` object on the heap in HotSpot). `name` (the reference): **stack**; the `"Asha"` string: the **string pool** (heap). The `Student` class metadata: **Metaspace**.
7. About **16 MB more**. The `List<Integer>` needs about 20 MB (16 MB of `Integer` objects at 16 bytes each, plus about 4 MB of references) versus about 4 MB for the `int[]` (4 bytes per element). Values from -128 to 127 are cached, so real numbers vary.
8. **`OutOfMemoryError: Requested array size exceeds VM limit`** (the requested length is above what the JVM allows). A smaller but still huge array would give `Java heap space`.
9. A **Full GC** (stop-the-world) took 450 ms yet reclaimed almost nothing (120 MB to 118 MB, with the heap nearly full at 128 MB). This is a classic **memory leak** or **undersized heap** signature, and an `OutOfMemoryError` is likely soon.
10. **`ClassCastException: class com.example.User cannot be cast to class com.example.User`.** A class is identified by its name **plus its defining class loader**, so two loaders produce two different types.

---

## 16. Interview Questions

**1. JDK vs JRE vs JVM?** The JVM runs bytecode; the JRE is the JVM plus libraries; the JDK is the JRE plus development tools like `javac`.

**2. Why is Java platform independent?** Source compiles to platform-neutral bytecode, and each platform has its own JVM that executes it.

**3. What is bytecode?** The JVM's platform-independent instruction set in `.class` files.

**4. Is Java compiled or interpreted?** Both: compiled to bytecode, then interpreted and JIT-compiled to native code at run time.

**5. What does the JIT compiler do?** Compiles frequently executed bytecode into optimized native code while the program runs.

**6. What is tiered compilation (C1 and C2)?** Code starts interpreted, then C1 compiles it quickly, and very hot code is recompiled by C2 with aggressive optimization.

**7. What is escape analysis?** A JIT analysis that finds objects that never leave a method, so the JVM can avoid heap allocation and remove locks.

**8. What are the class loading phases?** Loading, linking (verification, preparation, resolution), and initialization.

**9. What are the types of class loaders?** Bootstrap, Platform (formerly Extension), Application, and custom loaders.

**10. Explain the parent delegation model.** A loader first asks its parent to load a class, and only loads it itself if the parent cannot. This protects core classes and keeps them unique.

**11. How is a class uniquely identified?** By its fully qualified name plus its defining class loader.

**12. `ClassNotFoundException` vs `NoClassDefFoundError`?** The first is a checked exception from a failed dynamic load; the second is an `Error` when a class available at compile time is missing at run time (or its static initialization failed).

**13. What triggers class initialization?** `new`, static method call, non-constant static field access, `Class.forName`, subclass initialization, and the main class.

**14. What are the runtime data areas?** Heap, Metaspace (method area), JVM stacks, PC registers, native method stacks, plus the code cache and direct memory.

**15. Stack vs heap?** The stack is per thread and holds frames (locals, references); the heap is shared and holds objects. Stack memory is freed on return; heap memory is reclaimed by GC.

**16. What is in a stack frame?** The local variable array, the operand stack, and frame data (constant pool reference, return address).

**17. What causes `StackOverflowError`?** Excessively deep or infinite recursion exhausting the thread stack.

**18. What is Metaspace, and how does it differ from PermGen?** Metaspace holds class metadata in native memory and grows automatically; PermGen was a fixed-size area inside the heap, removed in Java 8.

**19. Where is the string pool?** In the heap (since Java 7).

**20. What is the heap structure?** Young generation (Eden plus two survivor spaces) and old generation.

**21. Why is the heap generational?** Most objects die young, so frequent cheap collections of the young area are far more efficient than scanning everything.

**22. What is a minor GC vs a major/full GC?** A minor GC collects the young generation (fast, frequent); a full GC collects the whole heap (slow, a warning sign if frequent).

**23. What are GC roots?** Starting points for reachability: thread stack locals, static fields, active threads, JNI references, loaded classes.

**24. Does Java use reference counting?** No, it uses reachability tracing, so cyclic garbage is collected.

**25. What is stop-the-world?** A pause where all application threads halt so the GC can work safely.

**26. Name the garbage collectors.** Serial, Parallel, G1, ZGC, Shenandoah, Epsilon (and the removed CMS).

**27. What is the default GC?** G1 since Java 9, unless the machine is small (under 2 CPUs or about 1.8 GB), when Serial is chosen.

**28. How does G1 work?** It splits the heap into regions, collects the regions with the most garbage first, and aims for a pause-time goal using young, concurrent marking and mixed collections.

**29. What makes ZGC special?** Concurrent collection with sub-millisecond pauses regardless of heap size.

**30. Strong, soft, weak and phantom references?** Never collected while reachable; collected when memory is tight; collected at the next GC; used for post-mortem cleanup.

**31. Can Java have memory leaks?** Yes: objects that remain reachable but are no longer needed (static collections, caches, listeners, `ThreadLocal`s).

**32. Types of `OutOfMemoryError`?** Heap space, GC overhead limit exceeded, Metaspace, unable to create native thread, direct buffer memory.

**33. How do you analyze a memory leak?** Heap dump on OOM (or `jcmd GC.heap_dump`), analyze with Eclipse MAT or VisualVM (histogram, dominator tree, paths to GC roots), compare successive dumps.

**34. What do `-Xms`, `-Xmx` and `-Xss` do?** Initial heap, maximum heap, and per-thread stack size.

**35. Why shouldn't you call `System.gc()`?** It is only a hint and can trigger costly Full GCs; the JVM manages collection better.

**36. Why is `finalize()` discouraged?** It is unpredictable, slow, and deprecated for removal; use try-with-resources or `Cleaner`.

**37. How do you size the heap in a container?** Use `-XX:MaxRAMPercentage` (about 60 to 75), leaving room for Metaspace, thread stacks, the code cache and direct buffers.

**38. What are compressed oops?** 32-bit object references used on 64-bit JVMs for heaps up to about 32 GB, reducing memory use.

**39. What is deoptimization?** Discarding JIT-compiled code when an optimization assumption proves false, falling back to the interpreter.

**40. Why does a JVM benchmark need warm-up?** Early runs are interpreted or partially compiled; steady-state speed appears only after JIT compilation. Use JMH.

**41. Which tools monitor a JVM?** `jps`, `jcmd`, `jstack`, `jmap`, `jstat`, VisualVM, JFR and Mission Control, MAT, and Actuator metrics.

**42. What is GraalVM native image?** Ahead-of-time compilation into a native executable: fast startup and low memory, but no JIT and extra build-time configuration.

**43. What is JNI?** The Java Native Interface for calling native C/C++ code.

**44. What does `invokedynamic` do?** A bytecode instruction enabling dynamic call-site linking; it powers lambdas and modern string concatenation.

**45. What does a Kubernetes "OOMKilled" mean for a Java app?** The OS killed the process because total memory (heap, native, stacks) exceeded the container limit; it is not a Java `OutOfMemoryError`.

---

## 17. Cheat Sheet

**Execution pipeline**

```
.java --javac--> .class (bytecode) --> class loader --> verify --> interpreter + JIT --> machine code
```

**Memory map**

| Area | Holds | Shared? | Error when full |
|---|---|---|---|
| **Heap** | Objects, arrays, string pool | Yes | `OutOfMemoryError: Java heap space` |
| **Metaspace** | Class metadata (native memory) | Yes | `OutOfMemoryError: Metaspace` |
| **Stack** | Frames (locals, references) | Per thread | `StackOverflowError` |
| **Code cache** | JIT native code | Yes | Compilation stops |
| **Direct memory** | NIO buffers | Yes | `OutOfMemoryError: Direct buffer memory` |

**Heap**

```
Young: Eden + S0 + S1   (minor GC, frequent, fast)   --promotion-->   Old: Tenured   (full GC, rare, slow)
```

**Collectors**

| Collector | Flag | Use |
|---|---|---|
| G1 | default | General purpose |
| Parallel | `-XX:+UseParallelGC` | Throughput |
| ZGC | `-XX:+UseZGC` | Ultra-low latency |
| Serial | `-XX:+UseSerialGC` | Tiny apps |

**Flags to remember**

```
-Xms -Xmx -Xss            heap sizes and thread stack
-XX:MaxRAMPercentage=70   container-friendly heap sizing
-Xlog:gc*                 GC logging
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=...
```

**Golden rules**

```
1. Class identity = name + class loader; loaders delegate to the parent first.
2. Locals/references on the stack; objects on the heap; class metadata in Metaspace.
3. GC = reachability from roots; cycles are collected; leaks = still-reachable-but-unused.
4. Most objects die young: young GC is cheap, Full GC is a warning sign.
5. Measure before tuning; reduce allocation before tweaking the GC.
6. Container memory limit > heap + metaspace + stacks + native; use MaxRAMPercentage.
7. Always enable heap dumps on OOM in production.
```

---

## 18. Practice Tasks with Solutions

**1. Read bytecode.** Compile this and run `javap -c -p Counter`. Explain each instruction of `increment()`.

```java
public class Counter {
    private int count;
    public void increment() { count++; }
}
```

Expect `aload_0`, `dup`, `getfield`, `iconst_1`, `iadd`, `putfield`, `return`. This shows that `count++` is **read, add, write**, which is why it is not atomic (see the concurrency notes).

**2. Watch class loading.** Run any program with `java -Xlog:class+load -cp . Main` (or `-verbose:class`) and find where your classes load, and which ones load lazily only when first used.

**3. Prove lazy initialization.** Reproduce Puzzle 1 and add a second class with a `static final String` constant versus a `static final Object` to see which triggers initialization.

**4. Cause and analyze a `StackOverflowError`.** Write the recursion from Puzzle 5, print the depth, then run with `-Xss256k` and `-Xss4m` and compare the reached depth.

**5. Create an `OutOfMemoryError` and analyze it with MAT.**

```bash
java -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./dump.hprof LeakDemo
```

Open `dump.hprof` in Eclipse MAT, run the **Leak Suspects** report, and find the `ArrayList` holding the `byte[]` arrays. Then fix the leak (bound the list) and verify that memory stays flat.

**6. Read a GC log.** Run a program that allocates short-lived objects and some long-lived ones:

```bash
java -Xmx128m -Xlog:gc*:file=gc.log MyApp
```

Identify young pauses, any mixed collections, and whether a Full GC happens. Compare the **sawtooth baseline** for a leaking and a non-leaking version.

**7. Compare collectors.** Run the same allocation-heavy program with `-XX:+UseSerialGC`, `-XX:+UseParallelGC`, `-XX:+UseG1GC` and `-XX:+UseZGC`. Compare total runtime (throughput) and maximum pause (from the GC logs).

**8. Weak references.** Build a `WeakHashMap<Object, String>`, insert one entry keyed by an object you then null out, call `System.gc()`, and print the map size.

```java
Map<Object, String> map = new WeakHashMap<>();
Object key = new Object();
map.put(key, "value");
System.out.println(map.size());      // 1
key = null;
System.gc();
Thread.sleep(100);
System.out.println(map.size());      // typically 0
```

**9. Monitor a live JVM.** Start a Spring Boot app, then use `jps`, `jstat -gcutil <pid> 1000`, `jcmd <pid> Thread.print` and VisualVM. Hit the app with load and watch Eden fill and clear.

**10. See the JIT.** Run a hot loop with `-XX:+PrintCompilation` and observe methods being compiled. Time the first 1,000 iterations versus later ones to see **warm-up**.

**11. Container sizing.** Run a container with a 512 MB limit and the default JVM settings, and print `Runtime.getRuntime().maxMemory()`. Then add `-XX:MaxRAMPercentage=70` and compare. Predict the result first (about 128 MB, then about 358 MB).

### Challenge questions

1. A service shows steady memory growth until it gets `OutOfMemoryError` every few days. You can only run a few commands in production. What do you do?
2. Why might a service on a 1-CPU Kubernetes pod show long GC pauses even though the same code ran smoothly on your laptop?
3. After raising `-Xmx` from 1 GB to 2 GB (container limit 2 GB), the pod starts getting OOMKilled. Why?

**Answers**

1. Capture evidence: enable (or run) `jcmd <pid> GC.heap_dump` plus `jcmd <pid> GC.class_histogram` at two points in time and compare; set `-XX:+HeapDumpOnOutOfMemoryError` so the next crash leaves a dump; enable GC logging to confirm a rising post-GC baseline. Analyze with MAT's dominator tree and paths to GC roots to find the retaining structure, then fix it (bounded cache, listener removal, `ThreadLocal.remove()`).
2. A 1-CPU container makes the JVM select **Serial GC** (and size threads for one CPU), so all collection is single-threaded stop-the-world. Your laptop likely used G1 with several cores. Set the collector deliberately (`-XX:+UseG1GC` if the CPU limit permits) or give the pod more CPU.
3. Total process memory is more than the heap: Metaspace, thread stacks, code cache, direct buffers and GC structures sit on top of `-Xmx`. With a 2 GB heap and a 2 GB limit, the process exceeds the limit and the OS kills it (exit 137). Use `-XX:MaxRAMPercentage=60` to `75` and leave headroom.

---

*End of notes.*
