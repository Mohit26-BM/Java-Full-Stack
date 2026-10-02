public abstract class Payment {

    private String transactionId;
    private String customerName;
    private double amount;

    public Payment(String transactionId, String customerName, double amount) {
        this.amount = amount;
        this.transactionId = transactionId;
        this.customerName = customerName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }
    public void displayTransactionDetails()
    {
    	System.out.println("The transaction ID is: " + getTransactionId());
    	System.out.println("The Customer name is: " + getCustomerName());
    	System.out.println("The amount is: " + getAmount());
    }
    public abstract void processPayment();
}
