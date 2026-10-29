package com.example.bank.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** Sample accounts for customers 1 to 3, inserted only when the "dev" profile is active and the table is empty. */
@Configuration
@Profile("dev")
public class DemoDataInitializer {

    @Bean
    CommandLineRunner demoAccounts(AccountRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new Account(1L, "SAVINGS", LocalDate.of(2023, 5, 10), new BigDecimal("125000.50")),
                        new Account(1L, "CURRENT", LocalDate.of(2024, 2, 1), new BigDecimal("48250.00")),
                        new Account(2L, "SAVINGS", LocalDate.of(2022, 11, 18), new BigDecimal("87500.75")),
                        new Account(3L, "FIXED_DEPOSIT", LocalDate.of(2024, 8, 5), new BigDecimal("500000.00"))));
            }
        };
    }
}
