package com.company.wallet.service;

import java.time.YearMonth;
import java.util.List;

import com.company.wallet.exception.WalletException;
import com.company.wallet.model.CustomerPortfolio;
import com.company.wallet.model.MonthlySummary;
import com.company.wallet.model.ReconciliationRow;

public interface ReportService {

    List<MonthlySummary> walletMonthlySummary(String walletNumber) throws WalletException;

    List<MonthlySummary> monthlyReport(YearMonth month);

    List<CustomerPortfolio> customerPortfolio();

    List<ReconciliationRow> reconciliation();

    long countLedgerRows();
}
