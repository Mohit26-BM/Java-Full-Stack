import java.util.Scanner;


public class LinearSearch {

	public static void main(String[] args) {
		int [] numbers = {10, 20, 30 , 40, 50};
		Scanner sc = new Scanner (System.in);
		System.out.print("Enter the number to search: ");
		int target = sc.nextInt();
		
		boolean found = false;
		
		int i = 0;
		
		for (i = 0; i < numbers.length; i++)
		{
			if (numbers[i] == target)
			{
				found = true;
				break;
			}
		}
		if (found == true)
		{
			System.out.print("The element found in the array.");
		}
		else {
			System.out.print("The element not found in the array.");
		}
		
		}

}
