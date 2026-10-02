public class BankApplication {

    public static void main(String[] args) {

        SavingsAccount savings =
            new SavingsAccount(101, "Ravi", 10000, 5);

        savings.deposit(2000);
        savings.withdraw(3000);

        float interest = savings.calculateInterest();

        System.out.println("Interest: " + interest);

        savings.displayAccountDetails();


        CurrentAccount current =
            new CurrentAccount(102, "Priya", 5000, 3000);

        current.deposit(2000);
        current.withdraw(7000);
        current.withdraw(2000);

        current.displayAccountDetails();
    }
}