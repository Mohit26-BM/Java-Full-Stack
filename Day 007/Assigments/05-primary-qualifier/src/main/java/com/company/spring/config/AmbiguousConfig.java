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
STEP 1 - THE PROBLEM.

Two PaymentService beans, neither marked @Primary, and CheckoutService
asks for "a PaymentService" without a qualifier. Spring cannot choose,
so the container fails to start (NoUniqueBeanDefinitionException).
*/
@Configuration
@Import(CommonConfig.class)
@ComponentScan("com.company.spring.checkout.basic")
public class AmbiguousConfig {

    @Bean
    public PaymentService cardPayment(PaymentRepository repository) {
        return new CardPaymentService(repository);
    }

    @Bean
    public PaymentService upiPayment(PaymentRepository repository) {
        return new UpiPaymentService(repository);
    }
}
