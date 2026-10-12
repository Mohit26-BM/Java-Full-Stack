package com.company.wallet.service;

import java.math.BigDecimal;
import java.util.List;

import com.company.wallet.exception.WalletException;
import com.company.wallet.model.BulkCredit;
import com.company.wallet.model.BulkCreditResult;
import com.company.wallet.model.TransferResult;
import com.company.wallet.model.Wallet;
import com.company.wallet.model.WalletStatus;
import com.company.wallet.model.WalletTransaction;

/*
All methods throw a subclass of WalletException for business errors:
ValidationException, CustomerNotFoundException, WalletNotFoundException,
WalletInactiveException, InsufficientFundsException,
TransactionFailedException (rolled back).
*/
public interface WalletService {

    Wallet createWallet(long customerId, BigDecimal openingDeposit) throws WalletException;

    Wallet getWallet(String walletNumber) throws WalletException;

    List<Wallet> getAllWallets();

    void changeStatus(String walletNumber, WalletStatus status) throws WalletException;

    WalletTransaction deposit(String walletNumber, BigDecimal amount, String description)
            throws WalletException;

    WalletTransaction withdraw(String walletNumber, BigDecimal amount, String description)
            throws WalletException;

    TransferResult transfer(String fromWallet, String toWallet, BigDecimal amount, String description)
            throws WalletException;

    /*
    Demonstration: debits the source and writes its ledger row,
    then fails on purpose before the credit -> full rollback.
    */
    TransferResult transferWithForcedFailure(String fromWallet, String toWallet, BigDecimal amount)
            throws WalletException;

    /*
    Batch operation: credit many wallets in one transaction.
    All-or-nothing.
    */
    BulkCreditResult bulkCredit(List<BulkCredit> credits) throws WalletException;

    List<WalletTransaction> history(String walletNumber, int limit) throws WalletException;

    /*
    Concurrency experiment only. useRowLock = false skips FOR UPDATE;
    pauseMillis waits between reading the balance and writing it.
    */
    WalletTransaction withdrawForExperiment(String walletNumber,
                                            BigDecimal amount,
                                            boolean useRowLock,
                                            long pauseMillis)
            throws WalletException;
}
