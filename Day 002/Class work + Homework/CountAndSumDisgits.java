import java.util.Scanner;

public class CountAndSumDisgits {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter a number: ");
        int num = sc.nextInt();

        int count = 0;
        int sum = 0;

        while (num > 0) {
            int digit = num % 10;

            sum = sum + digit;
            count = count + 1;

            num = num / 10;
        }

        System.out.println("Number of digits: " + count);
        System.out.println("Sum of digits: " + sum);

        sc.close();
    }
}