package com.company.spring.checkout.qualified;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.company.spring.payment.PaymentReceipt;
import com.company.spring.payment.PaymentService;

/*
@Qualifier("upiPayment") names EXACTLY which bean to inject
(the bean name = the @Bean method name in the configuration).

The choice is made HERE, at the injection point, by the class that
needs the dependency - and it wins even if another bean is @Primary.
*/
@Service
public class QualifiedCheckoutService {

    private final PaymentService paymentService;

    public QualifiedCheckoutService(@Qualifier("upiPayment") PaymentService paymentService) {

        this.paymentService = paymentService;

        System.out.println("   [QualifiedCheckoutService] injected PaymentService = "
                + paymentService.getClass().getSimpleName() + "  (@Qualifier(\"upiPayment\"))");
    }

    public PaymentReceipt checkout(String orderId, BigDecimal amount) {

        System.out.println("   Checkout " + orderId + " for Rs. " + amount
                + " using " + paymentService.getMethodName());

        return paymentService.pay(orderId, amount);
    }
}
