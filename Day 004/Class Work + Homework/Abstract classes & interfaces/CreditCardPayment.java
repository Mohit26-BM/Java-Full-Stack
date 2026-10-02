public class CreditCardPayment extends Payment implements Refundable, Rewardable {

    public CreditCardPayment(String transactionId, String customerName, double amount) {
        super(transactionId, customerName, amount);
    }

    @Override
    public void processPayment() {
        System.out.println("Processing credit card payment...");
        System.out.println("Credit card payment completed successfully.");
    }

    @Override
    public void processRefund() {
        System.out.println("Credit card refund processed successfully.");
    }

    @Override
    public int calculateRewardPoints() {
        return (int) (getAmount() / 100);
    }
}