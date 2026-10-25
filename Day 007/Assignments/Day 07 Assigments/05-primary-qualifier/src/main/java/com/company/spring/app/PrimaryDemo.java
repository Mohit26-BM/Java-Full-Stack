package com.company.spring.app;

import java.math.BigDecimal;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.checkout.basic.CheckoutService;
import com.company.spring.config.PrimaryConfig;
import com.company.spring.repository.PaymentRepository;

/*
SOLUTION 1: @Primary on cardPayment. CheckoutService is unchanged.
*/
public class PrimaryDemo {

    public static void main(String[] args) {

        System.out.println("SOLUTION 1 - @Primary on cardPayment (CheckoutService unchanged)\n");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(PrimaryConfig.class)) {

            System.out.println("Container started.\n");

            CheckoutService checkoutService = context.getBean(CheckoutService.class);

            PaymentPrinter.printReceipt(
                    checkoutService.checkout("ORD-5001", new BigDecimal("2499.00")));

            System.out.println("\n   Is cardPayment primary? "
                    + context.getBeanFactory().getBeanDefinition("cardPayment").isPrimary());
            System.out.println("   Is upiPayment primary?  "
                    + context.getBeanFactory().getBeanDefinition("upiPayment").isPrimary());

            PaymentPrinter.printRecent(context.getBean(PaymentRepository.class), 3);
        }
    }
}
