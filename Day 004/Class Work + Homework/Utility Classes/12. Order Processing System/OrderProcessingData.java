public class OrderProcessingData {

    String orderId;
    String customerName;
    String category;
    double amount;
    String status;

    OrderProcessingData(String orderId, String customerName,
                        String category, double amount, String status) {

        this.orderId = orderId;
        this.customerName = customerName;
        this.category = category;
        this.amount = amount;
        this.status = status;
    }
}