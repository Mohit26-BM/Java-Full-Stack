package libraryMembership;

public class LibraryMembershipSystem {

    public static void main(String[] args) {

        try {

            /*
            Creating different membership types.
            */

            LibraryMembership goldMember =
                    new GoldMembership(
                            101,
                            "Ravi"
                    );

            LibraryMembership silverMember =
                    new SilverMembership(
                            102,
                            "Priya"
                    );

            LibraryMembership platinumMember =
                    new PlatinumMembership(
                            103,
                            "Amit"
                    );


            /*
            Gold Membership
            */

            System.out.println("GOLD MEMBERSHIP");

            goldMember.displayMembershipDetails();

            System.out.println(
                    "Overdue Penalty for 5 days: Rs. "
                    + goldMember.calculateOverduePenalty(5)
            );

            System.out.println("-------------------------");


            /*
            Silver Membership
            */

            System.out.println();
            System.out.println("SILVER MEMBERSHIP");

            silverMember.displayMembershipDetails();

            System.out.println(
                    "Overdue Penalty for 5 days: Rs. "
                    + silverMember.calculateOverduePenalty(5)
            );

            System.out.println("-------------------------");


            /*
            Platinum Membership
            */

            System.out.println();
            System.out.println("PLATINUM MEMBERSHIP");

            platinumMember.displayMembershipDetails();

            System.out.println(
                    "Overdue Penalty for 5 days: Rs. "
                    + platinumMember.calculateOverduePenalty(5)
            );

            System.out.println("-------------------------");


            /*
            Invalid overdue days test
            */

            System.out.println();
            System.out.println("INVALID INPUT TEST");

            goldMember.calculateOverduePenalty(-5);

        }
        catch (LibraryMembershipException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
    }
}