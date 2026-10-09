# Spring Boot Separate File Logging Demo

A complete Maven-based Spring Boot REST API demonstrating how to write application logs to `logs/application.log` using SLF4J and Spring Boot's default Logback implementation.

## Requirements

- JDK 17 or newer
- Maven 3.6+ (or use an IDE with Maven support)
- Port 8080 available

## Run on Windows

1. Extract the ZIP file.
2. Open Command Prompt or PowerShell in the extracted `springboot-file-logging` directory.
3. Check Java and Maven:

   ```bat
   java -version
   mvn -version
   ```

4. Start the app:

   ```bat
   mvn spring-boot:run
   ```

Or package it and run the executable Spring Boot JAR:

```bat
mvn clean package
java -jar target/springboot-file-logging-0.0.1-SNAPSHOT.jar
```

The log file is configured as `logs/application.log`, relative to the process working directory. The application writes logs to the console and the file.

## Run on Linux/macOS

```bash
mvn spring-boot:run
```

Or:

```bash
mvn clean package
java -jar target/springboot-file-logging-0.0.1-SNAPSHOT.jar
```

## Test the endpoints

List employees:

```bash
curl http://localhost:8080/api/employees
```

Get employee 1:

```bash
curl http://localhost:8080/api/employees/1
```

Create an employee (Windows Command Prompt):

```bat
curl -X POST http://localhost:8080/api/employees -H "Content-Type: application/json" -d "{\"name\":\"Meera\",\"department\":\"HR\"}"
```

Create an employee (PowerShell, macOS, or Linux):

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Meera","department":"HR"}'
```

Try a missing employee to generate a warning and a 404 response:

```bash
curl http://localhost:8080/api/employees/999
```

Delete employee 1:

```bash
curl -X DELETE http://localhost:8080/api/employees/1
```

After sending requests, open `logs/application.log` to see the recorded events.

## Logging configuration

`src/main/resources/application.properties` contains:

```properties
logging.file.name=logs/application.log
logging.level.root=INFO
logging.level.com.example.logging=DEBUG
```

- `DEBUG`: diagnostic details for this application package
- `INFO`: significant normal application events
- `WARN`: unexpected or recoverable problems
- `ERROR`: failures; passing the exception object records its stack trace

The employee data is held in memory for demonstration purposes and is reset when the application restarts. This is not a database-backed production application.

Do not log passwords, access tokens, authorization headers, payment card data, or other secrets.
