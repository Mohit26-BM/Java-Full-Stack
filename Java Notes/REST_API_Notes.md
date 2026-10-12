# REST APIs with Spring Boot: Detailed Notes

REST principles, resource and URL design, HTTP methods and status codes, building APIs in Spring Boot (controllers, DTOs, validation, error handling, pagination), a complete CRUD example, calling APIs from React and Java, CORS, documentation, testing, versioning, best practices, mistakes and interview questions.

> This builds on the earlier notes: HTTP basics and MVC (Spring Boot notes), entities and repositories (JPA notes), the global exception handler (Exception Handling notes) and Feign (Microservices notes).

---

## Table of Contents

1. [What Is an API, and What Is REST](#1-what-is-an-api-and-what-is-rest)
2. [Resources and URI Design](#2-resources-and-uri-design)
3. [HTTP Methods in REST](#3-http-methods-in-rest)
4. [Status Codes](#4-status-codes)
5. [Request and Response Anatomy](#5-request-and-response-anatomy)
6. [Building REST Controllers in Spring Boot](#6-building-rest-controllers-in-spring-boot)
7. [DTOs and Layered Architecture](#7-dtos-and-layered-architecture)
8. [Validation](#8-validation)
9. [Error Handling](#9-error-handling)
10. [Pagination, Sorting, Filtering](#10-pagination-sorting-filtering)
11. [Complete CRUD Example](#11-complete-crud-example)
12. [Calling REST APIs](#12-calling-rest-apis)
13. [CORS](#13-cors)
14. [API Documentation with OpenAPI](#14-api-documentation-with-openapi)
15. [Testing REST APIs](#15-testing-rest-apis)
16. [Versioning, Idempotency, Caching, Security Basics](#16-versioning-idempotency-caching-security-basics)
17. [Best Practices Checklist](#17-best-practices-checklist)
18. [Common Mistakes](#18-common-mistakes)
19. [Which Status Code? Quiz](#19-which-status-code-quiz)
20. [Interview Questions](#20-interview-questions)
21. [Cheat Sheet](#21-cheat-sheet)
22. [Practice Tasks with Solutions](#22-practice-tasks-with-solutions)

---

## 1. What Is an API, and What Is REST

### API

An **API (Application Programming Interface)** is a contract that lets one piece of software use another without knowing its internals.

**Analogy: a restaurant.**

| Restaurant | Web API |
|---|---|
| Menu | **API documentation** |
| Waiter | **The API** |
| Your order slip | **Request** |
| Kitchen | **Server and database** |
| The plate delivered | **Response** |
| "Table 5, order 12" | **Resource URL** |

You never enter the kitchen; you order through the waiter.

### REST

> **REST (REpresentational State Transfer)** is an architectural style for designing web APIs around **resources** identified by URLs and manipulated through standard **HTTP methods**.

A **RESTful API** typically exchanges **JSON** over HTTP. A **resource** is any "thing" (employee, order, product). A **representation** is its data in a format (usually JSON).

```
GET /api/v1/employees/5   ->   { "id": 5, "name": "Asha", "department": "IT" }
```

### The six REST constraints

| Constraint | Meaning | Why |
|---|---|---|
| **Client-server** | UI and data/logic are separate | Each can evolve independently (React frontend, Spring backend) |
| **Stateless** | **Each request carries everything needed**; the server keeps no client session between requests | Easy scaling and load balancing |
| **Cacheable** | Responses say whether they can be cached | Performance |
| **Uniform interface** | Standard methods, URLs for resources, self-descriptive messages | Predictability |
| **Layered system** | Gateways, proxies and load balancers can sit in between | Flexibility and security |
| **Code on demand** (optional) | Server can send executable code | Rarely used |

**Stateless analogy:** calling customer care where **every call starts fresh**. You must give your customer ID each time; the agent does not remember your last call. For APIs, that "ID" is usually a **token** in the `Authorization` header.

### REST vs other styles

| Style | Idea | Typical use |
|---|---|---|
| **REST** | Resources over HTTP, JSON | Most web and mobile backends |
| **SOAP** | Strict XML protocol with contracts | Legacy enterprise and banking |
| **GraphQL** | One endpoint; client specifies exactly the fields it wants | Complex client data needs |
| **gRPC** | Binary Protobuf over HTTP/2 | Fast service-to-service calls |

### Richardson Maturity Model

| Level | Description |
|---|---|
| 0 | One endpoint, one method for everything (RPC style) |
| 1 | **Separate resources** (many URLs) |
| 2 | **HTTP methods and status codes used properly** (what most "REST" APIs are) |
| 3 | **HATEOAS**: responses include links to related actions |

Aim for **level 2**; level 3 is rare in practice.

---

## 2. Resources and URI Design

### Rules

| Rule | Good | Bad |
|---|---|---|
| Use **nouns**, not verbs (the HTTP method is the verb) | `GET /employees` | `GET /getEmployees` |
| Use **plural** nouns consistently | `/employees/5` | `/employee/5`, mixing both |
| **Lowercase**, words separated by **hyphens** | `/order-items` | `/orderItems`, `/Order_Items` |
| **Hierarchy** shows relationships | `/departments/3/employees` | `/getEmployeesOfDepartment?id=3` |
| **Path** identifies a resource; **query** filters or sorts | `/employees?dept=IT&sort=name` | `/employees/dept/IT/sort/name` |
| No file extensions | `/employees/5` | `/employees/5.json` |
| No trailing slash, no verbs in the path | `/employees` | `/employees/` |
| Version the API | `/api/v1/employees` | (none, until you break clients) |

### Typical resource map

```
GET     /api/v1/employees                   list (with filters, paging)
GET     /api/v1/employees/{id}              one employee
POST    /api/v1/employees                   create
PUT     /api/v1/employees/{id}              replace
PATCH   /api/v1/employees/{id}              partial update
DELETE  /api/v1/employees/{id}              delete

GET     /api/v1/departments/{id}/employees  employees of a department
```

### Actions that are not CRUD

Prefer modeling them as **resources or sub-resources**; pragmatic APIs sometimes use an action sub-path:

```
POST /orders/{id}/cancel         (pragmatic)
POST /orders/{id}/payments       (a payment is a resource)
```

### Path variable or query parameter?

| Use a **path variable** when | Use a **query parameter** when |
|---|---|
| It **identifies** a specific resource (`/employees/5`) | It **filters, sorts, pages or modifies** a collection (`?dept=IT&page=2`) |
| It is required | It is optional |

---

## 3. HTTP Methods in REST

### CRUD mapping

**Analogy: a contacts app.**

| Operation | HTTP method | Contacts app |
|---|---|---|
| **R**ead | `GET` | Open a contact |
| **C**reate | `POST` | Add a **new** contact |
| **U**pdate (replace) | `PUT` | Replace the **whole** contact card |
| **U**pdate (partial) | `PATCH` | Edit **one field**, like the phone number |
| **D**elete | `DELETE` | Remove the contact |

### Properties of each method

| Method | Purpose | Safe? | Idempotent? | Request body? | Cacheable? |
|---|---|---|---|---|---|
| `GET` | Read | **Yes** | **Yes** | No | Yes |
| `POST` | Create / trigger an action | No | **No** | Yes | Rarely |
| `PUT` | Replace a resource fully | No | **Yes** | Yes | No |
| `PATCH` | Partially modify | No | Not guaranteed | Yes | No |
| `DELETE` | Remove | No | **Yes** | Usually no | No |
| `HEAD` | Like GET, headers only | Yes | Yes | No | Yes |
| `OPTIONS` | Which methods are allowed (used by CORS preflight) | Yes | Yes | No | No |

- **Safe** = does not change server state.
- **Idempotent** = calling it many times has the **same effect as calling it once**.

**Idempotency analogy:** a **lift button**. Pressing it 5 times is the same as once (idempotent). A **vending machine "buy" button** dispenses another item each press (not idempotent, like `POST`).

```
PUT    /employees/5   (same body twice) -> employee 5 ends up identical either way
DELETE /employees/5   (twice)           -> gone after the first; the second finds nothing, state is the same
POST   /employees     (twice)           -> TWO employees created
```

### POST vs PUT vs PATCH

| | POST | PUT | PATCH |
|---|---|---|---|
| Typical use | **Create** (server assigns the ID) | **Replace** a resource at a known URL | **Change a few fields** |
| URL | Collection: `/employees` | Resource: `/employees/5` | Resource: `/employees/5` |
| Body | Full new resource | **Full** resource (missing fields reset or null) | **Only the fields to change** |
| Idempotent | No | Yes | Not necessarily |

Rules of thumb:

- Never use `GET` to change data (links, crawlers and prefetching can trigger it).
- Don't tunnel everything through `POST`.
- Don't use `PUT` for partial updates.

---

## 4. Status Codes

The **status code** tells the client what happened.

**Analogy: parcel tracking.**
- **2xx**: delivered successfully.
- **3xx**: redirected to a new address.
- **4xx**: the **sender's** mistake (wrong address, no permission).
- **5xx**: the **courier's** problem (vehicle broke down).

### The families

| Range | Meaning |
|---|---|
| **1xx** | Informational (rare) |
| **2xx** | Success |
| **3xx** | Redirection or caching (`304 Not Modified`) |
| **4xx** | **Client error** |
| **5xx** | **Server error** |

### The important ones

| Code | Name | When |
|---|---|---|
| **200** | OK | Successful GET, PUT or PATCH with a body |
| **201** | Created | Successful POST that created a resource (include a `Location` header) |
| **202** | Accepted | Accepted for asynchronous processing |
| **204** | No Content | Success with no body (typical for DELETE) |
| **304** | Not Modified | Cached copy is still valid |
| **400** | Bad Request | Malformed request or **validation failure** |
| **401** | Unauthorized | **Not authenticated** (no or invalid credentials) |
| **403** | Forbidden | Authenticated, but **not allowed** |
| **404** | Not Found | Resource does not exist |
| **405** | Method Not Allowed | Wrong HTTP method for this URL |
| **406** | Not Acceptable | Cannot produce the format requested in `Accept` |
| **409** | Conflict | Duplicate or a state conflict (email already exists) |
| **415** | Unsupported Media Type | Wrong `Content-Type` (for example XML sent to a JSON-only API) |
| **422** | Unprocessable Entity | Well-formed but semantically invalid (some teams use this instead of 400 for validation) |
| **429** | Too Many Requests | Rate limit exceeded |
| **500** | Internal Server Error | Unexpected server failure |
| **502 / 503 / 504** | Bad Gateway / Service Unavailable / Gateway Timeout | Downstream or overload problems |

### Typical outcomes by operation

| Operation | Success | Common errors |
|---|---|---|
| `GET /employees` | 200 (empty list is still **200** with `[]`) | |
| `GET /employees/{id}` | 200 | 404 |
| `POST /employees` | **201** + `Location` | 400 (invalid), 409 (duplicate) |
| `PUT /employees/{id}` | 200 (or 204) | 400, 404 |
| `PATCH /employees/{id}` | 200 | 400, 404 |
| `DELETE /employees/{id}` | **204** | 404 |

### 401 vs 403

- **401**: "I don't know who you are." Log in.
- **403**: "I know who you are, and you may not do this."

**Never return `200 OK` with an error message in the body.** Clients and tools rely on the status code.

---

## 5. Request and Response Anatomy

### Request

```
POST /api/v1/employees HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOi...

{
  "name": "Asha",
  "email": "asha@example.com",
  "department": "IT",
  "salary": 85000
}
```

### Response

```
HTTP/1.1 201 Created
Content-Type: application/json
Location: http://localhost:8080/api/v1/employees/1

{
  "id": 1,
  "name": "Asha",
  "email": "asha@example.com",
  "department": "IT",
  "salary": 85000.0
}
```

### Important headers

| Header | Direction | Purpose |
|---|---|---|
| `Content-Type` | Both | Format of the **body being sent** (`application/json`) |
| `Accept` | Request | Format the client **wants back** |
| `Authorization` | Request | Credentials or token (`Bearer <jwt>`) |
| `Location` | Response | URL of a newly created resource |
| `Cache-Control`, `ETag` | Response | Caching rules and versioning |
| `If-None-Match` | Request | Conditional GET (`304` if unchanged) |
| `Origin` / `Access-Control-Allow-Origin` | Request / Response | CORS (section 13) |

### Content negotiation

The client says what it can send (`Content-Type`) and wants back (`Accept`). The server answers with `406` if it cannot produce it and `415` if it cannot read what was sent.

```java
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
             produces = MediaType.APPLICATION_JSON_VALUE)
```

### JSON and Jackson

Spring Boot uses **Jackson** to convert between Java objects and JSON automatically.

```java
public record EmployeeResponse(Long id, String name, double salary) { }
// serialized to: {"id":1,"name":"Asha","salary":85000.0}
```

| Jackson annotation | Purpose |
|---|---|
| `@JsonProperty("full_name")` | Rename a field in JSON |
| `@JsonIgnore` | Omit a field |
| `@JsonInclude(JsonInclude.Include.NON_NULL)` | Omit null fields |
| `@JsonFormat(pattern = "yyyy-MM-dd")` | Custom date format |

Java `LocalDate`/`LocalDateTime` serialize as ISO strings (`"2026-10-12"`) by default in Spring Boot. Records work for both request and response bodies.

---

## 6. Building REST Controllers in Spring Boot

### Setup

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### `@RestController`

```java
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController { ... }
```

`@RestController` = `@Controller` + `@ResponseBody`: every method's return value is **written directly to the response body** as JSON (not interpreted as a view name).

### Mapping annotations

| Annotation | HTTP method |
|---|---|
| `@GetMapping` | GET |
| `@PostMapping` | POST |
| `@PutMapping` | PUT |
| `@PatchMapping` | PATCH |
| `@DeleteMapping` | DELETE |
| `@RequestMapping` | Any (class-level base path, or with `method = ...`) |

### Getting data from the request

| Annotation | Reads | Example URL / request |
|---|---|---|
| `@PathVariable` | A segment of the path | `/employees/5` |
| `@RequestParam` | Query string or form field | `/employees?dept=IT` |
| `@RequestBody` | The JSON body, converted to an object | POST/PUT/PATCH body |
| `@RequestHeader` | A header | `Authorization` |
| `@CookieValue` | A cookie | |

```java
@GetMapping("/{id}")
public EmployeeResponse get(@PathVariable Long id) { ... }

@GetMapping
public List<EmployeeResponse> search(
        @RequestParam(required = false) String dept,
        @RequestParam(defaultValue = "0") int page) { ... }

@PostMapping
public EmployeeResponse create(@RequestBody EmployeeRequest request) { ... }

@GetMapping("/me")
public String me(@RequestHeader("Authorization") String auth) { ... }
```

- `@RequestParam` is **required by default**; use `required = false` or `defaultValue`.
- If the variable name differs from the path placeholder: `@PathVariable("id") Long employeeId`.
- A path variable of the wrong type (`/employees/abc` for a `Long`) gives **400**.

### Controlling the response

**Option 1: return an object** (status `200`):

```java
@GetMapping("/{id}")
public EmployeeResponse get(@PathVariable Long id) {
    return service.get(id);
}
```

**Option 2: `ResponseEntity`** for full control over status, headers and body:

```java
return ResponseEntity.ok(body);                                          // 200
return ResponseEntity.status(HttpStatus.CREATED).body(body);             // 201
return ResponseEntity.created(location).body(body);                      // 201 + Location header
return ResponseEntity.noContent().build();                               // 204
return ResponseEntity.notFound().build();                                // 404
return ResponseEntity.badRequest().body(error);                          // 400
return ResponseEntity.ok().header("X-Total-Count", "42").body(list);     // custom header
```

**Option 3: `@ResponseStatus`** for a fixed status:

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) { service.delete(id); }
```

**The Location header on create:**

```java
URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(created.id())
        .toUri();
return ResponseEntity.created(location).body(created);
```

### What if a controller returns `null`?

Spring responds **`200 OK` with an empty body**. Return a proper `404` instead (throw an exception).

### `@RestController` vs `@Controller`

| | `@Controller` | `@RestController` |
|---|---|---|
| Returns | A **view name** (HTML page) | **Data** (JSON) |
| Used for | Server-rendered pages (Thymeleaf) | REST APIs |

---

## 7. DTOs and Layered Architecture

### The layers

```
Client -> Controller -> Service -> Repository -> Database
          (HTTP,       (business    (data
           DTOs,        logic,       access,
           status)      transactions) entities)
```

| Layer | Responsibility | Does not do |
|---|---|---|
| **Controller** | Map HTTP to Java calls; validate input; choose status codes | Business logic |
| **Service** | Business rules, transactions, mapping between entity and DTO | Know about HTTP |
| **Repository** | Database access | Business rules |

Keep controllers **thin**.

### What is a DTO?

A **DTO (Data Transfer Object)** is a simple class or record describing the data shape of an **API request or response**, separate from your database entity.

**Analogy:** a restaurant's **menu** vs its **kitchen inventory**. Customers see menu items (DTOs), not raw stock with costs, suppliers and internal codes (entities).

### Why not expose entities directly

| Problem | Explanation |
|---|---|
| **Security** | Leaks sensitive fields (password hash, internal flags) |
| **Over-posting** | A client could set fields it should not (`role`, `id`, `salary`) |
| **Coupling** | Changing the database changes the public API |
| **Lazy-loading errors** | Serializing an entity can trigger `LazyInitializationException` |
| **Infinite recursion** | Bidirectional relationships loop in JSON |
| **Different shapes** | Create, update and read need different fields |

### Request and response DTOs

```java
// what the client SENDS
public record EmployeeRequest(String name, String email, String department, double salary) { }

// what the client RECEIVES
public record EmployeeResponse(Long id, String name, String email, String department, double salary) {

    public static EmployeeResponse from(Employee e) {
        return new EmployeeResponse(e.getId(), e.getName(), e.getEmail(),
                                    e.getDepartment(), e.getSalary());
    }
}
```

Note the request has **no `id`** (the server assigns it) and the response could omit sensitive fields. For large projects, **MapStruct** generates mapping code; for small ones, a static `from()` method is fine.

---

## 8. Validation

### Why validate

**Never trust client input.** Validation at the API boundary protects your data and returns clear errors.

**Analogy:** the **security check at the entrance**. Bad input is stopped at the door, not discovered halfway inside.

### Bean Validation annotations (`jakarta.validation.constraints`)

| Annotation | Checks |
|---|---|
| `@NotNull` | Not `null` |
| `@NotEmpty` | Not null and not empty (strings, collections) |
| `@NotBlank` | Not null and contains a non-whitespace character (strings) |
| `@Size(min, max)` | Length or size range |
| `@Min`, `@Max` | Numeric bounds |
| `@Positive`, `@PositiveOrZero`, `@Negative` | Sign checks |
| `@DecimalMin`, `@DecimalMax`, `@Digits` | Decimal bounds and precision |
| `@Email` | Looks like an email |
| `@Pattern(regexp)` | Matches a regex |
| `@Past`, `@Future`, `@PastOrPresent` | Date checks |
| `@AssertTrue`, `@AssertFalse` | Boolean conditions |

### Apply to a DTO and trigger with `@Valid`

```java
public record EmployeeRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Department is required")
        String department,

        @Positive(message = "Salary must be positive")
        double salary) { }
```

```java
@PostMapping
public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) { ... }
```

**Without `@Valid`, no validation happens.** With it, a failure throws `MethodArgumentNotValidException`, which your global handler turns into a **400** (section 9).

### Validating path and query parameters

```java
@RestController
@Validated                                           // enables parameter validation
public class EmployeeController {

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable @Positive Long id) { ... }

    @GetMapping
    public List<EmployeeResponse> list(@RequestParam @Min(0) int page) { ... }
}
```

Violations raise `ConstraintViolationException` (or `HandlerMethodValidationException` in newer Spring versions). Handle both as **400**.

### Validation vs business rules

| Bean Validation (format) | Service-layer rules |
|---|---|
| "Email looks valid", "salary > 0" | "Email already registered" (**409**), "cannot delete a department that still has employees" |

### Custom validator (when built-ins are not enough)

```java
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = IndianMobileValidator.class)
public @interface IndianMobile {
    String message() default "Invalid mobile number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class IndianMobileValidator implements ConstraintValidator<IndianMobile, String> {
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        return value == null || value.matches("[6-9]\\d{9}");
    }
}
```

---

## 9. Error Handling

Consistent, informative errors are part of a good API. (The mechanics are in the Exception Handling notes; this is the REST view.)

### The error contract

Always return the **same JSON shape** for errors:

```java
public record ApiError(int status, String error, String message,
                       Map<String, String> fieldErrors, Instant timestamp) { }
```

### Global handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)                 // 404
    public ResponseEntity<ApiError> notFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(DuplicateResourceException.class)                // 409
    public ResponseEntity<ApiError> conflict(DuplicateResourceException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)           // 400: @Valid failures
    public ResponseEntity<ApiError> invalid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)           // 400: malformed JSON
    public ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)       // 400: /employees/abc
    public ResponseEntity<ApiError> typeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value for '" + ex.getName() + "'", null);
    }

    @ExceptionHandler(ConstraintViolationException.class)              // 400: @Validated params
    public ResponseEntity<ApiError> constraint(ConstraintViolationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)                                 // 500: last resort
    public ResponseEntity<ApiError> fallback(Exception ex) {
        // log the real exception here; never leak internals
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, Map<String, String> fields) {
        ApiError body = new ApiError(status.value(), status.getReasonPhrase(), message, fields, Instant.now());
        return ResponseEntity.status(status).body(body);
    }
}
```

### What the client sees

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": {
    "email": "Email must be valid",
    "salary": "Salary must be positive"
  },
  "timestamp": "2026-10-12T12:45:10Z"
}
```

### Modern alternative: `ProblemDetail` (RFC 7807 / 9457)

Spring 6 supports the standard "problem details" format (`application/problem+json`):

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ProblemDetail notFound(ResourceNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setTitle("Resource not found");
    return pd;
}
```

Enable it for Spring's own exceptions with `spring.mvc.problemdetails.enabled=true`. Choose **one** error format for the whole API.

### Rules

- Services **throw** exceptions; the controller layer has **no try/catch**.
- Never return stack traces or SQL errors to clients.
- Log the full exception server-side once.
- Use the right status: `400` input, `404` missing, `409` conflict, `500` unexpected.

---

## 10. Pagination, Sorting, Filtering

### Why paginate

Returning every row of a large table is slow, memory-hungry and abusable.

**Analogy:** you don't read the whole library at once; you read **page by page** (search-engine results pages).

### Request

```
GET /api/v1/employees?dept=IT&page=0&size=5&sort=salary,desc
```

| Parameter | Meaning |
|---|---|
| `page` | Zero-based page number |
| `size` | Items per page |
| `sort` | `field,direction` (repeatable) |
| `dept` | Your own filter |

### Controller and repository

```java
@GetMapping
public PageResponse<EmployeeResponse> list(
        @RequestParam(required = false) String dept,
        @PageableDefault(size = 10, sort = "name") Pageable pageable) {
    return PageResponse.from(service.list(dept, pageable));
}
```

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findByDepartment(String department, Pageable pageable);
    boolean existsByEmail(String email);
}
```

Spring converts the query parameters into a `Pageable` automatically.

### A stable page envelope

Wrap Spring's `Page` in **your own** response type so the JSON contract does not depend on Spring internals (recent Spring Data versions warn against serializing `PageImpl` directly):

```java
public record PageResponse<T>(List<T> content, int page, int size,
                              long totalElements, int totalPages) {

    public static <T> PageResponse<T> from(Page<T> p) {
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(),
                                  p.getTotalElements(), p.getTotalPages());
    }
}
```

```json
{
  "content": [ { "id": 1, "name": "Asha" } ],
  "page": 0,
  "size": 5,
  "totalElements": 12,
  "totalPages": 3
}
```

### Filtering

- Simple: optional `@RequestParam` filters mapped to derived queries.
- Many optional filters: use JPA **Specifications** or Querydsl.
- Whitelist sortable fields; don't let clients sort by arbitrary columns.

---

## 11. Complete CRUD Example

An **Employee** API combining everything above. (Use H2 or MySQL as in the JPA notes.)

### Entity

```java
@Entity
@Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String department;
    private double salary;

    protected Employee() { }

    public Employee(String name, String email, String department, double salary) {
        this.name = name; this.email = email; this.department = department; this.salary = salary;
    }
    // getters and setters
}
```

### Exceptions

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) { super(message); }
}
```

### Service

```java
@Service
public class EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> list(String dept, Pageable pageable) {
        Page<Employee> page = (dept == null) ? repo.findAll(pageable)
                                             : repo.findByDepartment(dept, pageable);
        return page.map(EmployeeResponse::from);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(Long id) {
        return EmployeeResponse.from(find(id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest req) {
        if (repo.existsByEmail(req.email())) {
            throw new DuplicateResourceException("Email already in use: " + req.email());
        }
        Employee saved = repo.save(new Employee(req.name(), req.email(), req.department(), req.salary()));
        return EmployeeResponse.from(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest req) {
        Employee e = find(id);
        e.setName(req.name());
        e.setEmail(req.email());
        e.setDepartment(req.department());
        e.setSalary(req.salary());                 // saved automatically (dirty checking)
        return EmployeeResponse.from(e);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Employee " + id + " not found");
        }
        repo.deleteById(id);
    }

    private Employee find(Long id) {
        return repo.findById(id)
                   .orElseThrow(() -> new ResourceNotFoundException("Employee " + id + " not found"));
    }
}
```

### Controller

```java
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) { this.service = service; }

    @GetMapping
    public PageResponse<EmployeeResponse> list(
            @RequestParam(required = false) String dept,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return PageResponse.from(service.list(dept, pageable));
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
```

DTOs (`EmployeeRequest` with validation, `EmployeeResponse`, `PageResponse`) and `GlobalExceptionHandler` are as shown in sections 7 to 10.

### Try it with `curl`

```bash
# create -> 201 Created + Location header
curl -i -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Asha","email":"asha@example.com","department":"IT","salary":85000}'

# read one -> 200
curl http://localhost:8080/api/v1/employees/1

# list with paging and a filter -> 200
curl "http://localhost:8080/api/v1/employees?dept=IT&page=0&size=5&sort=salary,desc"

# update -> 200
curl -X PUT http://localhost:8080/api/v1/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Asha K","email":"asha@example.com","department":"IT","salary":90000}'

# delete -> 204 No Content
curl -i -X DELETE http://localhost:8080/api/v1/employees/1

# missing -> 404 with the ApiError body
curl -i http://localhost:8080/api/v1/employees/999
```

### Request flow for `POST /api/v1/employees`

```
JSON body
  -> Jackson converts it to EmployeeRequest
  -> @Valid runs Bean Validation (400 if invalid)
  -> Controller calls service.create()
  -> Service checks the duplicate email (409), saves via the repository
  -> Entity -> EmployeeResponse
  -> 201 Created + Location + JSON
```

### Partial update with `PATCH`

```java
public record EmployeePatch(String name, String department, Double salary) { }   // all optional

@PatchMapping("/{id}")
public EmployeeResponse patch(@PathVariable Long id, @RequestBody EmployeePatch patch) {
    return service.patch(id, patch);
}
```

```java
@Transactional
public EmployeeResponse patch(Long id, EmployeePatch p) {
    Employee e = find(id);
    if (p.name() != null)       e.setName(p.name());
    if (p.department() != null) e.setDepartment(p.department());
    if (p.salary() != null)     e.setSalary(p.salary());
    return EmployeeResponse.from(e);
}
```

This simple approach cannot tell "field omitted" from "set to `null`". For strict semantics, use **JSON Merge Patch** (RFC 7396) or **JSON Patch** (RFC 6902).

---

## 12. Calling REST APIs

### Postman, curl and the browser

Use **Postman** or **Insomnia** to explore an API (set method, URL, headers, body). `curl` works in the terminal. The browser address bar can only issue `GET`.

### From React (`fetch`)

```javascript
// GET
const res = await fetch("http://localhost:8080/api/v1/employees?page=0&size=10");
if (!res.ok) throw new Error(`HTTP ${res.status}`);
const page = await res.json();

// POST
const response = await fetch("http://localhost:8080/api/v1/employees", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ name: "Asha", email: "asha@example.com", department: "IT", salary: 85000 }),
});

if (!response.ok) {                          // fetch does NOT throw on 4xx/5xx
  const error = await response.json();       // the ApiError body
  throw new Error(error.message);
}
const created = await response.json();
```

> **`fetch` only rejects on network failure.** A `404` or `500` still resolves, so always check `response.ok`. Axios throws on non-2xx by default.

### From Spring: `RestClient` (Spring 6.1+)

```java
RestClient client = RestClient.create("http://localhost:8080");

EmployeeResponse e = client.get()
        .uri("/api/v1/employees/{id}", 1)
        .retrieve()
        .body(EmployeeResponse.class);

EmployeeResponse created = client.post()
        .uri("/api/v1/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new EmployeeRequest("Asha", "asha@example.com", "IT", 85000))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
            throw new IllegalArgumentException("Rejected: " + resp.getStatusCode());
        })
        .body(EmployeeResponse.class);
```

| Client | Notes |
|---|---|
| `RestClient` | Modern, synchronous, fluent (**preferred** for new code) |
| `WebClient` | Reactive, non-blocking |
| `RestTemplate` | Older, in maintenance mode |
| **OpenFeign** | Declarative interface; popular between microservices (see Microservices notes) |

---

## 13. CORS

### The problem

Browsers enforce the **same-origin policy**: JavaScript on `http://localhost:3000` (React) cannot call `http://localhost:8080` (Spring) unless the **server permits it**. Without CORS configuration the browser blocks the response:

```
Access to fetch at 'http://localhost:8080/...' from origin 'http://localhost:3000'
has been blocked by CORS policy
```

**Origin** = scheme + host + port. Different port means different origin.

**Analogy:** a building's **security desk** that admits visitors only from approved companies. The browser asks the server, "does this origin have permission?"

CORS is enforced by the **browser**. `curl` and Postman are unaffected, which is why an API can work in Postman and still fail from React.

### Preflight

For non-simple requests (JSON `POST`, `PUT`, `DELETE`, custom headers like `Authorization`), the browser first sends an **`OPTIONS` preflight** request asking which origins, methods and headers are allowed. If the server answers correctly, the real request follows.

### Configuration

**Per controller:**

```java
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class EmployeeController { ... }
```

**Globally (recommended):**

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
```

Notes:

- List the **specific origins** you trust. `allowedOrigins("*")` allows everyone and **cannot be combined with credentials** (cookies).
- With **Spring Security**, configure CORS in the security filter chain too, or preflight requests get rejected before reaching MVC.
- Alternative for development: a **proxy** in the React dev server, so the browser sees one origin.

---

## 14. API Documentation with OpenAPI

An API needs a **menu**. **OpenAPI** (formerly Swagger) describes endpoints in a machine-readable format and generates an interactive UI.

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version><!-- latest 2.x --></version>
</dependency>
```

| URL | Content |
|---|---|
| `/swagger-ui/index.html` | Interactive UI to try each endpoint |
| `/v3/api-docs` | The OpenAPI JSON |

```java
@Tag(name = "Employees", description = "Employee management")
@RestController
public class EmployeeController {

    @Operation(summary = "Get an employee by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Long id) { ... }
}
```

Frontend developers can generate typed clients from the OpenAPI document.

---

## 15. Testing REST APIs

### Slice test with `MockMvc` (fast, no real server)

```java
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean EmployeeService service;     // @MockBean before Spring Boot 3.4

    @Test
    void getReturnsEmployee() throws Exception {
        when(service.get(1L)).thenReturn(
            new EmployeeResponse(1L, "Asha", "asha@example.com", "IT", 85000));

        mockMvc.perform(get("/api/v1/employees/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Asha"));
    }

    @Test
    void getMissingReturns404() throws Exception {
        when(service.get(99L)).thenThrow(new ResourceNotFoundException("Employee 99 not found"));

        mockMvc.perform(get("/api/v1/employees/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value("Employee 99 not found"));
    }

    @Test
    void createRejectsInvalidBody() throws Exception {
        String body = """
            {"name": "", "email": "not-an-email", "department": "IT", "salary": -5}
            """;

        mockMvc.perform(post("/api/v1/employees")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(body))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.fieldErrors.email").exists());
    }
}
```

(Static imports: `MockMvcRequestBuilders.*`, `MockMvcResultMatchers.*`, `Mockito.when`.)

`@WebMvcTest` loads **only the web layer**, including your `@RestControllerAdvice`, so error handling is tested too.

### Other levels

| Level | Tool | Tests |
|---|---|---|
| Unit | JUnit and Mockito | Service logic |
| Web slice | `@WebMvcTest` + `MockMvc` | Controllers, validation, error mapping |
| Integration | `@SpringBootTest` with `TestRestTemplate` or `MockMvc` against a test database | The full stack |
| Manual | Postman / curl | Exploration |

**What to test per endpoint:** the happy path, validation failure (400), not found (404), duplicates (409), and authorization rules.

---

## 16. Versioning, Idempotency, Caching, Security Basics

### Versioning

Change an API **without breaking existing clients**.

| Strategy | Example |
|---|---|
| **URI path** (most common) | `/api/v1/employees` |
| Header | `X-API-Version: 1` |
| Media type | `Accept: application/vnd.company.v1+json` |
| Query parameter | `/employees?version=1` |

Rules: **adding** optional fields is non-breaking; **removing or renaming** fields, changing types, or changing meanings is breaking and needs a new version. Announce deprecations.

### Idempotency for safe retries

Networks fail and clients retry. For non-idempotent operations such as a payment `POST`, accept an **idempotency key**:

```
POST /payments
Idempotency-Key: 7c9e6679-7425-40de-944b-e07fc1f90ae7
```

The server stores processed keys and returns the **original result** for repeats, so a retry does not charge twice.

### Caching

- `GET` responses can carry `Cache-Control: max-age=60`.
- **ETag** plus `If-None-Match` lets the server answer **`304 Not Modified`** with no body when the data has not changed.

### HATEOAS (brief)

Responses include **links** to related actions so clients discover the API dynamically:

```json
{ "id": 5, "name": "Asha",
  "_links": { "self": "/employees/5", "department": "/departments/3" } }
```

Spring HATEOAS supports it. It is Richardson level 3 and rarely used in everyday projects.

### Security basics (details in the Spring Security notes)

| Concern | Practice |
|---|---|
| Transport | **HTTPS** everywhere |
| Authentication | Token-based (**JWT** or OAuth2), sent as `Authorization: Bearer <token>` |
| Authorization | Role and ownership checks per endpoint |
| Input | Validate everything; use DTOs to prevent over-posting |
| Errors | Never leak stack traces, SQL or internals |
| Abuse | Rate limiting (`429`), request size limits |
| Secrets | Not in code or Git |
| CORS | Specific origins only |
| IDs | Check that the user may access **that** resource (not just that they are logged in) |

Because REST is **stateless**, there is no server-side session; the token travels with every request.

---

## 17. Best Practices Checklist

- Use **nouns, plural, lowercase** URLs; version them (`/api/v1/...`).
- Use the **right HTTP method** and the **right status code** (201 + `Location`, 204 for delete, 404, 409, 400).
- Return **DTOs**, never entities; separate request and response types.
- **Validate** input with `@Valid`; keep business rules in the service.
- Keep controllers **thin**; transactions in the **service** layer.
- Handle errors in **one global handler** with **one consistent error format**.
- **Paginate** collections; limit page size; whitelist sort fields.
- Make `PUT` and `DELETE` **idempotent**; support idempotency keys for payments.
- Configure **CORS** with specific origins.
- **Document** with OpenAPI; **test** controllers with MockMvc.
- Use **HTTPS**, authentication and authorization.
- Return **`[]` not `null`**, and an empty list is **200**, not 404.
- **Log** with correlation IDs; don't log secrets.
- Don't break clients: add, don't remove or rename.

---

## 18. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Returning JPA entities from controllers | Leaks fields, lazy-loading errors, recursion, tight coupling | DTOs |
| Verbs in URLs (`/getEmployee`, `/deleteUser`) | Not RESTful | Nouns plus HTTP methods |
| `200 OK` with an error message in the body | Clients can't detect failure | Proper 4xx/5xx |
| Using `POST` for everything | Loses semantics, caching, idempotency | Correct methods |
| `GET` that modifies data | Prefetching or crawlers can trigger changes | `POST`, `PUT` or `DELETE` |
| `PUT` for partial updates | Missing fields get wiped | `PATCH` |
| Missing `@Valid` on `@RequestBody` | No validation runs | Add `@Valid` |
| `@RequestBody` forgotten | Fields stay `null` | Add it on JSON bodies |
| Business logic in controllers | Untestable, duplicated | Move to the service |
| try/catch in every controller method | Inconsistent errors | Global `@RestControllerAdvice` |
| Returning all rows | Slow, memory risk | Pagination |
| Different error formats per endpoint | Hard for clients | One `ApiError` or `ProblemDetail` |
| Exposing stack traces | Security risk | Generic message plus server-side log |
| 404 for an empty list | Wrong semantics | `200` with `[]` |
| Returning `null` from a controller | `200` with an empty body | Throw `ResourceNotFoundException` |
| No CORS config for React | Browser blocks calls | `addCorsMappings` |
| `allowedOrigins("*")` with credentials | Not allowed, and unsafe | List specific origins |
| Assuming `fetch` rejects on 404 | It resolves normally | Check `response.ok` |
| Bidirectional entity JSON | Infinite recursion | DTOs |
| Breaking changes in the same version | Clients crash | New version |
| Path variable name mismatch (`{id}` vs `@PathVariable Long userId`) | Binding error | Match names or use `@PathVariable("id")` |
| Trusting client-sent `id` or `role` | Over-posting / privilege escalation | Exclude from request DTOs |
| Non-idempotent `PUT` | Retries cause damage | Make it a full replace |
| Hard-coded secrets and URLs | Breaks across environments, leaks secrets | Externalized config |

---

## 19. Which Status Code? Quiz

| # | Scenario | Answer |
|---|---|---|
| 1 | `GET /employees/42` and no such employee | **404** |
| 2 | `POST /employees` succeeds | **201** with `Location` |
| 3 | `POST /employees` with an invalid email format | **400** (or 422) |
| 4 | `POST /employees` with an email that already exists | **409** |
| 5 | `DELETE /employees/5` succeeds | **204** |
| 6 | Request without an auth token | **401** |
| 7 | Valid token, but the user is not an admin | **403** |
| 8 | Unexpected `NullPointerException` on the server | **500** |
| 9 | `PUT` sent to an endpoint that only supports `GET` | **405** |
| 10 | XML body sent to a JSON-only endpoint | **415** |
| 11 | Client exceeded the rate limit | **429** |
| 12 | `GET /employees?dept=Legal` finds nobody | **200** with `[]` |
| 13 | The gateway's downstream service is down | **502** or **503** |
| 14 | `PUT` updated the resource and returns it | **200** |
| 15 | Request accepted; processing happens later | **202** |
| 16 | `/employees/abc` where the id must be a number | **400** |
| 17 | Malformed JSON body | **400** |
| 18 | Client asks for `Accept: application/xml` and you only serve JSON | **406** |

---

## 20. Interview Questions

**1. What is a REST API?** An API built around resources identified by URLs and manipulated with standard HTTP methods, usually exchanging JSON, following REST constraints.

**2. What are the REST constraints?** Client-server, stateless, cacheable, uniform interface, layered system, and optional code on demand.

**3. What does stateless mean?** The server stores no client session between requests; each request contains all information needed (such as a token).

**4. REST vs SOAP vs GraphQL?** REST: resources over HTTP, flexible. SOAP: strict XML contracts. GraphQL: one endpoint, the client selects fields.

**5. What are the main HTTP methods and their CRUD mapping?** GET reads, POST creates, PUT replaces, PATCH partially updates, DELETE removes.

**6. What are safe and idempotent methods?** Safe: no state change (GET, HEAD, OPTIONS). Idempotent: repeating has the same effect (GET, PUT, DELETE).

**7. Is POST idempotent?** No; repeating it creates duplicates.

**8. PUT vs PATCH?** PUT replaces the entire resource; PATCH modifies only some fields.

**9. PUT vs POST?** POST creates under a collection (server picks the ID); PUT creates or replaces at a known URL and is idempotent.

**10. How do you design REST URLs?** Plural nouns, lowercase with hyphens, hierarchy for relations, query parameters for filter/sort/page, versioning.

**11. Path variable vs query parameter?** Path identifies a resource; query filters, sorts or paginates.

**12. Which status code for create, delete, not found, validation error, conflict?** 201, 204, 404, 400, 409.

**13. 401 vs 403?** 401: not authenticated. 403: authenticated but not allowed.

**14. `@RestController` vs `@Controller`?** `@RestController` returns data (JSON) via `@ResponseBody`; `@Controller` returns view names.

**15. `@PathVariable` vs `@RequestParam` vs `@RequestBody`?** Path segment; query string or form field; the request body converted from JSON.

**16. What is `ResponseEntity`?** A wrapper to control status code, headers and body of the response.

**17. What is a DTO and why use it?** A dedicated object for API data; it hides entity internals, prevents over-posting and decouples the API from the database.

**18. How do you validate a request in Spring Boot?** Bean Validation annotations on the DTO, `@Valid` on the `@RequestBody`, and a handler for `MethodArgumentNotValidException`.

**19. `@Valid` vs `@Validated`?** `@Valid` triggers validation of an object (and nested ones); `@Validated` (Spring) enables validation groups and parameter-level validation on a class.

**20. `@NotNull` vs `@NotEmpty` vs `@NotBlank`?** Not null; not null and not empty; not null and contains a non-whitespace character.

**21. How do you handle exceptions globally?** `@RestControllerAdvice` with `@ExceptionHandler` methods returning a consistent error body.

**22. How do you return a 404?** Throw a custom exception handled by the global handler (or `ResponseStatusException`).

**23. What does a controller returning `null` produce?** `200 OK` with an empty body.

**24. How do you implement pagination?** `Pageable` parameter, `Page` from the repository, wrapped in a stable response DTO.

**25. What is CORS and why does it occur?** A browser security mechanism restricting cross-origin calls; the server must allow origins via CORS configuration.

**26. What is a preflight request?** An `OPTIONS` request the browser sends to check permissions before certain cross-origin calls.

**27. How do you version an API?** URI path (`/v1`), header, or media type; avoid breaking changes within a version.

**28. What is HATEOAS?** Including links in responses so clients can navigate the API.

**29. How do you secure a REST API?** HTTPS, token-based authentication (JWT/OAuth2), authorization checks, input validation, rate limiting, safe error responses.

**30. How do you make a POST safe to retry?** An idempotency key stored server-side.

**31. How do you test a controller?** `@WebMvcTest` with `MockMvc` and a mocked service; assert status codes and JSON.

**32. How do you document an API?** OpenAPI with springdoc, producing Swagger UI.

**33. What is the Richardson Maturity Model?** Levels 0 to 3 measuring REST adoption: single endpoint, resources, HTTP verbs and status codes, then hypermedia.

**34. What are `Content-Type` and `Accept`?** What the body is, and what the client wants back.

**35. Why does `fetch` not throw on 404?** It rejects only on network failure; check `response.ok`.

**36. Why must `@Transactional` be on the service layer?** It defines the unit of work spanning several repository calls, independent of HTTP.

---

## 21. Cheat Sheet

| Operation | Method | URL | Success | Typical errors |
|---|---|---|---|---|
| List | GET | `/api/v1/employees?page=0&size=10` | 200 | 400 |
| Read one | GET | `/api/v1/employees/{id}` | 200 | 404 |
| Create | POST | `/api/v1/employees` | **201** + `Location` | 400, 409 |
| Replace | PUT | `/api/v1/employees/{id}` | 200 | 400, 404 |
| Partial update | PATCH | `/api/v1/employees/{id}` | 200 | 400, 404 |
| Delete | DELETE | `/api/v1/employees/{id}` | **204** | 404 |

| Spring piece | Purpose |
|---|---|
| `@RestController` + `@RequestMapping` | Define the API class and base path |
| `@GetMapping`, `@PostMapping`, ... | Map methods |
| `@PathVariable`, `@RequestParam`, `@RequestBody`, `@RequestHeader` | Read input |
| `@Valid` + Bean Validation annotations | Validate the request |
| `ResponseEntity`, `@ResponseStatus` | Control the response |
| `@RestControllerAdvice` + `@ExceptionHandler` | Global error handling |
| `Pageable` / `Page` | Pagination and sorting |
| `@CrossOrigin` / `CorsRegistry` | CORS |
| springdoc | Swagger UI |
| `@WebMvcTest` + `MockMvc` | Controller tests |

**Golden rules**

```
Nouns in URLs, verbs in HTTP methods.   Right status code every time.
DTOs in, DTOs out; never entities.      Validate at the door (@Valid).
One global error handler, one error shape.   Paginate every list.
Stateless: the token travels with each request.   Don't break clients: version.
```

---

## 22. Practice Tasks with Solutions

**1. Build the Employee CRUD API** from section 11 with H2. Verify each `curl` command and status code.

**2. Add search by name (case-insensitive, partial match)**

```java
Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);
```

```java
@GetMapping("/search")
public PageResponse<EmployeeResponse> search(@RequestParam String name, Pageable pageable) {
    return PageResponse.from(service.search(name, pageable));
}
```

**3. Add a salary range filter** (`/employees?minSalary=50000&maxSalary=90000`)

```java
Page<Employee> findBySalaryBetween(double min, double max, Pageable pageable);
```

**4. Add `PATCH`** (section 11), then test updating only the salary.

**5. Return a nested resource**: `GET /departments/{id}/employees`

```java
@GetMapping("/departments/{deptId}/employees")
public List<EmployeeResponse> byDepartment(@PathVariable Long deptId) {
    return service.byDepartment(deptId);       // 404 if the department does not exist
}
```

**6. Write MockMvc tests** for: successful GET (200), missing resource (404), invalid POST (400), duplicate email (409).

```java
@Test
void duplicateEmailReturns409() throws Exception {
    when(service.create(any())).thenThrow(new DuplicateResourceException("Email already in use"));

    mockMvc.perform(post("/api/v1/employees")
               .contentType(MediaType.APPLICATION_JSON)
               .content("""
                   {"name":"Asha","email":"asha@example.com","department":"IT","salary":85000}
                   """))
           .andExpect(status().isConflict());
}
```

**7. Connect a React page**: fetch the employee list on load, show it in a table, and add a form that POSTs a new employee. Show validation errors from `fieldErrors` next to the inputs.

**8. Fix CORS** so the React app on `http://localhost:5173` can call your API (section 13).

**9. Add Swagger UI** and confirm all endpoints appear at `/swagger-ui/index.html`.

**10. Make a payment-style endpoint idempotent**: accept an `Idempotency-Key` header, store `key -> response` in a map (or table), and return the stored response if the same key arrives again.

```java
private final Map<String, PaymentResponse> processed = new ConcurrentHashMap<>();

@PostMapping("/payments")
public ResponseEntity<PaymentResponse> pay(@RequestHeader("Idempotency-Key") String key,
                                           @Valid @RequestBody PaymentRequest req) {
    PaymentResponse result = processed.computeIfAbsent(key, k -> service.charge(req));
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
}
```

(In production, store keys in a database with an expiry, and return the original status.)

### Challenge questions

1. A client calls `POST /employees` twice with the same body due to a retry. What happens in your API, and how can you prevent a duplicate?
2. A React app gets "blocked by CORS policy", but the same request works in Postman. Why?
3. Why is `GET /employees/5/delete` a design mistake even if it works?

**Answers**

1. Two employees are created (POST is not idempotent), unless a uniqueness rule (such as a unique email returning **409**) or an **idempotency key** stops the second one. The unique constraint is the right safeguard for data like email; idempotency keys suit actions like payments.
2. CORS is enforced by the **browser**, not the server. Postman is not a browser and ignores it. The server must send the right `Access-Control-Allow-*` headers for the React origin.
3. `GET` must be **safe**: crawlers, link prefetching and caches can call it without the user intending to delete anything. Deletion should use `DELETE /employees/5`, with the action in the HTTP method, not the URL.

---

*End of notes.*
