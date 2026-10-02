class BankAccount {

    int accountNumber;
    String holderName;
    double balance;

    static String bankName = "State Bank";
    static int totalAccounts = 0;

    BankAccount(int accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;

        totalAccounts++;
    }

    void deposit(double amount) {
        balance = balance + amount;
        System.out.println("Deposited: " + amount);
    }

    void withdraw(double amount) {

        if (amount <= balance) {
            balance = balance - amount;
            System.out.println("Withdrawn: " + amount);
        } 
        else {
            System.out.println("Insufficient balance.");
        }
    }

    void displayAccount() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Holder Name: " + holderName);
        System.out.println("Balance: " + balance);
        System.out.println();
    }

    static void displayBankName() {
        System.out.println("Bank Name: " + bankName);
    }

    static void displayTotalAccounts() {
        System.out.println("Total Accounts: " + totalAccounts);
    }
}

public class BankAccountDemo {

    public static void main(String[] args) {

        BankAccount.displayBankName();


        BankAccount account1 = new BankAccount(101, "Rahul", 50000);
        BankAccount account2 = new BankAccount(102, "Amit", 30000);


        account1.deposit(10000);
        account1.withdraw(5000);

        account2.deposit(5000);
        account2.withdraw(10000);

        System.out.println("\nAccount 1 Details:");
        account1.displayAccount();

        System.out.println("Account 2 Details:");
        account2.displayAccount();

        BankAccount.displayTotalAccounts();
    }
}