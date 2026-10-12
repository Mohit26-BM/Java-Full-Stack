# Java Multithreading and Concurrency: Detailed Notes

Threads from scratch, then thread safety (race conditions, `synchronized`, `volatile`, the memory model), inter-thread communication, deadlock, the `java.util.concurrent` toolkit (executors, locks, atomics, concurrent collections, synchronizers), `CompletableFuture`, virtual threads (Java 21), and concurrency in Spring Boot, with puzzles, interview questions and practice tasks.

> **Note on output:** the order of output from concurrent programs can vary between runs. Examples say where that is the case. Where an example is deterministic, it is because it uses `join`, a latch or a similar guarantee.

---

## Table of Contents

1. [Why Concurrency](#1-why-concurrency)
2. [Creating and Running Threads](#2-creating-and-running-threads)
3. [Thread Lifecycle and Control Methods](#3-thread-lifecycle-and-control-methods)
4. [The Core Problem: Shared Mutable State](#4-the-core-problem-shared-mutable-state)
5. [synchronized](#5-synchronized)
6. [volatile and the Java Memory Model](#6-volatile-and-the-java-memory-model)
7. [wait, notify and Producer-Consumer](#7-wait-notify-and-producer-consumer)
8. [Deadlock, Livelock, Starvation](#8-deadlock-livelock-starvation)
9. [Executors and Thread Pools](#9-executors-and-thread-pools)
10. [Callable, Future and CompletableFuture](#10-callable-future-and-completablefuture)
11. [Locks](#11-locks)
12. [Atomic Classes](#12-atomic-classes)
13. [Concurrent Collections](#13-concurrent-collections)
14. [Synchronizers](#14-synchronizers)
15. [ThreadLocal](#15-threadlocal)
16. [Fork/Join and Parallel Streams](#16-forkjoin-and-parallel-streams)
17. [Virtual Threads (Java 21)](#17-virtual-threads-java-21)
18. [Concurrency in Spring Boot](#18-concurrency-in-spring-boot)
19. [Best Practices](#19-best-practices)
20. [Common Mistakes](#20-common-mistakes)
21. [Output Prediction Puzzles](#21-output-prediction-puzzles)
22. [Interview Questions](#22-interview-questions)
23. [Cheat Sheet](#23-cheat-sheet)
24. [Practice Tasks with Solutions](#24-practice-tasks-with-solutions)

---

## 1. Why Concurrency

### Process vs thread

| | Process | Thread |
|---|---|---|
| What | A running **program** with its own memory | A **path of execution inside** a process |
| Memory | **Separate** | **Shared** with other threads of the same process (heap), but each has its **own stack** |
| Creation cost | Heavy | Lighter |
| Communication | Hard (pipes, sockets) | Easy (shared objects), which is also the danger |
| Crash impact | Isolated | A bug can affect all threads |

**Analogy: a restaurant.** The **restaurant** is a process (its own building, kitchen, stock). The **chefs** are threads: several people working in the **same kitchen** and sharing the same stove, fridge and ingredients. Sharing is efficient, but two chefs grabbing the last egg at the same moment causes trouble.

### Concurrency vs parallelism

| | Concurrency | Parallelism |
|---|---|---|
| Meaning | **Dealing with** many tasks at once (interleaving) | **Doing** many tasks at the same instant |
| Needs | One core is enough | **Multiple cores** |
| Analogy | **One chef** switching between several dishes | **Several chefs** cooking different dishes simultaneously |

### Why use threads

- **Responsiveness:** keep a UI or server responsive while a slow task runs.
- **Throughput:** serve many web requests at once (Tomcat uses a thread per request).
- **Use waiting time:** while one thread waits for a database or network, another computes.
- **Use all CPU cores** for heavy computation.

### The price

- **Race conditions, deadlocks, visibility bugs**: hard to reproduce and debug.
- Context-switching overhead.
- More complex code and testing.

> **Principle:** the best concurrent code **avoids shared mutable state** altogether. Everything below is about handling it safely when you cannot.

---

## 2. Creating and Running Threads

### Way 1: extend `Thread`

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Running in " + getName());
    }
}

new MyThread().start();
```

### Way 2: implement `Runnable` (preferred)

```java
Runnable task = () -> System.out.println("Hello from " + Thread.currentThread().getName());

Thread t = new Thread(task, "worker-1");
t.start();
```

| | `extends Thread` | `implements Runnable` |
|---|---|---|
| Inheritance | Uses up your **one** superclass | Class can still extend something else |
| Design | Mixes the **task** and the **thread** | Separates **what to run** from **how to run it** |
| Reuse with pools | Awkward | Natural (executors take `Runnable`) |
| Verdict | Rarely preferred | **Prefer this** |

### Way 3: `Callable` (returns a value, can throw)

```java
ExecutorService ex = Executors.newFixedThreadPool(2);
Callable<Integer> job = () -> { Thread.sleep(500); return 42; };

Future<Integer> future = ex.submit(job);
Integer result = future.get();        // waits for the result
ex.shutdown();
```

| | `Runnable` | `Callable<V>` |
|---|---|---|
| Method | `void run()` | `V call() throws Exception` |
| Returns a result | No | **Yes** |
| Throws checked exceptions | No | Yes |

### `start()` vs `run()` (a classic trap)

```java
Thread t = new Thread(() -> System.out.println(Thread.currentThread().getName()));

t.run();      // runs in the CURRENT thread: prints "main"; no new thread is created
t.start();    // creates a NEW thread, which then calls run(): prints e.g. "Thread-0"
```

- **`start()`** asks the JVM to create a new thread and call `run()` on it.
- **Calling `run()` directly** is just an ordinary method call.
- Calling `start()` **twice** on the same thread throws `IllegalThreadStateException`.

### The `main` thread

A Java program starts with one thread, **`main`**. The JVM exits when all **non-daemon** threads have finished.

---

## 3. Thread Lifecycle and Control Methods

### States (`Thread.State`)

```
          start()                       run() ends
  NEW ------------> RUNNABLE ---------------------------> TERMINATED
                      |   ^
        waiting for   |   | lock acquired / notified / timeout / joined thread ends
        a lock        v   |
                   BLOCKED       WAITING        TIMED_WAITING
              (monitor lock)  (wait(), join(),  (sleep(ms), wait(ms),
                               park())           join(ms))
```

| State | Meaning |
|---|---|
| `NEW` | Created, not started |
| `RUNNABLE` | Running, or ready to run (waiting for CPU time) |
| `BLOCKED` | Waiting to **acquire a monitor lock** (`synchronized`) |
| `WAITING` | Waiting indefinitely for another thread (`wait()`, `join()`) |
| `TIMED_WAITING` | Waiting with a timeout (`sleep`, `wait(ms)`, `join(ms)`) |
| `TERMINATED` | Finished |

### Key methods

| Method | Behavior |
|---|---|
| `start()` | Begin execution in a new thread |
| `Thread.sleep(ms)` | Pause the **current** thread; **does not release locks** |
| `join()` / `join(ms)` | Wait for **another** thread to finish |
| `interrupt()` | **Request** that a thread stop what it is doing (cooperative) |
| `isInterrupted()` / `Thread.interrupted()` | Check the flag (the static one also **clears** it) |
| `isAlive()` | Started and not finished? |
| `Thread.currentThread()` | The running thread |
| `setName()` / `getName()` | Name threads for debugging |
| `setDaemon(true)` | Make it a background thread (**before** `start()`) |
| `setPriority(1..10)` | A **hint** to the scheduler, not a guarantee |
| `Thread.yield()` | Hint to let others run |
| `getState()` | The current state |

### `join`: waiting for a thread

```java
Thread t = new Thread(() -> System.out.println("child"));
t.start();
t.join();                              // main waits here
System.out.println("parent");          // always printed after "child"
```

Without `join`, the order of "child" and "parent" is not guaranteed.

### Interruption (cooperative cancellation)

`interrupt()` does **not** force a thread to stop; it sets a flag (or wakes the thread from `sleep`/`wait`, throwing `InterruptedException`). The thread decides how to react.

```java
Thread worker = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
        try {
            Thread.sleep(1000);
            System.out.println("working");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();     // restore the flag (it was cleared)
            break;                                  // and stop
        }
    }
    System.out.println("cleaning up, then exiting");
});

worker.start();
Thread.sleep(3500);
worker.interrupt();
```

**Analogy:** tapping a colleague on the shoulder. They finish what they are doing safely and then decide to stop. (`Thread.stop()` is the dangerous equivalent of yanking the power cord; it is unsafe and no longer usable.)

**Rule:** when you catch `InterruptedException` and cannot handle it fully, **restore the interrupt flag** (`Thread.currentThread().interrupt()`) or rethrow it. Do not swallow it.

### Daemon threads

Background helpers (garbage collection, monitoring) that **do not keep the JVM alive**. When only daemon threads remain, the JVM exits, abruptly stopping them (so they must not do critical work like writing files).

**Analogy:** cleaners who leave when the restaurant closes.

### Exceptions in threads

An uncaught exception **terminates only that thread**; the others (and `main`) continue.

```java
Thread t = new Thread(() -> { throw new RuntimeException("boom"); });
t.start();
t.join();
System.out.println("main continues");   // still printed (the stack trace appears on stderr)
```

Set a handler: `Thread.setDefaultUncaughtExceptionHandler((th, ex) -> log(th, ex));`

---

## 4. The Core Problem: Shared Mutable State

### The race condition

```java
class Counter {
    int count = 0;
    void increment() { count++; }
}

Counter c = new Counter();
Runnable r = () -> { for (int i = 0; i < 100_000; i++) c.increment(); };

Thread t1 = new Thread(r);
Thread t2 = new Thread(r);
t1.start(); t2.start();
t1.join();  t2.join();

System.out.println(c.count);       // expected 200000, usually LESS (varies every run)
```

### Why: `count++` is three steps, not one

```
1. READ  count     (from memory)
2. ADD   1
3. WRITE count     (back to memory)
```

Two threads can interleave:

```
count = 5
Thread A: READ 5
Thread B: READ 5
Thread A: WRITE 6
Thread B: WRITE 6        <- one increment is LOST; count should be 7
```

**Analogy:** two cashiers booking the **last seat** on a flight at the same moment, each checking "available", then each selling it. Or two people editing the same cell in a shared sheet: the later save overwrites the earlier one (a **lost update**).

### The three hazards

| Hazard | Problem | Fix |
|---|---|---|
| **Atomicity** | Compound actions (`count++`, check-then-act) can be interleaved | `synchronized`, locks, atomics |
| **Visibility** | One thread's write may not be seen by another (CPU caches, compiler optimization) | `volatile`, `synchronized`, `final` |
| **Ordering** | The compiler/CPU may reorder instructions | The memory model rules (`volatile`, `synchronized`) |

### Vocabulary

- **Critical section:** code that touches shared state and must not run concurrently.
- **Thread-safe:** behaves correctly when used by multiple threads without external coordination.
- **Check-then-act** bugs: `if (!map.containsKey(k)) map.put(k, v)`: another thread can act between the check and the act.

### Four ways to fix the counter (preview)

```java
// 1. synchronized
synchronized void increment() { count++; }

// 2. AtomicInteger
private final AtomicInteger count = new AtomicInteger();
void increment() { count.incrementAndGet(); }

// 3. ReentrantLock
lock.lock(); try { count++; } finally { lock.unlock(); }

// 4. LongAdder (best under heavy contention)
private final LongAdder count = new LongAdder();
void increment() { count.increment(); }
```

---

## 5. synchronized

### What it does

> `synchronized` ensures that **only one thread at a time** executes a block of code guarded by the same **lock (monitor)**, and that changes made inside are **visible** to the next thread that takes that lock.

**Analogy:** a **single-occupancy washroom with one key**. Whoever holds the key goes in; everyone else waits in line. When the occupant leaves and returns the key, the next person enters and sees the room as it was left (visibility).

Every Java object has an **intrinsic lock** (monitor).

### Synchronized method

```java
class Counter {
    private int count;

    public synchronized void increment() {       // lock = this
        count++;
    }

    public synchronized int get() {
        return count;
    }
}
```

### Synchronized block (finer control)

```java
class Counter {
    private int count;
    private final Object lock = new Object();    // a private lock object

    public void increment() {
        // ... code that needs no lock ...
        synchronized (lock) {                    // only the critical section is locked
            count++;
        }
    }
}
```

### Which lock is used?

| Form | Lock |
|---|---|
| `synchronized void m()` | `this` (the instance) |
| `static synchronized void m()` | `ClassName.class` (the class object) |
| `synchronized (obj) { ... }` | `obj` |

An **instance** lock and a **class** lock are **different locks**: a `static synchronized` method and an instance `synchronized` method do **not** block each other.

### Properties

| Property | Detail |
|---|---|
| **Mutual exclusion** | One thread at a time per lock |
| **Visibility** | Releasing a lock publishes all writes; acquiring reads them |
| **Reentrant** | A thread holding a lock can enter another `synchronized` block on the **same** lock without deadlocking itself |
| **Automatic release** | Released even if an exception occurs |
| **Not fair** | No guarantee of ordering among waiting threads |
| **Not interruptible** | A thread waiting for the lock cannot be interrupted (use `ReentrantLock.lockInterruptibly`) |
| **Blocked state** | A thread waiting to enter is `BLOCKED` |

### Rules and good practice

- **Lock on a private final object**, not on `this` or a public object. Outside code can lock `this` too and interfere.
- **Never lock on** a `String` literal, a boxed `Integer`, or a field that gets reassigned (shared or changing identity).
- Keep critical sections **short**; do not do slow I/O while holding a lock.
- **Guard all access** to shared state (reads too), with the **same** lock.
- Do not call unknown "alien" code (callbacks, overridable methods) while holding a lock; it invites deadlock.

### Synchronized collections vs concurrent collections

`Collections.synchronizedList(...)`, `Vector`, `Hashtable` lock the **whole structure** per call. Compound actions (iterate, check-then-act) still need manual locking. Prefer **concurrent collections** (section 13).

### Thread-safe singleton

```java
// Double-checked locking (volatile is REQUIRED)
public class Singleton {
    private static volatile Singleton instance;
    private Singleton() { }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// Simpler and safe: initialization-on-demand holder
public class Singleton2 {
    private Singleton2() { }
    private static class Holder { static final Singleton2 INSTANCE = new Singleton2(); }
    public static Singleton2 getInstance() { return Holder.INSTANCE; }
}
```

(An `enum` singleton is also thread-safe.)

---

## 6. volatile and the Java Memory Model

### The visibility problem

Each CPU core has **caches**, and the compiler may optimize reads. A thread may keep using a stale copy of a variable.

```java
class Worker {
    private boolean running = true;              // NOT volatile

    void work() {
        while (running) { /* spin */ }           // may NEVER see the change below
    }

    void stop() { running = false; }
}
```

Another thread calls `stop()`, but `work()` can loop forever.

**Analogy:** a **shared notice board** vs a **private note copied earlier**. A reader using the stale note never sees the update on the board.

### The fix: `volatile`

```java
private volatile boolean running = true;
```

`volatile` guarantees:

- **Visibility:** a write is immediately visible to all threads' subsequent reads.
- **Ordering:** reads and writes around it are not reordered across the volatile access.

### What `volatile` does NOT do

**It does not make compound operations atomic.**

```java
private volatile int count;
void increment() { count++; }        // STILL a race condition: read-add-write is three steps
```

| | `volatile` | `synchronized` / atomics |
|---|---|---|
| Visibility | Yes | Yes |
| Atomicity of compound actions | **No** | Yes |
| Blocking | No | Yes (`synchronized`) |

### When to use `volatile`

- A **flag** written by one thread and read by others (`running`, `shutdownRequested`).
- Safe publication of an immutable reference (as in double-checked locking).
- **Single writer** scenarios or independent reads and writes.

### The Java Memory Model: "happens-before"

The JMM defines when one thread's writes are guaranteed visible to another. Key rules:

| Rule | Guarantee |
|---|---|
| **Monitor lock** | An unlock **happens-before** every later lock of the same monitor |
| **volatile** | A write **happens-before** every later read of that variable |
| **Thread start** | `thread.start()` happens-before any action in that thread |
| **Thread join** | Everything a thread did happens-before another thread's successful `join()` on it |
| **final fields** | Correctly constructed `final` fields are visible without synchronization |

### Safe sharing strategies (in order of preference)

1. **Don't share:** keep data **thread-confined** (local variables, `ThreadLocal`).
2. **Share immutable data:** objects that never change are inherently thread-safe (`String`, records with immutable fields, `List.of`).
3. **Share mutable data with proper synchronization.**

---

## 7. wait, notify and Producer-Consumer

### What and why

Threads sometimes need to **wait for a condition** (such as "buffer not empty") and be **told** when it changes, instead of wasting CPU in a busy loop.

| Method | Meaning |
|---|---|
| `wait()` | Release the lock and sleep until notified |
| `notify()` | Wake **one** waiting thread |
| `notifyAll()` | Wake **all** waiting threads |

**Analogy:** the **kitchen pass in a restaurant**. A waiter waits for the chef's bell (`wait`); the chef rings it when a dish is ready (`notify`). The waiter does not stand there checking every second.

### Rules

- Must be called **while holding the object's lock** (inside `synchronized` on that object), or you get `IllegalMonitorStateException`.
- `wait()` **releases** the lock while waiting, and **re-acquires** it before returning.
- **Always call `wait()` inside a `while` loop** that rechecks the condition (guards against spurious wakeups and conditions changing before you run). Never `if`.
- Prefer `notifyAll()` unless you are certain only one waiter can proceed.

### `sleep` vs `wait`

| | `sleep()` | `wait()` |
|---|---|---|
| Class | `Thread` (static) | `Object` |
| Releases the lock? | **No** | **Yes** |
| Needs a lock held? | No | **Yes** |
| Wakes up by | Time passing (or interrupt) | `notify` / `notifyAll` (or timeout/interrupt) |
| Purpose | Pause | Coordinate between threads |

### Bounded buffer with `wait` and `notifyAll`

```java
class Buffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity = 5;

    public synchronized void produce(int item) throws InterruptedException {
        while (queue.size() == capacity) {      // full: wait
            wait();
        }
        queue.add(item);
        notifyAll();                            // tell consumers there is data
    }

    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {               // empty: wait
            wait();
        }
        int item = queue.remove();
        notifyAll();                            // tell producers there is space
        return item;
    }
}
```

### The modern way: `BlockingQueue`

```java
BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

Thread producer = new Thread(() -> {
    try {
        for (int i = 1; i <= 10; i++) {
            queue.put(i);                       // blocks when full
            System.out.println("Produced " + i);
        }
        queue.put(-1);                          // "poison pill": tells the consumer to stop
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
});

Thread consumer = new Thread(() -> {
    try {
        while (true) {
            int v = queue.take();               // blocks when empty
            if (v == -1) break;
            System.out.println("Consumed " + v);
        }
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
});

producer.start(); consumer.start();
```

`BlockingQueue` does all the waiting and signalling for you. **Prefer it** to hand-written `wait`/`notify`.

---

## 8. Deadlock, Livelock, Starvation

### Deadlock

> Two or more threads are **blocked forever**, each waiting for a lock the other holds.

**Analogy:** two cars meet on a **one-lane bridge** and neither will reverse. Or two people each holding one chopstick, each waiting for the other's.

```java
Object lockA = new Object();
Object lockB = new Object();

Thread t1 = new Thread(() -> {
    synchronized (lockA) {
        pause(100);
        synchronized (lockB) { System.out.println("t1 done"); }     // waits for B
    }
});

Thread t2 = new Thread(() -> {
    synchronized (lockB) {
        pause(100);
        synchronized (lockA) { System.out.println("t2 done"); }     // waits for A
    }
});

t1.start(); t2.start();      // typically hangs forever
```

### The four conditions (all must hold)

1. **Mutual exclusion:** a resource is held by one thread at a time.
2. **Hold and wait:** a thread holds one lock while waiting for another.
3. **No preemption:** locks cannot be taken away.
4. **Circular wait:** a cycle of threads each waiting for the next.

**Break any one** to prevent deadlock.

### Prevention

| Strategy | How |
|---|---|
| **Consistent lock ordering** (the most practical) | Always acquire locks in the **same global order** |
| **`tryLock` with timeout** | Back off and retry if a lock cannot be taken |
| **Avoid nested locks** | Hold one lock at a time |
| **Fewer, coarser locks** or lock-free structures | Reduce lock interactions |
| **Don't call alien code while holding a lock** | Callbacks can take other locks |

**Fix for the example above:** both threads lock `lockA` first, then `lockB`.

### Bank transfer with ordered locks

```java
void transfer(Account from, Account to, double amount) {
    Account first  = from.id < to.id ? from : to;      // fixed order by id
    Account second = first == from ? to : from;

    synchronized (first) {
        synchronized (second) {
            from.balance -= amount;
            to.balance   += amount;
        }
    }
}
```

Without ordering, `transfer(A, B)` and `transfer(B, A)` running at the same time can deadlock.

### Detecting deadlock

- `jps` to find the process id, then **`jstack <pid>`**: it reports "Found one Java-level deadlock" with the involved threads and locks.
- **VisualVM** or **JConsole**: "Detect Deadlock".
- Thread dumps: `kill -3 <pid>` (Unix) prints them.

### Livelock and starvation

| Problem | Meaning | Analogy |
|---|---|---|
| **Livelock** | Threads keep reacting to each other and **make no progress**, but are not blocked | Two people in a corridor both step aside the same way, repeatedly |
| **Starvation** | A thread **never gets** the resource or CPU because others always take it | Someone never getting served because VIP customers keep arriving |

Fairness (`new ReentrantLock(true)`) and bounded work reduce starvation.

---

## 9. Executors and Thread Pools

### Why not `new Thread()` everywhere?

- Creating a thread is **expensive** (memory for a stack, OS resources).
- **Unbounded** thread creation can crash the application under load.
- Hard to manage lifecycle, results and errors.

### The idea

A **thread pool** keeps a fixed set of reusable worker threads that take tasks from a **queue**.

**Analogy:** a **call center** with a fixed number of agents. Callers (tasks) wait in a queue; when an agent finishes one call, they take the next. You don't hire and fire an agent for each call.

### `ExecutorService`

```java
ExecutorService pool = Executors.newFixedThreadPool(4);

List<Future<Integer>> futures = new ArrayList<>();
for (int i = 1; i <= 5; i++) {
    final int n = i;
    futures.add(pool.submit(() -> n * n));         // Callable<Integer>
}

for (Future<Integer> f : futures) {
    System.out.println(f.get());                   // 1 4 9 16 25 (in submission order)
}

pool.shutdown();                                   // no new tasks; finish queued ones
pool.awaitTermination(10, TimeUnit.SECONDS);
```

**Java 19+: try-with-resources** (the pool waits for tasks to finish and shuts down):

```java
try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
    pool.submit(() -> doWork());
}
```

### Factory methods

| Method | Behavior | Caution |
|---|---|---|
| `newFixedThreadPool(n)` | `n` threads, **unbounded** queue | Queue can grow without limit |
| `newCachedThreadPool()` | Creates threads as needed, reuses idle ones | **Unbounded threads** can exhaust memory |
| `newSingleThreadExecutor()` | One thread, tasks in order | |
| `newScheduledThreadPool(n)` | Delayed and periodic tasks | |
| `newWorkStealingPool()` | `ForkJoinPool` with work stealing | |
| `newVirtualThreadPerTaskExecutor()` | One **virtual thread** per task (Java 21) | See section 17 |

### `shutdown` vs `shutdownNow`

| Method | Effect |
|---|---|
| `shutdown()` | Stop accepting tasks; **finish** queued and running ones |
| `shutdownNow()` | Try to stop running tasks (interrupt) and **return** the queued ones |
| `awaitTermination(t, unit)` | Block until tasks finish or the timeout passes |

> **A pool that is not shut down keeps its (non-daemon) threads alive, so the JVM never exits.** Always shut pools down.

### `submit` vs `execute`

| | `execute(Runnable)` | `submit(Runnable/Callable)` |
|---|---|---|
| Returns | `void` | A **`Future`** |
| Exception in the task | Printed by the thread's handler | **Captured in the `Future`**, silently lost unless you call `get()` |

```java
pool.submit(() -> { throw new RuntimeException("x"); });    // nothing printed!
pool.submit(() -> { ... }).get();                           // get() throws ExecutionException with the cause
```

### `ThreadPoolExecutor`: the production way

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    4,                                           // core pool size
    8,                                           // maximum pool size
    60, TimeUnit.SECONDS,                        // idle time for threads above core
    new ArrayBlockingQueue<>(100),               // BOUNDED work queue
    new ThreadPoolExecutor.CallerRunsPolicy());  // what to do when full
```

How a new task is handled:

```
1. fewer than core threads running  -> start a new thread
2. else, queue not full             -> put it in the queue
3. else, fewer than max threads     -> start an extra thread
4. else                             -> REJECT (rejection policy)
```

| Rejection policy | Behavior |
|---|---|
| `AbortPolicy` (default) | Throws `RejectedExecutionException` |
| `CallerRunsPolicy` | The submitting thread runs the task (natural back-pressure) |
| `DiscardPolicy` | Silently drops the task |
| `DiscardOldestPolicy` | Drops the oldest queued task |

### Sizing a pool

| Workload | Threads |
|---|---|
| **CPU-bound** (computation) | About the number of cores (`Runtime.getRuntime().availableProcessors()`) or cores + 1 |
| **I/O-bound** (waiting on DB/network) | More: roughly `cores x (1 + wait time / compute time)` |

Measure rather than guess, and use **bounded queues** so overload is visible.

### Scheduled tasks

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

scheduler.schedule(() -> System.out.println("once, after 2s"), 2, TimeUnit.SECONDS);
scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);       // every 5s from each START
scheduler.scheduleWithFixedDelay(task, 0, 5, TimeUnit.SECONDS);    // 5s AFTER each task finishes
```

An uncaught exception in a periodic task **cancels future runs**, so catch exceptions inside the task.

---

## 10. Callable, Future and CompletableFuture

### `Future`

A **placeholder for a result that will be available later**.

**Analogy:** the **buzzer or token** at a restaurant counter. You place the order and carry on; when the buzzer rings, you collect the food.

| Method | Purpose |
|---|---|
| `get()` | **Block** until done and return the result (throws `ExecutionException` if the task failed) |
| `get(timeout, unit)` | Wait at most this long |
| `isDone()` | Finished? |
| `cancel(mayInterrupt)` | Try to cancel |
| `isCancelled()` | Was it cancelled? |

Limitation: `Future.get()` **blocks**, and you cannot easily **chain** or **combine** futures. That is what `CompletableFuture` adds.

### `CompletableFuture` (Java 8)

A future that you can **compose**, **combine**, and attach **callbacks** to, without blocking.

**Analogy:** a **recipe pipeline**: "when the vegetables are chopped, then fry them; prepare the sauce in parallel; when both are ready, combine and serve."

```java
CompletableFuture.supplyAsync(() -> "hello")       // run asynchronously, producing a value
    .thenApply(String::toUpperCase)                // transform the result
    .thenAccept(System.out::println);              // consume it   -> HELLO
```

### Creating

| Method | Purpose |
|---|---|
| `supplyAsync(supplier)` | Run and **return a value** |
| `runAsync(runnable)` | Run, **no value** |
| `completedFuture(v)` | Already completed (useful in tests and fallbacks) |
| `supplyAsync(supplier, executor)` | Run on **your** executor |

### Transforming and chaining

| Method | Meaning |
|---|---|
| `thenApply(fn)` | Transform the result (like `map`) |
| `thenAccept(consumer)` | Use the result, return nothing |
| `thenRun(runnable)` | Run something after, ignoring the result |
| `thenCompose(fn)` | Chain a **dependent async step** (like `flatMap`) |
| `thenCombine(other, fn)` | **Combine two independent** futures |
| `allOf(f1, f2, ...)` | Completes when **all** complete |
| `anyOf(f1, f2, ...)` | Completes when **any** completes |

### Error handling

| Method | Meaning |
|---|---|
| `exceptionally(fn)` | Recover from an exception with a fallback value |
| `handle((result, ex) -> ...)` | Handle both success and failure |
| `whenComplete((result, ex) -> ...)` | Observe the outcome, don't change it |
| `orTimeout(t, unit)` (Java 9) | Fail with `TimeoutException` after a delay |
| `completeOnTimeout(value, t, unit)` | Use a default value on timeout |

### `get` vs `join`

| | `get()` | `join()` |
|---|---|---|
| Exceptions | Checked: `InterruptedException`, `ExecutionException` | Unchecked: `CompletionException` |
| Use in | Traditional code | Lambdas and streams |

### Real example: call two services in parallel (microservices style)

```java
CompletableFuture<Customer> customerF =
    CompletableFuture.supplyAsync(() -> customerService.get(id), ioExecutor);

CompletableFuture<List<Account>> accountsF =
    CompletableFuture.supplyAsync(() -> accountClient.getAccounts(id), ioExecutor);

CustomerDetails details = customerF
    .thenCombine(accountsF, CustomerDetails::new)           // when BOTH are ready
    .orTimeout(2, TimeUnit.SECONDS)
    .exceptionally(ex -> CustomerDetails.unavailable(id))   // fallback
    .join();
```

If each call takes 300 ms, sequential code takes about 600 ms; this takes about 300 ms.

### Dependent steps: `thenCompose`

```java
CompletableFuture<List<Order>> orders =
    CompletableFuture.supplyAsync(() -> findUserId("asha"))
        .thenCompose(userId -> CompletableFuture.supplyAsync(() -> loadOrders(userId)));
```

### Waiting for many

```java
List<CompletableFuture<String>> futures = urls.stream()
    .map(u -> CompletableFuture.supplyAsync(() -> fetch(u), ioExecutor))
    .toList();

CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
List<String> results = futures.stream().map(CompletableFuture::join).toList();
```

### Important: the default executor

`supplyAsync` without an executor uses the shared **`ForkJoinPool.commonPool()`**, sized by CPU cores. **Blocking I/O in it can starve** other work (including parallel streams). For I/O, **pass your own executor**.

---

## 11. Locks

### `ReentrantLock`

```java
private final ReentrantLock lock = new ReentrantLock();

void update() {
    lock.lock();
    try {
        // critical section
    } finally {
        lock.unlock();                // ALWAYS unlock in finally
    }
}
```

Features beyond `synchronized`:

```java
if (lock.tryLock(1, TimeUnit.SECONDS)) {            // wait at most 1s
    try { ... } finally { lock.unlock(); }
} else {
    // could not get the lock: do something else (avoids deadlock)
}

lock.lockInterruptibly();                           // waiting can be interrupted
new ReentrantLock(true);                            // fair: longest-waiting thread first
```

### `Condition`: `wait/notify` for locks

```java
Condition notEmpty = lock.newCondition();

lock.lock();
try {
    while (queue.isEmpty()) notEmpty.await();       // like wait()
    ...
} finally { lock.unlock(); }

// elsewhere:
notEmpty.signalAll();                               // like notifyAll()
```

One lock can have **several** conditions (for example `notFull` and `notEmpty`), which is more precise than a single monitor.

### `ReadWriteLock`

**Analogy:** a **library reading room**. Many people may read at once, but when someone **updates the catalog**, everyone else must wait.

```java
ReadWriteLock rw = new ReentrantReadWriteLock();

rw.readLock().lock();      // MANY readers at the same time
try { ... } finally { rw.readLock().unlock(); }

rw.writeLock().lock();     // ONE writer, exclusive
try { ... } finally { rw.writeLock().unlock(); }
```

Useful for **read-heavy** data. `StampedLock` adds optimistic reads for even higher performance (more complex).

### `synchronized` vs `ReentrantLock`

| | `synchronized` | `ReentrantLock` |
|---|---|---|
| Release | **Automatic** | **Manual** (`finally`), easy to forget |
| `tryLock` / timeout | No | **Yes** |
| Interruptible waiting | No | **Yes** |
| Fairness option | No | Yes |
| Multiple conditions | No (one monitor) | **Yes** |
| Simplicity | **Simpler** | More flexible |
| Advice | Default choice for simple cases | When you need the extra features |

---

## 12. Atomic Classes

### What and why

`java.util.concurrent.atomic` provides **lock-free, thread-safe** variables using hardware **compare-and-swap (CAS)**.

```java
AtomicInteger counter = new AtomicInteger();

counter.incrementAndGet();                 // ++counter, atomically
counter.getAndIncrement();                 // counter++
counter.addAndGet(5);
counter.compareAndSet(5, 10);              // set to 10 only if it is currently 5
counter.updateAndGet(x -> x * 2);
counter.accumulateAndGet(3, Integer::sum);
```

### How CAS works

```
loop:
   old = read value
   new = old + 1
   if (value is still == old) { value = new; done }     // one atomic CPU instruction
   else                       { retry }                 // someone else changed it
```

**Analogy:** editing a shared document: "apply my change only if nobody has modified it since I read it; otherwise reload and retry."

### The family

| Class | Use |
|---|---|
| `AtomicInteger`, `AtomicLong` | Counters and numbers |
| `AtomicBoolean` | Flags with atomic check-and-set |
| `AtomicReference<V>` | An object reference updated atomically |
| `LongAdder`, `DoubleAdder` | **High-contention** counters (splits updates across cells) |
| `AtomicIntegerArray` etc. | Arrays of atomics |

### When to use what

| Situation | Choice |
|---|---|
| A single counter or flag | **Atomic** classes |
| Heavy contention, only summing at the end | **`LongAdder`** |
| Several variables that must change **together** | **Lock** (atomics protect one variable at a time) |

---

## 13. Concurrent Collections

(Compare with the Collections notes.)

| Collection | Notes |
|---|---|
| **`ConcurrentHashMap`** | Fine-grained concurrency; atomic `merge`, `compute`, `putIfAbsent`; **no `null` keys or values** |
| **`CopyOnWriteArrayList` / `CopyOnWriteArraySet`** | Copies the array on **every write**; iteration over a snapshot, never throws `ConcurrentModificationException`. Best for **read-heavy**, rarely changing data |
| **`ConcurrentLinkedQueue` / `ConcurrentLinkedDeque`** | Non-blocking thread-safe queues |
| **`BlockingQueue`** family | `put` blocks when full, `take` blocks when empty |
| **`ConcurrentSkipListMap` / `Set`** | Concurrent **sorted** map and set |

### `BlockingQueue` implementations

| Class | Description |
|---|---|
| `ArrayBlockingQueue` | **Bounded**, array-backed |
| `LinkedBlockingQueue` | Optionally bounded, linked nodes |
| `PriorityBlockingQueue` | Unbounded, ordered by priority |
| `DelayQueue` | Elements become available after a delay |
| `SynchronousQueue` | **No storage**: a direct hand-off between threads |

### `ConcurrentHashMap` atomic operations

```java
ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();

// WRONG: check-then-act race, even on a thread-safe map
if (!counts.containsKey("java")) counts.put("java", 1);

// RIGHT: one atomic operation
counts.putIfAbsent("java", 1);
counts.merge("java", 1, Integer::sum);                 // atomic increment
counts.computeIfAbsent("py", k -> expensive());        // computes once per key
```

### `synchronizedList` vs `CopyOnWriteArrayList` vs `ConcurrentHashMap`

| | `Collections.synchronized*` | Concurrent collections |
|---|---|---|
| Locking | **One lock** over the whole collection | Fine-grained or lock-free |
| Compound actions | You must lock manually (including iteration) | Atomic methods provided |
| Iteration | Must be externally synchronized | Safe (weakly consistent or snapshot) |
| Scalability | Poor | Good |

---

## 14. Synchronizers

Coordination tools for **groups of threads**.

### `CountDownLatch`: wait until N things have happened

**Analogy:** waiting for **all three chefs to finish** before serving the meal. It is **one-shot**: once it reaches zero, it stays open.

```java
CountDownLatch latch = new CountDownLatch(3);

for (int i = 1; i <= 3; i++) {
    final int id = i;
    pool.submit(() -> {
        System.out.println("Worker " + id + " done");
        latch.countDown();                // decrement
    });
}

latch.await();                            // the main thread waits until the count hits 0
System.out.println("All workers finished");      // always printed last
```

### `CyclicBarrier`: threads wait for **each other**

**Analogy:** friends who **meet at a checkpoint**, and nobody moves on until everyone has arrived. It is **reusable** (cyclic).

```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("All arrived, next phase"));

Runnable worker = () -> {
    // phase 1 work...
    barrier.await();                      // wait for the other 2 (handle checked exceptions)
    // phase 2 work...
};
```

| | `CountDownLatch` | `CyclicBarrier` |
|---|---|---|
| Who waits | One or more threads wait for **events** | **All participating threads** wait for each other |
| Reusable | **No** | **Yes** |
| Count | Decremented by anyone | Each of N parties calls `await` |

### `Semaphore`: limit concurrent access

**Analogy:** a **parking lot with N spaces**. A car enters only if a space is free; otherwise it waits at the barrier.

```java
Semaphore permits = new Semaphore(3);           // at most 3 threads at once

void useScarceResource() throws InterruptedException {
    permits.acquire();                          // take a permit (blocks if none)
    try {
        // use the database connection / API with limited concurrency
    } finally {
        permits.release();
    }
}
```

Uses: connection limiting, rate limiting concurrent calls. `new Semaphore(1)` behaves like a (non-reentrant) lock.

### Others

| Class | Purpose |
|---|---|
| `Phaser` | A flexible, multi-phase barrier with dynamic parties |
| `Exchanger` | Two threads swap objects at a meeting point |

---

## 15. ThreadLocal

### What

A variable where **each thread has its own independent copy**.

**Analogy:** a **personal locker for each thread**.

```java
private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

void handleRequest(String user) {
    currentUser.set(user);
    try {
        doWork();                              // anywhere deeper: currentUser.get()
    } finally {
        currentUser.remove();                  // ALWAYS clean up
    }
}
```

### Uses

- Carrying **request context** (user, trace id, locale) through layers without passing parameters.
- Per-thread non-thread-safe helpers.
- Spring uses it internally: **`SecurityContextHolder`**, transaction context, and request attributes are all thread-bound.

### Dangers

- **Memory leaks in thread pools:** pooled threads live forever, so a value you forget to `remove()` lingers (and can **leak between requests**, a security risk).
- Values **do not propagate** to new threads or `@Async` tasks automatically (`InheritableThreadLocal` copies only at thread creation).
- Heavy use with huge numbers of virtual threads is discouraged (Java 21 offers scoped values as an alternative).

---

## 16. Fork/Join and Parallel Streams

### Fork/Join: divide and conquer

Split a big task into smaller subtasks, run them in parallel, and combine the results. Idle workers **steal** work from busy ones (work-stealing).

```java
class SumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10_000;
    private final int[] arr; private final int lo, hi;

    SumTask(int[] arr, int lo, int hi) { this.arr = arr; this.lo = lo; this.hi = hi; }

    @Override
    protected Long compute() {
        if (hi - lo <= THRESHOLD) {                    // small enough: do it directly
            long sum = 0;
            for (int i = lo; i < hi; i++) sum += arr[i];
            return sum;
        }
        int mid = (lo + hi) / 2;
        SumTask left  = new SumTask(arr, lo, mid);
        SumTask right = new SumTask(arr, mid, hi);
        left.fork();                                   // run left asynchronously
        long rightResult = right.compute();            // compute right in this thread
        return left.join() + rightResult;
    }
}

long total = ForkJoinPool.commonPool().invoke(new SumTask(arr, 0, arr.length));
```

- `RecursiveTask<V>` returns a value; `RecursiveAction` does not.
- Best for **CPU-bound, divisible** work.

### Parallel streams

```java
long count = numbers.parallelStream().filter(n -> n % 2 == 0).count();
```

They run on the **common ForkJoinPool**. Use them only for large, CPU-bound, **stateless** work (see the Streams notes). Don't mutate shared state inside them, and don't block on I/O in them.

---

## 17. Virtual Threads (Java 21)

### The problem they solve

A traditional **platform thread** maps 1:1 to an **OS thread**: heavy (about 1 MB of stack) and limited to thousands. A "thread per request" server that **blocks** on database or network calls wastes those expensive threads while they wait.

### What a virtual thread is

A **lightweight thread managed by the JVM**, not the OS. Thousands or **millions** can exist; they are mounted on a small pool of carrier (platform) threads, and when a virtual thread **blocks** on I/O, it is **unmounted**, freeing the carrier for other work.

**Analogy:** **platform threads** are a few expensive, specialised chefs. **Virtual threads** are cheap, plentiful waiters: when a waiter is waiting for the kitchen, they step aside and another takes the floor.

### Usage

```java
Thread vt = Thread.ofVirtual().name("vt-1").start(() -> System.out.println("hello"));
Thread.startVirtualThread(() -> doWork());

try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
    IntStream.range(0, 10_000).forEach(i ->
        ex.submit(() -> {
            Thread.sleep(Duration.ofSeconds(1));       // blocks cheaply
            return i;
        }));
}   // 10,000 tasks sleeping 1 second finish in roughly 1 second, not 10,000 seconds
```

### Rules

| Guidance | Reason |
|---|---|
| Use for **I/O-bound, blocking** tasks (web requests, DB calls) | That is where they shine |
| **Do not pool** virtual threads | They are cheap: create one per task |
| Do not use for **CPU-bound** work | No benefit; you still have only N cores |
| Prefer `ReentrantLock` to `synchronized` around **blocking** calls (Java 21) | Blocking inside `synchronized` can **pin** the carrier thread (later JDKs reduce this) |
| Limit concurrency with a **`Semaphore`**, not a pool size | e.g. protect a downstream service |
| Minimize heavy `ThreadLocal` use | Millions of threads multiply the cost |

### Spring Boot

On Java 21 and Spring Boot 3.2+:

```properties
spring.threads.virtual.enabled=true
```

Tomcat and async executors then use virtual threads, so simple blocking code scales much better with no reactive rewrite.

Related features (**structured concurrency** and **scoped values**) were preview APIs in Java 21; check the current status for your JDK before relying on them.

---

## 18. Concurrency in Spring Boot

### Every request is a thread

Tomcat handles each HTTP request on a thread from its pool (default max **200**: `server.tomcat.threads.max`). So **all controller, service and repository code runs concurrently**.

### Singleton beans are shared across threads

```java
@Service
public class OrderService {
    private int processedCount;                 // BAD: shared mutable state, a race condition

    public void process() { processedCount++; }
}
```

**Rules for Spring beans:**

- Keep singleton beans **stateless**: no mutable instance fields. Use local variables and method parameters.
- If you need shared state, use `AtomicInteger`, `ConcurrentHashMap`, or a database.
- Per-request state belongs in a **request-scoped** bean or local variables.

### Connection pool interplay

Database access is limited by the **HikariCP pool size** (default 10). With 200 request threads and 10 connections, threads **wait** for a connection. Size the pools together; thread count alone does not give throughput.

### `@Async`: run a method in another thread

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(4);
        ex.setMaxPoolSize(8);
        ex.setQueueCapacity(100);
        ex.setThreadNamePrefix("async-");
        ex.initialize();
        return ex;
    }
}

@Service
public class NotificationService {

    @Async("taskExecutor")
    public CompletableFuture<Void> sendEmail(String to) {
        // slow work runs on an async-* thread
        return CompletableFuture.completedFuture(null);
    }
}
```

**Pitfalls:**

| Pitfall | Detail |
|---|---|
| **Self-invocation** | Calling an `@Async` method from the **same class** bypasses the proxy; it runs synchronously |
| **Return type** | Use `void`, `Future` or `CompletableFuture` |
| **Exceptions in `void` methods are lost** | Provide an `AsyncUncaughtExceptionHandler` |
| **Thread-bound context does not propagate** | The `SecurityContext`, **`@Transactional`** transactions and request attributes **do not follow** into the async thread. A transaction cannot span threads |
| **Unbounded default executor** | Configure your own `ThreadPoolTaskExecutor` |

### `@Scheduled`

```java
@EnableScheduling
@Component
class Jobs {
    @Scheduled(fixedRate = 5000)                      // every 5 s
    void heartbeat() { ... }

    @Scheduled(cron = "0 0 2 * * *")                  // daily at 02:00
    void nightlyCleanup() { ... }
}
```

The default scheduler uses **one thread**, so a slow job delays the others. Configure `spring.task.scheduling.pool.size` if needed. In a **multi-instance** deployment, every instance runs the job; use a lock (for example ShedLock) to run it once.

### `@Transactional` and threads

A transaction is bound to the **current thread** (`ThreadLocal`). Spawning a thread or `CompletableFuture` **inside** a transactional method means the new thread **does not see** that transaction.

### Concurrent updates to the same row

Two requests updating the same record can **overwrite each other**. Solutions: **optimistic locking** (`@Version` on the entity), or **pessimistic locking** (`@Lock(PESSIMISTIC_WRITE)`), or atomic SQL updates.

---

## 19. Best Practices

| Practice | Why |
|---|---|
| **Avoid shared mutable state**; prefer immutability and thread confinement | Eliminates whole classes of bugs |
| Prefer **high-level utilities** (executors, `ConcurrentHashMap`, `BlockingQueue`, `CompletableFuture`) over raw threads and `wait/notify` | Tested and less error-prone |
| Use **thread pools**, not `new Thread` per task | Bounded resources |
| Use **bounded queues** and a rejection policy | Make overload visible |
| **Name your threads** (`ThreadFactory`) | Readable thread dumps and logs |
| Keep **lock scope small**; use a **private lock object** | Less contention |
| Acquire multiple locks in a **consistent order** | Prevent deadlock |
| Prefer **atomics/concurrent collections** to manual locking for simple cases | Simpler and scalable |
| **Never swallow `InterruptedException`** | Cancellation relies on it |
| **Always shut down** executors | Otherwise the JVM may never exit |
| Always **`unlock()` in `finally`** | Prevents a permanently held lock |
| Set **timeouts** (`tryLock`, `get(timeout)`, `orTimeout`) | Avoid hanging forever |
| Document **thread-safety** of classes | Clear contracts (`@ThreadSafe`, `@GuardedBy`) |
| Test concurrency with **stress tests**; run repeatedly | Races appear only occasionally |
| Don't rely on thread **priorities** or **timing** | Unreliable |
| Keep Spring beans **stateless** | They are shared |

---

## 20. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Calling `run()` instead of `start()` | No new thread | `start()` |
| Calling `start()` twice | `IllegalThreadStateException` | Create a new `Thread` |
| `count++` on shared data without protection | Lost updates | `synchronized`, atomics |
| Thinking `volatile` makes `count++` safe | Still not atomic | Atomics or locks |
| Check-then-act (`containsKey` then `put`) | Race between check and act | `putIfAbsent`, `merge`, `computeIfAbsent` |
| Reading shared data without synchronization | Stale values | Synchronize reads as well as writes |
| `wait()` outside `synchronized` | `IllegalMonitorStateException` | Hold the monitor |
| `wait()` in an `if` | Spurious wakeups or changed conditions | `while` loop |
| `notify()` where `notifyAll()` is needed | Lost wakeups, hangs | `notifyAll()` |
| Inconsistent lock ordering | Deadlock | Global lock order, `tryLock` |
| Locking on `this`, a `String` literal, or a boxed value | Outside code interferes or shares your lock | Private final lock object |
| Forgetting `unlock()` in `finally` | Lock held forever | `try/finally` |
| Not shutting down an `ExecutorService` | JVM never exits | `shutdown()` or try-with-resources |
| `submit()` with an exception and no `get()` | Failure silently lost | Call `get()`, or use `execute`, or handle inside the task |
| `newFixedThreadPool` / `newCachedThreadPool` in production | Unbounded queue or threads, out of memory | Configure `ThreadPoolExecutor` |
| Blocking I/O in `CompletableFuture`'s default pool | Starves the common pool | Pass your own executor |
| Swallowing `InterruptedException` | Cancellation broken | Restore the flag or rethrow |
| Slow work inside a synchronized block | Contention, poor throughput | Keep it short |
| Calling `Thread.sleep` to "wait for the other thread" | Fragile timing | `join`, latches, futures |
| Mutable instance fields in Spring singletons | Shared across all requests | Stateless beans |
| Using `@Transactional` or security context in another thread | Not propagated | Pass data explicitly; separate transactions |
| `@Async` self-invocation | Runs synchronously | Call through another bean |
| Not removing `ThreadLocal` values in pools | Leaks across requests | `remove()` in `finally` |
| Parallel stream with side effects | Races | Use `collect` |
| Pooling virtual threads | Defeats their purpose | One per task |
| Relying on thread priorities | Not portable | Design without them |

---

## 21. Output Prediction Puzzles

**Puzzle 1: `run()` vs `start()`**

```java
Thread t = new Thread(() -> System.out.println(Thread.currentThread().getName()));
t.run();
t.start();
```

**Puzzle 2: `join`**

```java
Thread t = new Thread(() -> System.out.println("child"));
t.start();
t.join();
System.out.println("parent");
```

**Puzzle 3: starting twice**

```java
Thread t = new Thread(() -> System.out.println("hi"));
t.start();
t.start();
```

**Puzzle 4: exception in a thread**

```java
Thread t = new Thread(() -> { throw new RuntimeException("boom"); });
t.start();
t.join();
System.out.println("main continues");
```

**Puzzle 5: the racing counter**

```java
// two threads each call count++ 100,000 times on a shared int, then main joins both
System.out.println(count);
```

**Puzzle 6: `volatile` counter**

```java
private volatile int count;
// two threads each call count++ 100,000 times
```

**Puzzle 7: two kinds of `synchronized`**

```java
class A {
    synchronized void m1() { /* long work */ }            // locks on `this`
    static synchronized void m2() { /* long work */ }     // locks on A.class
}
// Thread 1 calls new A().m1() while Thread 2 calls A.m2(). Do they block each other?
```

**Puzzle 8: `wait` without a lock**

```java
new Object().wait();
```

**Puzzle 9: swallowed task exception**

```java
ExecutorService pool = Executors.newFixedThreadPool(1);
pool.submit(() -> { throw new RuntimeException("x"); });
pool.shutdown();
```

**Puzzle 10: the program that never exits**

```java
ExecutorService pool = Executors.newFixedThreadPool(2);
pool.submit(() -> System.out.println("task"));
// main ends here with no shutdown
```

**Puzzle 11: `sleep` and locks**

```java
synchronized void a() throws InterruptedException { Thread.sleep(5000); }
synchronized void b() { System.out.println("b"); }
// Thread 1 calls a(); one second later Thread 2 calls b() on the same object.
```

**Puzzle 12: check-then-act on a concurrent map**

```java
if (!map.containsKey(key)) map.put(key, 1);   // map is a ConcurrentHashMap
```

**Answers**

1. **`main`**, then (typically) **`Thread-0`**. `run()` executes in the calling thread; only `start()` creates a new one.
2. **`child`** then **`parent`**, always. `join()` makes `main` wait.
3. The first `start()` works; the second throws **`IllegalThreadStateException`**.
4. The stack trace `Exception in thread "Thread-0" java.lang.RuntimeException: boom` appears on stderr, then **`main continues`** is printed. A dying thread does not kill the others.
5. **Usually less than 200000, and different each run.** `count++` is not atomic, so increments are lost.
6. **Still usually less than 200000.** `volatile` gives visibility but not atomicity of read-modify-write.
7. **No.** They use **different locks** (`this` vs `A.class`), so they run concurrently.
8. **`IllegalMonitorStateException`**. The calling thread does not hold the object's monitor.
9. **Nothing is printed.** `submit` captures the exception in a `Future` that nobody reads. `execute` would print the stack trace.
10. The program **prints `task` but never exits**: the pool's non-daemon worker threads keep the JVM alive. Call `pool.shutdown()`.
11. **Thread 2 waits about 4 seconds, then prints `b`.** `sleep` does **not release** the lock, so `b()` is blocked until `a()` finishes.
12. It is **not atomic**: two threads can both see the key missing and both `put`, so one update is lost. Use `putIfAbsent`, or `merge` for counting.

---

## 22. Interview Questions

**1. Process vs thread?** A process is a running program with its own memory; threads are execution paths inside a process that share its heap but have separate stacks.

**2. What is multithreading?** Running multiple threads within one program to improve responsiveness and throughput.

**3. Concurrency vs parallelism?** Concurrency is managing many tasks that overlap in time; parallelism is executing them simultaneously on multiple cores.

**4. Ways to create a thread?** Extend `Thread`, implement `Runnable`, or use `Callable` with an executor (plus lambdas and virtual threads).

**5. Why prefer `Runnable` to extending `Thread`?** It separates the task from the thread, keeps your single inheritance slot free, and works with executors.

**6. `start()` vs `run()`?** `start()` creates a new thread that calls `run()`; calling `run()` directly executes in the current thread.

**7. `Runnable` vs `Callable`?** `Callable` returns a value and can throw checked exceptions; `Runnable` cannot.

**8. Thread states?** NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED.

**9. `sleep()` vs `wait()`?** `sleep` pauses the current thread and keeps locks; `wait` releases the lock, must be inside `synchronized`, and is woken by `notify`.

**10. `join()`?** Makes the calling thread wait until the target thread finishes.

**11. What does `interrupt()` do?** Sets the interrupt flag or wakes a blocked thread with `InterruptedException`; cancellation is cooperative.

**12. What are daemon threads?** Background threads that do not prevent JVM exit.

**13. What is a race condition?** A bug where the outcome depends on the timing of unsynchronized access to shared data.

**14. Why is `count++` not thread-safe?** It is read, add, write: three steps that can interleave.

**15. What does `synchronized` do?** Provides mutual exclusion on an object's monitor and guarantees visibility.

**16. Object lock vs class lock?** Instance `synchronized` uses `this`; `static synchronized` uses the `Class` object; they are separate locks.

**17. What does reentrant mean?** A thread holding a lock can acquire it again without deadlocking itself.

**18. What does `volatile` guarantee?** Visibility and ordering of a variable, but not atomicity of compound actions.

**19. `volatile` vs `synchronized`?** `volatile` is visibility only and non-blocking; `synchronized` provides atomicity, visibility and mutual exclusion.

**20. What is the Java Memory Model's happens-before?** Rules guaranteeing that one action's effects are visible to another (lock release/acquire, volatile write/read, `start`, `join`).

**21. What is deadlock, and what are its conditions?** Threads blocked forever waiting on each other's locks; requires mutual exclusion, hold and wait, no preemption, circular wait.

**22. How do you prevent deadlock?** Consistent lock ordering, `tryLock` with timeout, avoiding nested locks.

**23. How do you detect deadlock?** Thread dumps (`jstack`), VisualVM or JConsole.

**24. Livelock vs starvation?** Livelock: threads keep reacting without progress. Starvation: a thread never gets the resource.

**25. Why use thread pools?** Reuse threads, bound resources, manage tasks and avoid creation cost.

**26. How does `ThreadPoolExecutor` handle a new task?** Start a thread up to core size, then queue, then add threads up to max, then reject.

**27. Rejection policies?** Abort (throws), CallerRuns, Discard, DiscardOldest.

**28. `submit` vs `execute`?** `submit` returns a `Future` and captures exceptions in it; `execute` returns nothing and lets exceptions propagate to the thread handler.

**29. `shutdown()` vs `shutdownNow()`?** `shutdown` lets queued tasks finish; `shutdownNow` interrupts running tasks and returns queued ones.

**30. What is a `Future`?** A handle to an asynchronous result, retrieved with the blocking `get()`.

**31. What does `CompletableFuture` add over `Future`?** Non-blocking composition (`thenApply`, `thenCompose`, `thenCombine`), callbacks, error handling and timeouts.

**32. `thenApply` vs `thenCompose`?** `thenApply` transforms the value (map); `thenCompose` chains another asynchronous step returning a future (flatMap).

**33. `get()` vs `join()`?** `get` throws checked exceptions; `join` throws unchecked `CompletionException`.

**34. `synchronized` vs `ReentrantLock`?** `ReentrantLock` adds `tryLock`, timeouts, interruptible waits, fairness and multiple conditions, but needs manual unlock.

**35. What is `ReadWriteLock`?** Many concurrent readers or one exclusive writer.

**36. What are atomic classes and CAS?** Lock-free variables using compare-and-swap hardware instructions.

**37. `ConcurrentHashMap` vs `Hashtable` vs `synchronizedMap`?** `ConcurrentHashMap` uses fine-grained concurrency and atomic compound methods; the others lock the whole map.

**38. `CopyOnWriteArrayList`: when to use?** Read-heavy, write-rare data; iterators work on a snapshot.

**39. `CountDownLatch` vs `CyclicBarrier`?** A latch is one-shot (waiters wait for a count to reach zero); a barrier makes a group wait for each other and is reusable.

**40. What is a `Semaphore`?** A counter of permits limiting how many threads access a resource at once.

**41. What is `ThreadLocal`, and what is its risk?** Per-thread variables; leaks and cross-request contamination in thread pools if not removed.

**42. What are virtual threads?** JVM-managed lightweight threads (Java 21) that make blocking I/O cheap; use one per task, don't pool them.

**43. How would you make a Spring singleton service thread-safe?** Keep it stateless; use atomics or concurrent collections for shared state.

**44. Why does `@Transactional` not work across threads?** The transaction is bound to the creating thread via `ThreadLocal`.

**45. How do you implement producer-consumer?** `BlockingQueue` (or `wait/notify` with a bounded buffer).

---

## 23. Cheat Sheet

| Need | Use |
|---|---|
| Run a task in the background | `ExecutorService.submit(...)` (not `new Thread`) |
| Get a result later | `Callable` + `Future`, or `CompletableFuture` |
| Chain or combine async work | `CompletableFuture` (`thenApply`, `thenCompose`, `thenCombine`, `allOf`) |
| Protect a compound action | `synchronized` or `ReentrantLock` |
| A simple counter | `AtomicInteger` / `LongAdder` |
| A visibility flag | `volatile` |
| Shared map | `ConcurrentHashMap` (`merge`, `putIfAbsent`) |
| Read-mostly list | `CopyOnWriteArrayList` |
| Producer-consumer | `BlockingQueue` |
| Wait for N tasks | `CountDownLatch` |
| Threads meet at a point | `CyclicBarrier` |
| Limit concurrency | `Semaphore` |
| Many readers, one writer | `ReadWriteLock` |
| Per-thread data | `ThreadLocal` (and `remove()`) |
| Cheap blocking tasks at scale | Virtual threads |
| Delayed or periodic tasks | `ScheduledExecutorService` / `@Scheduled` |

**Thread-safety toolbox, in order of preference**

```
1. Don't share (local variables, thread confinement)
2. Share immutable data
3. Use concurrent collections / atomics / high-level utilities
4. Use locks (synchronized / ReentrantLock) with small critical sections
```

**Golden rules**

```
1. start() creates a thread, run() does not.
2. count++ is NOT atomic; volatile does NOT fix it.
3. Always check-and-act atomically (putIfAbsent, merge).
4. wait() in a while loop, inside synchronized.
5. Same lock order everywhere; unlock in finally.
6. submit() hides exceptions; shut down your pools.
7. Never swallow InterruptedException.
8. Keep Spring singleton beans stateless; @Transactional and security context are thread-bound.
```

---

## 24. Practice Tasks with Solutions

**1. Thread-safe counter, four ways.** Make the racing counter from section 4 correct with `synchronized`, `AtomicInteger`, `ReentrantLock` and `LongAdder`, and confirm the result is exactly 200000.

```java
AtomicInteger count = new AtomicInteger();
Runnable r = () -> { for (int i = 0; i < 100_000; i++) count.incrementAndGet(); };
Thread t1 = new Thread(r), t2 = new Thread(r);
t1.start(); t2.start(); t1.join(); t2.join();
System.out.println(count.get());     // 200000
```

**2. Print odd and even numbers alternately with two threads** (`wait`/`notifyAll`):

```java
class OddEvenPrinter {
    private int current = 1;
    private final int max;

    OddEvenPrinter(int max) { this.max = max; }

    synchronized void print(boolean odd) throws InterruptedException {
        while (current <= max) {
            if ((current % 2 == 1) == odd) {
                System.out.println((odd ? "Odd : " : "Even: ") + current++);
                notifyAll();
            } else {
                wait();
            }
        }
        notifyAll();                    // let the other thread notice we are done
    }
}

OddEvenPrinter p = new OddEvenPrinter(10);
Thread odd  = new Thread(() -> { try { p.print(true);  } catch (InterruptedException e) { Thread.currentThread().interrupt(); } });
Thread even = new Thread(() -> { try { p.print(false); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } });
odd.start(); even.start();
// Odd : 1, Even: 2, Odd : 3, ... Even: 10
```

**3. Parallel sum with an `ExecutorService`**

```java
static long parallelSum(int[] a, int parts) throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(parts);
    try {
        int chunk = (a.length + parts - 1) / parts;
        List<Future<Long>> futures = new ArrayList<>();
        for (int p = 0; p < parts; p++) {
            int from = p * chunk;
            int to = Math.min(a.length, from + chunk);
            futures.add(pool.submit(() -> {
                long s = 0;
                for (int i = from; i < to; i++) s += a[i];
                return s;
            }));
        }
        long total = 0;
        for (Future<Long> f : futures) total += f.get();
        return total;
    } finally {
        pool.shutdown();
    }
}
```

**4. Create, detect and fix a deadlock.** Build the `lockA`/`lockB` example (section 8), run it, then use `jps` and `jstack <pid>` to see "Found one Java-level deadlock". Fix it by using the **same lock order** in both threads.

**5. Producer-consumer with `BlockingQueue`** (section 7). Extend it to **two producers and two consumers**, and use **one poison pill per consumer**.

**6. Limit concurrent calls with a `Semaphore`.** Simulate 10 tasks that call a "database" but allow at most 3 at once; print the number currently inside.

```java
Semaphore permits = new Semaphore(3);
AtomicInteger inside = new AtomicInteger();

Runnable call = () -> {
    try {
        permits.acquire();
        try {
            System.out.println("inside: " + inside.incrementAndGet());     // never more than 3
            Thread.sleep(500);
            inside.decrementAndGet();
        } finally {
            permits.release();
        }
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
};

ExecutorService pool = Executors.newFixedThreadPool(10);
for (int i = 0; i < 10; i++) pool.submit(call);
pool.shutdown();
```

**7. Parallel service calls with `CompletableFuture`.** Simulate `getCustomer()` and `getAccounts()` that each `sleep(300)`. Compare the total time sequentially (about 600 ms) and with `thenCombine` (about 300 ms). Add `.orTimeout` and an `exceptionally` fallback.

**8. Bank transfer without deadlock.** Implement `transfer(from, to, amount)` with **ordered locks** (section 8). Run 1,000 random transfers between 5 accounts from 8 threads and verify that the **total balance is unchanged**.

**9. Safe word counter.** Count word frequencies across many files in parallel using a `ConcurrentHashMap` and `merge`. Then show what happens with a plain `HashMap` (lost counts).

**10. Spring `@Async`.** Add `@EnableAsync`, a `ThreadPoolTaskExecutor` and an `@Async` email method. Log `Thread.currentThread().getName()` in the controller and in the async method. Confirm they differ, and confirm that calling the async method from within the **same class** runs on the **same** thread (the self-invocation trap).

### Challenge questions

1. A `ConcurrentHashMap<String, List<String>>` is used as `map.get(k).add(v)`. Is this thread-safe?
2. Why can a program using `newFixedThreadPool(10)` still run out of memory under heavy load?
3. A web request handler spawns a `CompletableFuture` that calls a `@Transactional` service method. Does it run inside the request's transaction?

**Answers**

1. **No.** The map is thread-safe, but the **`ArrayList` inside is not**. Concurrent `add` calls can corrupt it. Use `map.computeIfAbsent(k, x -> new CopyOnWriteArrayList<>()).add(v)` or synchronize access to each list.
2. Its work queue is **unbounded** (`LinkedBlockingQueue`). If tasks arrive faster than 10 threads can process them, the queue grows until memory is exhausted. Use a `ThreadPoolExecutor` with a **bounded queue** and a rejection policy.
3. **No.** Transactions are bound to the **thread** that started them. The async thread starts its own (separate) transaction, so a failure in one will not roll back the other.

---

*End of notes.*
