package airlinePassangerValidator;

public class AirlinePassengerData {

    private String passengerName;
    private String passportNumber;
    private String bookingReference;
    private Integer age;

    /*
    Constructor
    */

    AirlinePassengerData(String passengerName,
                         String passportNumber,
                         String bookingReference,
                         Integer age) {

        this.passengerName = passengerName;
        this.passportNumber = passportNumber;
        this.bookingReference = bookingReference;
        this.age = age;
    }

    /*
    Getters
    */

    public String getPassengerName() {
        return passengerName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public Integer getAge() {
        return age;
    }
}