package com.company.wallet.dao;

import java.time.YearMonth;
import java.util.List;

import com.company.wallet.model.CustomerPortfolio;
import com.company.wallet.model.MonthlySummary;
import com.company.wallet.model.ReconciliationRow;

public interface ReportDao {

    /*
    Credits / debits / count per month for ONE wallet.
    */
    List<MonthlySummary> walletMonthlySummary(long walletId);

    /*
    JOIN report: customers + wallets + wallet_transactions,
    one row per wallet that had activity in the month.
    */
    List<MonthlySummary> monthlyReport(YearMonth month);

    /*
    JOIN report: wallet count and total balance per customer.
    */
    List<CustomerPortfolio> customerPortfolio();

    /*
    Stored balance vs ledger balance for every wallet.
    */
    List<ReconciliationRow> reconciliation();
}
