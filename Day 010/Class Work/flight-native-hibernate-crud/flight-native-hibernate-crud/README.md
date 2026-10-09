# Flight CRUD REST API - Pure Native Hibernate with Java Configuration

## Technology Stack

- Java 17
- Spring Boot 3.5.6
- Spring Web
- Native Hibernate ORM
- Hibernate SessionFactory
- Hibernate Session
- Hibernate Transaction
- HQL
- MySQL
- Jakarta Bean Validation
- Maven

## Important

This project intentionally does NOT use:

- `spring-boot-starter-data-jpa`
- `JpaRepository`
- `CrudRepository`
- `EntityManager`
- Spring Data JPA derived query methods
- Spring Data `@Query`
- `hibernate.cfg.xml`

Hibernate is configured completely using Java configuration.

## Configuration Approach

The complete Hibernate configuration is in:

    src/main/java/com/example/flightcrud/config/HibernateConfig.java

The application creates the native Hibernate SessionFactory manually:

    org.hibernate.cfg.Configuration configuration =
        new org.hibernate.cfg.Configuration();

Database and Hibernate properties are created in Java:

    Properties properties = new Properties();

    properties.put(Environment.DRIVER, ...);
    properties.put(Environment.URL, ...);
    properties.put(Environment.USER, ...);
    properties.put(Environment.PASS, ...);
    properties.put(Environment.DIALECT, ...);
    properties.put(Environment.HBM2DDL_AUTO, "update");

Then:

    configuration.setProperties(properties);

The entity is registered directly:

    configuration.addAnnotatedClass(Flight.class);

Finally:

    return configuration.buildSessionFactory();

There is NO `hibernate.cfg.xml` in this project.

## Why the Fully Qualified Configuration Class?

Spring also has an annotation called `Configuration`:

    org.springframework.context.annotation.Configuration

Hibernate has a class with the same simple name:

    org.hibernate.cfg.Configuration

Therefore this project uses:

    import org.springframework.context.annotation.Configuration;

and creates Hibernate Configuration using:

    org.hibernate.cfg.Configuration configuration =
        new org.hibernate.cfg.Configuration();

This avoids the common import collision.

## Project Architecture

REST Controller
       |
       v
FlightService
       |
       v
FlightDao
       |
       v
Hibernate SessionFactory
       |
       v
Session
       |
       v
Transaction
       |
       v
MySQL

## Project Structure

src/main/java/com/example/flightcrud
|
+-- FlightCrudApplication.java
|
+-- config
|   +-- HibernateConfig.java
|
+-- entity
|   +-- Flight.java
|
+-- dao
|   +-- FlightDao.java
|   +-- FlightDaoImpl.java
|
+-- service
|   +-- FlightService.java
|   +-- FlightServiceImpl.java
|
+-- controller
|   +-- FlightController.java
|
+-- exception
    +-- FlightNotFoundException.java
    +-- GlobalExceptionHandler.java

src/main/resources
|
+-- application.properties

## Database Configuration

Database configuration is now entirely in:

    HibernateConfig.java

Default values:

    Database URL:
    jdbc:mysql://localhost:3306/flightdb

    Username:
    root

    Password:
    root

If your MySQL credentials are different, change them in
`HibernateConfig.java`.

The following setting automatically creates the database if required:

    createDatabaseIfNotExist=true

Hibernate also uses:

    hibernate.hbm2ddl.auto = update

to create/update the `flights` table.

For production applications, prefer Flyway or Liquibase for schema management.

## Flight Entity

The Flight entity contains:

- flightId
- provider
- source
- destination
- price
- dateOfTravel

The ID uses:

    @GeneratedValue(strategy = GenerationType.IDENTITY)

Therefore MySQL generates the ID automatically.

## CRUD APIs

### CREATE

POST:

    http://localhost:8080/api/flights

JSON:

    {
        "provider": "IndiGo",
        "source": "Hyderabad",
        "destination": "Mumbai",
        "price": 5500.00,
        "dateOfTravel": "2026-12-20"
    }

Native Hibernate:

    session.persist(flight);

### READ ONE

GET:

    http://localhost:8080/api/flights/1

Native Hibernate:

    session.get(Flight.class, flightId);

### READ ALL

GET:

    http://localhost:8080/api/flights

HQL:

    FROM Flight f ORDER BY f.flightId

Native Hibernate:

    session.createQuery(hql, Flight.class)
           .getResultList();

### UPDATE

PUT:

    http://localhost:8080/api/flights/1

JSON:

    {
        "provider": "Air India",
        "source": "Hyderabad",
        "destination": "Delhi",
        "price": 6200.00,
        "dateOfTravel": "2026-12-25"
    }

The DAO loads the existing entity, changes its properties and commits
the transaction.

### DELETE

DELETE:

    http://localhost:8080/api/flights/1

Native Hibernate:

    session.remove(flight);

## HQL Searches

Search by source:

    GET /api/flights/search/source?source=Hyderabad

Search by route:

    GET /api/flights/search/route?source=Hyderabad&destination=Mumbai

Search by price:

    GET /api/flights/search/price?maxPrice=6000

Search by date range:

    GET /api/flights/search/date-range?startDate=2026-12-01&endDate=2026-12-31

Cheapest flight:

    GET /api/flights/search/cheapest

## Transaction Handling

INSERT, UPDATE and DELETE use:

    Transaction transaction = session.beginTransaction();

    // database operation

    transaction.commit();

If an exception occurs:

    transaction.rollback();

## Native Hibernate APIs Demonstrated

### SessionFactory

Creates Hibernate Sessions.

    sessionFactory.openSession();

### Session

Represents a Hibernate unit of work.

    Session session = sessionFactory.openSession();

### Transaction

Provides transaction boundaries.

    Transaction transaction =
        session.beginTransaction();

### persist()

Used to insert a new entity.

    session.persist(flight);

### get()

Used to retrieve by primary key.

    session.get(Flight.class, flightId);

### remove()

Used to delete an entity.

    session.remove(flight);

### createQuery()

Used to execute HQL.

    session.createQuery(
        "FROM Flight",
        Flight.class
    );

## HQL vs SQL

HQL uses entity and Java property names.

Correct:

    FROM Flight f
    WHERE f.source = :source

Not:

    SELECT * FROM flights WHERE source = ?

The second example is SQL, not HQL.

## Validation

The project includes:

- @NotBlank
- @NotNull
- @Positive
- @DecimalMin
- @Digits
- @FutureOrPresent
- @Valid
- @Validated

## Run

Start MySQL first.

Update the username/password in:

    HibernateConfig.java

Then:

    mvn clean install

Run:

    mvn spring-boot:run

Application:

    http://localhost:8080

## Testing

Create:

    POST /api/flights

Read all:

    GET /api/flights

Read one:

    GET /api/flights/1

Update:

    PUT /api/flights/1

Delete:

    DELETE /api/flights/1

Search:

    GET /api/flights/search/source?source=Hyderabad

    GET /api/flights/search/route?source=Hyderabad&destination=Mumbai

    GET /api/flights/search/price?maxPrice=6000

    GET /api/flights/search/date-range?startDate=2026-12-01&endDate=2026-12-31

    GET /api/flights/search/cheapest

## Native Hibernate vs Spring Data JPA

This project demonstrates:

    Spring Boot
        |
        +-- Native Hibernate
                |
                +-- SessionFactory
                +-- Session
                +-- Transaction
                +-- HQL

It does NOT demonstrate:

    JpaRepository
    CrudRepository
    Spring Data @Query
    EntityManager

This makes the project suitable for teaching native Hibernate CRUD
before introducing Spring Data JPA.
