import java.util.function.Predicate;

public class BankTransactionValidation {

    public static void main(String[] args) {

        BankTransactionData t1 =
                new BankTransactionData(
                        101, "ACC1001",
                        "DEPOSIT", 15000, "SUCCESS");

        BankTransactionData t2 =
                new BankTransactionData(
                        102, "ACC1002",
                        "WITHDRAWAL", 5000, "SUCCESS");

        BankTransactionData t3 =
                new BankTransactionData(
                        103, "ACC1003",
                        "TRANSFER", 20000, "SUCCESS");

        BankTransactionData t4 =
                new BankTransactionData(
                        104, "ACC1004",
                        "TRANSFER", 8000, "FAILED");

        BankTransactionData t5 =
                new BankTransactionData(
                        105, "ACC1005",
                        "DEPOSIT", 12000, "FAILED");

        BankTransactionData t6 =
                new BankTransactionData(
                        106, "ACC1006",
                        "TRANSFER", 15000, "PENDING");


        Predicate<BankTransactionData> successfulTransaction =
                t -> t.status.equals("SUCCESS");

        Predicate<BankTransactionData> highValueTransaction =
                t -> t.amount > 10000;

        Predicate<BankTransactionData> transferTransaction =
                t -> t.transactionType.equals("TRANSFER");

        Predicate<BankTransactionData> failedTransaction =
                t -> t.status.equals("FAILED");


        System.out.println("1. Successful Transactions");

        if (successfulTransaction.test(t1))
            t1.displayTransaction();

        if (successfulTransaction.test(t2))
            t2.displayTransaction();

        if (successfulTransaction.test(t3))
            t3.displayTransaction();

        if (successfulTransaction.test(t4))
            t4.displayTransaction();

        if (successfulTransaction.test(t5))
            t5.displayTransaction();

        if (successfulTransaction.test(t6))
            t6.displayTransaction();


        System.out.println("2. Transactions Greater Than 10000");

        if (highValueTransaction.test(t1))
            t1.displayTransaction();

        if (highValueTransaction.test(t2))
            t2.displayTransaction();

        if (highValueTransaction.test(t3))
            t3.displayTransaction();

        if (highValueTransaction.test(t4))
            t4.displayTransaction();

        if (highValueTransaction.test(t5))
            t5.displayTransaction();

        if (highValueTransaction.test(t6))
            t6.displayTransaction();


        System.out.println("3. Transfer Transactions");

        if (transferTransaction.test(t1))
            t1.displayTransaction();

        if (transferTransaction.test(t2))
            t2.displayTransaction();

        if (transferTransaction.test(t3))
            t3.displayTransaction();

        if (transferTransaction.test(t4))
            t4.displayTransaction();

        if (transferTransaction.test(t5))
            t5.displayTransaction();

        if (transferTransaction.test(t6))
            t6.displayTransaction();


        System.out.println("4. Failed Transactions");

        if (failedTransaction.test(t1))
            t1.displayTransaction();

        if (failedTransaction.test(t2))
            t2.displayTransaction();

        if (failedTransaction.test(t3))
            t3.displayTransaction();

        if (failedTransaction.test(t4))
            t4.displayTransaction();

        if (failedTransaction.test(t5))
            t5.displayTransaction();

        if (failedTransaction.test(t6))
            t6.displayTransaction();


        System.out.println(
                "5. Successful Transfer Transactions Greater Than 10000");

        Predicate<BankTransactionData> successfulTransfer =
                successfulTransaction.and(transferTransaction);

        Predicate<BankTransactionData> highValueSuccessfulTransfer =
                successfulTransfer.and(highValueTransaction);

        if (highValueSuccessfulTransfer.test(t1))
            t1.displayTransaction();

        if (highValueSuccessfulTransfer.test(t2))
            t2.displayTransaction();

        if (highValueSuccessfulTransfer.test(t3))
            t3.displayTransaction();

        if (highValueSuccessfulTransfer.test(t4))
            t4.displayTransaction();

        if (highValueSuccessfulTransfer.test(t5))
            t5.displayTransaction();

        if (highValueSuccessfulTransfer.test(t6))
            t6.displayTransaction();


        System.out.println("6. Deposit OR Transfer");

        Predicate<BankTransactionData> depositTransaction =
                t -> t.transactionType.equals("DEPOSIT");

        Predicate<BankTransactionData> depositOrTransfer =
                depositTransaction.or(transferTransaction);

        if (depositOrTransfer.test(t1))
            t1.displayTransaction();

        if (depositOrTransfer.test(t2))
            t2.displayTransaction();

        if (depositOrTransfer.test(t3))
            t3.displayTransaction();

        if (depositOrTransfer.test(t4))
            t4.displayTransaction();

        if (depositOrTransfer.test(t5))
            t5.displayTransaction();

        if (depositOrTransfer.test(t6))
            t6.displayTransaction();


        System.out.println("7. Transactions That Are NOT Failed");

        Predicate<BankTransactionData> notFailed =
                failedTransaction.negate();

        if (notFailed.test(t1))
            t1.displayTransaction();

        if (notFailed.test(t2))
            t2.displayTransaction();

        if (notFailed.test(t3))
            t3.displayTransaction();

        if (notFailed.test(t4))
            t4.displayTransaction();

        if (notFailed.test(t5))
            t5.displayTransaction();

        if (notFailed.test(t6))
            t6.displayTransaction();
    }
}