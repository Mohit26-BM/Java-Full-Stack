public class BankTransactionData {

    int transactionId;
    String accountNumber;
    String transactionType;
    double amount;
    String status;

    BankTransactionData(int transactionId, String accountNumber,
                        String transactionType, double amount,
                        String status) {

        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
    }

    void displayTransaction() {
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Transaction Type: " + transactionType);
        System.out.println("Amount: " + amount);
        System.out.println("Status: " + status);
        System.out.println();
    }
}