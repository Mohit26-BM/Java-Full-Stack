package libraryMembership;

public abstract class LibraryMembership {

    private int memberId;
    private String memberName;

    /*
    Constructor
    */

    LibraryMembership(int memberId, String memberName)
            throws LibraryMembershipException {

        if (memberId <= 0) {
            throw new LibraryMembershipException(
                    "Member ID must be greater than 0");
        }

        if (memberName == null || memberName.trim().isEmpty()) {
            throw new LibraryMembershipException(
                    "Member name cannot be empty");
        }

        this.memberId = memberId;
        this.memberName = memberName;
    }

    /*
    Getters
    */

    public int getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    /*
    Abstract methods
    Each membership type provides its own rules.
    */

    public abstract double calculateSubscriptionCharge();

    public abstract double calculateOverduePenalty(int overdueDays) throws LibraryMembershipException;

    public abstract String getMembershipBenefit();

    /*
    Common method to display member information.
    */

    public void displayMembershipDetails() {

        System.out.println("Member ID: " + memberId);
        System.out.println("Member Name: " + memberName);
        System.out.println("Membership Type: "
                + getClass().getSimpleName());

        System.out.println("Subscription Charge: Rs. "
                + calculateSubscriptionCharge());

        System.out.println("Membership Benefit: "
                + getMembershipBenefit());
    }
}
