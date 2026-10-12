package com.company.wallet.app;

import java.math.BigDecimal;
import java.util.List;

import com.company.wallet.model.Customer;
import com.company.wallet.model.CustomerPortfolio;
import com.company.wallet.model.MonthlySummary;
import com.company.wallet.model.ReconciliationRow;
import com.company.wallet.model.Wallet;
import com.company.wallet.model.WalletTransaction;

/*
Console table output, shared by the menu and the demo runner.
*/
public final class Printer {

    static final String LINE =
            "------------------------------------------------------------------------------------------";

    private Printer() {
    }

    public static void customers(List<Customer> customers) {

        System.out.println(LINE);
        System.out.println(String.format("%-4s %-20s %-30s %s", "ID", "Name", "Email", "Phone"));
        System.out.println(LINE);

        for (Customer customer : customers) {
            System.out.println(customer);
        }

        System.out.println(LINE);
    }

    public static void wallets(List<Wallet> wallets) {

        if (wallets.isEmpty()) {
            System.out.println("No wallets.");
            return;
        }

        System.out.println(LINE);
        System.out.println(String.format("%-11s %-20s %16s  %s", "Wallet", "Owner", "Balance", "Status"));
        System.out.println(LINE);

        for (Wallet wallet : wallets) {
            System.out.println(wallet);
        }

        System.out.println(LINE);
    }

    public static void history(List<WalletTransaction> transactions) {

        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        System.out.println(LINE);
        System.out.println(String.format("%-6s %-16s %-13s %15s %15s  %-11s %s",
                "ID", "Date", "Type", "Amount", "Balance after", "Other", "Description"));
        System.out.println(LINE);

        for (WalletTransaction transaction : transactions) {
            System.out.println(transaction);
        }

        System.out.println(LINE);
    }

    public static void walletMonthly(List<MonthlySummary> rows) {

        if (rows.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        System.out.println(LINE);
        System.out.println(String.format("%-8s %16s %16s %16s %6s", "Month", "Money in", "Money out", "Net", "Count"));
        System.out.println(LINE);

        for (MonthlySummary row : rows) {
            System.out.println(String.format("%-8s %16s %16s %16s %6d",
                    row.getMonth(), money(row.getTotalCredits()), money(row.getTotalDebits()),
                    money(row.getNetChange()), row.getTransactionCount()));
        }

        System.out.println(LINE);
    }

    public static void monthlyReport(List<MonthlySummary> rows) {

        if (rows.isEmpty()) {
            System.out.println("No activity in this month.");
            return;
        }

        BigDecimal totalIn = BigDecimal.ZERO;
        BigDecimal totalOut = BigDecimal.ZERO;

        System.out.println(LINE);
        System.out.println(String.format("%-11s %-18s %15s %15s %15s %6s",
                "Wallet", "Customer", "Money in", "Money out", "Net", "Count"));
        System.out.println(LINE);

        for (MonthlySummary row : rows) {

            System.out.println(String.format("%-11s %-18.18s %15s %15s %15s %6d",
                    row.getWalletNumber(), row.getCustomerName(),
                    money(row.getTotalCredits()), money(row.getTotalDebits()),
                    money(row.getNetChange()), row.getTransactionCount()));

            totalIn = totalIn.add(row.getTotalCredits());
            totalOut = totalOut.add(row.getTotalDebits());
        }

        System.out.println(LINE);
        System.out.println(String.format("%-30s %15s %15s %15s", "TOTAL",
                money(totalIn), money(totalOut), money(totalIn.subtract(totalOut))));
    }

    public static void portfolio(List<CustomerPortfolio> rows) {

        System.out.println(LINE);
        System.out.println(String.format("%-4s %-22s %8s %18s", "ID", "Customer", "Wallets", "Total balance"));
        System.out.println(LINE);

        for (CustomerPortfolio row : rows) {
            System.out.println(String.format("%-4d %-22.22s %8d %18s",
                    row.getCustomerId(), row.getFullName(), row.getWalletCount(),
                    money(row.getTotalBalance())));
        }

        System.out.println(LINE);
    }

    public static void reconciliation(List<ReconciliationRow> rows) {

        int mismatches = 0;

        System.out.println(LINE);
        System.out.println(String.format("%-11s %18s %18s  %s", "Wallet", "Stored balance", "Ledger balance", "Check"));
        System.out.println(LINE);

        for (ReconciliationRow row : rows) {

            if (!row.isMatching()) {
                mismatches++;
            }

            System.out.println(String.format("%-11s %18s %18s  %s",
                    row.getWalletNumber(), money(row.getStoredBalance()), money(row.getLedgerBalance()),
                    row.isMatching() ? "OK" : "MISMATCH"));
        }

        System.out.println(LINE);
        System.out.println(mismatches == 0
                ? "[OK] Every wallet balance equals the sum of its ledger rows."
                : "[!] " + mismatches + " wallet(s) do not match their ledger "
                        + "(expected for the 'no lock' concurrency experiment wallets).");
    }

    public static String money(BigDecimal value) {
        return value == null ? "-" : "Rs. " + value;
    }
}
