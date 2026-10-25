package com.company.spring.payment;

import java.math.BigDecimal;

/*
Result of a successful payment (also saved as a row in a05_payments).
*/
public class PaymentReceipt {

    private final int paymentId;
    private final String orderId;
    private final BigDecimal amount;
    private final String paymentMethod;
    private final String paymentReference;

    public PaymentReceipt(int paymentId, String orderId, BigDecimal amount,
                          String paymentMethod, String paymentReference) {

        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentReference = paymentReference;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    @Override
    public String toString() {
        return String.format("%-5d %-9s %12s  %-7s %s",
                paymentId, orderId, "Rs. " + amount, paymentMethod, paymentReference);
    }
}
