import java.util.function.Consumer;

public class BankTransactionProcessing {

    public static void main(String[] args) {

        BankTransactionData1 transaction1 =
                new BankTransactionData1("TXN101", "ACC1001", 5000, "SUCCESS");

        BankTransactionData1 transaction2 =
                new BankTransactionData1("TXN102", "ACC1002", 12000, "SUCCESS");

        BankTransactionData1 transaction3 =
                new BankTransactionData1("TXN103", "ACC1003", 3000, "FAILED");

        BankTransactionData1 transaction4 =
                new BankTransactionData1("TXN104", "ACC1004", 7500, "SUCCESS");


        /*
        Consumer to display transaction details
        */

        Consumer<BankTransactionData1> displayTransaction =
                transaction -> {

                    System.out.println("Transaction: "
                            + transaction.transactionId);

                    System.out.println("Account: "
                            + transaction.accountNumber);

                    System.out.println("Amount: "
                            + transaction.amount);

                    System.out.println("Status: "
                            + transaction.status);

                    System.out.println("-------------------------");
                };


        /*
        Consumer for audit message
        */

        Consumer<BankTransactionData1> auditConsumer =
                transaction -> {

                    System.out.println(
                            "Audit: Transaction "
                            + transaction.transactionId
                            + " has been recorded."
                    );
                };


        /*
        Consumer for notification
        */

        Consumer<BankTransactionData1> notificationConsumer =
                transaction -> {

                    System.out.println(
                            "Notification: Account "
                            + transaction.accountNumber
                            + " transaction status is "
                            + transaction.status
                    );
                };


        /*
        Chain all Consumers using andThen()
        */

        Consumer<BankTransactionData1> transactionProcessing =
                displayTransaction
                .andThen(auditConsumer)
                .andThen(notificationConsumer);


        /*
        Process each transaction
        */

        transactionProcessing.accept(transaction1);
        System.out.println();

        transactionProcessing.accept(transaction2);
        System.out.println();

        transactionProcessing.accept(transaction3);
        System.out.println();

        transactionProcessing.accept(transaction4);
    }
}