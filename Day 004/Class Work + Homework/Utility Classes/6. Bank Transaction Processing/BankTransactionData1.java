public class BankTransactionData1 {

    String transactionId;
    String accountNumber;
    double amount;
    String status;

    BankTransactionData1(String transactionId, String accountNumber,
                        double amount, String status) {

        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.status = status;
    }
}