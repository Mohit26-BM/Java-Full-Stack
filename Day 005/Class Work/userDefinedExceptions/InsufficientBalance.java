package userDefinedExceptions;

public class InsufficientBalance {
	public static void main (String[] args)
	{
		double balance = 2000.0;
		double withdrawAmount = 1500.0;
		
		try {
			if (withdrawAmount > balance)
			{
				throw new InsufficientBalanceException("Insufficient balance for withdrawal");
			}
			System.out.println("Withdrawal successful. Remaining balance: " + (balance - withdrawAmount));
		}
		catch (InsufficientBalanceException e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
