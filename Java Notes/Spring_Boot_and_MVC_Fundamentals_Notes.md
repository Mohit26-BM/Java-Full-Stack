# Spring, Spring Boot and Spring MVC: Fundamentals

From "why do we need a framework" through IoC and DI, beans, containers, scopes, Spring Boot, Maven in Eclipse, HTTP basics and the MVC pattern with Thymeleaf. Each topic covers what, why and how, with real-life analogies and working code.

---

## Table of Contents

1. [Why Frameworks Exist](#1-why-frameworks-exist)
2. [What Is Spring](#2-what-is-spring)
3. [IoC and Dependency Injection](#3-ioc-and-dependency-injection)
4. [Beans and Their Lifecycle](#4-beans-and-their-lifecycle)
5. [Three Ways to Configure Beans](#5-three-ways-to-configure-beans)
6. [The Spring Container: BeanFactory vs ApplicationContext](#6-the-spring-container-beanfactory-vs-applicationcontext)
7. [Bean Scopes](#7-bean-scopes)
8. [Changing the JDK Version in Eclipse](#8-changing-the-jdk-version-in-eclipse)
9. [Spring Boot](#9-spring-boot)
10. [Importing a Maven Project into Eclipse](#10-importing-a-maven-project-into-eclipse)
11. [Web Basics: Request, Response, HTTP Methods](#11-web-basics-request-response-http-methods)
12. [The MVC Design Pattern](#12-the-mvc-design-pattern)
13. [Spring MVC in Practice](#13-spring-mvc-in-practice)
14. [Thymeleaf Cheat Sheet](#14-thymeleaf-cheat-sheet)
15. [Common Mistakes](#15-common-mistakes)
16. [Interview Questions and Answers](#16-interview-questions-and-answers)
17. [Cheat Sheet](#17-cheat-sheet)
18. [Practice Exercises with Solutions](#18-practice-exercises-with-solutions)

---

## 1. Why Frameworks Exist

### What a real project needs

Writing "business logic" (calculate interest, place an order) is only part of building an application. A real project also needs plumbing:

| # | Concern | What it means | Spring's answer |
|---|---|---|---|
| 1 | **Database connectivity** | Talk to a database | JDBC, Hibernate, Spring JDBC, Spring Data JPA |
| 2 | **Reading values from the UI** | Receive form and URL input, show pages | Spring MVC |
| 3 | **Dependency resolving** | (a) Find and download libraries; (b) create and connect objects | (a) Maven or Gradle; (b) Spring DI |
| 4 | **Application-to-application communication** | One app calls another over the network | REST |
| 5 | **Unit testing** | Verify code automatically | JUnit, Mockito |
| 6 | **Cloud and queues** | Messaging, external services | Spring Kafka/AMQP/JMS, Spring Cloud |

> The trainer's rough estimate: a project is business logic plus infrastructure code, and **about 60% is infrastructure**. Frameworks take that 60% off your hands so you can focus on the 40% that is unique to the business.

### What a framework is

> A **framework** is a **predefined flow** for a task. You fill in the parts specific to you.

**Analogy: a PowerPoint template.** The template already has the layout, fonts, slide master and colors. You only add your content. You don't design a presentation from scratch every time.

### Framework vs library

| Library | Framework |
|---|---|
| **You call** the library | The framework **calls your code** |
| You control the flow | The framework controls the flow |
| Like buying furniture pieces and arranging them yourself | Like a ready-made flat with plumbing and wiring done; you decorate the rooms |
| Example: a JSON parsing jar | Example: Spring |

This reversal ("Don't call us, we'll call you") is called the **Hollywood principle** and is the idea behind IoC (section 3).

### The JDBC-to-Spring-Data story

**Plain JDBC** (every step by hand):

```java
Connection con = DriverManager.getConnection(url, user, pass);
PreparedStatement ps = con.prepareStatement("SELECT * FROM student WHERE id = ?");
ps.setInt(1, 101);
ResultSet rs = ps.executeQuery();
Student s = null;
if (rs.next()) {
    s = new Student(rs.getInt("id"), rs.getString("name"));
}
rs.close(); ps.close(); con.close();
```

**Spring Data JPA** (declare what you want):

```java
public interface StudentRepository extends JpaRepository<Student, Integer> { }

Student s = repository.findById(101).orElse(null);
```

**Analogy:** driving a manual car through traffic versus booking a ride. Same destination; far less effort.

---

## 2. What Is Spring

> **Spring** is a Java framework for building enterprise applications. Its core is a container that creates and wires your objects.

The trainer's note says "for developing web applications". More precisely, Spring is **general-purpose**: its core works in console apps, batch jobs and desktop apps too. Web support (Spring MVC) is one module on top.

### Two basic principles (from the notes)

1. **Dependency Injection (DI):** the process of injecting the dependent beans.
2. **Inversion of Control (IoC):** delegating the creation of dependent objects to a third-party **container**.

Section 3 explains both and how they relate.

### Main parts of the Spring ecosystem

| Part | Purpose |
|---|---|
| Spring Core / Context | IoC container, DI, beans |
| Spring AOP | Cross-cutting concerns (logging, transactions, security) |
| Spring JDBC / ORM / Data | Database access |
| Spring MVC | Web layer |
| Spring Test | Testing support |
| **Spring Boot** | Fast setup with auto-configuration |
| Spring Security | Authentication and authorization |
| Spring Cloud | Microservices tooling |

---

## 3. IoC and Dependency Injection

### The problem: objects creating their own dependencies

```java
class Car {
    private Engine engine = new PetrolEngine();   // Car chooses AND creates its engine

    void drive() { engine.start(); }
}
```

Problems:

- `Car` is **tightly coupled** to `PetrolEngine`. Want a diesel engine? Edit `Car`.
- **Hard to test**: you cannot substitute a fake engine.
- `Car` must know *how* to build the engine (maybe it needs its own parts).

### The solution: someone else supplies the dependency

```java
interface Engine { void start(); }

@Component
class PetrolEngine implements Engine {
    public void start() { System.out.println("Petrol engine started"); }
}

@Component
class Car {
    private final Engine engine;

    Car(Engine engine) {            // supplied from outside
        this.engine = engine;
    }

    void drive() { engine.start(); }
}
```

`Car` no longer says *how* to get an engine; it only says "I need an `Engine`." The Spring container hands one over.

### Real-life analogies

- **A phone and its SIM card.** The phone doesn't manufacture the SIM. You insert one. Swap the SIM and the phone works with a different network. (Dependency injection.)
- **A restaurant.** You don't cook; you say what you want, and the kitchen prepares and serves it. (Inversion of control.)
- **Hiring through an agency.** A company says "I need an accountant" and the agency finds and sends one. The company doesn't recruit personally.

### IoC vs DI: how they relate

| | IoC | DI |
|---|---|---|
| What | The **principle**: control of object creation moves from your code to a container | A **technique** that implements IoC: the container injects the dependencies |
| Who creates objects | The container | The container, and hands them in |
| Analogy | "The agency handles hiring" | "The agency sends the accountant to your desk" |

So IoC is the broader idea and DI is the most common way Spring achieves it. The notes call them "two principles"; strictly, **DI is the implementation of IoC**.

### Types of injection

| Type | Example | Notes |
|---|---|---|
| **Constructor** (recommended) | `Car(Engine e)` | Dependencies are required and can be `final`; works in plain unit tests |
| Setter | `void setEngine(Engine e)` | For optional dependencies |
| Field | `@Autowired private Engine engine;` | Short but hides dependencies and is hard to test; avoid |

```java
@Component
class Car {
    private final Engine engine;

    @Autowired                   // optional when the class has exactly one constructor
    Car(Engine engine) { this.engine = engine; }
}
```

### Why DI is valuable

- **Loose coupling:** depend on interfaces, swap implementations by configuration.
- **Easy testing:** inject a mock in tests.
- **Less boilerplate:** no manual `new` chains.
- **Centralized configuration:** one place decides which implementation is used.

### When there are several candidates

```java
@Component class PetrolEngine implements Engine { }
@Component class DieselEngine implements Engine { }
```

Spring cannot pick, so it throws `NoUniqueBeanDefinitionException`. Resolve with:

```java
@Component @Primary
class PetrolEngine implements Engine { }          // default choice

// or choose explicitly where injected
Car(@Qualifier("dieselEngine") Engine engine) { }
```

**Analogy:** two accountants available. Either mark one as the default (`@Primary`) or ask for one by name (`@Qualifier`).

---

## 4. Beans and Their Lifecycle

> A **bean** is a Java object **managed by the Spring container**.

Not every object is a bean. If you write `new Car()` yourself, it is an ordinary object. If the container creates and manages it, it's a bean.

**Analogy:** a **permanent employee on the company payroll**. The company (container) hires them, gives them a desk, wires them to their team, and handles their exit. A plain object is a freelancer you manage yourself.

### Bean names

- Default name for a class `Car` annotated `@Component`: **`car`** (class name, first letter lowercase).
- For `@Bean` methods, the name is the method name.

### Lifecycle (simplified)

```
1. Container instantiates the bean (calls the constructor)
2. Dependencies are injected
3. Initialization callback runs  (@PostConstruct)
4. Bean is READY and in use
5. Container shuts down: destruction callback runs (@PreDestroy)
```

```java
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
class DbConnectionPool {

    @PostConstruct
    void init() {
        System.out.println("Opening connections");
    }

    @PreDestroy
    void cleanup() {
        System.out.println("Closing connections");
    }
}
```

> Spring 6 / Spring Boot 3 use the `jakarta.*` packages. Older tutorials show `javax.annotation.*`, which no longer works with Boot 3.

---

## 5. Three Ways to Configure Beans

The container needs to be told **which classes are beans** and **how they connect**. This is "configuration metadata."

| # | Way | Status |
|---|---|---|
| 1 | **XML file** | Legacy. Still supported but rarely used in new code |
| 2 | **Java class** (`@Configuration` + `@Bean`) | Modern, explicit |
| 3 | **Annotations** (`@Component`, `@Service`...) | Modern, most common |

> The notes say XML is "completely deprecated". It isn't formally deprecated; it is simply **obsolete in practice** and you will only meet it in older projects.

### The same example three ways

**1. XML (`beans.xml`)**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="engine" class="com.example.PetrolEngine"/>

    <bean id="car" class="com.example.Car">
        <constructor-arg ref="engine"/>
    </bean>
</beans>
```

```java
ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
```

**2. Java class**

```java
@Configuration
public class AppConfig {

    @Bean
    public Engine engine() {
        return new PetrolEngine();
    }

    @Bean
    public Car car(Engine engine) {          // Spring injects the engine bean
        return new Car(engine);
    }
}
```

```java
ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
```

Use this when you cannot annotate a class (a third-party library class) or need custom construction logic.

**3. Annotations plus component scanning**

```java
@Component
class PetrolEngine implements Engine { }

@Component
class Car {
    Car(Engine engine) { }
}

@Configuration
@ComponentScan("com.example")        // scan this package for annotated classes
class AppConfig { }
```

### Stereotype annotations

| Annotation | Meaning |
|---|---|
| `@Component` | Generic bean |
| `@Service` | Business logic layer |
| `@Repository` | Data access layer (also translates database exceptions) |
| `@Controller` | Web MVC controller |
| `@RestController` | `@Controller` + `@ResponseBody` (returns data, not a page) |
| `@Configuration` | Class that declares `@Bean` methods |

`@Service`, `@Repository` and `@Controller` are all specializations of `@Component`. They behave similarly but state **intent**.

---

## 6. The Spring Container: BeanFactory vs ApplicationContext

> The **container** creates beans, wires dependencies and manages their lifecycle.

### The two container interfaces

```
BeanFactory (I)
     ^
     |  extends
ApplicationContext (I)
```

**Analogy:** `BeanFactory` is a **basic feature phone** (calls and SMS). `ApplicationContext` is a **smartphone**: everything the basic phone does, plus much more.

### Comparison

| Feature | BeanFactory | ApplicationContext |
|---|---|---|
| Bean lifecycle management | Yes | Yes |
| Dependency injection | Yes | Yes |
| Bean loading | **Lazy** (on first request) | **Eager** (singletons created at startup) |
| AOP integration | Basic | Easy |
| Internationalization (i18n, message bundles) | No | Yes |
| Event publishing and handling | No | Yes |
| Used in practice | Almost never directly | **The standard** |

### Lazy vs eager

- **Lazy:** a bean is created the first time someone asks for it. Like a restaurant that **cooks when you order**.
- **Eager:** all singleton beans are created at startup. Like a **buffet prepared before opening**. Slower to start, but configuration errors show up immediately, and requests are fast.

Even in an `ApplicationContext`, you can make one bean lazy:

```java
@Component
@Lazy
class HeavyReportGenerator { }
```

### ApplicationContext implementations

| Class | Configuration source |
|---|---|
| `ClassPathXmlApplicationContext` | XML file on the classpath |
| `FileSystemXmlApplicationContext` | XML file at a file-system path |
| `XmlWebApplicationContext` | XML in a web application |
| `AnnotationConfigApplicationContext` | Java config classes and annotations |

> The trainer's note spells the web one `WebXmlApplicationContext`. The actual class name is **`XmlWebApplicationContext`**.

In Spring Boot you rarely create these yourself; `SpringApplication.run(...)` creates the right context.

### Using the container directly

```java
@Configuration
@ComponentScan("com.example")
class AppConfig { }

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                 new AnnotationConfigApplicationContext(AppConfig.class)) {

            Car car = ctx.getBean(Car.class);
            car.drive();                          // Petrol engine started
        }                                         // closing the context triggers @PreDestroy
    }
}
```

---

## 7. Bean Scopes

> **Scope** defines **how many instances** of a bean the container creates and how long each lives.

| Scope | Behavior | Analogy |
|---|---|---|
| **singleton** (default) | **One** instance per container, shared everywhere | The building's single water tank |
| **prototype** | A **new** instance every time it is requested | A disposable cup |
| **request** | New instance per **HTTP request** | A visitor slip, issued per visit |
| **session** | New instance per **HTTP session** (per user login) | A hotel key card, valid for your stay |
| **application** | One instance per **web application** (`ServletContext`) | The notice board in the building lobby |

Request, session and application scopes are **only available in a web-aware context**.

> The trainer's note says application scope creates "a new instance for every new application context". More precisely, it is **one instance for the whole web application** (the `ServletContext`), shared by all users. Like singleton, but tied to the web app's lifecycle.

### Singleton vs prototype in code

```java
@Component
class SingletonBean { }                        // default scope: singleton

@Component
@Scope("prototype")
class PrototypeBean { }

ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

SingletonBean s1 = ctx.getBean(SingletonBean.class);
SingletonBean s2 = ctx.getBean(SingletonBean.class);
System.out.println(s1 == s2);      // true:  same object

PrototypeBean p1 = ctx.getBean(PrototypeBean.class);
PrototypeBean p2 = ctx.getBean(PrototypeBean.class);
System.out.println(p1 == p2);      // false: different objects
```

### Web scopes

```java
@Component
@RequestScope
class RequestInfo { }

@Component
@SessionScope
class ShoppingCart { }

@Component
@ApplicationScope
class SiteCounter { }
```

### Important details

- Spring's "singleton" means **one per container (per bean definition)**, not one per JVM like the classic GoF Singleton pattern.
- **Singleton beans are shared by many threads**, so they must be **stateless** (no per-user data in fields). Store per-user data in a session-scoped bean or a database.
- For **prototype**, Spring creates the object but does **not** manage its destruction; `@PreDestroy` is not called on it.
- Injecting a short-lived bean (request, session or prototype) into a singleton only injects **once**, at creation. Use a scoped proxy (`@RequestScope` and `@SessionScope` add one automatically) or `ObjectProvider<T>`.

### Which scope to choose

```
Stateless service, repository, controller      -> singleton (default)
Needs fresh state each time                    -> prototype
Data tied to one HTTP request                  -> request
Data tied to one user's login                  -> session
Data shared by the entire web application      -> application
```

---

## 8. Changing the JDK Version in Eclipse

Use this when the project is compiling against the wrong Java version (for example, Java 8 instead of 17).

### Method 1: Build path (plain Java projects), from the notes

1. Right-click the **project** and choose **Build Path**, then **Configure Build Path**.
2. Open the **Libraries** tab.
3. Click **Add Library**.
4. Select **JRE System Library** and click **Next**.
5. Choose **Alternate JRE** and pick the required version (for example **JDK 17**) from the dropdown.
6. Click **Finish**.
7. Click **Apply**, then **Apply and Close**.

If JDK 17 is not in the dropdown: **Window, Preferences, Java, Installed JREs, Add, Standard VM**, then select the JDK installation folder.

### Method 2: Maven projects (what you normally need)

For a Maven or Spring Boot project, Eclipse **rebuilds its settings from `pom.xml`**, so manual build-path changes get overwritten. Set the version in the pom:

```xml
<properties>
    <java.version>17</java.version>
</properties>
```

(or `<maven.compiler.release>17</maven.compiler.release>` in a plain Maven project)

Then right-click the project, **Maven, Update Project** (or press **Alt+F5**).

### Also check

- **Project, Properties, Java Compiler**: compiler compliance level matches.
- Spring Boot **3.x and later requires Java 17 or newer**. A lower JDK causes errors such as "class file has wrong version".

---

## 9. Spring Boot

### What

**Spring Boot** is a layer on top of Spring that gives you **sensible defaults and automatic setup**, so you can run an application with almost no configuration.

### Why

Plain Spring needed a lot of setup: XML or Java configuration, choosing and matching library versions, deploying to an external server. Boot removes that ceremony.

### Analogy

| Plain Spring | Spring Boot |
|---|---|
| Buying a plot and building a house brick by brick | Moving into a **furnished flat** |
| Cooking from raw ingredients | A **meal kit** with everything portioned |

### Advantages (from the notes), explained

**1. Auto-configuration**
Boot looks at the libraries on your classpath and configures beans automatically. Add a database driver and a data source is set up; add the web starter and MVC is configured.
*Analogy:* a **smart home** that adjusts itself to the devices you plug in.

**2. Spring starters**
One dependency pulls in a **matching, tested group** of libraries.

| Starter | Brings |
|---|---|
| `spring-boot-starter-web` | Spring MVC, JSON support, embedded Tomcat |
| `spring-boot-starter-thymeleaf` | Thymeleaf templates |
| `spring-boot-starter-data-jpa` | Hibernate and Spring Data JPA |
| `spring-boot-starter-test` | JUnit 5, Mockito, AssertJ, Spring Test |
| `spring-boot-starter-security` | Spring Security |

*Analogy:* a **combo meal** instead of ordering each item and hoping they go together.

**3. Embedded server**
Tomcat (default; Jetty and Undertow are alternatives) is **inside** your application. You run a jar; no separate server install.
*Analogy:* a **food truck** carries its own kitchen. A traditional deployment is like a restaurant that needs a building arranged first.

```
java -jar myapp.jar        # starts the server on port 8080 by default
```

**4. `public static void main`: the entry point**
A Boot application is a normal Java application with a `main` method.

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

`@SpringBootApplication` combines three annotations:

| Annotation | Role |
|---|---|
| `@Configuration` | This class can declare beans |
| `@EnableAutoConfiguration` | Turn on auto-configuration |
| `@ComponentScan` | Scan this package and sub-packages for beans |

Because it scans from its own package downward, the main class should sit in the **root package** of your project.

**5. Test environment: JUnit and Mockito**
`spring-boot-starter-test` provides everything.

```java
@SpringBootTest
class DemoApplicationTests {
    @Test
    void contextLoads() { }       // fails if the application cannot start
}
```

### "springboot = spring + annotations - xml"

A handy shorthand from the trainer. A fuller version:

> **Spring Boot = Spring + auto-configuration + starters + embedded server, with no XML and minimal boilerplate.**

Boot does not replace Spring. It **is** Spring, with the setup done for you.

### Typical project layout

```
demo/
 +-- pom.xml
 +-- src/main/java/com/example/demo/
 |        DemoApplication.java
 |        controller/WelcomeController.java
 +-- src/main/resources/
 |        application.properties
 |        templates/welcome.html         <- Thymeleaf pages
 |        static/                        <- css, js, images
 +-- src/test/java/...
```

### Configuration: `application.properties`

```properties
server.port=9090
spring.application.name=demo
spring.thymeleaf.cache=false
```

Changing the port is a one-line edit; no server config files.

### Creating a new Boot project

- Go to **start.spring.io** (Spring Initializr): choose Maven, Java 17+, add *Spring Web* and *Thymeleaf*, generate, unzip.
- Or in Eclipse with the Spring Tools plugin: **New, Spring Starter Project**.

### Minimal `pom.xml`

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.x.x</version>   <!-- use the version Spring Initializr generates -->
</parent>

<properties>
    <java.version>17</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Starters need no version numbers; the parent manages them.

### Spring vs Spring Boot

| | Spring | Spring Boot |
|---|---|---|
| Setup | Manual | Auto-configured |
| Dependencies | Choose each, match versions | Starters, versions managed |
| Server | External, deploy a WAR | Embedded, run a JAR |
| Configuration | XML or Java config | Mostly properties and defaults |
| Boilerplate | High | Low |

---

## 10. Importing a Maven Project into Eclipse

### Steps (from the notes)

1. **File, Import**.
2. Search for **Maven**.
3. Choose **Existing Maven Projects** and click **Next**.
4. **Browse** to the folder that **directly contains `pom.xml`** (the project's base folder).
5. Select the folder, and the project appears in the list with its `pom.xml` ticked.
6. Click **Next**, then **Finish**.

Eclipse reads `pom.xml`, downloads dependencies into the local Maven repository (`~/.m2`) and sets up the build path.

### Running it

- Right-click the main class, **Run As, Java Application** (or **Spring Boot App** with the Spring Tools plugin).
- Or from a terminal in the project folder: `mvn spring-boot:run`.
- Open `http://localhost:8080/...` in the browser.

### Troubleshooting

| Problem | Fix |
|---|---|
| Red errors after import, dependencies missing | Right-click project, **Maven, Update Project** (Alt+F5), tick *Force Update of Snapshots/Releases* |
| "No pom.xml found" | You selected a parent folder; browse to the one that contains `pom.xml` |
| Wrong Java version | Section 8: set `<java.version>` in the pom, then Update Project |
| Dependencies will not download | Check internet, proxy settings, or delete the broken folder in `~/.m2/repository` |
| "Port 8080 already in use" | Stop the other app, or set `server.port` |

---

## 11. Web Basics: Request, Response, HTTP Methods

### Client and server

```
Client (browser, mobile app)                 Server (Spring Boot app)
        |                                            |
        |  ------------- Request ------------------> |
        |                                            |  process
        |  <------------ Response ------------------ |
```

Every web interaction is a **request** and a **response**. In Java, they are represented by request and response objects.

### What a request contains

| Part | Meaning | Example |
|---|---|---|
| **HTTP method** | What you want to do | `GET`, `POST` |
| **URL pattern (path)** | Which resource | `/products/5` |
| **Request parameters** | Small inputs in the URL or form | `?name=Asha&city=Pune` |
| **Headers** | Extra information about the request | `Content-Type`, `Authorization`, `Accept` |
| **Payload (body)** | The data being sent | JSON or form data |

**Analogy: a courier parcel.**

| Parcel | Request |
|---|---|
| Type of service (deliver, return, track) | HTTP method |
| Destination address | URL pattern |
| Delivery instruction notes | Request parameters |
| Details on the shipping label | Headers |
| The package contents | Payload |

### HTTP methods

| Method | Purpose | Safe? | Idempotent? | Body? | Library analogy |
|---|---|---|---|---|---|
| **GET** | Read data | Yes | Yes | No | Read a book |
| **POST** | Create / submit | No | No | Yes | Submit a new-member form |
| **PUT** | Replace a whole resource | No | Yes | Yes | Replace an entire record |
| **PATCH** | Change part of a resource | No | Not guaranteed | Yes | Correct one field |
| **DELETE** | Remove | No | Yes | Usually no | Remove a book from the catalog |
| **HEAD** | Like GET, headers only | Yes | Yes | No | Look at the spine label without opening the book |
| **OPTIONS** | Ask which methods are allowed | Yes | Yes | No | Ask the desk what services exist |

- **Safe:** does not change server data.
- **Idempotent:** repeating it has the same effect as doing it once. (Deleting record 5 twice leaves the same state; posting an order twice creates two orders.)

HTML forms natively support only `GET` and `POST`. `PUT` and `DELETE` are normally sent by REST clients or JavaScript.

### Common response status codes

| Code | Meaning |
|---|---|
| 200 OK | Success |
| 201 Created | New resource created |
| 204 No Content | Success, nothing to return |
| 400 Bad Request | Invalid input |
| 401 Unauthorized | Not logged in |
| 403 Forbidden | Logged in, but not allowed |
| 404 Not Found | No such resource |
| 500 Internal Server Error | Server-side failure |

---

## 12. The MVC Design Pattern

### What

**MVC** (Model-View-Controller) splits a UI application into three roles:

| Part | Role | Restaurant analogy |
|---|---|---|
| **Model** | The data (and business logic) | The kitchen and its ingredients |
| **View** | What the user sees (HTML page) | The beautifully plated dish |
| **Controller** | Receives requests, coordinates | The waiter: takes your order, talks to the kitchen, brings the dish |

### Why

If everything sits in one place (HTML mixed with database calls mixed with logic), changing the page design risks breaking the logic, and nothing can be tested separately. **Separation of concerns** lets each part change independently and be tested alone.

### The flow in Spring MVC

```
Browser
   |  1. GET /welcome
   v
DispatcherServlet          (the single front door of Spring MVC)
   |  2. which method handles /welcome?  -> HandlerMapping
   v
Controller method          (@GetMapping("/welcome"))
   |  3. calls Service -> Repository -> Database
   |  4. returns: view name + model data
   v
ViewResolver               (turns "welcome" into templates/welcome.html)
   |
   v
View (Thymeleaf)           5. fills the HTML with model data
   |
   v
Browser  <-- 6. finished HTML response
```

**Analogy:** a hotel reception (DispatcherServlet) that directs every guest to the right department, collects the result, and presents it.

### Typical layers in a real app

```
Controller  ->  Service  ->  Repository  ->  Database
 (web)         (business)     (data access)
```

Each layer talks only to the next one down, and each is a Spring bean wired by DI.

---

## 13. Spring MVC in Practice

### 13.1 The first page: Welcome

**Required starters:** `spring-boot-starter-web` and `spring-boot-starter-thymeleaf`.

**Controller**

```java
@Controller
public class WelcomeController {

    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("name", "Asha");     // data for the view
        return "welcome";                       // view name
    }
}
```

**View: `src/main/resources/templates/welcome.html`** (from the notes)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <meta charset="UTF-8">
    <title>Welcome</title>
</head>

<body>
    <h1>Welcome to Spring MVC</h1>
    <h2>Hello, <span th:text="${name}"></span></h2>
</body>

</html>
```

Visit `http://localhost:8080/welcome` and you see **Hello, Asha**.

### How the pieces match

- Returning `"welcome"` makes the view resolver look for `templates/welcome.html` (default prefix `templates/`, suffix `.html`).
- `model.addAttribute("name", "Asha")` creates a variable `name`.
- `${name}` in the template reads that variable.
- `th:text` replaces the tag's content with the value.

### 13.2 Model vs ModelAndView

Both carry data from the controller to the view. The difference is **how the view name travels**.

**With `Model`:** the model is a method parameter; the method **returns the view name** separately.

```java
@GetMapping("/welcome")
public String welcome(Model model) {
    model.addAttribute("name", "Asha");
    model.addAttribute("city", "Pune");     // as many attributes as you like
    return "welcome";
}
```

**With `ModelAndView`:** one object holds **both the model data and the view name**, and is returned.

```java
@GetMapping("/welcome2")
public ModelAndView welcome2() {
    ModelAndView mv = new ModelAndView("welcome");   // view name
    mv.addObject("name", "Asha");                    // data
    mv.addObject("city", "Pune");
    return mv;
}
```

| | `Model` | `ModelAndView` |
|---|---|---|
| Kind | Interface, passed in as a parameter | Class, created and returned |
| Holds | Data only | Data **and** view name |
| Method returns | `String` (the view name) | `ModelAndView` |
| Number of values | Any number | Any number |

> The notes describe `ModelAndView` as holding "one value and one view name". It can actually hold **many** values (call `addObject` repeatedly). The real distinction is that it bundles the data with the view name, while `Model` carries data only.

### 13.3 Completing the table from the notes: a product list

The notes include an HTML table with an empty header and a `Price` column. Here is a working version.

**Model class**

```java
public record Product(int id, String name, String category, double price) { }
```

**Controller**

```java
@Controller
public class ProductController {

    @GetMapping("/products")
    public String list(Model model) {
        List<Product> products = List.of(
            new Product(1, "Pen", "Stationery", 10.0),
            new Product(2, "Notebook", "Stationery", 45.0),
            new Product(3, "Bag", "Accessories", 799.0)
        );
        model.addAttribute("products", products);
        return "products";
    }
}
```

**View: `templates/products.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Products</title>
</head>
<body>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Category</th>
            <th>Price</th>
        </tr>
        <tr th:each="p : ${products}">
            <td th:text="${p.id()}">1</td>
            <td th:text="${p.name()}">Sample</td>
            <td th:text="${p.category()}">Category</td>
            <td th:text="${p.price()}">0.0</td>
        </tr>
    </table>
</body>
</html>
```

- `th:each="p : ${products}"` repeats the `<tr>` once per product, like a `for-each` loop.
- The dummy text (`Sample`, `0.0`) is replaced at runtime but shows if you open the file directly in a browser. That is Thymeleaf's **natural templates** feature, useful for designers.
- With a record, call the accessor explicitly (`p.name()`). With a normal class with getters, `${p.name}` works.

### 13.4 Reading input from the UI

**Query parameter:** `/greet?name=Asha`

```java
@GetMapping("/greet")
public String greet(@RequestParam(defaultValue = "Guest") String name, Model model) {
    model.addAttribute("name", name);
    return "welcome";
}
```

**Path variable:** `/products/2`

```java
@GetMapping("/products/{id}")
public String one(@PathVariable int id, Model model) {
    // look up product by id, add to model
    return "product-detail";
}
```

**Form submission (POST)**

```html
<form th:action="@{/login}" method="post">
    <input type="text" name="username">
    <input type="password" name="password">
    <button type="submit">Login</button>
</form>
```

```java
@PostMapping("/login")
public String login(@RequestParam String username,
                    @RequestParam String password) {
    // validate...
    return "redirect:/welcome";      // redirect after POST
}
```

### 13.5 `@Controller` vs `@RestController`

| | `@Controller` | `@RestController` |
|---|---|---|
| Returns | A **view name** (HTML page) | **Data** (usually JSON) written directly to the response |
| Used for | Server-rendered web pages | REST APIs (app-to-app communication) |

```java
@RestController
public class ProductApi {

    @GetMapping("/api/products")
    public List<Product> all() {
        return List.of(new Product(1, "Pen", "Stationery", 10.0));   // becomes JSON
    }
}
```

This is item 4 from section 1: one application calling another, typically exchanging JSON over HTTP.

---

## 14. Thymeleaf Cheat Sheet

**Setup:** `<html xmlns:th="http://www.thymeleaf.org">`

### Expression types

| Syntax | Meaning | Example |
|---|---|---|
| `${...}` | Variable from the model | `${name}` |
| `*{...}` | Property of the selected object | `*{email}` |
| `#{...}` | Message from a resource bundle (i18n) | `#{welcome.msg}` |
| `@{...}` | URL | `@{/products}` |
| `~{...}` | Fragment reference | `~{footer :: copy}` |

### Common attributes

| Attribute | Purpose | Example |
|---|---|---|
| `th:text` | Set text (HTML-escaped) | `<span th:text="${name}">` |
| `th:utext` | Set text **without escaping** (risky) | `th:utext="${html}"` |
| `th:each` | Loop | `th:each="p : ${products}"` |
| `th:if` / `th:unless` | Conditional | `th:if="${p.price() > 100}"` |
| `th:href` | Link | `th:href="@{/products}"` |
| `th:src` | Image or script source | `th:src="@{/img/logo.png}"` |
| `th:action` | Form target | `th:action="@{/login}"` |
| `th:value` | Input value | `th:value="${name}"` |
| `th:class` | Dynamic class | `th:class="${p.price() > 100} ? 'big' : 'small'"` |

> `th:text` escapes HTML, so malicious input like `<script>` shows as plain text. Use `th:utext` only for content you fully trust.

---

## 15. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Creating beans with `new` yourself and expecting injection | Not managed, dependencies are `null` | Let the container create it |
| Main class in a different package from other classes | `@ComponentScan` misses them, so "no bean found" | Keep the main class in the root package |
| Two beans of the same type, no choice made | `NoUniqueBeanDefinitionException` | `@Primary` or `@Qualifier` |
| Bean not found | `NoSuchBeanDefinitionException` | Add a stereotype annotation or `@Bean`; check package scanning |
| Circular dependencies (A needs B, B needs A) | Startup failure (Boot prohibits it by default) | Restructure; extract shared logic into a third bean |
| Storing user data in singleton fields | Shared across all users, thread-unsafe | Keep singletons stateless |
| Field injection everywhere | Hidden dependencies, hard to test | Constructor injection |
| Expecting `@PreDestroy` on prototype beans | Never called | Clean up manually |
| Using `javax.annotation.PostConstruct` on Boot 3 | Wrong package | Use `jakarta.annotation.PostConstruct` |
| Returning a view name with no matching template | "Whitelabel Error Page" or `TemplateInputException` | File must be `templates/<name>.html` |
| Using `@RestController` when you want a page | Raw text instead of HTML | Use `@Controller` |
| Forgetting the Thymeleaf starter | View name printed as plain text | Add `spring-boot-starter-thymeleaf` |
| `<td></td` (missing `>`) in HTML | Broken table markup | `<td></td>` |
| Editing Eclipse build path on a Maven project | Overwritten on next update | Change `pom.xml`, then Update Project |
| Wrong JDK for Boot 3 | Build or run errors | Use Java 17 or newer |
| Opening a folder one level too high when importing | "No projects found" | Select the folder with `pom.xml` |

---

## 16. Interview Questions and Answers

**1. What is a framework, and how is it different from a library?**
A framework provides a predefined flow and calls your code (inversion of control). With a library, you call it.

**2. What is Spring?**
A Java framework for building enterprise applications, centered on an IoC container that manages objects and their dependencies.

**3. What is IoC?**
Handing control of object creation and wiring from your code to a container.

**4. What is DI?**
A technique implementing IoC: the container supplies an object's dependencies instead of the object creating them.

**5. IoC vs DI?**
IoC is the principle; DI is the mechanism Spring uses to apply it.

**6. Types of DI?**
Constructor, setter and field injection. Constructor injection is preferred.

**7. Why prefer constructor injection?**
Dependencies are explicit and required, fields can be `final`, and the class is easy to test without Spring.

**8. What is a bean?**
An object managed by the Spring container.

**9. How can beans be configured?**
XML, Java configuration (`@Configuration` and `@Bean`), and annotations (`@Component`, `@Service`...) with component scanning.

**10. What is the difference between `@Component`, `@Service`, `@Repository` and `@Controller`?**
All are beans. They signal the layer: generic, business logic, data access, and web. `@Repository` also translates persistence exceptions.

**11. `@Bean` vs `@Component`?**
`@Component` goes on **your class** and is found by scanning. `@Bean` goes on a **method** in a config class, useful for third-party classes or custom construction.

**12. BeanFactory vs ApplicationContext?**
`ApplicationContext` extends `BeanFactory` and adds AOP integration, i18n, event handling and eager singleton loading. `BeanFactory` loads lazily.

**13. Name some ApplicationContext implementations.**
`ClassPathXmlApplicationContext`, `FileSystemXmlApplicationContext`, `XmlWebApplicationContext`, `AnnotationConfigApplicationContext`.

**14. What are the bean scopes?**
Singleton (default), prototype, request, session, application (and websocket).

**15. Is Spring's singleton the same as the Singleton design pattern?**
No. Spring's is one instance per container per bean definition; the pattern is one instance per class loader.

**16. How do you resolve ambiguity between beans of the same type?**
`@Primary` or `@Qualifier`.

**17. What is the bean lifecycle?**
Instantiate, inject dependencies, initialization callback, use, destruction callback.

**18. What is Spring Boot, and why use it?**
A Spring extension offering auto-configuration, starters, an embedded server and a simple `main` entry point to cut setup work.

**19. What does `@SpringBootApplication` do?**
It combines `@Configuration`, `@EnableAutoConfiguration` and `@ComponentScan`.

**20. What is a starter?**
A single dependency that pulls in a curated set of compatible libraries for a purpose.

**21. What is auto-configuration?**
Boot configures beans automatically based on the libraries present on the classpath.

**22. Which embedded servers does Boot support?**
Tomcat (default), Jetty and Undertow.

**23. What is MVC?**
A pattern splitting an application into Model (data), View (UI) and Controller (request handling).

**24. What is the DispatcherServlet?**
Spring MVC's front controller: all requests go through it, and it routes them to the right controller and view.

**25. `Model` vs `ModelAndView`?**
`Model` carries data and the method returns the view name; `ModelAndView` bundles data and view name in one returned object.

**26. `@Controller` vs `@RestController`?**
`@Controller` returns view names for HTML pages; `@RestController` returns data (such as JSON) directly.

**27. What does a request contain?**
HTTP method, URL, parameters, headers and payload.

**28. What is the difference between `PUT` and `PATCH`?**
`PUT` replaces the whole resource; `PATCH` changes part of it.

**29. What does idempotent mean?**
Repeating the request has the same effect as doing it once (GET, PUT, DELETE are; POST is not).

**30. What is Thymeleaf?**
A server-side template engine that fills HTML templates with model data using `th:` attributes.

---

## 17. Cheat Sheet

**Core ideas**
- Framework: predefined flow; it calls you.
- IoC: the container controls object creation. DI: the container injects dependencies.
- Bean: an object managed by the container.

**Configuration:** XML (legacy), Java (`@Configuration` + `@Bean`), annotations (`@Component` + `@ComponentScan`).

**Containers:** `BeanFactory` (basic, lazy) then `ApplicationContext` (full, eager).

**Scopes:** singleton (default), prototype, request, session, application.

**Boot:** auto-configuration, starters, embedded Tomcat, `main` method, `starter-test`.

**Boot main class:**

```java
@SpringBootApplication
public class App {
    public static void main(String[] args) { SpringApplication.run(App.class, args); }
}
```

**MVC flow:** Browser, DispatcherServlet, Controller, (Service, Repository), view name plus model, ViewResolver, Thymeleaf, HTML.

**Request:** method + URL + parameters + headers + payload.

**Controller recipe:**

```java
@Controller
class X {
    @GetMapping("/path")
    String page(Model model) {
        model.addAttribute("key", value);
        return "viewName";           // templates/viewName.html
    }
}
```

**Eclipse:** Import: *File, Import, Maven, Existing Maven Projects*. JDK: set `<java.version>` in the pom, then *Maven, Update Project*.

---

## 18. Practice Exercises with Solutions

### Exercise 1: Dependency injection with annotations

Create `Engine`, `PetrolEngine` and `Car`, and run them with `AnnotationConfigApplicationContext`.

```java
interface Engine { void start(); }

@Component
class PetrolEngine implements Engine {
    public void start() { System.out.println("Petrol engine started"); }
}

@Component
class Car {
    private final Engine engine;
    Car(Engine engine) { this.engine = engine; }
    void drive() { engine.start(); System.out.println("Car is moving"); }
}

@Configuration
@ComponentScan("com.example")
class AppConfig { }

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                 new AnnotationConfigApplicationContext(AppConfig.class)) {
            ctx.getBean(Car.class).drive();
        }
    }
}
```

**Output:**

```
Petrol engine started
Car is moving
```

### Exercise 2: Prove singleton vs prototype

Use the code from section 7. Expected output: `true` for the singleton comparison and `false` for the prototype comparison.

### Exercise 3: Swap the implementation without touching `Car`

Add a second engine and make it the default.

```java
@Component
@Primary
class DieselEngine implements Engine {
    public void start() { System.out.println("Diesel engine started"); }
}
```

`Car` is unchanged, and now prints *Diesel engine started*. This is the benefit of DI.

### Exercise 4: Convert XML to Java config

Rewrite the `beans.xml` from section 5 as an `@Configuration` class with two `@Bean` methods, as shown in the section 5 Java-config example.

### Exercise 5: Welcome page

Create a Spring Boot project with Web and Thymeleaf, add `WelcomeController` and `welcome.html` from section 13.1, run it, and open `http://localhost:8080/welcome`.

### Exercise 6: Change the port

In `src/main/resources/application.properties`:

```properties
server.port=9090
```

The page is now at `http://localhost:9090/welcome`.

### Exercise 7: Product table

Build the product list from section 13.3, then add one more product and confirm a new row appears with no HTML change.

### Exercise 8: Rewrite with `ModelAndView`

```java
@GetMapping("/products")
public ModelAndView list() {
    ModelAndView mv = new ModelAndView("products");
    mv.addObject("products", List.of(
        new Product(1, "Pen", "Stationery", 10.0),
        new Product(2, "Bag", "Accessories", 799.0)));
    return mv;
}
```

### Exercise 9: Greet by query parameter

`/greet?name=Ravi` shows *Hello, Ravi*; `/greet` shows *Hello, Guest*. Use the `@RequestParam(defaultValue = "Guest")` code from section 13.4.

### Exercise 10: Smoke test

```java
@SpringBootTest
class DemoApplicationTests {
    @Test
    void contextLoads() { }
}
```

Run it. It passes only if all beans can be created, which tests your wiring.

### Challenge questions

1. Why does `new Car(new PetrolEngine())` in `main` not get Spring's benefits?
2. A `@Service` singleton has a field `private String currentUser;` set on every request. What goes wrong?
3. Your page shows the text `welcome` instead of HTML. Name two likely causes.
4. Which scope would you choose for a shopping cart, and why?

**Answers**

1. Spring did not create it, so it is not a bean and gets no injection, lifecycle callbacks or proxies.
2. All requests share one instance. Concurrent users overwrite each other's `currentUser`, so the data is wrong and thread-unsafe. Keep singletons stateless.
3. The Thymeleaf starter is missing, or the controller uses `@RestController` (which returns the string as data).
4. **Session scope**: one cart per user's session, living as long as they are logged in.

---

*End of notes.*
