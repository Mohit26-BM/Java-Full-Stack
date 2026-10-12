# Employee Directory (JDBC)

Console application for HR to add, find, search, update and delete employees, using plain JDBC with MySQL.

## Tech

Java 8, Maven, MySQL 8 (`mysql-connector-j`), JDBC (`PreparedStatement`, try-with-resources).

## Structure

```text
src/main/java/com/company/jdbc
├── config/     DBConnection (reads db.properties)
├── model/      Employee, Department
├── dao/        EmployeeDao, DepartmentDao + JDBC implementations, EmployeeSortField
├── service/    EmployeeService + implementation (validation, business rules)
├── exception/  custom exceptions
└── app/        EmployeeDirectoryApp (main menu)
src/main/resources
├── schema.sql        creates the database and tables
├── sample-data.sql   5 departments, 15 employees
└── db.properties     MySQL URL / user / password
```

## Setup

1. Run the SQL scripts (MySQL command line or Workbench):

   ```text
   mysql -u root -p < src/main/resources/schema.sql
   mysql -u root -p < src/main/resources/sample-data.sql
   ```

2. Set your MySQL password in `src/main/resources/db.properties`.
3. In Eclipse: **File → Import → Maven → Existing Maven Projects**, select this folder.
4. Run `com.company.jdbc.app.EmployeeDirectoryApp` as a Java Application.

## Features

- Add, find by ID, search by department, update contact info (email/phone), delete
- Validation before insert: blank name, invalid email, invalid salary, phone, joining date, department
- Duplicate `employee_code` / `email` reported clearly
- Optional challenge: list all employees with sorting (name / joining date) and pagination (`LIMIT` / `OFFSET`)
