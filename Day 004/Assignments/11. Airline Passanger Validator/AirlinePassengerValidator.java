package airlinePassangerValidator;

public class AirlinePassengerValidator {

    /*
    Validates passenger name.
    */

    public static void validateName(String name)
            throws PassengerValidationException {

        if (name == null || name.isEmpty()) {

            throw new PassengerValidationException(
                    "Passenger name cannot be empty");
        }

        if (!name.matches("[a-zA-Z ]+")) {

            throw new PassengerValidationException(
                    "Passenger name can contain only letters and spaces");
        }
    }


    /*
    Validates passport number.
    Format:
    One uppercase letter followed by seven digits.
    Example: A1234567
    */

    public static void validatePassport(String passport)
            throws PassengerValidationException {

        if (passport == null || passport.isEmpty()) {

            throw new PassengerValidationException(
                    "Passport number cannot be empty");
        }

        if (!passport.matches("[A-Z][0-9]{7}")) {

            throw new PassengerValidationException(
                    "Passport number must contain one uppercase letter followed by 7 digits");
        }
    }


    /*
    Validates booking reference.
    It must contain exactly 6 uppercase letters or digits.
    */

    public static void validateBookingReference(String reference)
            throws PassengerValidationException {

        if (reference == null || reference.isEmpty()) {

            throw new PassengerValidationException(
                    "Booking reference cannot be empty");
        }

        if (!reference.matches("[A-Z0-9]{6}")) {

            throw new PassengerValidationException(
                    "Booking reference must contain exactly 6 uppercase letters or digits");
        }
    }


    /*
    Validates passenger age.
    */

    public static void validateAge(Integer age)
            throws PassengerValidationException {

        if (age == null) {

            throw new PassengerValidationException(
                    "Age cannot be null");
        }

        if (age < 1 || age > 120) {

            throw new PassengerValidationException(
                    "Age must be between 1 and 120");
        }
    }


    /*
    Validates all passenger information.
    */

    public static void validatePassenger(
            AirlinePassengerData passenger)
            throws PassengerValidationException {

        validateName(
                passenger.getPassengerName());

        validatePassport(
                passenger.getPassportNumber());

        validateBookingReference(
                passenger.getBookingReference());

        validateAge(
                passenger.getAge());

        System.out.println(
                "Passenger information is valid.");
    }


    /*
    Displays passenger information.
    */

    public static void displayPassenger(
            AirlinePassengerData passenger) {

        System.out.println(
                "Passenger Name: "
                + passenger.getPassengerName());

        System.out.println(
                "Passport Number: "
                + passenger.getPassportNumber());

        System.out.println(
                "Booking Reference: "
                + passenger.getBookingReference());

        System.out.println(
                "Age: "
                + passenger.getAge());
    }


    public static void main(String[] args) {

        try {

            /*
            Valid passenger.
            */

            AirlinePassengerData passenger1 =
                    new AirlinePassengerData(
                            "Ravi Kumar",
                            "A1234567",
                            "AB1234",
                            28
                    );

            System.out.println("PASSENGER 1");

            displayPassenger(passenger1);

            validatePassenger(passenger1);

            System.out.println("-------------------------");


            /*
            Another valid passenger.
            */

            AirlinePassengerData passenger2 =
                    new AirlinePassengerData(
                            "Priya Sharma",
                            "B7654321",
                            "XY7890",
                            32
                    );

            System.out.println();
            System.out.println("PASSENGER 2");

            displayPassenger(passenger2);

            validatePassenger(passenger2);

            System.out.println("-------------------------");


            /*
            Invalid name test.
            */

            System.out.println();
            System.out.println("INVALID NAME TEST");

            AirlinePassengerData passenger3 =
                    new AirlinePassengerData(
                            "Amit123",
                            "C1234567",
                            "CD1234",
                            25
                    );

            validatePassenger(passenger3);


        }
        catch (PassengerValidationException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }


        /*
        Invalid passport test.
        */

        try {

            System.out.println();
            System.out.println("INVALID PASSPORT TEST");

            AirlinePassengerData passenger4 =
                    new AirlinePassengerData(
                            "Neha Patel",
                            "12345678",
                            "EF5678",
                            30
                    );

            validatePassenger(passenger4);

        }
        catch (PassengerValidationException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }


        /*
        Invalid booking reference test.
        */

        try {

            System.out.println();
            System.out.println("INVALID BOOKING REFERENCE TEST");

            AirlinePassengerData passenger5 =
                    new AirlinePassengerData(
                            "Rahul Singh",
                            "D9876543",
                            "ABC12",
                            27
                    );

            validatePassenger(passenger5);

        }
        catch (PassengerValidationException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }


        /*
        Invalid age test.
        */

        try {

            System.out.println();
            System.out.println("INVALID AGE TEST");

            AirlinePassengerData passenger6 =
                    new AirlinePassengerData(
                            "Karan Mehta",
                            "E4567890",
                            "GH1234",
                            150
                    );

            validatePassenger(passenger6);

        }
        catch (PassengerValidationException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}
