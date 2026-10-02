import java.util.Scanner;

public class GradeCalculation {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter marks for Subject 1: ");
        double m1 = sc.nextDouble();

        System.out.print("Enter marks for Subject 2: ");
        double m2 = sc.nextDouble();

        System.out.print("Enter marks for Subject 3: ");
        double m3 = sc.nextDouble();

        System.out.print("Enter marks for Subject 4: ");
        double m4 = sc.nextDouble();

        System.out.print("Enter marks for Subject 5: ");
        double m5 = sc.nextDouble();

        double total = m1 + m2 + m3 + m4 + m5;

        double average = total / 5;

        char grade;

        if (average >= 90 && average <= 100) {
            grade = 'A';
        } else if (average >= 75) {
            grade = 'B';
        } else if (average >= 60) {
            grade = 'C';
        } else if (average >= 50) {
            grade = 'D';
        } else {
            grade = 'F';
        }

        System.out.println("\nTotal Marks = " + total);
        System.out.println("Average Marks = " + average);
        System.out.println("Grade = " + grade);

        sc.close();
    }
}