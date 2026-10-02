import java.util.Scanner;

public class Method {

    public static void welcomeMessage() {
        System.out.println("Welcome to ABC Bank ATM.");
    }
    public static float getBalance(Scanner sc) {
        System.out.println("Kindly enter your balance amount.");
        return sc.nextFloat();
    }

    public static float getWithdrawal(Scanner sc) {
        System.out.println("Kindly enter the amount that you would like to withdraw.");
        return sc.nextFloat();
    }

    public static void withdraw(float balance, float withdrawal) {

        if (withdrawal <= 0) {
            System.out.println("Withdrawal amount must be greater than 0.");
        }
        else if (withdrawal > balance) {
            System.out.println("Withdrawal amount cannot be greater than the balance amount.");
        }
        else {
            float remainingBalance = balance - withdrawal;

            System.out.println("You have withdrawn: " + withdrawal);
            System.out.println("Remaining balance: " + remainingBalance);
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        welcomeMessage();

        float balance = getBalance(sc);

        float withdrawal = getWithdrawal(sc);

        withdraw(balance, withdrawal);

        sc.close();
    }
}

