import java.util.Scanner;

public class ATMPinSecurity {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int correctPin = 1234;
        int attempts = 0;
        boolean verified = false;

        while (attempts < 3) {

            System.out.print("Enter PIN: ");
            int pin = sc.nextInt();

            if (pin == correctPin) {
                System.out.println("PIN verified successfully.");
                System.out.println("Welcome to the ATM.");

                verified = true;
                break;
            } 
            else {
                attempts++;
                System.out.println("Incorrect PIN.");
            }
        }

        if (!verified) {
            System.out.println("Maximum attempts exceeded.");
            System.out.println("Your account has been temporarily locked.");
        }

        sc.close();
    }
}