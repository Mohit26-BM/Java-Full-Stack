package com.company.wallet.model;

import java.math.BigDecimal;

/*
What a successful transfer returns.
*/
public class TransferResult {

    private final String reference;
    private final long debitTransactionId;
    private final long creditTransactionId;
    private final BigDecimal sourceBalanceAfter;
    private final BigDecimal destinationBalanceAfter;

    public TransferResult(String reference,
                          long debitTransactionId,
                          long creditTransactionId,
                          BigDecimal sourceBalanceAfter,
                          BigDecimal destinationBalanceAfter) {

        this.reference = reference;
        this.debitTransactionId = debitTransactionId;
        this.creditTransactionId = creditTransactionId;
        this.sourceBalanceAfter = sourceBalanceAfter;
        this.destinationBalanceAfter = destinationBalanceAfter;
    }

    public String getReference() {
        return reference;
    }

    public long getDebitTransactionId() {
        return debitTransactionId;
    }

    public long getCreditTransactionId() {
        return creditTransactionId;
    }

    public BigDecimal getSourceBalanceAfter() {
        return sourceBalanceAfter;
    }

    public BigDecimal getDestinationBalanceAfter() {
        return destinationBalanceAfter;
    }
}
