public class BankAccount {

    int accountNumber;
    String accountHolderName;
    int balance;

    BankAccount(int accountNumber, String accountHolderName, int balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    void deposit(int amount) {
        if (amount > 0) {
            balance = balance + amount;
            System.out.println("Amount deposited: " + amount);
            System.out.println("New Balance: " + balance);
        } else {
            System.out.println("Invalid Deposit amount.");
        }
    }

    void withdraw(int amount) {
        if (amount > 0 && amount <= balance) {
            balance = balance - amount;
            System.out.println("Amount withdrawn: " + amount);
            System.out.println("Remaining Balance: " + balance);
        } else {
            System.out.println("Invalid withdrawal amount.");
        }
    }

    void displayAccountDetails() {
        System.out.println("The Account Number is: " + accountNumber);
        System.out.println("The Account Holder is: " + accountHolderName);
        System.out.println("The Account Balance is: " + balance);
    }
}