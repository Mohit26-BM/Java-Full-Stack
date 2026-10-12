package com.company.wallet.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.company.wallet.dao.CustomerDao;
import com.company.wallet.dao.TransactionDao;
import com.company.wallet.dao.WalletDao;
import com.company.wallet.exception.CustomerNotFoundException;
import com.company.wallet.exception.InsufficientFundsException;
import com.company.wallet.exception.ValidationException;
import com.company.wallet.exception.WalletException;
import com.company.wallet.exception.WalletInactiveException;
import com.company.wallet.exception.WalletNotFoundException;
import com.company.wallet.model.BulkCredit;
import com.company.wallet.model.BulkCreditResult;
import com.company.wallet.model.TransactionType;
import com.company.wallet.model.TransferResult;
import com.company.wallet.model.Wallet;
import com.company.wallet.model.WalletStatus;
import com.company.wallet.model.WalletTransaction;

/*
Business rules for wallets. Every operation that changes money:

  - validates input BEFORE touching the database,
  - runs inside ONE transaction (TransactionManager),
  - LOCKS the wallet rows it changes (SELECT ... FOR UPDATE),
    so the balance it checks is the balance it updates,
  - writes a ledger row (wallet_transactions) for every balance change.
*/
public class WalletServiceImpl implements WalletService {

    private static final int MAX_DESCRIPTION = 255;

    private final WalletDao walletDao;
    private final CustomerDao customerDao;
    private final TransactionDao transactionDao;
    private final TransactionManager transactionManager;

    public WalletServiceImpl(WalletDao walletDao,
                             CustomerDao customerDao,
                             TransactionDao transactionDao,
                             TransactionManager transactionManager) {

        this.walletDao = walletDao;
        this.customerDao = customerDao;
        this.transactionDao = transactionDao;
        this.transactionManager = transactionManager;
    }

    // =========================================================
    // WALLETS
    // =========================================================

    @Override
    public Wallet createWallet(long customerId, BigDecimal openingDeposit)
            throws WalletException {

        BigDecimal opening = openingDeposit == null ? BigDecimal.ZERO : openingDeposit;

        if (opening.signum() < 0) {
            throw new ValidationException("Opening deposit cannot be negative.");
        }

        if (opening.signum() > 0) {
            InputRules.amount(opening);
        }

        return transactionManager.inTransaction(connection -> {

            if (!customerDao.exists(connection, customerId)) {
                throw new CustomerNotFoundException("Customer " + customerId + " was not found.");
            }

            /*
            getGeneratedKeys() gives the new wallet_id, which becomes
            the readable wallet number, e.g. 7 -> WAL0000007.
            Both statements are in the same transaction, so nobody
            ever sees a committed wallet without a number.
            */
            long walletId = walletDao.insert(connection, customerId);
            String walletNumber = String.format("WAL%07d", walletId);

            walletDao.assignWalletNumber(connection, walletId, walletNumber);

            if (opening.signum() > 0) {

                walletDao.updateBalance(connection, walletId, opening);

                transactionDao.insert(connection, new WalletTransaction(
                        newReference(), walletId, TransactionType.DEPOSIT,
                        opening, opening, null, "Opening deposit"));
            }

            return walletDao.findByNumber(connection, walletNumber, false);
        });
    }

    @Override
    public Wallet getWallet(String walletNumber) throws WalletException {

        String number = InputRules.walletNumber(walletNumber);

        Wallet wallet = walletDao.findByNumber(number);

        if (wallet == null) {
            throw new WalletNotFoundException("Wallet " + number + " does not exist.");
        }

        return wallet;
    }

    @Override
    public List<Wallet> getAllWallets() {
        return walletDao.findAll();
    }

    @Override
    public void changeStatus(String walletNumber, WalletStatus status) throws WalletException {

        String number = InputRules.walletNumber(walletNumber);

        if (!walletDao.updateStatus(number, status)) {
            throw new WalletNotFoundException("Wallet " + number + " does not exist.");
        }
    }

    @Override
    public List<WalletTransaction> history(String walletNumber, int limit) throws WalletException {

        return transactionDao.findByWallet(getWallet(walletNumber).getWalletId(), limit);
    }

    // =========================================================
    // DEPOSIT / WITHDRAW
    // =========================================================

    @Override
    public WalletTransaction deposit(String walletNumber, BigDecimal amount, String description)
            throws WalletException {

        String number = InputRules.walletNumber(walletNumber);
        BigDecimal value = InputRules.amount(amount);
        String text = InputRules.optionalText(description, MAX_DESCRIPTION);

        return transactionManager.inTransaction(connection -> {

            Wallet wallet = lockWallet(connection, number, "Wallet");
            requireActive(wallet);

            BigDecimal newBalance = wallet.getBalance().add(value);

            walletDao.updateBalance(connection, wallet.getWalletId(), newBalance);

            return saveLedgerRow(connection, new WalletTransaction(
                    newReference(), wallet.getWalletId(), TransactionType.DEPOSIT,
                    value, newBalance, null, text == null ? "Deposit" : text));
        });
    }

    @Override
    public WalletTransaction withdraw(String walletNumber, BigDecimal amount, String description)
            throws WalletException {

        return executeWithdraw(walletNumber, amount, description, true, 0);
    }

    @Override
    public WalletTransaction withdrawForExperiment(String walletNumber,
                                                   BigDecimal amount,
                                                   boolean useRowLock,
                                                   long pauseMillis)
            throws WalletException {

        return executeWithdraw(walletNumber, amount, "Concurrency experiment", useRowLock, pauseMillis);
    }

    private WalletTransaction executeWithdraw(String walletNumber,
                                              BigDecimal amount,
                                              String description,
                                              boolean useRowLock,
                                              long pauseMillis)
            throws WalletException {

        String number = InputRules.walletNumber(walletNumber);
        BigDecimal value = InputRules.amount(amount);
        String text = InputRules.optionalText(description, MAX_DESCRIPTION);

        return transactionManager.inTransaction(connection -> {

            Wallet wallet = walletDao.findByNumber(connection, number, useRowLock);

            if (wallet == null) {
                throw new WalletNotFoundException("Wallet " + number + " does not exist.");
            }

            requireActive(wallet);

            pause(pauseMillis);

            requireFunds(wallet, value);

            BigDecimal newBalance = wallet.getBalance().subtract(value);

            walletDao.updateBalance(connection, wallet.getWalletId(), newBalance);

            return saveLedgerRow(connection, new WalletTransaction(
                    newReference(), wallet.getWalletId(), TransactionType.WITHDRAWAL,
                    value, newBalance, null, text == null ? "Withdrawal" : text));
        });
    }

    // =========================================================
    // TRANSFER
    // =========================================================

    @Override
    public TransferResult transfer(String fromWallet, String toWallet, BigDecimal amount, String description)
            throws WalletException {

        return executeTransfer(fromWallet, toWallet, amount, description, false);
    }

    @Override
    public TransferResult transferWithForcedFailure(String fromWallet, String toWallet, BigDecimal amount)
            throws WalletException {

        return executeTransfer(fromWallet, toWallet, amount, "Forced-failure demo", true);
    }

    /*
    One transaction, four writes:
        debit source -> ledger TRANSFER_OUT -> credit destination -> ledger TRANSFER_IN
    Both ledger rows share one reference.
    */
    private TransferResult executeTransfer(String fromWallet,
                                           String toWallet,
                                           BigDecimal amount,
                                           String description,
                                           boolean failAfterDebit)
            throws WalletException {

        String from = InputRules.walletNumber(fromWallet);
        String to = InputRules.walletNumber(toWallet);
        BigDecimal value = InputRules.amount(amount);
        String text = InputRules.optionalText(description, MAX_DESCRIPTION);

        if (from.equals(to)) {
            throw new ValidationException("Source and destination wallets must be different.");
        }

        return transactionManager.inTransaction(connection -> {

            /*
            Lock both rows in a FIXED order (by wallet number) so that
            A->B and B->A transfers running together cannot deadlock.
            */
            boolean fromFirst = from.compareTo(to) < 0;

            Wallet first = walletDao.findByNumber(connection, fromFirst ? from : to, true);
            Wallet second = walletDao.findByNumber(connection, fromFirst ? to : from, true);

            Wallet source = fromFirst ? first : second;
            Wallet destination = fromFirst ? second : first;

            if (source == null) {
                throw new WalletNotFoundException("Source wallet " + from + " does not exist.");
            }

            if (destination == null) {
                throw new WalletNotFoundException(
                        "Destination wallet " + to + " does not exist. Transfer rejected.");
            }

            requireActive(source);
            requireActive(destination);
            requireFunds(source, value);

            String reference = newReference();
            String note = text == null ? "Transfer" : text;

            // ---------- debit ----------

            BigDecimal sourceBalance = source.getBalance().subtract(value);
            walletDao.updateBalance(connection, source.getWalletId(), sourceBalance);

            WalletTransaction debit = saveLedgerRow(connection, new WalletTransaction(
                    reference, source.getWalletId(), TransactionType.TRANSFER_OUT,
                    value, sourceBalance, destination.getWalletId(), note));

            if (failAfterDebit) {

                /*
                The debit and its ledger row are already written
                (inside the open transaction). Rollback must undo both.
                */
                throw new IllegalStateException(
                        "Forced failure after debiting " + from + " (before crediting " + to + ")");
            }

            // ---------- credit ----------

            BigDecimal destinationBalance = destination.getBalance().add(value);
            walletDao.updateBalance(connection, destination.getWalletId(), destinationBalance);

            WalletTransaction credit = saveLedgerRow(connection, new WalletTransaction(
                    reference, destination.getWalletId(), TransactionType.TRANSFER_IN,
                    value, destinationBalance, source.getWalletId(), note));

            return new TransferResult(reference,
                    debit.getTransactionId(), credit.getTransactionId(),
                    sourceBalance, destinationBalance);
        });
    }

    // =========================================================
    // BATCH: BULK CREDIT
    // =========================================================

    /*
    Credits many wallets in ONE transaction using two JDBC batches:
      1. UPDATE wallets ... (one per wallet)          -> addBatch / executeBatch
      2. INSERT INTO wallet_transactions (one per line) -> addBatch / executeBatch

    All-or-nothing: if any line is invalid, or any wallet is missing or
    not ACTIVE, nothing is credited. (For a cashback/payroll run it is
    safer to fix the file and re-run than to pay only some people.)
    */
    @Override
    public BulkCreditResult bulkCredit(List<BulkCredit> credits) throws WalletException {

        if (credits == null || credits.isEmpty()) {
            throw new ValidationException("The bulk credit list is empty.");
        }

        // ---------- validate every line first ----------

        List<String> problems = new ArrayList<>();
        List<String> numbers = new ArrayList<>();

        for (BulkCredit credit : credits) {

            try {
                numbers.add(InputRules.walletNumber(credit.getWalletNumber()));
                InputRules.amount(credit.getAmount());
                InputRules.optionalText(credit.getDescription(), MAX_DESCRIPTION);
            } catch (ValidationException e) {
                problems.add("Line " + credit.getLineNumber() + ": " + e.getMessage());
            }
        }

        if (!problems.isEmpty()) {
            throw new ValidationException("Bulk credit rejected, nothing was credited:\n  "
                    + String.join("\n  ", problems));
        }

        String batchReference = "BULK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return transactionManager.inTransaction(connection -> {

            // ---------- lock every wallet once, in wallet-number order ----------

            Map<String, Wallet> wallets = new TreeMap<>();

            for (String number : numbers) {
                wallets.put(number, null);
            }

            List<String> missing = new ArrayList<>();
            List<String> inactive = new ArrayList<>();

            for (String number : wallets.keySet()) {

                Wallet wallet = walletDao.findByNumber(connection, number, true);

                if (wallet == null) {
                    missing.add(number);
                } else if (wallet.getStatus() != WalletStatus.ACTIVE) {
                    inactive.add(number + " (" + wallet.getStatus() + ")");
                }

                wallets.put(number, wallet);
            }

            if (!missing.isEmpty()) {
                throw new WalletNotFoundException(
                        "Bulk credit rejected, nothing was credited. Unknown wallets: " + missing);
            }

            if (!inactive.isEmpty()) {
                throw new WalletInactiveException(
                        "Bulk credit rejected, nothing was credited. Wallets not ACTIVE: " + inactive);
            }

            // ---------- running balances + ledger rows ----------

            Map<Long, BigDecimal> newBalances = new LinkedHashMap<>();
            List<WalletTransaction> ledgerRows = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (int i = 0; i < credits.size(); i++) {

                BulkCredit credit = credits.get(i);
                Wallet wallet = wallets.get(numbers.get(i));

                BigDecimal current = newBalances.containsKey(wallet.getWalletId())
                        ? newBalances.get(wallet.getWalletId())
                        : wallet.getBalance();

                BigDecimal updated = current.add(credit.getAmount());
                newBalances.put(wallet.getWalletId(), updated);

                ledgerRows.add(new WalletTransaction(
                        batchReference + "-" + (i + 1),
                        wallet.getWalletId(),
                        TransactionType.DEPOSIT,
                        credit.getAmount(),
                        updated,
                        null,
                        credit.getDescription() == null ? "Bulk credit" : credit.getDescription().trim()));

                total = total.add(credit.getAmount());
            }

            // ---------- the two batches ----------

            int[] balanceCounts = walletDao.updateBalancesBatch(connection, newBalances);
            int[] ledgerCounts = transactionDao.insertBatch(connection, ledgerRows);

            requireAllSucceeded(balanceCounts, "wallet balance update");
            requireAllSucceeded(ledgerCounts, "ledger insert");

            return new BulkCreditResult(credits.size(), newBalances.size(), total,
                    balanceCounts, ledgerCounts);
        });
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Wallet lockWallet(Connection connection, String number, String label)
            throws SQLException, WalletNotFoundException {

        Wallet wallet = walletDao.findByNumber(connection, number, true);

        if (wallet == null) {
            throw new WalletNotFoundException(label + " " + number + " does not exist.");
        }

        return wallet;
    }

    private WalletTransaction saveLedgerRow(Connection connection, WalletTransaction row)
            throws SQLException {

        row.setTransactionId(transactionDao.insert(connection, row));

        return row;
    }

    private void requireActive(Wallet wallet) throws WalletInactiveException {

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletInactiveException("Wallet " + wallet.getWalletNumber()
                    + " is " + wallet.getStatus() + " and cannot send or receive money.");
        }
    }

    private void requireFunds(Wallet wallet, BigDecimal amount) throws InsufficientFundsException {

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in " + wallet.getWalletNumber()
                    + ": balance Rs. " + wallet.getBalance() + ", requested Rs. " + amount + ".");
        }
    }

    /*
    1 = one row changed. SUCCESS_NO_INFO = driver did not report a
    count (allowed). Anything else means a row was not changed.
    */
    private void requireAllSucceeded(int[] counts, String what) throws SQLException {

        for (int count : counts) {

            if (count != 1 && count != Statement.SUCCESS_NO_INFO) {
                throw new SQLException("Batch " + what + " returned update count " + count);
            }
        }
    }

    private String newReference() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private void pause(long millis) {

        if (millis <= 0) {
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
