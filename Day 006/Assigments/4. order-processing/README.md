# E-Commerce Order Processing (JDBC)

Console application that places multi-product orders atomically: the order header, its items, the stock reductions and the order total are saved in one transaction, or not at all.

## Tech

Java 8, Maven, MySQL 8 / InnoDB (`mysql-connector-j`), JDBC transactions, generated keys, JOINs, `SELECT ... FOR UPDATE`.

## Structure

```text
src/main/java/com/company/jdbc
├── config/     DBConnection (reads db.properties)
├── model/      Customer, Product, Order, OrderItem, OrderItemRequest, OrderStatus
├── dao/        Customer/Product/Order/OrderItem DAOs + JDBC implementations
├── service/    CustomerService, ProductService, OrderService (+ implementations)
├── exception/  custom exceptions
└── app/        OrderProcessingApp (main menu), ConsoleInput
src/main/resources
├── schema.sql        creates the database and tables
├── sample-data.sql   5 customers, 8 products, 2 orders
└── db.properties     MySQL URL / user / password (not committed)
```

## Setup

1. Run `schema.sql`, then `sample-data.sql`, in MySQL.
2. Check the MySQL password in `src/main/resources/db.properties`.
3. In Eclipse: **File → Import → Maven → Existing Maven Projects**, select this folder.
4. Run `com.company.jdbc.app.OrderProcessingApp` as a Java Application.

## How `placeOrder` works (`OrderServiceImpl`)

1. `setAutoCommit(false)` (original value restored in `finally`)
2. Check the customer exists
3. Insert the order header and read its ID with `getGeneratedKeys()`
4. For each product (in product ID order, to avoid deadlocks): lock the row with `FOR UPDATE`, verify stock, insert the order item with the generated order ID, reduce stock
5. Save the total (sum of quantity × unit price)
6. `commit()`. On any failure, `rollback()`, so no partial order remains

Duplicate lines for the same product are merged. The unit price is copied into `order_items`, so later price changes don't alter old orders.

## Demos

- **Place Order (menu 7)** prints checks after success: every item uses the generated order ID, stock dropped by exactly the ordered quantity, and the saved total equals the sum of the items.
- **Rollback Demo (menu 11)** writes everything, then fails before commit. It shows order and item row counts and stock unchanged. An order whose last item lacks stock (e.g. product 7, stock 0) behaves the same way.
- **Cancel Order (menu 10)** restores stock in one transaction. A second cancel of the same order is refused.
