import java.util.Scanner;

public class LoanEligibility {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter age: ");
        int age = sc.nextInt();

        System.out.print("Enter monthly salary: ");
        double salary = sc.nextDouble();

        System.out.print("Enter credit score: ");
        int creditScore = sc.nextInt();

        System.out.print("Enter existing monthly EMI: ");
        double emi = sc.nextDouble();

        double emiPercentage = (emi / salary) * 100;

        if (age < 21 || age > 60) {
            System.out.println("Not Eligible");
            System.out.println("Reason: Age must be between 21 and 60.");
        }
        else if (salary < 30000) {
            System.out.println("Not Eligible");
            System.out.println("Reason: Monthly salary must be at least 30000.");
        }
        else if (creditScore < 700) {
            System.out.println("Not Eligible");
            System.out.println("Reason: Credit score must be at least 700.");
        }
        else if (emiPercentage > 40) {
            System.out.println("Not Eligible");
            System.out.println("Reason: Existing EMI is more than 40% of monthly salary.");
        }
        else {
            System.out.println("Eligible for Loan");
        }

        sc.close();
    }
}