package com.company.spring.checkout.basic;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.company.spring.payment.PaymentReceipt;
import com.company.spring.payment.PaymentService;

/*
Asks for "a PaymentService" with NO hint about which one.

- If only one PaymentService bean exists -> it is injected.
- If several exist and none is @Primary  -> AMBIGUOUS: startup fails.
- If several exist and one is @Primary   -> the primary one is injected.
*/
@Service
public class CheckoutService {

    private final PaymentService paymentService;

    public CheckoutService(PaymentService paymentService) {

        this.paymentService = paymentService;

        System.out.println("   [CheckoutService] injected PaymentService = "
                + paymentService.getClass().getSimpleName());
    }

    public PaymentReceipt checkout(String orderId, BigDecimal amount) {

        System.out.println("   Checkout " + orderId + " for Rs. " + amount
                + " using " + paymentService.getMethodName());

        return paymentService.pay(orderId, amount);
    }
}
