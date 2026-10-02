package libraryMembership;

public class GoldMembership extends LibraryMembership {

    private static final double SUBSCRIPTION_CHARGE = 1000;
    private static final double PENALTY_PER_DAY = 20;

    GoldMembership(int memberId, String memberName)
            throws LibraryMembershipException {

        super(memberId, memberName);
    }

    /*
    Gold membership subscription charge.
    */

    @Override
    public double calculateSubscriptionCharge() {
        return SUBSCRIPTION_CHARGE;
    }

    /*
    Gold membership overdue penalty.
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
    Gold membership benefit.
    */

    @Override
    public String getMembershipBenefit() {
        return "Can borrow up to 5 books";
    }
}