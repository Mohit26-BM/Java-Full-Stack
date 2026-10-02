/*
ONLINE BANKING APPLICATION

This program demonstrates:

1. Identifying classes and objects
2. Encapsulation
3. Inheritance
4. Polymorphism
5. Abstraction
6. Code reuse and reduced duplication
7. Extensibility for future account types
*/

abstract class OnlineBankAccount {

    /*
    ENCAPSULATION

    Account information is kept private so that it
    cannot be directly accessed from outside the class.
    */
    private int accountNumber;
    private String holderName;
    private double balance;

    /*
    CONSTRUCTOR

    Initializes the common information for every
    type of bank account.
    */
    OnlineBankAccount(int accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
    }

    /*
    ENCAPSULATION

    Getter methods provide controlled access to
    private account information.
    */
    public int getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    /*
    COMMON ACCOUNT OPERATION

    Deposit is common to different types of accounts,
    so it is implemented only once in the parent class.
    */
    public void deposit(double amount) {

        if (amount > 0) {
            balance = balance + amount;
            System.out.println("Deposited: " + amount);
        }
        else {
            System.out.println("Invalid deposit amount.");
        }
    }

    /*
    COMMON ACCOUNT OPERATION

    Withdrawal is implemented in the parent class
    to reduce code duplication.
    */
    public void withdraw(double amount) {

        if (amount > 0 && amount <= balance) {
            balance = balance - amount;
            System.out.println("Withdrawn: " + amount);
        }
        else {
            System.out.println("Invalid withdrawal amount.");
        }
    }

    /*
    COMMON METHOD

    Displays information shared by all account types.
    */
    public void displayAccountDetails() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Holder Name: " + holderName);
        System.out.println("Balance: " + balance);
    }

    /*
    ABSTRACTION

    Every account must provide an interest calculation,
    but the parent class does not decide how it is calculated.
    */
    abstract double calculateInterest();

    /*
    ABSTRACTION

    Each account type can provide its own account type.
    */
    abstract String getAccountType();
}


/*
INHERITANCE

SavingsAccount inherits common properties and
methods from OnlineBankAccount.
*/
class OnlineSavingsAccount extends OnlineBankAccount {

    private double interestRate;

    OnlineSavingsAccount(int accountNumber, String holderName,
                         double balance, double interestRate) {

        super(accountNumber, holderName, balance);
        this.interestRate = interestRate;
    }

    /*
    POLYMORPHISM

    Savings account calculates interest based on
    its own interest rate.
    */
    @Override
    double calculateInterest() {
        return getBalance() * interestRate / 100;
    }

    @Override
    String getAccountType() {
        return "Savings Account";
    }
}


/*
INHERITANCE

CurrentAccount inherits common properties and
methods from OnlineBankAccount.
*/
class OnlineCurrentAccount extends OnlineBankAccount {

    private double overdraftLimit;

    OnlineCurrentAccount(int accountNumber, String holderName,
                         double balance, double overdraftLimit) {

        super(accountNumber, holderName, balance);
        this.overdraftLimit = overdraftLimit;
    }

    /*
    POLYMORPHISM

    Current accounts use a different interest calculation.
    */
    @Override
    double calculateInterest() {
        return 0;
    }

    @Override
    String getAccountType() {
        return "Current Account";
    }
}


/*
INHERITANCE

FixedDepositAccount inherits common properties and
methods from OnlineBankAccount.
*/
class OnlineFixedDepositAccount extends OnlineBankAccount {

    private double interestRate;
    private int duration;

    OnlineFixedDepositAccount(int accountNumber, String holderName,
                              double balance, double interestRate,
                              int duration) {

        super(accountNumber, holderName, balance);
        this.interestRate = interestRate;
        this.duration = duration;
    }

    /*
    POLYMORPHISM

    Fixed deposit calculates interest based on
    interest rate and duration.
    */
    @Override
    double calculateInterest() {
        return getBalance() * interestRate * duration / 100;
    }

    @Override
    String getAccountType() {
        return "Fixed Deposit Account";
    }
}


/*
MAIN CLASS

Creates different account objects and demonstrates
polymorphism.
*/
public class OnlineBankingApplication {

    public static void main(String[] args) {

        /*
        OBJECTS

        The reference type is OnlineBankAccount,
        while the actual objects are different account types.

        This demonstrates POLYMORPHISM.
        */
        OnlineBankAccount account1 =
                new OnlineSavingsAccount(
                        101, "Rahul", 50000, 4.0);

        OnlineBankAccount account2 =
                new OnlineCurrentAccount(
                        102, "Amit", 75000, 25000);

        OnlineBankAccount account3 =
                new OnlineFixedDepositAccount(
                        103, "Priya", 100000, 6.0, 2);


        /*
        SAVINGS ACCOUNT
        */
        System.out.println("SAVINGS ACCOUNT");
        System.out.println();

        System.out.println("Account Type: " +
                           account1.getAccountType());

        account1.displayAccountDetails();

        account1.deposit(10000);

        account1.withdraw(5000);

        System.out.println("Interest: " +
                           account1.calculateInterest());

        System.out.println();


        /*
        CURRENT ACCOUNT
        */
        System.out.println("CURRENT ACCOUNT");
        System.out.println();

        System.out.println("Account Type: " +
                           account2.getAccountType());

        account2.displayAccountDetails();

        account2.deposit(5000);

        account2.withdraw(10000);

        System.out.println("Interest: " +
                           account2.calculateInterest());

        System.out.println();


        /*
        FIXED DEPOSIT ACCOUNT
        */
        System.out.println("FIXED DEPOSIT ACCOUNT");
        System.out.println();

        System.out.println("Account Type: " +
                           account3.getAccountType());

        account3.displayAccountDetails();

        System.out.println("Interest: " +
                           account3.calculateInterest());

        System.out.println();
    }
}