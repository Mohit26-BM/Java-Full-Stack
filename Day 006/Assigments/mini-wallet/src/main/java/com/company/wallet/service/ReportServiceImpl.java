package com.company.wallet.service;

import java.time.YearMonth;
import java.util.List;

import com.company.wallet.dao.ReportDao;
import com.company.wallet.dao.TransactionDao;
import com.company.wallet.dao.WalletDao;
import com.company.wallet.exception.WalletException;
import com.company.wallet.exception.WalletNotFoundException;
import com.company.wallet.model.CustomerPortfolio;
import com.company.wallet.model.MonthlySummary;
import com.company.wallet.model.ReconciliationRow;
import com.company.wallet.model.Wallet;

public class ReportServiceImpl implements ReportService {

    private final ReportDao reportDao;
    private final WalletDao walletDao;
    private final TransactionDao transactionDao;

    public ReportServiceImpl(ReportDao reportDao, WalletDao walletDao, TransactionDao transactionDao) {
        this.reportDao = reportDao;
        this.walletDao = walletDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public List<MonthlySummary> walletMonthlySummary(String walletNumber) throws WalletException {

        String number = InputRules.walletNumber(walletNumber);

        Wallet wallet = walletDao.findByNumber(number);

        if (wallet == null) {
            throw new WalletNotFoundException("Wallet " + number + " does not exist.");
        }

        return reportDao.walletMonthlySummary(wallet.getWalletId());
    }

    @Override
    public List<MonthlySummary> monthlyReport(YearMonth month) {
        return reportDao.monthlyReport(month);
    }

    @Override
    public List<CustomerPortfolio> customerPortfolio() {
        return reportDao.customerPortfolio();
    }

    @Override
    public List<ReconciliationRow> reconciliation() {
        return reportDao.reconciliation();
    }

    @Override
    public long countLedgerRows() {
        return transactionDao.countAll();
    }
}
