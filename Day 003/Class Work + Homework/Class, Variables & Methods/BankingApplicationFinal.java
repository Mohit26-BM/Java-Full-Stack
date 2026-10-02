class FinalBankAccount {

    int accountNumber;
    String holderName;
    double balance;

    static String bankName = "ABC Bank";
    static int totalAccounts = 0;
    static double minimumBalance = 1000;

    FinalBankAccount() {
        accountNumber = 0;
        holderName = "Unknown";
        balance = minimumBalance;

        totalAccounts++;
    }

    FinalBankAccount(int accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;

        if (balance >= minimumBalance) {
            this.balance = balance;
        } else {
            this.balance = minimumBalance;
        }

        totalAccounts++;
    }

    void deposit(double amount) {
        if (amount > 0) {
            balance = balance + amount;
            System.out.println("Deposited: " + amount);
        } else {
            System.out.println("Deposit amount must be greater than zero.");
        }
    }

    void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be greater than zero.");
        } else if (balance - amount < minimumBalance) {
            System.out.println("Withdrawal failed.");
            System.out.println("Minimum balance of " + minimumBalance + " must be maintained.");
        } else {
            balance = balance - amount;
            System.out.println("Withdrawn: " + amount);
        }
    }

    void checkBalance() {
        System.out.println("Current Balance: " + balance);
    }

    void displayAccountDetails() {
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

    static void changeMinimumBalance(double newMinimumBalance) {
        if (newMinimumBalance >= 0) {
            minimumBalance = newMinimumBalance;
            System.out.println("Minimum balance changed to: " + minimumBalance);
        } else {
            System.out.println("Minimum balance cannot be negative.");
        }
    }
}

public class BankingApplicationFinal {

    public static void main(String[] args) {

        FinalBankAccount.displayBankName();

        FinalBankAccount account1 =
                new FinalBankAccount(101, "Rahul", 10000);

        FinalBankAccount account2 =
                new FinalBankAccount(102, "Amit", 15000);

        FinalBankAccount account3 =
                new FinalBankAccount();

        System.out.println("\nAccount 1 Details:");
        account1.displayAccountDetails();

        System.out.println("Account 2 Details:");
        account2.displayAccountDetails();

        System.out.println("Account 3 Details:");
        account3.displayAccountDetails();

        System.out.println("Account 1 Deposit:");
        account1.deposit(5000);

        System.out.println("\nAccount 1 Withdrawal:");
        account1.withdraw(3000);

        System.out.println("\nAccount 1 Balance:");
        account1.checkBalance();

        System.out.println("\nAccount 2 Withdrawal:");
        account2.withdraw(14500);

        System.out.println("\nAccount 2 Invalid Withdrawal:");
        account2.withdraw(1);

        System.out.println("\nAccount 2 Invalid Deposit:");
        account2.deposit(-500);

        System.out.println("\nChanging Minimum Balance:");
        FinalBankAccount.changeMinimumBalance(2000);

        System.out.println("\nAccount 1 Withdrawal After Minimum Balance Change:");
        account1.withdraw(10000);

        System.out.println("\nFinal Account Details:");

        account1.displayAccountDetails();
        account2.displayAccountDetails();
        account3.displayAccountDetails();

        FinalBankAccount.displayTotalAccounts();
    }
}