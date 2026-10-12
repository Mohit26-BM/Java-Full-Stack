package com.company.jdbc.app;

import java.math.BigDecimal;
import java.util.Scanner;

/*
Keyboard input helpers. They only check the FORMAT of what is typed.
Business rules (rating 1-5, period format ...) are checked in the service.
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

    public static BigDecimal readDecimal(String message) {

        while (true) {

            String input = readLine(message);

            try {

                return new BigDecimal(input);

            } catch (NumberFormatException e) {

                System.out.println("Please enter a number, e.g. 4.25");
            }
        }
    }
}
