package com.company.spring.payment;

import java.math.BigDecimal;
import java.util.UUID;

import com.company.spring.repository.PaymentRepository;

/*
One implementation of PaymentService (CARD).

A plain class: it is registered as a bean by an @Bean method in each
scenario configuration, so each scenario can decide whether it is
@Primary or not without editing this class.
*/
public class CardPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;

    public CardPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String getMethodName() {
        return "CARD";
    }

    @Override
    public PaymentReceipt pay(String orderId, BigDecimal amount) {

        System.out.println("   Card payment: charging the saved Visa card ending 4242");

        String reference = "CARD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return paymentRepository.save(orderId, amount, getMethodName(), reference);
    }
}