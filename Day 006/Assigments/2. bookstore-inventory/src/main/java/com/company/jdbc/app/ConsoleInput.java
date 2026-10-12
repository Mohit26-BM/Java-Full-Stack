package com.company.jdbc.app;

import java.math.BigDecimal;
import java.util.Scanner;

/*
Keyboard input helpers.

These only check the FORMAT of what is typed (is it a number?).
Business rules (price > 0, stock not negative ...) are checked
in the service layer.

Methods named readOptional... return null when the user just
presses Enter.
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

            Integer value = parseInt(readLine(message));

            if (value != null && value > 0) {
                return value;
            }

            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    public static int readNonNegativeInt(String message) {

        while (true) {

            Integer value = parseInt(readLine(message));

            if (value != null && value >= 0) {
                return value;
            }

            System.out.println("Please enter a whole number (0 or more).");
        }
    }

    public static Integer readOptionalPositiveInt(String message) {

        while (true) {

            String input = readLine(message);

            if (input.isEmpty()) {
                return null;
            }

            Integer value = parseInt(input);

            if (value != null && value > 0) {
                return value;
            }

            System.out.println(
                    "Please enter a whole number greater than 0, "
                    + "or press Enter to skip."
            );
        }
    }

    public static BigDecimal readMoney(String message) {

        while (true) {

            BigDecimal value = parseMoney(readLine(message));

            if (value != null) {
                return value;
            }

            System.out.println("Please enter a valid amount, e.g. 499.50");
        }
    }

    public static BigDecimal readOptionalMoney(String message) {

        while (true) {

            String input = readLine(message);

            if (input.isEmpty()) {
                return null;
            }

            BigDecimal value = parseMoney(input);

            if (value != null) {
                return value;
            }

            System.out.println(
                    "Please enter a valid amount, or press Enter to skip."
            );
        }
    }

    public static boolean readYes(String message) {

        return readLine(message).equalsIgnoreCase("y");
    }

    private static Integer parseInt(String input) {

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static BigDecimal parseMoney(String input) {

        try {
            return new BigDecimal(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
