package ticketReservationSystem;

public class TicketReservationSystem {

    /*
    Maximum number of seats.
    */

    private static final int TOTAL_SEATS = 5;


    /*
    Checks whether a particular seat is already booked.
    Since there is no List, the demo will pass existing
    reservations individually.
    */

    public boolean isSeatBooked(
            int seatNumber,
            TicketReservation ticket1,
            TicketReservation ticket2,
            TicketReservation ticket3,
            TicketReservation ticket4,
            TicketReservation ticket5) {

        return isSameBookedSeat(seatNumber, ticket1)
                || isSameBookedSeat(seatNumber, ticket2)
                || isSameBookedSeat(seatNumber, ticket3)
                || isSameBookedSeat(seatNumber, ticket4)
                || isSameBookedSeat(seatNumber, ticket5);
    }


    /*
    Helper method for checking a booked seat.
    */

    private boolean isSameBookedSeat(
            int seatNumber,
            TicketReservation ticket) {

        return ticket != null
                && ticket.isBooked()
                && ticket.getSeatNumber() == seatNumber;
    }


    /*
    Overloaded booking method that checks all existing
    reservations.
    */

    public void bookTicket(
            TicketReservation ticket,
            TicketReservation ticket1,
            TicketReservation ticket2,
            TicketReservation ticket3,
            TicketReservation ticket4,
            TicketReservation ticket5)
            throws TicketReservationException {

        if (ticket.isBooked()) {
            throw new TicketReservationException(
                    "Ticket is already booked");
        }

        if (isSeatBooked(
                ticket.getSeatNumber(),
                ticket1,
                ticket2,
                ticket3,
                ticket4,
                ticket5)) {

            throw new TicketReservationException(
                    "Seat " + ticket.getSeatNumber()
                    + " is already booked");
        }

        ticket.setBooked(true);

        System.out.println(
                "Ticket " + ticket.getTicketId()
                + " booked successfully.");
    }


    /*
    Cancels a ticket.
    */

    public void cancelTicket(TicketReservation ticket)
            throws TicketReservationException {

        if (!ticket.isBooked()) {
            throw new TicketReservationException(
                    "Ticket is not currently booked");
        }

        ticket.setBooked(false);

        System.out.println(
                "Ticket " + ticket.getTicketId()
                + " cancelled successfully.");
    }


    /*
    Displays available seats.
    */

    public void displayAvailableSeats(
            TicketReservation ticket1,
            TicketReservation ticket2,
            TicketReservation ticket3,
            TicketReservation ticket4,
            TicketReservation ticket5) {

        System.out.println("AVAILABLE SEATS");

        for (int seat = 1; seat <= TOTAL_SEATS; seat++) {

            if (!isSeatBooked(
                    seat,
                    ticket1,
                    ticket2,
                    ticket3,
                    ticket4,
                    ticket5)) {

                System.out.println("Seat " + seat);
            }
        }
    }
}