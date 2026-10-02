import java.util.Scanner;

public class ElectricityBillCalculation {

    public static int calculateBill(int units) {

        if (units <= 100) {
            return 2 * units;
        }
        else if (units <= 200) {
            return (100 * 2) + ((units - 100) * 3);
        }
        else if (units <= 400) {
            return (100 * 2) + (100 * 3) + ((units - 200) * 5);
        }
        else {
            return (100 * 2) + (100 * 3) + (200 * 5) + ((units - 400) * 7);
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int units = sc.nextInt();

        int bill = calculateBill(units);

        System.out.println("The bill is: " + bill);

        sc.close();
    }
}

