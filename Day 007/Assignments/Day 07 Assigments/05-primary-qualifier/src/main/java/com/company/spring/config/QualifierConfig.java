package com.company.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.company.spring.payment.CardPaymentService;
import com.company.spring.payment.PaymentService;
import com.company.spring.payment.UpiPaymentService;
import com.company.spring.repository.PaymentRepository;

/*
SOLUTION 2 - @Qualifier.

Same two beans as AmbiguousConfig - still NO @Primary.
The ambiguity is solved at the injection point instead:
QualifiedCheckoutService asks for @Qualifier("upiPayment").
*/
@Configuration
@Import(CommonConfig.class)
@ComponentScan("com.company.spring.checkout.qualified")
public class QualifierConfig {

    @Bean
    public PaymentService cardPayment(PaymentRepository repository) {
        return new CardPaymentService(repository);
    }

    @Bean
    public PaymentService upiPayment(PaymentRepository repository) {
        return new UpiPaymentService(repository);
    }
}
