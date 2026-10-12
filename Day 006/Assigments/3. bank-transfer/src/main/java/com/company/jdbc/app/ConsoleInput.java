package com.company.jdbc.app;

import java.math.BigDecimal;
import java.util.Scanner;

/*
Keyboard input helpers.

These only check the FORMAT of what is typed (is it a number?).
Business rules (amount > 0, ACTIVE account ...) are checked
in the service layer.
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

            String input = readLine(message);

            try {

                int value = Integer.parseInt(input);

                if (value > 0) {
                    return value;
                }

            } catch (NumberFormatException e) {
                // fall through to the message below
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

                System.out.println("Please enter a valid amount, e.g. 2500.50");
            }
        }
    }

    public static boolean readYes(String message) {

        return readLine(message).equalsIgnoreCase("y");
    }
}
