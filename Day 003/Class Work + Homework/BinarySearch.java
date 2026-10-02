import java.util.Scanner;
import java.util.Arrays;

public class BinarySearch {
    public static void main(String[] args) {

        int[] numbers = {1, 4, 2, 8, 10, 12};

        Arrays.sort(numbers);

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number to search: ");
        int target = sc.nextInt();

        int left = 0;
        int right = numbers.length - 1;

        while (left <= right) {

            int mid = left + (right - left) / 2;

            if (numbers[mid] == target) {
                System.out.println("The element is found in the array.");
                sc.close();
                return;
            }
            else if (numbers[mid] < target) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }

        System.out.println("The element is not found in the array.");

        sc.close();
    }
}
