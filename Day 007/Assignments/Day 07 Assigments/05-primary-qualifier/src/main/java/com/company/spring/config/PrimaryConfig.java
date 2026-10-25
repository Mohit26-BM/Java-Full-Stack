package com.company.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import com.company.spring.payment.CardPaymentService;
import com.company.spring.payment.PaymentService;
import com.company.spring.payment.UpiPaymentService;
import com.company.spring.repository.PaymentRepository;

/*
SOLUTION 1 - @Primary.

Identical to AmbiguousConfig, except cardPayment is marked @Primary:
"when several PaymentService beans match and nobody says which one,
use this one". CheckoutService is UNCHANGED and now gets CARD.

On a class the same thing looks like:
    @Service
    @Primary
    public class CardPaymentService implements PaymentService { ... }
*/
@Configuration
@Import(CommonConfig.class)
@ComponentScan("com.company.spring.checkout.basic")
public class PrimaryConfig {

    @Bean
    @Primary
    public PaymentService cardPayment(PaymentRepository repository) {
        return new CardPaymentService(repository);
    }

    @Bean
    public PaymentService upiPayment(PaymentRepository repository) {
        return new UpiPaymentService(repository);
    }
}
