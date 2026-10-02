public class UPIPayment extends Payment implements Refundable, QRPayable {

    public UPIPayment(String transactionId, String customerName, double amount) {
        super(transactionId, customerName, amount);
    }

    @Override
    public void processPayment() {
        System.out.println("Processing UPI payment...");
        System.out.println("UPI payment completed successfully.");
    }

    @Override
    public void processRefund() {
        System.out.println("UPI refund processed successfully.");
    }

    @Override
    public void generateQRCode() {
        System.out.println("UPI QR code generated successfully.");
    }
}