package password;

public class PasswordValidationEngine {

    /*
    Validates the password against all required rules.
    */

    public static void validatePassword(String password)
            throws PasswordValidationException {

        /*
        Check for null or empty password
        */

        if (password == null || password.isEmpty()) {
            throw new PasswordValidationException(
                    "Password cannot be empty");
        }


        /*
        Check minimum length
        */

        if (password.length() < 8) {
            throw new PasswordValidationException(
                    "Password must contain at least 8 characters");
        }


        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialCharacter = false;


        /*
        Check every character in the password
        */

        for (int i = 0; i < password.length(); i++) {

            char ch = password.charAt(i);

            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            }

            else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            }

            else if (Character.isDigit(ch)) {
                hasDigit = true;
            }

            else {
                hasSpecialCharacter = true;
            }
        }


        /*
        Check uppercase requirement
        */

        if (!hasUppercase) {
            throw new PasswordValidationException(
                    "Password must contain at least one uppercase letter");
        }


        /*
        Check lowercase requirement
        */

        if (!hasLowercase) {
            throw new PasswordValidationException(
                    "Password must contain at least one lowercase letter");
        }


        /*
        Check digit requirement
        */

        if (!hasDigit) {
            throw new PasswordValidationException(
                    "Password must contain at least one digit");
        }


        /*
        Check special character requirement
        */

        if (!hasSpecialCharacter) {
            throw new PasswordValidationException(
                    "Password must contain at least one special character");
        }


        /*
        If all rules pass
        */

        System.out.println("Password is valid");
    }


    public static void main(String[] args) {

        /*
        Positive test case
        */

        System.out.println("TEST 1");

        try {

            validatePassword("Ravi@123");

        }
        catch (PasswordValidationException e) {

            System.out.println("Error: " + e.getMessage());
        }


        /*
        Negative test case: no uppercase letter
        */

        System.out.println();
        System.out.println("TEST 2");

        try {

            validatePassword("ravi@123");

        }
        catch (PasswordValidationException e) {

            System.out.println("Error: " + e.getMessage());
        }


        /*
        Negative test case: no digit
        */

        System.out.println();
        System.out.println("TEST 3");

        try {

            validatePassword("Ravi@abcd");

        }
        catch (PasswordValidationException e) {

            System.out.println("Error: " + e.getMessage());
        }


        /*
        Negative test case: password too short
        */

        System.out.println();
        System.out.println("TEST 4");

        try {

            validatePassword("Ra@123");

        }
        catch (PasswordValidationException e) {

            System.out.println("Error: " + e.getMessage());
        }


        /*
        Negative test case: no special character
        */

        System.out.println();
        System.out.println("TEST 5");

        try {

            validatePassword("Ravi1234");

        }
        catch (PasswordValidationException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }
}