package com.company.spring.app;

import java.math.BigDecimal;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.checkout.qualified.QualifiedCheckoutService;
import com.company.spring.config.QualifierConfig;
import com.company.spring.repository.PaymentRepository;

/*
SOLUTION 2: no @Primary at all; QualifiedCheckoutService uses
@Qualifier("upiPayment") on its constructor parameter.
*/
public class QualifierDemo {

    public static void main(String[] args) {

        System.out.println("SOLUTION 2 - no @Primary; @Qualifier(\"upiPayment\") at the injection point\n");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(QualifierConfig.class)) {

            System.out.println("Container started.\n");

            QualifiedCheckoutService checkoutService = context.getBean(QualifiedCheckoutService.class);

            PaymentPrinter.printReceipt(
                    checkoutService.checkout("ORD-5002", new BigDecimal("899.50")));

            PaymentPrinter.printRecent(context.getBean(PaymentRepository.class), 3);
        }
    }
}
