import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CombinedBankingUseCase {

    public static void main(String[] args) {

        /*
        Creating 10 bank transactions
        */

        BankingTransactionData transaction1 =
                new BankingTransactionData(
                        "TXN101", "ACC1001", "TRANSFER",
                        75000, "SUCCESS");

        BankingTransactionData transaction2 =
                new BankingTransactionData(
                        "TXN102", "ACC1002", "DEPOSIT",
                        25000, "SUCCESS");

        BankingTransactionData transaction3 =
                new BankingTransactionData(
                        "TXN103", "ACC1003", "TRANSFER",
                        90000, "SUCCESS");

        BankingTransactionData transaction4 =
                new BankingTransactionData(
                        "TXN104", "ACC1004", "WITHDRAWAL",
                        15000, "SUCCESS");

        BankingTransactionData transaction5 =
                new BankingTransactionData(
                        "TXN105", "ACC1005", "TRANSFER",
                        60000, "PENDING");

        BankingTransactionData transaction6 =
                new BankingTransactionData(
                        "TXN106", "ACC1006", "TRANSFER",
                        45000, "SUCCESS");

        BankingTransactionData transaction7 =
                new BankingTransactionData(
                        "TXN107", "ACC1007", "DEPOSIT",
                        80000, "SUCCESS");

        BankingTransactionData transaction8 =
                new BankingTransactionData(
                        "TXN108", "ACC1008", "TRANSFER",
                        55000, "FAILED");

        BankingTransactionData transaction9 =
                new BankingTransactionData(
                        "TXN109", "ACC1009", "TRANSFER",
                        65000, "SUCCESS");

        BankingTransactionData transaction10 =
                new BankingTransactionData(
                        "TXN110", "ACC1010", "WITHDRAWAL",
                        70000, "PENDING");


        /*
        Predicate 1
        Identifies successful transactions.
        */

        Predicate<BankingTransactionData> successfulTransaction =
                transaction -> transaction.status.equals("SUCCESS");


        /*
        Predicate 2
        Identifies high-value transactions above 50000.
        */

        Predicate<BankingTransactionData> highValueTransaction =
                transaction -> transaction.amount > 50000;


        /*
        Predicate 3
        Identifies transfer transactions.
        */

        Predicate<BankingTransactionData> transferTransaction =
                transaction -> transaction.type.equals("TRANSFER");


        /*
        Predicate 4
        Identifies pending transactions.
        */

        Predicate<BankingTransactionData> pendingTransaction =
                transaction -> transaction.status.equals("PENDING");


        /*
        Combining predicates to identify
        successful high-value transfers.
        */

        Predicate<BankingTransactionData> successfulHighValueTransfer =
                successfulTransaction
                .and(highValueTransaction)
                .and(transferTransaction);


        /*
        Function 1
        Transaction -> Transaction ID
        */

        Function<BankingTransactionData, String> transactionId =
                transaction -> transaction.transactionId;


        /*
        Function 2
        Transaction -> Amount
        */

        Function<BankingTransactionData, Double> transactionAmount =
                transaction -> transaction.amount;


        /*
        Function 3
        Transaction -> Transaction Description
        */

        Function<BankingTransactionData, String> transactionDescription =
                transaction -> transaction.type
                        + " of Rs. "
                        + transaction.amount;


        /*
        Consumer
        Prints the audit message.
        */

        Consumer<BankingTransactionData> auditConsumer =
                transaction -> {

                    System.out.println("AUDIT:");
                    System.out.println(
                            "Transaction "
                            + transaction.transactionId
                            + " processed successfully."
                    );
                    System.out.println();
                };


        /*
        Supplier
        Generates transaction IDs starting from TXN1001.
        */

        AtomicInteger transactionNumber =
                new AtomicInteger(1001);

        Supplier<String> transactionIdSupplier =
                () -> "TXN" + transactionNumber.getAndIncrement();


        /*
        Supplier demonstration
        */

        System.out.println("GENERATED TRANSACTION IDs");

        System.out.println(transactionIdSupplier.get());
        System.out.println(transactionIdSupplier.get());
        System.out.println(transactionIdSupplier.get());

        System.out.println();


        /*
        Processing Pipeline
        Predicate -> Function -> Consumer
        */

        System.out.println("SUCCESSFUL HIGH-VALUE TRANSFERS");

        if (successfulHighValueTransfer.test(transaction1)) {

            System.out.println(
                    transactionId.apply(transaction1)
                    + " -> "
                    + transactionDescription.apply(transaction1));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction1));

            auditConsumer.accept(transaction1);
        }


        if (successfulHighValueTransfer.test(transaction2)) {

            System.out.println(
                    transactionId.apply(transaction2)
                    + " -> "
                    + transactionDescription.apply(transaction2));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction2));

            auditConsumer.accept(transaction2);
        }


        if (successfulHighValueTransfer.test(transaction3)) {

            System.out.println(
                    transactionId.apply(transaction3)
                    + " -> "
                    + transactionDescription.apply(transaction3));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction3));

            auditConsumer.accept(transaction3);
        }


        if (successfulHighValueTransfer.test(transaction4)) {

            System.out.println(
                    transactionId.apply(transaction4)
                    + " -> "
                    + transactionDescription.apply(transaction4));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction4));

            auditConsumer.accept(transaction4);
        }


        if (successfulHighValueTransfer.test(transaction5)) {

            System.out.println(
                    transactionId.apply(transaction5)
                    + " -> "
                    + transactionDescription.apply(transaction5));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction5));

            auditConsumer.accept(transaction5);
        }


        if (successfulHighValueTransfer.test(transaction6)) {

            System.out.println(
                    transactionId.apply(transaction6)
                    + " -> "
                    + transactionDescription.apply(transaction6));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction6));

            auditConsumer.accept(transaction6);
        }


        if (successfulHighValueTransfer.test(transaction7)) {

            System.out.println(
                    transactionId.apply(transaction7)
                    + " -> "
                    + transactionDescription.apply(transaction7));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction7));

            auditConsumer.accept(transaction7);
        }


        if (successfulHighValueTransfer.test(transaction8)) {

            System.out.println(
                    transactionId.apply(transaction8)
                    + " -> "
                    + transactionDescription.apply(transaction8));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction8));

            auditConsumer.accept(transaction8);
        }


        if (successfulHighValueTransfer.test(transaction9)) {

            System.out.println(
                    transactionId.apply(transaction9)
                    + " -> "
                    + transactionDescription.apply(transaction9));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction9));

            auditConsumer.accept(transaction9);
        }


        if (successfulHighValueTransfer.test(transaction10)) {

            System.out.println(
                    transactionId.apply(transaction10)
                    + " -> "
                    + transactionDescription.apply(transaction10));

            System.out.println(
                    "Amount: Rs. "
                    + transactionAmount.apply(transaction10));

            auditConsumer.accept(transaction10);
        }
    }
}