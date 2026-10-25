package com.company.spring.checkout.multi;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.company.spring.payment.PaymentReceipt;
import com.company.spring.payment.PaymentService;

/*
OPTIONAL EXTENSION: use ALL implementations, chosen at run time.

Asking for Map<String, PaymentService> makes Spring inject every
PaymentService bean, keyed by bean name. No ambiguity: we asked for
all of them. Adding a fourth implementation needs no change here.
*/
@Service
public class PaymentRouter {

    private final Map<String, PaymentService> paymentServices;

    public PaymentRouter(Map<String, PaymentService> paymentServices) {

        this.paymentServices = paymentServices;

        System.out.println("   [PaymentRouter] injected ALL PaymentService beans: " + paymentServices.keySet());
    }

    public PaymentReceipt checkout(String beanName, String orderId, BigDecimal amount) {

        PaymentService paymentService = paymentServices.get(beanName);

        if (paymentService == null) {
            throw new IllegalArgumentException("Unknown payment method '" + beanName
                    + "'. Available: " + paymentServices.keySet());
        }

        System.out.println("   Router: " + orderId + " -> " + paymentService.getMethodName());

        return paymentService.pay(orderId, amount);
    }
}
