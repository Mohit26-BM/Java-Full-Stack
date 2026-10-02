public class CurrentAccount extends BankAccount {

    int overdraftLimit;

    CurrentAccount(int accountNumber, String accountHolderName,
                   int balance, int overdraftLimit) {

        super(accountNumber, accountHolderName, balance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    void withdraw(int amount) {

        if (amount > 0 && amount <= balance + overdraftLimit) {

            balance = balance - amount;

            System.out.println("Amount withdrawn: " + amount);
            System.out.println("Balance Left is: " + balance);

        } else {

            System.out.println("Withdrawal rejected.");
        }
    }

    @Override
    void displayAccountDetails() {

        super.displayAccountDetails();

        System.out.println("The overdraft limit is: " + overdraftLimit);
    }
}