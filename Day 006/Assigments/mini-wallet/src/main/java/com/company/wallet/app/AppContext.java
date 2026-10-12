package com.company.wallet.app;

import javax.sql.DataSource;

import com.company.wallet.config.DatabaseConfig;
import com.company.wallet.dao.CustomerDao;
import com.company.wallet.dao.CustomerDaoImpl;
import com.company.wallet.dao.ReportDaoImpl;
import com.company.wallet.dao.TransactionDao;
import com.company.wallet.dao.TransactionDaoImpl;
import com.company.wallet.dao.WalletDao;
import com.company.wallet.dao.WalletDaoImpl;
import com.company.wallet.service.CustomerService;
import com.company.wallet.service.CustomerServiceImpl;
import com.company.wallet.service.ReportService;
import com.company.wallet.service.ReportServiceImpl;
import com.company.wallet.service.TransactionManager;
import com.company.wallet.service.WalletService;
import com.company.wallet.service.WalletServiceImpl;

/*
Wires the layers together (manual dependency injection):

    config (DataSource) -> DAOs -> TransactionManager -> services -> app

This is the only place that creates objects. In a Spring application
this class disappears and the framework does the same wiring.
*/
public final class AppContext implements AutoCloseable {

    private final DatabaseConfig databaseConfig;
    private final CustomerService customerService;
    private final WalletService walletService;
    private final ReportService reportService;

    public AppContext() {

        databaseConfig = DatabaseConfig.create();

        DataSource dataSource = databaseConfig.getDataSource();

        CustomerDao customerDao = new CustomerDaoImpl(dataSource);
        WalletDao walletDao = new WalletDaoImpl(dataSource);
        TransactionDao transactionDao = new TransactionDaoImpl(dataSource);

        TransactionManager transactionManager = new TransactionManager(dataSource);

        customerService = new CustomerServiceImpl(customerDao, walletDao, transactionManager);
        walletService = new WalletServiceImpl(walletDao, customerDao, transactionDao, transactionManager);
        reportService = new ReportServiceImpl(new ReportDaoImpl(dataSource), walletDao, transactionDao);
    }

    public CustomerService customers() {
        return customerService;
    }

    public WalletService wallets() {
        return walletService;
    }

    public ReportService reports() {
        return reportService;
    }

    /*
    Borrowed pool connections right now (should be 0 when idle).
    */
    public int activeConnections() {
        return databaseConfig.getActiveConnections();
    }

    @Override
    public void close() {
        databaseConfig.close();
    }
}
