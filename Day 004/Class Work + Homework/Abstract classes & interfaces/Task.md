# Java Use Case: Payment Processing System

## Objective

Build a small **Payment Processing System** in Java to practice:

- Abstract classes
- Interfaces
- Inheritance
- Method overriding
- Constructors
- Polymorphism
- Runtime method dispatch

The system should support multiple payment methods while keeping common payment-processing logic in an abstract class and defining payment-specific capabilities through interfaces.

## Business Scenario

An online shopping application allows customers to make payments using different payment methods:

- Credit Card
- UPI
- Net Banking

All payment methods have common information and behavior, such as transaction ID, customer name, amount, starting the payment, completing the payment, and displaying transaction details.

However, each payment method processes the payment differently. Some payment methods also support additional capabilities:

- Credit Card supports **reward points**
- UPI supports **QR-based payment**
- Net Banking supports **bank authentication**

Design the application using an **abstract class** for common payment behavior and **interfaces** for additional capabilities.

# Requirements

## 1. Create an Abstract Class `Payment`

Create an abstract class named `Payment` with:

```text
transactionId
customerName
amount
```

Requirements:

- Keep the attributes private.
- Create a parameterized constructor.
- Create appropriate getter methods.
- Create a concrete method:

```java
void displayTransactionDetails()
```

This method should display the transaction ID, customer name, and amount.

Create the following abstract method:

```java
abstract void processPayment();
```

Each payment type must provide its own implementation.

# 2. Create Interface `Refundable`

Create an interface named `Refundable` containing:

```java
void processRefund();
```

Any payment method implementing this interface must provide refund functionality.

# 3. Create Interface `Rewardable`

Create an interface named `Rewardable` containing:

```java
int calculateRewardPoints();
```

This interface represents payment methods that provide reward points.

# 4. Create Interface `QRPayable`

Create an interface named `QRPayable` containing:

```java
void generateQRCode();
```

This interface represents payment methods that support QR-based payments.

# 5. Create Class `CreditCardPayment`

Create:

```java
CreditCardPayment extends Payment implements Refundable, Rewardable
```

It should also implement:

```java
Refundable
Rewardable
```

Implement `processPayment()` to display messages such as:

```text
Processing credit card payment...
Credit card payment completed successfully.
```

Implement `processRefund()`:

```text
Credit card refund processed successfully.
```

Implement `calculateRewardPoints()` using:

```text
Reward Points = amount / 100
```

For example, an amount of 5000 gives 50 reward points.

# 6. Create Class `UPIPayment`

Create:

```java
UPIPayment extends Payment
```

It should implement:

```java
Refundable
QRPayable
```

Implement `processPayment()`:

```text
Processing UPI payment...
UPI payment completed successfully.
```

Implement `processRefund()`:

```text
UPI refund processed successfully.
```

Implement `generateQRCode()`:

```text
QR Code generated successfully.
```

# 7. Create Class `NetBankingPayment`

Create:

```java
NetBankingPayment extends Payment
```

It should implement:

```java
Refundable
```

Implement `processPayment()`:

```text
Authenticating bank account...
Processing net banking payment...
Net banking payment completed successfully.
```

Implement `processRefund()`:

```text
Net banking refund processed successfully.
```

# 8. Demonstrate Polymorphism

Create a class:

```java
PaymentDemo
```

Inside `main()`, create payment objects using the abstract class reference:

```java
Payment p1 = new CreditCardPayment("TXN101", "Ravi", 5000);
Payment p2 = new UPIPayment("TXN102", "Priya", 2500);
Payment p3 = new NetBankingPayment("TXN103", "Arun", 7500);
```

Call:

```java
p1.displayTransactionDetails();
p1.processPayment();

p2.displayTransactionDetails();
p2.processPayment();

p3.displayTransactionDetails();
p3.processPayment();
```

Observe that the same `processPayment()` call executes different implementations depending on the actual object.

# 9. Demonstrate Interface-Based Programming

Use interface references to access additional capabilities.

For example:

```java
Rewardable rewardable = new CreditCardPayment("TXN104", "Kiran", 10000);
System.out.println("Reward Points: " + rewardable.calculateRewardPoints());
```

For UPI:

```java
QRPayable qrPayment = new UPIPayment("TXN105", "Meena", 3000);
qrPayment.generateQRCode();
```

# Expected Output

A possible output is:

```text
Transaction ID: TXN101
Customer: Ravi
Amount: 5000.0

Processing credit card payment...
Credit card payment completed successfully.

Reward Points: 50

Transaction ID: TXN102
Customer: Priya
Amount: 2500.0

Processing UPI payment...
UPI payment completed successfully.
QR Code generated successfully.

Transaction ID: TXN103
Customer: Arun
Amount: 7500.0

Authenticating bank account...
Processing net banking payment...
Net banking payment completed successfully.
```

# Class Structure

```text
                    Payment
                 <<abstract>>
                      |
       +--------------+--------------+
       |              |              |
CreditCardPayment  UPIPayment   NetBankingPayment
       |              |              |
       +-- Refundable +-- Refundable +-- Refundable
       +-- Rewardable +-- QRPayable
```

# Key Learning Requirements

## Abstract Class

Use `Payment` as an abstract class because all payment types share common state and behavior, but the actual payment-processing logic differs.

## Abstract Method

Use:

```java
abstract void processPayment();
```

Each subclass must implement this method.

## Interface

Use interfaces to represent additional capabilities that are not common to every payment type:

```text
Refundable
Rewardable
QRPayable
```

## Multiple Interfaces

Demonstrate that a Java class can implement multiple interfaces:

```java
class CreditCardPayment extends Payment
        implements Refundable, Rewardable
```

## Runtime Polymorphism

Demonstrate:

```java
Payment payment = new CreditCardPayment(...);
```

and:

```java
Payment payment = new UPIPayment(...);
```

The same reference type should invoke different overridden implementations.

# Restrictions

1. Do not create objects directly from the `Payment` abstract class.
2. Do not put the implementation of `processPayment()` inside `Payment`.
3. Do not duplicate common attributes such as transaction ID, customer name, and amount in every child class.
4. Use interfaces for additional capabilities.
5. Use private fields and appropriate getters.
6. Use method overriding wherever required.
7. Demonstrate runtime polymorphism using a parent-class reference.

# Progressive Challenges

## Challenge 1 – Add Debit Card Payment

Create:

```java
DebitCardPayment extends Payment
```

Implement `Refundable` and add appropriate payment-processing behavior.

## Challenge 2 – Add Wallet Payment

Create:

```java
WalletPayment extends Payment
```

Implement:

```java
Refundable
QRPayable
```

## Challenge 3 – Add Transaction Validation

Before processing a payment, validate:

```text
amount > 0
customer name is not empty
transaction ID is not empty
```

If validation fails, display an appropriate error message.

## Challenge 4 – Payment Processor

Create a class:

```java
PaymentProcessor
```

with:

```java
void process(Payment payment)
```

Inside this method:

```java
payment.displayTransactionDetails();
payment.processPayment();
```

Test the method with different payment objects.

## Challenge 5 – Interface-Based Refund Processing

Create:

```java
void refundPayment(Refundable payment)
```

This method should process refunds without depending on the concrete payment class.

For example:

```java
refundPayment(new CreditCardPayment(...));
refundPayment(new UPIPayment(...));
refundPayment(new NetBankingPayment(...));
```

# Expected Concepts Covered

By completing this exercise, you should be able to demonstrate:

- Abstract classes
- Abstract methods
- Concrete methods inside abstract classes
- Constructors in inheritance
- Method overriding
- Runtime polymorphism
- Interfaces
- Multiple interface implementation
- Interface references
- Parent-class references
- Encapsulation
- Code reuse
- Loose coupling
- Designing classes based on common behavior and capabilities
