package com.company.spring.app;

import java.math.BigDecimal;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.checkout.basic.CheckoutService;
import com.company.spring.checkout.multi.PaymentRouter;
import com.company.spring.checkout.qualified.QualifiedCheckoutService;
import com.company.spring.config.ThreeImplementationsConfig;
import com.company.spring.repository.PaymentRepository;

/*
OPTIONAL EXTENSION: three implementations (CARD, UPI, WALLET),
@Primary and @Qualifier together, and a router that uses all of them.
*/
public class ThirdImplementationDemo {

    public static void main(String[] args) {

        System.out.println("EXTENSION - 3 implementations; cardPayment is @Primary\n");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(ThreeImplementationsConfig.class)) {

            System.out.println("Container started.\n");

            System.out.println("1. CheckoutService (no qualifier) -> gets the @Primary bean:");
            PaymentPrinter.printReceipt(context.getBean(CheckoutService.class)
                    .checkout("ORD-5003", new BigDecimal("1200.00")));

            System.out.println("\n2. QualifiedCheckoutService -> @Qualifier wins over @Primary:");
            PaymentPrinter.printReceipt(context.getBean(QualifiedCheckoutService.class)
                    .checkout("ORD-5004", new BigDecimal("350.00")));

            System.out.println("\n3. PaymentRouter -> every implementation, chosen at run time:");

            PaymentRouter router = context.getBean(PaymentRouter.class);

            PaymentPrinter.printReceipt(router.checkout("walletPayment", "ORD-5005", new BigDecimal("150.00")));
            PaymentPrinter.printReceipt(router.checkout("upiPayment", "ORD-5006", new BigDecimal("640.00")));

            PaymentPrinter.printRecent(context.getBean(PaymentRepository.class), 4);
        }
    }
}
