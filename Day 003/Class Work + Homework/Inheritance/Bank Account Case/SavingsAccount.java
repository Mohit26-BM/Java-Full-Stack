public class SavingsAccount extends BankAccount {

    float interestRate;

    SavingsAccount(int accountNumber, String accountHolderName,
                   int balance, float interestRate) {

        super(accountNumber, accountHolderName, balance);
        this.interestRate = interestRate;
    }

    float calculateInterest() {

        float interest = balance * interestRate / 100;
        return interest;
    }

    @Override
    void displayAccountDetails() {

        super.displayAccountDetails();

        System.out.println("The Interest Rate is: " + interestRate + "%");
        System.out.println("The Interest is: " + calculateInterest());
    }
}