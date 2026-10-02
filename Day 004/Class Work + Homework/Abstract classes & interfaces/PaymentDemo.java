public class PaymentDemo {

    public static void main(String[] args) {

        Payment p1 = new CreditCardPayment("TXN101", "Ravi", 5000);
        Payment p2 = new UPIPayment("TXN102", "Priya", 2500);
        Payment p3 = new NetBankingPayment("TXN103", "Arun", 7500);

        System.out.println("Credit Card Payment:");
        p1.displayTransactionDetails();
        p1.processPayment();

        System.out.println();

        System.out.println("UPI Payment:");
        p2.displayTransactionDetails();
        p2.processPayment();

        System.out.println();

        System.out.println("Net Banking Payment:");
        p3.displayTransactionDetails();
        p3.processPayment();
        
        Rewardable rewardable = new CreditCardPayment("TXN104", "Kiran", 10000);

        System.out.println();
        System.out.println("Reward Points:");
        System.out.println("Reward points earned: " + rewardable.calculateRewardPoints());


        QRPayable qrPayment = new UPIPayment("TXN105", "Meena", 3000);

        System.out.println();
        System.out.println("QR Payment:");
        qrPayment.generateQRCode();
    }
}