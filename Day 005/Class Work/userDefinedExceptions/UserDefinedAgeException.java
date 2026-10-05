package userDefinedExceptions;

public class UserDefinedAgeException {

	public static void main(String[] args) {

        int age = 16;

        try {
            if (age < 18) {
                throw new AgeException("Age must be 18 or above");
            }

            System.out.println("Eligible to vote");
        }
        catch (AgeException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
