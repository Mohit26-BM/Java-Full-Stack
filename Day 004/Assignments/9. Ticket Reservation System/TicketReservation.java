package ticketReservationSystem;

public class TicketReservation {

    private String ticketId;
    private String passengerName;
    private String source;
    private String destination;
    private int seatNumber;
    private boolean booked;

    /*
    Constructor
    */

    TicketReservation(String ticketId, String passengerName,
                      String source, String destination,
                      int seatNumber)
            throws TicketReservationException {

        if (ticketId == null || ticketId.trim().isEmpty()) {
            throw new TicketReservationException(
                    "Ticket ID cannot be empty");
        }

        if (passengerName == null
                || passengerName.trim().isEmpty()) {

            throw new TicketReservationException(
                    "Passenger name cannot be empty");
        }

        if (source == null || source.trim().isEmpty()) {
            throw new TicketReservationException(
                    "Source cannot be empty");
        }

        if (destination == null
                || destination.trim().isEmpty()) {

            throw new TicketReservationException(
                    "Destination cannot be empty");
        }

        if (source.equalsIgnoreCase(destination)) {
            throw new TicketReservationException(
                    "Source and destination cannot be the same");
        }

        if (seatNumber < 1 || seatNumber > 5) {
            throw new TicketReservationException(
                    "Seat number must be between 1 and 5");
        }

        this.ticketId = ticketId;
        this.passengerName = passengerName;
        this.source = source;
        this.destination = destination;
        this.seatNumber = seatNumber;
        this.booked = false;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    /*
    Display reservation details
    */

    public void displayTicket() {

        System.out.println("Ticket ID: " + ticketId);
        System.out.println("Passenger: " + passengerName);
        System.out.println("Source: " + source);
        System.out.println("Destination: " + destination);
        System.out.println("Seat Number: " + seatNumber);

        if (booked) {
            System.out.println("Status: BOOKED");
        }
        else {
            System.out.println("Status: CANCELLED");
        }
    }
}