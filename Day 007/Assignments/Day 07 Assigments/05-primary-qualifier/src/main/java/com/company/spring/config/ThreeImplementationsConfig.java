package com.company.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import com.company.spring.payment.CardPaymentService;
import com.company.spring.payment.PaymentService;
import com.company.spring.payment.UpiPaymentService;
import com.company.spring.payment.WalletPaymentService;
import com.company.spring.repository.PaymentRepository;

/*
OPTIONAL EXTENSION - a third implementation (WALLET), with both
@Primary (on card) and @Qualifier (in QualifiedCheckoutService)
present at the same time, plus PaymentRouter that takes all of them.

Shows:
  - CheckoutService          (no qualifier) -> CARD   (@Primary)
  - QualifiedCheckoutService (@Qualifier)   -> UPI    (@Qualifier beats @Primary)
  - PaymentRouter            (Map)          -> all three
*/
@Configuration
@Import(CommonConfig.class)
@ComponentScan({
        "com.company.spring.checkout.basic",
        "com.company.spring.checkout.qualified",
        "com.company.spring.checkout.multi"
})
public class ThreeImplementationsConfig {

    @Bean
    @Primary
    public PaymentService cardPayment(PaymentRepository repository) {
        return new CardPaymentService(repository);
    }

    @Bean
    public PaymentService upiPayment(PaymentRepository repository) {
        return new UpiPaymentService(repository);
    }

    @Bean
    public PaymentService walletPayment(PaymentRepository repository) {
        return new WalletPaymentService(repository);
    }
}
