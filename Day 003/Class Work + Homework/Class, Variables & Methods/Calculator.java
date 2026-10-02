
public class Calculator {

		int add (int a, int b) {
			return a + b;
		}
		int subtract (int a, int b)
		{
			return a-b;
		}
		int multiply (int a , int b) {
			return a*b;
		}
		int divide (int a, int b)
		{
			return a/b;
		}
		
		public static void main (String [] args)
		{
			Calculator calculator = new Calculator ();
			int a = 10;
			int b = 5;
			System.out.println("The sum is: " + calculator.add(a, b));
			System.out.println("The difference is: " + calculator.subtract(a, b));
			System.out.println("The product is: " + calculator.multiply(a, b));
			System.out.println("The division is: " + calculator.divide(a, b));
			
		}
}
