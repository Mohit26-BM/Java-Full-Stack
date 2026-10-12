package com.company.jdbc.app;

import java.math.BigDecimal;
import java.util.Scanner;

/*
Keyboard input helpers.

These only check the FORMAT of what is typed (is it a number?).
Business rules (stock available, price > 0 ...) are checked
in the service layer.

readOptional... methods return null when the user presses Enter.
*/
public final class ConsoleInput {

    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleInput() {
    }

    public static String readLine(String message) {

        System.out.print(message);

        return scanner.nextLine().trim();
    }

    public static int readPositiveInt(String message) {

        while (true) {

            Long value = parseLong(readLine(message));

            if (value != null && value > 0 && value <= Integer.MAX_VALUE) {
                return value.intValue();
            }

            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    public static int readNonNegativeInt(String message) {

        while (true) {

            Long value = parseLong(readLine(message));

            if (value != null && value >= 0 && value <= Integer.MAX_VALUE) {
                return value.intValue();
            }

            System.out.println("Please enter a whole number (0 or more).");
        }
    }

    public static long readPositiveLong(String message) {

        while (true) {

            Long value = parseLong(readLine(message));

            if (value != null && value > 0) {
                return value;
            }

            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    public static Long readOptionalPositiveLong(String message) {

        while (true) {

            String input = readLine(message);

            if (input.isEmpty()) {
                return null;
            }

            Long value = parseLong(input);

            if (value != null && value > 0) {
                return value;
            }

            System.out.println(
                    "Please enter a whole number greater than 0, "
                    + "or press Enter to finish."
            );
        }
    }

    public static BigDecimal readMoney(String message) {

        while (true) {

            String input = readLine(message);

            try {

                return new BigDecimal(input);

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid amount, e.g. 799.50");
            }
        }
    }

    public static BigDecimal readOptionalMoney(String message) {

        while (true) {

            String input = readLine(message);

            if (input.isEmpty()) {
                return null;
            }

            try {

                return new BigDecimal(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid amount, or press Enter to skip.");
            }
        }
    }

    public static boolean readYes(String message) {

        return readLine(message).equalsIgnoreCase("y");
    }

    private static Long parseLong(String input) {

        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
