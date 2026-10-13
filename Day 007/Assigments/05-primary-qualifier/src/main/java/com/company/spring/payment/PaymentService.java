package com.company.spring.payment;

import java.math.BigDecimal;

/*
ONE interface, SEVERAL implementations (Card, UPI, Wallet).

When a class asks for "a PaymentService", Spring finds more than one
matching bean and must be told which one to inject - that is what
@Primary and @Qualifier are for.
*/
public interface PaymentService {

    String getMethodName();

    PaymentReceipt pay(String orderId, BigDecimal amount);
}
