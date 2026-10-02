package libraryMembership;

public class SilverMembership extends LibraryMembership {

    private static final double SUBSCRIPTION_CHARGE = 700;
    private static final double PENALTY_PER_DAY = 15;

    SilverMembership(int memberId, String memberName)
            throws LibraryMembershipException {

        super(memberId, memberName);
    }

    /*
    Silver membership subscription charge.
    */

    @Override
    public double calculateSubscriptionCharge() {
        return SUBSCRIPTION_CHARGE;
    }

    /*
    Silver membership overdue penalty.
    */

    @Override
    public double calculateOverduePenalty(int overdueDays)
            throws LibraryMembershipException {

        if (overdueDays < 0) {
            throw new LibraryMembershipException(
                    "Overdue days cannot be negative");
        }

        return overdueDays * PENALTY_PER_DAY;
    }

    /*
    Silver membership benefit.
    */

    @Override
    public String getMembershipBenefit() {
        return "Can borrow up to 3 books";
    }
}