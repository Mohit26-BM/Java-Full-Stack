# CSB Training – Mohit

Coursework from the NIIT Java Full Stack training program, organised by day.

## Contents

| Folder | What's inside |
|---|---|
| `Day 001` | Agile assignment: stakeholders, product vision and user stories for an e-commerce app (PDF) |
| `Day 002` | Basic Java programs (conditions, loops, methods) and Git/DevOps assignment reports (PDF) |
| `Day 003` | OOP: classes, constructors, static members, inheritance, polymorphism, searching, String methods |
| `Day 004` | Abstract classes, interfaces, custom exceptions, threads, file I/O, and functional interfaces (`Predicate`, `Function`, `Consumer`, `Supplier`) |
| `Day 005` | Collections, user-defined exceptions, SQL schema assignments, and the `testbank` project |
| `Day 006`–`Day 010` | Reserved for upcoming days |
| `Java Notes` | Class notes: core Java, lambdas, Map API, modern Java features, Spring intro, Git practice |

`Progressive_Banking_Project_Description.pdf` is the specification for the course's main banking project.

## testbank

A console-based banking application (Phases 1–3 of the banking project spec), located at
`Day 005/Homework (assignments)/testbank`.

- **Features:** manage customers and accounts; deposit, withdraw and transfer money; view transaction history by account, type or date range
- **Structure:** `entity` → `dao` (JDBC) → `service` → `controller` (`MainBank` is the entry point)
- **Tech:** Java 8, Maven, MySQL (`mysql-connector-j`), JUnit 5

To run it, create a MySQL database named `testbank` with `customer`, `account` and `transaction` tables, update the connection details in `util/DBConnection.java`, and run `MainBank`.

## Running the exercises

Each exercise in Days 002–005 is a standalone program with its own `main` method and can be run individually from an IDE.
