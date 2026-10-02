package bankSimulator;

public class BankAccountSimulator {

    public static void main(String[] args) {

        try {

            /*
            Creating Savings Account
            */

            BankAccount savingsAccount =
                    new SavingsAccount(
                            "SAV1001",
                            "Ravi",
                            20000
                    );


            /*
            Creating Current Account
            */

            BankAccount currentAccount =
                    new CurrentAccount(
                            "CUR1001",
                            "Priya",
                            10000
                    );


            System.out.println("SAVINGS ACCOUNT");
            savingsAccount.displayAccountDetails();

            System.out.println();
            System.out.println("-------------------------");


            /*
            Deposit into Savings Account
            */

            System.out.println();
            System.out.println("DEPOSIT");

            savingsAccount.deposit(5000);

            System.out.println(
                    "Current Balance: Rs. "
                    + savingsAccount.getBalance());


            /*
            Withdrawal from Savings Account
            */

            System.out.println();
            System.out.println("WITHDRAWAL");

            savingsAccount.withdraw(3000);

            System.out.println(
                    "Current Balance: Rs. "
                    + savingsAccount.getBalance());


            /*
            Transfer from Savings to Current Account
            */

            System.out.println();
            System.out.println("TRANSFER");

            savingsAccount.transfer(
                    currentAccount,
                    5000
            );

            System.out.println(
                    "Savings Balance: Rs. "
                    + savingsAccount.getBalance());

            System.out.println(
                    "Current Account Balance: Rs. "
                    + currentAccount.getBalance());


            /*
            Current Account withdrawal
            */

            System.out.println();
            System.out.println("CURRENT ACCOUNT WITHDRAWAL");

            currentAccount.withdraw(12000);

            System.out.println(
                    "Current Account Balance: Rs. "
                    + currentAccount.getBalance());


            /*
            Negative deposit test
            */

            System.out.println();
            System.out.println("INVALID DEPOSIT");

            savingsAccount.deposit(-1000);


            /*
            Invalid withdrawal test
            */

            System.out.println();
            System.out.println("INVALID WITHDRAWAL");

            savingsAccount.withdraw(50000);


            /*
            Invalid transfer test
            */

            System.out.println();
            System.out.println("INVALID TRANSFER");

            savingsAccount.transfer(
                    currentAccount,
                    50000
            );


            /*
            Invalid account creation test
            */

            System.out.println();
            System.out.println("INVALID ACCOUNT");

            BankAccount invalidAccount =
                    new SavingsAccount(
                            "SAV1002",
                            "Amit",
                            500
                    );

        }
        catch (BankAccountException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}