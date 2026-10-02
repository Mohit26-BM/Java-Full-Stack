public class NetBankingPayment extends Payment implements Refundable {

    public NetBankingPayment(String transactionId, String customerName, double amount) {
        super(transactionId, customerName, amount);
    }

    @Override
    public void processPayment() {
        System.out.println("Authenticating bank account...");
        System.out.println("Processing net banking payment...");
        System.out.println("Net banking payment completed successfully.");
    }

    @Override
    public void processRefund() {
        System.out.println("Net banking refund processed successfully.");
    }
}