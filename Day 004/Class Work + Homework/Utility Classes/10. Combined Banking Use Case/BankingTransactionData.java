public class BankingTransactionData {

    String transactionId;
    String accountNumber;
    String type;
    double amount;
    String status;

    BankingTransactionData(String transactionId, String accountNumber,
                           String type, double amount, String status) {

        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.status = status;
    }
}
