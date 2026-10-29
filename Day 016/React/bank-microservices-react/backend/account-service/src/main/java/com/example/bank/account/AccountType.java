package com.example.bank.account;

import java.math.BigDecimal;

/**
 * The banking rules that differ per account type, in one place.
 * Amounts are in the account's currency units (the portal shows INR).
 */
public enum AccountType {

    //          opening   lowest allowed  daily outgoing  accepts   allows        interest
    //          balance   balance         limit           deposits  withdrawals   per year
    SAVINGS("1000.00", "1000.00", "50000.00", true, true, "0.0350"),
    /** The lowest balance is negative: an overdraft of up to 10,000.00 is allowed. Pays no interest. */
    CURRENT("0.00", "-10000.00", "200000.00", true, true, "0.0000"),
    /** Money is locked in: no deposits after opening, no withdrawals, no transfers in or out. */
    FIXED_DEPOSIT("10000.00", "0.00", "0.00", false, false, "0.0700");

    private final BigDecimal minimumOpeningBalance;
    private final BigDecimal minimumBalance;
    private final BigDecimal dailyOutgoingLimit;
    private final boolean acceptsDeposits;
    private final boolean allowsWithdrawals;
    private final BigDecimal annualInterestRate;

    AccountType(String minimumOpeningBalance, String minimumBalance, String dailyOutgoingLimit,
                boolean acceptsDeposits, boolean allowsWithdrawals, String annualInterestRate) {
        this.minimumOpeningBalance = new BigDecimal(minimumOpeningBalance);
        this.minimumBalance = new BigDecimal(minimumBalance);
        this.dailyOutgoingLimit = new BigDecimal(dailyOutgoingLimit);
        this.acceptsDeposits = acceptsDeposits;
        this.allowsWithdrawals = allowsWithdrawals;
        this.annualInterestRate = new BigDecimal(annualInterestRate);
    }

    public BigDecimal minimumOpeningBalance() {
        return minimumOpeningBalance;
    }

    public BigDecimal minimumBalance() {
        return minimumBalance;
    }

    public BigDecimal dailyOutgoingLimit() {
        return dailyOutgoingLimit;
    }

    public boolean acceptsDeposits() {
        return acceptsDeposits;
    }

    public boolean allowsWithdrawals() {
        return allowsWithdrawals;
    }

    /** As a fraction, for example 0.0350 for 3.5% a year. */
    public BigDecimal annualInterestRate() {
        return annualInterestRate;
    }
}
