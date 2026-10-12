# Microservices with Spring Boot and Spring Cloud: Notes

Detailed but compact. Covers concepts, the main patterns, and the Spring Cloud tools for each: Eureka, Feign, API Gateway, Resilience4j, Config Server, messaging, observability and deployment. A running example (customer-service and account-service) ties it together.

---

## Table of Contents

1. [Monolith vs Microservices](#1-monolith-vs-microservices)
2. [Do You Actually Need Microservices?](#2-do-you-actually-need-microservices)
3. [Core Principles](#3-core-principles)
4. [Architecture and Components](#4-architecture-and-components)
5. [Setting Up Spring Cloud](#5-setting-up-spring-cloud)
6. [Service Discovery: Eureka](#6-service-discovery-eureka)
7. [Service-to-Service Communication](#7-service-to-service-communication)
8. [API Gateway](#8-api-gateway)
9. [Resilience: Circuit Breaker, Retry, Timeout](#9-resilience-circuit-breaker-retry-timeout)
10. [Centralized Configuration](#10-centralized-configuration)
11. [Data Management and Saga](#11-data-management-and-saga)
12. [Asynchronous Messaging](#12-asynchronous-messaging)
13. [Observability](#13-observability)
14. [Security](#14-security)
15. [Containers and Deployment](#15-containers-and-deployment)
16. [Patterns at a Glance](#16-patterns-at-a-glance)
17. [Common Mistakes](#17-common-mistakes)
18. [Interview Questions](#18-interview-questions)
19. [Cheat Sheet](#19-cheat-sheet)
20. [Practice Tasks](#20-practice-tasks)

---

## 1. Monolith vs Microservices

### Monolith

The whole application is **one deployable unit**: customers, accounts, loans, cards and notifications all in one codebase and one process.

### Microservices

The application is split into **small, independent services**, each built around one business capability, running in its own process, with its own database, communicating over the network.

### Real-life analogy

| Monolith | Microservices |
|---|---|
| One **big department store**: one billing system, one management. If billing breaks, the whole store stops. | A **shopping mall** of independent shops. Each has its own staff and stock. One shop can renovate or expand without closing the mall. |

The mall also needs shared infrastructure: a **directory board** (service discovery), a **main entrance** (API gateway), **security** and **maintenance** (config, monitoring).

### Banking example

```
Monolith:          [ Customer + Account + Loan + Card + Notification ]  one app, one DB

Microservices:     [customer-service]  [account-service]  [loan-service]
                   [card-service]      [notification-service]     each with its own DB
```

### Comparison

| Aspect | Monolith | Microservices |
|---|---|---|
| Deployment | One unit; every change redeploys everything | Each service deploys independently |
| Scaling | Scale the whole app | Scale only the busy service |
| Technology | One stack | Different stacks per service possible |
| Database | Shared | **One per service** |
| Failure impact | One bug can take everything down | Isolated, if designed for it |
| Communication | In-process method calls (fast, reliable) | Network calls (slower, can fail) |
| Complexity | Simple to build and debug | **Operationally complex** |
| Teams | Everyone in one codebase | Small autonomous teams per service |
| Best for | Small apps, early stage | Large systems, many teams |

---

## 2. Do You Actually Need Microservices?

Microservices solve **organizational and scaling** problems and create **new technical** ones. They are not automatically better.

**Honest trade-offs**

- A method call becomes a network call: slower, and it can fail, time out or return late.
- A simple transaction (`@Transactional`) becomes a distributed data-consistency problem.
- Debugging one request means tracing across many services and logs.
- You now need discovery, gateway, config, monitoring, CI/CD and containers before you ship a feature.

**A poorly designed split gives a "distributed monolith"**: services that cannot be deployed or changed independently, but now communicate over a network. That has all the downsides of both.

**Rule of thumb**

```
Small team, simple domain, early product   -> start with a (modular) monolith
Many teams, parts need independent scaling -> microservices
Unsure                                     -> monolith first, split when the pain is real
```

### How to decide service boundaries

- Split by **business capability** (Customer, Account, Payment), not by technical layer (a "database service" or "controller service" is a bad split).
- Each service maps to a **bounded context** (a Domain-Driven Design idea): one clear meaning of its terms.
- Test: can one small team own it, change it, and deploy it **without coordinating** with other teams?

---

## 3. Core Principles

| Principle | Meaning |
|---|---|
| **Single responsibility** | One service, one business capability |
| **Independent deployability** | Release one service without touching others |
| **Database per service** | Each service owns its data; no shared tables |
| **Loose coupling** | Services depend on each other's **API contracts**, not internals |
| **Own your data, expose an API** | Other services ask; they never read your tables |
| **Design for failure** | Assume the network and other services will fail |
| **Decentralized governance** | Teams choose tools within agreed standards |
| **Automation** | CI/CD, infrastructure as code, automated tests |

**Database-per-service analogy:** each shop keeps its own ledger. To know another shop's stock you **ask them**; you don't walk in and read their ledger.

---

## 4. Architecture and Components

```
                    Clients (web, mobile)
                            |
                     [ API Gateway ]           single entry point
                            |
        +-------------------+-------------------+
        |                   |                   |
 [customer-service]  [account-service]   [loan-service]
        |                   |                   |
     own DB              own DB              own DB
        \                   |                  /
         +----- [ Eureka: Service Discovery ] -----+
         +----- [ Config Server ]  (settings)  ----+
         +----- [ Message Broker: Kafka/RabbitMQ ] -+
         +----- [ Monitoring / Tracing / Logging ] --+
```

### Concern to tool

| Concern | Problem | Spring / ecosystem solution |
|---|---|---|
| **Service discovery** | Where is account-service right now? | **Eureka** (or Consul; Kubernetes DNS) |
| **Client-side load balancing** | Which of the 3 instances should I call? | **Spring Cloud LoadBalancer** |
| **Declarative REST calls** | Cleaner inter-service calls | **OpenFeign** (or `RestClient`, `WebClient`) |
| **Single entry point** | Routing, auth, rate limiting | **Spring Cloud Gateway** |
| **Fault tolerance** | Downstream service is slow or down | **Resilience4j** |
| **Central config** | Settings for many services in one place | **Spring Cloud Config** (or Kubernetes ConfigMaps) |
| **Async communication** | Decouple services, handle spikes | **Kafka / RabbitMQ** |
| **Tracing and metrics** | Follow one request across services | **Micrometer, Zipkin/OpenTelemetry, Prometheus, Grafana** |
| **Security** | Who is calling? | **Spring Security, OAuth2/JWT** |
| **Packaging and running** | Consistent deployment | **Docker, Kubernetes** |

### Outdated names you will see in older tutorials

| Old | Status | Replaced by |
|---|---|---|
| Netflix **Ribbon** | Removed | Spring Cloud LoadBalancer |
| Netflix **Hystrix** | Removed/retired | Resilience4j |
| Netflix **Zuul** | Replaced | Spring Cloud Gateway |
| Spring Cloud **Sleuth** | Removed in Boot 3 | Micrometer Tracing |
| `RestTemplate` | Maintenance mode | `RestClient` / `WebClient` / Feign |

---

## 5. Setting Up Spring Cloud

Spring Cloud versions must **match your Spring Boot version**. Use a **BOM** so every Spring Cloud dependency gets a compatible version.

```xml
<properties>
    <java.version>17</java.version>
    <spring-cloud.version><!-- use the value Spring Initializr generates for your Boot version --></spring-cloud.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Easiest approach:** generate each project at **start.spring.io**, adding the Spring Cloud starters you need. It fills in the right versions.

### Typical ports (convention)

| Component | Port |
|---|---|
| API Gateway | 8080 |
| Eureka Server | 8761 |
| Config Server | 8888 |
| customer-service | 8081 |
| account-service | 8082 |

---

## 6. Service Discovery: Eureka

### Why

Service instances come and go (scaling, crashes, redeploys) and get new IPs and ports. Hard-coding `http://192.168.1.15:8082` breaks immediately.

**Analogy:** a **phone directory**. Each service registers itself ("account-service, call me here"). Others look it up **by name**, never by number.

### How it works

```
1. account-service starts  -> REGISTERS with Eureka (name, host, port)
2. Every ~30 s             -> sends a HEARTBEAT ("I'm alive")
3. customer-service wants account-service
                           -> asks Eureka -> gets the list of live instances
4. Heartbeats stop         -> Eureka REMOVES the instance
```

Clients **cache** the registry locally, so they keep working briefly even if Eureka is down.

### Eureka Server

Dependency: `spring-cloud-starter-netflix-eureka-server`

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```properties
server.port=8761
spring.application.name=eureka-server
eureka.client.register-with-eureka=false     # the server need not register with itself
eureka.client.fetch-registry=false
```

Dashboard: `http://localhost:8761`

### Eureka Client (every service)

Dependency: `spring-cloud-starter-netflix-eureka-client`

```properties
server.port=8082
spring.application.name=account-service                 # THE NAME other services use
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

`@EnableDiscoveryClient` is **optional** in current versions; having the client starter on the classpath is enough.

### Running several instances

```properties
server.port=0                                            # random free port
eureka.instance.instance-id=${spring.application.name}:${random.value}
```

Start the same service multiple times and the Eureka dashboard shows several instances under one name. Callers are load-balanced across them automatically.

### Good to know

- The **application name** is the identity. Spaces and mixed case cause confusion; use lowercase-with-hyphens.
- **Self-preservation mode:** if Eureka suddenly stops receiving many heartbeats (maybe a network glitch), it stops evicting instances to avoid wiping the registry. The dashboard shows a warning. Normal in local development.
- In **Kubernetes**, built-in Services and DNS do the discovery job, so Eureka is often unnecessary there.

---

## 7. Service-to-Service Communication

### Synchronous vs asynchronous

| | Synchronous (REST/HTTP) | Asynchronous (messaging) |
|---|---|---|
| Style | Ask and **wait** for the answer | Send a message and **move on** |
| Analogy | **Phone call** | **WhatsApp message / email** |
| Coupling | Tighter (caller needs the callee up now) | Looser |
| Use when | You need the answer now (fetch balance) | Fire-and-forget or event notification (account created, send email) |
| Tools | OpenFeign, `RestClient`, `WebClient` | Kafka, RabbitMQ |

### The running example

`customer-service` needs a customer's accounts, which belong to `account-service`.

**account-service**

```java
public record Account(Long id, Long customerId, String type, double balance) { }

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @GetMapping("/customer/{customerId}")
    public List<Account> byCustomer(@PathVariable Long customerId) {
        return accountRepository.findByCustomerId(customerId);   // account-service's own DB
    }
}
```

### Option 1: OpenFeign (declarative, the common choice)

Dependency: `spring-cloud-starter-openfeign`

```java
@SpringBootApplication
@EnableFeignClients
public class CustomerServiceApplication { ... }
```

```java
public record AccountDto(Long id, Long customerId, String type, double balance) { }

@FeignClient(name = "account-service")              // the Eureka name, not a URL
public interface AccountClient {

    @GetMapping("/accounts/customer/{customerId}")
    List<AccountDto> getAccounts(@PathVariable("customerId") Long customerId);
}
```

```java
public record CustomerDetails(Long id, String name, List<AccountDto> accounts) { }

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customers;
    private final AccountClient accountClient;

    public CustomerController(CustomerRepository customers, AccountClient accountClient) {
        this.customers = customers;
        this.accountClient = accountClient;
    }

    @GetMapping("/{id}/details")
    public CustomerDetails details(@PathVariable Long id) {
        Customer c = customers.findById(id).orElseThrow();
        List<AccountDto> accounts = accountClient.getAccounts(id);     // remote call
        return new CustomerDetails(c.getId(), c.getName(), accounts);
    }
}
```

**What Feign does:** you write an **interface**; Feign generates the HTTP client. It asks Eureka for `account-service` instances, picks one using the load balancer, and makes the call.

**Analogy:** instead of dialing numbers yourself, you tell your assistant "get me account-service" and they dial.

### Option 2: `RestTemplate` with `@LoadBalanced` (older style)

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() { return new RestTemplate(); }

AccountDto[] accounts = restTemplate.getForObject(
        "http://account-service/accounts/customer/{id}", AccountDto[].class, id);
```

`@LoadBalanced` lets you use the **service name** as the host. `RestTemplate` is in maintenance mode; prefer Feign or `RestClient` for new code.

### Option 3: `RestClient` or `WebClient`

- `RestClient` (Spring 6.1+): modern synchronous client with a fluent API.
- `WebClient`: reactive and non-blocking.

Both can be load-balanced by registering a builder with `@LoadBalanced`.

### Client-side load balancing

```
customer-service ---> asks Eureka: instances of account-service?
                      [ :8082 , :8083 , :8084 ]
                 ---> Spring Cloud LoadBalancer picks one (round-robin by default)
```

**Analogy:** a bank with several counters and a token display: you are directed to whichever counter is next.

"Client-side" means the **caller** chooses the instance, instead of a separate load-balancer server.

---

## 8. API Gateway

### What and why

A single **entry point** for all client requests, which then routes to the right service.

Without a gateway, clients must know every service's address and each service must separately handle auth, CORS and rate limiting.

**Analogy:** a **hotel reception desk** or airport entrance. Guests talk to one desk, which verifies them and directs them to the right department.

### What a gateway does

| Function | Example |
|---|---|
| **Routing** | `/customers/**` goes to customer-service |
| **Authentication** | Validate the JWT once, centrally |
| **Rate limiting** | Max 100 requests per minute per client |
| **Cross-cutting filters** | Logging, adding headers, correlation IDs |
| **CORS** | Configured in one place |
| **Aggregation / protocol translation** | Combine or adapt responses |

### Spring Cloud Gateway: routes

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: customer-service
          uri: lb://customer-service        # lb:// = load-balanced lookup via Eureka
          predicates:
            - Path=/customers/**
        - id: account-service
          uri: lb://account-service
          predicates:
            - Path=/accounts/**
```

Now `GET localhost:8080/customers/1/details` reaches customer-service, wherever it runs.

| Term | Meaning |
|---|---|
| **Route** | One mapping: id, destination `uri`, predicates, filters |
| **Predicate** | Condition to match a request (`Path`, `Method`, `Header`, `Query`) |
| **Filter** | Modify the request or response (`AddRequestHeader`, `RewritePath`, `StripPrefix`) |

> **Version note:** newer Spring Cloud releases renamed the gateway starters (for example `spring-cloud-starter-gateway-server-webflux`) and the property prefix (`spring.cloud.gateway.server.webflux.*`). Generate the project at start.spring.io and follow the reference docs for your release.

---

## 9. Resilience: Circuit Breaker, Retry, Timeout

### Why

In a chain `A -> B -> C`, if C is slow, B's threads pile up waiting, then A's, and the whole system **cascades into failure**.

### Circuit breaker

**Analogy:** the **MCB/fuse** in your home. When a fault is detected, it trips so the fault does not burn the whole house. After a while you try switching it on again.

```
CLOSED  --(failure rate too high)-->  OPEN  --(wait time passes)-->  HALF-OPEN
  ^                                    |                                  |
  |                       calls fail fast, fallback runs          a few trial calls
  +---------------(trial calls succeed)--------------------------------+
                          (trial calls fail -> back to OPEN)
```

| State | Behavior |
|---|---|
| **Closed** | Normal; calls pass through; failures are counted |
| **Open** | Calls are **rejected immediately** (fallback runs); no load on the sick service |
| **Half-open** | A few trial calls test recovery |

### Resilience4j

Dependencies: Resilience4j Spring Boot 3 starter (or `spring-cloud-starter-circuitbreaker-resilience4j`) and Spring AOP.

```java
@Service
public class AccountLookupService {

    private final AccountClient accountClient;
    public AccountLookupService(AccountClient accountClient) { this.accountClient = accountClient; }

    @CircuitBreaker(name = "accountService", fallbackMethod = "fallback")
    @Retry(name = "accountService")
    public List<AccountDto> accountsOf(Long customerId) {
        return accountClient.getAccounts(customerId);
    }

    // same parameters + a Throwable, same return type
    private List<AccountDto> fallback(Long customerId, Throwable t) {
        return List.of();       // or cached data, or a "temporarily unavailable" marker
    }
}
```

```yaml
resilience4j:
  circuitbreaker:
    instances:
      accountService:
        sliding-window-size: 10
        failure-rate-threshold: 50              # % of failures that opens the circuit
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
  retry:
    instances:
      accountService:
        max-attempts: 3
        wait-duration: 500ms
```

### The resilience toolkit

| Pattern | Annotation | Purpose | Analogy |
|---|---|---|---|
| **Circuit breaker** | `@CircuitBreaker` | Stop calling a failing service | Fuse |
| **Retry** | `@Retry` | Try again on transient errors | Redialing a busy number (after a pause) |
| **Time limiter** | `@TimeLimiter` | Give up after a timeout | Hanging up after 30 seconds of ringing |
| **Rate limiter** | `@RateLimiter` | Cap calls per period | A ticket counter limiting people per hour |
| **Bulkhead** | `@Bulkhead` | Limit concurrent calls so one dependency cannot use all threads | Watertight ship compartments |
| **Fallback** | `fallbackMethod` | A degraded but useful answer | "Showing cached balance" |

### Cautions

- **Retry only idempotent operations.** Retrying "debit Rs. 500" can debit twice. Use idempotency keys for writes.
- Always set **timeouts**; the default can be unbounded.
- Use **exponential backoff** with retries so a struggling service isn't hammered.
- Feign can use circuit breakers directly: `spring.cloud.openfeign.circuitbreaker.enabled=true` and `@FeignClient(..., fallback = MyFallback.class)`.

---

## 10. Centralized Configuration

### Why

With 20 services, each with its own `application.properties`, changing a shared setting means editing and redeploying 20 apps. Environments (dev, test, prod) multiply the problem.

**Analogy:** one **company policy manual** in the HR portal, instead of a separate photocopy on every desk.

### Spring Cloud Config

**Config Server** (reads settings from a Git repository)

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication { ... }
```

```properties
server.port=8888
spring.cloud.config.server.git.uri=https://github.com/your-org/config-repo
```

**Config repo layout**

```
config-repo/
   application.yml               # shared by all services
   account-service.yml           # account-service only
   account-service-prod.yml      # account-service, prod profile
```

**Client**

```properties
spring.application.name=account-service
spring.config.import=optional:configserver:http://localhost:8888
```

### Refreshing without restart

```java
@RefreshScope
@RestController
class InfoController {
    @Value("${app.message}") String message;
}
```

Change the value in Git, then `POST /actuator/refresh` (or use Spring Cloud Bus to broadcast to all).

### Notes

- Never commit **secrets** in plain text; use encryption, Vault or environment variables.
- On **Kubernetes**, ConfigMaps and Secrets often replace Config Server.

---

## 11. Data Management and Saga

### The problem

With a database per service, you lose two monolith conveniences:

1. **No cross-service joins.** Customer data and account data live in different databases.
2. **No single `@Transactional`** across services.

### Getting data from another service

| Approach | Description |
|---|---|
| **API composition** | Call the other service's API and combine results (the Feign example above) |
| **Data replication via events** | A service keeps a **local copy** of needed fields, updated from events |
| **CQRS read model** | A dedicated read database built from events for fast queries |

### Eventual consistency

Data across services is consistent **eventually**, not instantly. A balance shown in one service may be a few moments behind. Design the user experience for that.

### The Saga pattern (distributed transactions)

Two-phase commit across services is slow and fragile, so microservices use a **saga**: a sequence of **local transactions**, each followed by an event or call. If a step fails, **compensating actions** undo the earlier steps.

**Analogy: booking a holiday.** Book flight, then hotel, then cab. If the hotel is unavailable, you **cancel the flight** (compensation) and the refund restores the original state.

**Bank transfer saga**

```
1. account-service: debit account A        (local transaction)
2. account-service: credit account B       (local transaction)
   if step 2 FAILS -> compensate: credit account A back (refund)
```

| Style | How |
|---|---|
| **Choreography** | Each service reacts to events and emits the next; no central controller. Simple for small flows, hard to follow in big ones |
| **Orchestration** | A central coordinator tells each service what to do next and handles compensation. Clearer for complex flows |

### Related patterns

- **Event sourcing:** store the sequence of events, not just current state.
- **Outbox pattern:** write the business change and the outgoing event in the **same local transaction**, then publish from the outbox table. It avoids "saved to DB but event lost."

---

## 12. Asynchronous Messaging

### Why

- The caller does not wait or fail if the receiver is down.
- Absorbs traffic spikes (messages queue up).
- One event can have many consumers without the producer knowing about them.

**Analogy:** the **post office**. You drop the letter and leave; delivery happens later and you don't stand at the counter waiting.

### Example: account created, notify the customer

```
account-service --[AccountCreated event]--> Kafka topic --> notification-service (email)
                                                        --> customer-service (update stats)
```

```java
// producer (account-service)
kafkaTemplate.send("account-events", new AccountCreated(accountId, customerId));

// consumer (notification-service)
@KafkaListener(topics = "account-events", groupId = "notification-service")
public void handle(AccountCreated event) {
    // send email
}
```

### Kafka vs RabbitMQ (short)

| | Kafka | RabbitMQ |
|---|---|---|
| Model | Distributed **log**; consumers read at their own offset | **Queue** broker with routing |
| Strength | High throughput, replay, event streaming | Flexible routing, task queues |
| Retention | Keeps messages for a period; replayable | Removes messages once consumed |

### Delivery cautions

- Most brokers give **at-least-once** delivery, so a message may arrive twice. Make consumers **idempotent**.
- Plan for **poison messages** (dead-letter queues) and **ordering** (Kafka guarantees order only within a partition).

---

## 13. Observability

You cannot debug a distributed system by reading one log file. You need three signals:

| Signal | Question | Tools |
|---|---|---|
| **Logs** | What happened? | Centralized: ELK (Elasticsearch, Logstash, Kibana), Loki |
| **Metrics** | How is it performing? | Spring Boot **Actuator** + **Micrometer** + **Prometheus** + **Grafana** |
| **Traces** | Where did this request spend its time? | **Micrometer Tracing** + **Zipkin** / OpenTelemetry |

### Distributed tracing

A **trace ID** is attached to a request at the gateway and passed along every hop. Each hop adds a **span ID**.

**Analogy:** a **courier tracking number** that follows your parcel through every hub.

Log lines then show it, so you can search all services for one trace ID:

```
[customer-service, traceId=4bf92f35..., spanId=00f067aa...] Fetching accounts
[account-service,  traceId=4bf92f35..., spanId=a2fb4a1d...] Querying DB
```

> Spring Cloud **Sleuth** is gone in Boot 3; use **Micrometer Tracing**.

### Actuator essentials

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

`/actuator/health` is used by Eureka, Kubernetes and load balancers to check liveness.

---

## 14. Security

| Layer | Approach |
|---|---|
| **Edge** | Gateway authenticates users; validates **JWT** access tokens |
| **Identity** | Central identity provider (Keycloak, Auth0, Spring Authorization Server) using **OAuth2 / OpenID Connect** |
| **Service-to-service** | Pass or exchange tokens; mutual TLS inside the cluster |
| **Authorization** | Each service checks roles and scopes for its own data (`@PreAuthorize`) |
| **Secrets** | Vault, Kubernetes Secrets, environment variables, never in Git |
| **Network** | Only the gateway is public; services stay on a private network |

Each service should be a **resource server** that validates the token itself, so a compromised internal call cannot bypass checks.

---

## 15. Containers and Deployment

Microservices pair naturally with **containers**: each service is packaged with its runtime into an image that runs the same everywhere.

### Dockerfile for a Spring Boot service

```dockerfile
FROM eclipse-temurin:17-jre
COPY target/account-service.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```
docker build -t account-service .
docker run -p 8082:8082 account-service
```

### Docker Compose: many services locally

Compose starts the whole system (Eureka, gateway, services, databases) with one command.

### Kubernetes: production orchestration

Handles scaling, restarts, rolling updates, service discovery (DNS), config (ConfigMaps/Secrets) and load balancing.

### Pitfalls inside containers

- **`localhost` means "this container"**, not other services. Use service names (Compose) or Kubernetes Service names.
- Eureka may register the container's hostname; set `eureka.instance.prefer-ip-address=true` if other services cannot resolve it.
- Define **health checks** and **resource limits**.

### Pipeline

```
Commit -> build + unit tests -> image build -> integration tests -> deploy (rolling/canary)
```

Each service has **its own pipeline**; that is what makes independent deployment real.

---

## 16. Patterns at a Glance

| Pattern | One-liner |
|---|---|
| **API Gateway** | One entry point that routes and secures |
| **Service Discovery** | Find services by name, not address |
| **Client-side Load Balancing** | Caller picks an instance |
| **Circuit Breaker** | Fail fast on a broken dependency |
| **Retry / Timeout / Bulkhead** | Handle transient faults and limit the blast radius |
| **Database per Service** | Each service owns its data |
| **Saga** | Distributed transaction as local steps plus compensations |
| **Event-Driven Architecture** | Services communicate through events |
| **CQRS** | Separate read and write models |
| **Event Sourcing** | Store changes as a sequence of events |
| **Outbox** | Atomically save data and the event to publish |
| **Externalized Configuration** | Config outside the code |
| **Distributed Tracing** | One trace ID across services |
| **Backend for Frontend (BFF)** | A tailored gateway per client type (web, mobile) |
| **Strangler Fig** | Migrate a monolith gradually by routing pieces to new services |
| **Sidecar** | Helper container beside a service (proxy, logging) |

---

## 17. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Splitting too early or too fine | Distributed monolith, high cost | Start modular; split by business capability |
| Shared database between services | Tight coupling, no independent change | Database per service; communicate via APIs or events |
| Hard-coded URLs and ports | Breaks on scaling or redeploy | Discovery by service name |
| No timeouts on remote calls | Threads hang, cascading failure | Timeouts, circuit breakers |
| Retrying non-idempotent writes | Double debits | Idempotency keys; retry only safe operations |
| Long synchronous call chains | Latency adds up, failures multiply | Reduce hops; use events |
| Assuming a `@Transactional` spans services | It does not | Saga, eventual consistency |
| Missing `spring.application.name` | Service registers as `UNKNOWN` | Set it in every service |
| Eureka server registering with itself | Warnings and noise | `register-with-eureka=false`, `fetch-registry=false` |
| Spring Cloud and Spring Boot versions mismatched | Startup errors | Use the BOM version matching your Boot version |
| Following Ribbon, Hystrix or Zuul tutorials | Removed from modern Spring Cloud | LoadBalancer, Resilience4j, Gateway |
| `localhost` in Docker config | Cannot reach other containers | Use service names |
| Missing `@EnableFeignClients` | Feign interface not found as a bean | Add it to the main class |
| Feign `@PathVariable` without a name | "Name not specified" errors on some setups | `@PathVariable("id")` |
| Exposing every service publicly | Large attack surface | Only the gateway is public |
| No tracing or central logs | Impossible debugging | Trace IDs and centralized logging |
| Secrets in Git | Credential leaks | Vault, Secrets, env vars |
| Backward-incompatible API changes | Callers break | Version APIs; add fields, don't rename or remove |

---

## 18. Interview Questions

**1. What are microservices?** An architecture where an application is split into small, independently deployable services, each owning one business capability and its own data, communicating over the network.

**2. Monolith vs microservices?** One deployable unit vs many independent ones. Microservices gain independent scaling and deployment but add distributed-system complexity.

**3. Advantages?** Independent deployment and scaling, fault isolation, technology freedom, small focused teams.

**4. Disadvantages?** Network latency and failures, data consistency challenges, operational complexity, harder testing and debugging.

**5. What is service discovery, and why is it needed?** A registry (Eureka) where services register by name, so others find live instances without hard-coded addresses.

**6. How does Eureka work?** Clients register, send heartbeats about every 30 seconds, fetch and cache the registry, and are evicted when heartbeats stop.

**7. What is `@EnableEurekaServer`?** Turns a Spring Boot app into a Eureka registry server.

**8. What is OpenFeign?** A declarative HTTP client: you write an interface with annotations and Feign generates the implementation, integrating with discovery and load balancing.

**9. `@FeignClient(name = "account-service")`: what is the name?** The service's name as registered in Eureka (its `spring.application.name`).

**10. What is client-side load balancing?** The caller picks among available instances (Spring Cloud LoadBalancer, round-robin by default).

**11. What is an API gateway?** A single entry point for routing, authentication, rate limiting and cross-cutting concerns.

**12. What does `lb://service-name` mean in a gateway route?** Route to that service through the load balancer using Eureka lookup.

**13. What is a circuit breaker?** A pattern that stops calling a failing dependency (open state), returning fast with a fallback, and probes recovery (half-open).

**14. What replaced Hystrix?** Resilience4j.

**15. What is a fallback?** A degraded alternative response when a call fails or the circuit is open.

**16. What is the database-per-service pattern?** Each service owns its private database; others access data only through its API or events.

**17. How do you handle transactions across services?** The Saga pattern: local transactions with compensating actions, using choreography or orchestration.

**18. What is eventual consistency?** Data across services converges over time rather than being instantly consistent.

**19. Synchronous vs asynchronous communication?** Synchronous (REST) waits for a response; asynchronous (Kafka, RabbitMQ) sends messages without waiting, giving looser coupling.

**20. Why must message consumers be idempotent?** Brokers often deliver at least once, so duplicates can occur.

**21. What is distributed tracing?** Following one request across services via a shared trace ID (Micrometer Tracing with Zipkin or OpenTelemetry).

**22. What is Spring Cloud Config?** A server providing centralized, versioned (Git-backed) configuration to all services.

**23. What does `@RefreshScope` do?** Lets beans re-read changed configuration without restarting.

**24. How do you secure microservices?** Gateway authentication, OAuth2/JWT, each service validating tokens, mutual TLS between services, secrets management.

**25. What is the Strangler Fig pattern?** Gradually replacing parts of a monolith by routing functionality to new services until the monolith disappears.

**26. What is a distributed monolith?** Services that must be deployed together or share a database, giving the costs of both architectures.

**27. How do you version APIs?** URL or header versioning, additive changes, and deprecation periods so consumers can migrate.

**28. Eureka vs Kubernetes service discovery?** Eureka is application-level and common in Spring Cloud setups. Kubernetes provides discovery and load balancing natively through Services and DNS, often removing the need for Eureka.

---

## 19. Cheat Sheet

| Need | Tool | Key piece |
|---|---|---|
| Find services by name | Eureka | `@EnableEurekaServer`; `spring.application.name`; `eureka.client.service-url.defaultZone` |
| Call another service | OpenFeign | `@EnableFeignClients`; `@FeignClient(name = "...")` |
| Pick an instance | Spring Cloud LoadBalancer | `@LoadBalanced`, `lb://name` |
| Single entry point | Spring Cloud Gateway | routes: `id`, `uri: lb://...`, `predicates: Path=...` |
| Survive failures | Resilience4j | `@CircuitBreaker`, `@Retry`, `@TimeLimiter`, `@Bulkhead`, `@RateLimiter` |
| Central settings | Spring Cloud Config | `@EnableConfigServer`; `spring.config.import=configserver:...`; `@RefreshScope` |
| Async events | Kafka / RabbitMQ | `KafkaTemplate`, `@KafkaListener` |
| Distributed transactions | Saga / Outbox | Compensating actions |
| See what is happening | Actuator, Micrometer, Zipkin, Prometheus, Grafana | `/actuator/health` |
| Package and run | Docker, Kubernetes | `Dockerfile`, Compose, manifests |

**Startup order for the example**

```
1. Eureka Server          (8761)
2. Config Server          (8888)  -- if used
3. account-service        (8082)
4. customer-service       (8081)
5. API Gateway            (8080)
```

**Request flow**

```
Client -> Gateway (auth, route) -> customer-service
                                     -> (Feign + LoadBalancer + Eureka) -> account-service
                                     <- accounts
        <- combined response
```

---

## 20. Practice Tasks

1. **Eureka:** create a Eureka server and two clients (`customer-service`, `account-service`); confirm both appear on the dashboard.
2. **Feign:** make `customer-service` call `account-service` through a `@FeignClient`, returning a customer with their accounts.
3. **Load balancing:** run **two instances** of `account-service` (`server.port=0`), call the customer endpoint repeatedly, and log which instance served each request.
4. **Gateway:** add an API gateway so everything is reachable on port 8080 via `/customers/**` and `/accounts/**`.
5. **Resilience:** stop `account-service`, call the customer endpoint, and add a circuit breaker with a fallback that returns an empty account list. Watch the circuit open.
6. **Config:** move a shared property into a Config Server backed by a local Git repo.
7. **Design exercise:** sketch services and databases for an e-commerce app (users, catalog, orders, payments, notifications). For "place order", decide which calls are synchronous and which events, and describe the saga if payment fails.

### Challenge questions

1. `customer-service` calls `account-service` with Feign and the latter is down. What happens without any resilience setup, and what happens with a circuit breaker and fallback?
2. Two services need the same customer's name. Is sharing one database table acceptable? Why or why not?
3. A retry on `POST /transfer` caused a double debit. What went wrong and how do you fix it?

**Answers**

1. Without resilience, the call fails (after a timeout) with an exception and may hold threads, risking cascading failure. With a circuit breaker, repeated failures open the circuit; later calls fail instantly and the fallback returns a degraded response, protecting the caller's resources.
2. No. A shared table couples the services' schemas, so neither can change independently (breaking the independent-deployability principle). Use an API call, or replicate the needed fields via events.
3. The write operation was not idempotent, and the retry repeated a completed action. Fix: send an **idempotency key**; the server stores processed keys and ignores duplicates, or avoid auto-retry on non-idempotent calls.

---

*End of notes.*
