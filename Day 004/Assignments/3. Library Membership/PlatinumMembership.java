package libraryMembership;

public class PlatinumMembership extends LibraryMembership {

    private static final double SUBSCRIPTION_CHARGE = 1500;
    private static final double PENALTY_PER_DAY = 10;

    PlatinumMembership(int memberId, String memberName)
            throws LibraryMembershipException {

        super(memberId, memberName);
    }

    /*
    Platinum membership subscription charge.
    */

    @Override
    public double calculateSubscriptionCharge() {
        return SUBSCRIPTION_CHARGE;
    }

    /*
    Platinum membership overdue penalty.
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
    Platinum membership benefit.
    */

    @Override
    public String getMembershipBenefit() {
        return "Can borrow up to 10 books";
    }
}