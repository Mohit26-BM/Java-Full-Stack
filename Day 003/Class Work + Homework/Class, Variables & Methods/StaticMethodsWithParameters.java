class MathUtil {

    static int square(int number) {
        return number * number;
    }

    static int cube(int number) {
        return number * number * number;
    }

    static boolean isEven(int number) {
        return number % 2 == 0;
    }

    static int findMaximum(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }
}

public class StaticMethodsWithParameters {

    public static void main(String[] args) {

        int number = 5;

        int squareResult = MathUtil.square(number);
        int cubeResult = MathUtil.cube(number);
        boolean evenResult = MathUtil.isEven(number);
        int maximumResult = MathUtil.findMaximum(25, 40);

        System.out.println("Square of " + number + ": " + squareResult);
        System.out.println("Cube of " + number + ": " + cubeResult);
        System.out.println("Is " + number + " even? " + evenResult);
        System.out.println("Maximum: " + maximumResult);
    }
}