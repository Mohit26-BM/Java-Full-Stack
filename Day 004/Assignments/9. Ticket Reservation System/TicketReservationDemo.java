package ticketReservationSystem;

public class TicketReservationDemo {

    public static void main(String[] args) {

        try {

            /*
            Creating reservation objects.
            */

            TicketReservation ticket1 =
                    new TicketReservation(
                            "TKT101",
                            "Ravi",
                            "Mumbai",
                            "Pune",
                            1
                    );

            TicketReservation ticket2 =
                    new TicketReservation(
                            "TKT102",
                            "Priya",
                            "Mumbai",
                            "Nashik",
                            2
                    );

            TicketReservation ticket3 =
                    new TicketReservation(
                            "TKT103",
                            "Amit",
                            "Mumbai",
                            "Delhi",
                            3
                    );

            TicketReservation ticket4 =
                    new TicketReservation(
                            "TKT104",
                            "Neha",
                            "Mumbai",
                            "Goa",
                            4
                    );

            TicketReservation ticket5 =
                    new TicketReservation(
                            "TKT105",
                            "Karan",
                            "Mumbai",
                            "Surat",
                            5
                    );


            TicketReservationSystem system =
                    new TicketReservationSystem();


            /*
            Booking tickets.
            */

            System.out.println("BOOKING TICKETS");

            system.bookTicket(
                    ticket1,
                    null, null, null, null, null
            );

            system.bookTicket(
                    ticket2,
                    ticket1, null, null, null, null
            );

            system.bookTicket(
                    ticket3,
                    ticket1, ticket2, null, null, null
            );

            system.bookTicket(
                    ticket4,
                    ticket1, ticket2, ticket3, null, null
            );


            /*
            Display ticket details.
            */

            System.out.println();
            System.out.println("TICKET DETAILS");

            ticket1.displayTicket();

            System.out.println("-------------------------");

            ticket2.displayTicket();

            System.out.println("-------------------------");

            ticket3.displayTicket();

            System.out.println("-------------------------");


            /*
            Display available seats.
            */

            System.out.println();

            system.displayAvailableSeats(
                    ticket1,
                    ticket2,
                    ticket3,
                    ticket4,
                    ticket5
            );


            /*
            Cancellation.
            */

            System.out.println();
            System.out.println("CANCELLATION");

            system.cancelTicket(ticket2);


            /*
            Seat 2 should now be available.
            */

            System.out.println();

            system.displayAvailableSeats(
                    ticket1,
                    ticket2,
                    ticket3,
                    ticket4,
                    ticket5
            );


            /*
            Negative test:
            Try to book ticket5 using an already
            booked seat.
            */

            System.out.println();
            System.out.println("INVALID BOOKING TEST");

            TicketReservation duplicateSeatTicket =
                    new TicketReservation(
                            "TKT106",
                            "Anjali",
                            "Mumbai",
                            "Pune",
                            1
                    );

            system.bookTicket(
                    duplicateSeatTicket,
                    ticket1,
                    ticket2,
                    ticket3,
                    ticket4,
                    ticket5
            );

        }
        catch (TicketReservationException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}