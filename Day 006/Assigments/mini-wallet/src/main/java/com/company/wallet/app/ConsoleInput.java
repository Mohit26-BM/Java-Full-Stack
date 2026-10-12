package com.company.wallet.app;

import java.math.BigDecimal;
import java.util.Scanner;

/*
Keyboard input helpers. They only check the FORMAT of what is typed;
business rules live in the service layer.
*/
public final class ConsoleInput {

    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleInput() {
    }

    public static String readLine(String message) {

        System.out.print(message);

        return scanner.nextLine().trim();
    }

    public static long readPositiveLong(String message) {

        while (true) {

            String input = readLine(message);

            try {

                long value = Long.parseLong(input);

                if (value > 0) {
                    return value;
                }

            } catch (NumberFormatException e) {
                // fall through
            }

            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    public static int readPositiveInt(String message) {
        return (int) Math.min(Integer.MAX_VALUE, readPositiveLong(message));
    }

    /*
    Returns defaultValue when the user just presses Enter.
    */
    public static int readPositiveInt(String message, int defaultValue) {

        while (true) {

            String input = readLine(message);

            if (input.isEmpty()) {
                return defaultValue;
            }

            try {

                int value = Integer.parseInt(input);

                if (value > 0) {
                    return value;
                }

            } catch (NumberFormatException e) {
                // fall through
            }

            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    public static BigDecimal readMoney(String message) {

        while (true) {

            String input = readLine(message);

            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter an amount, e.g. 1500.50");
            }
        }
    }

    public static boolean readYes(String message) {
        return readLine(message).equalsIgnoreCase("y");
    }
}
