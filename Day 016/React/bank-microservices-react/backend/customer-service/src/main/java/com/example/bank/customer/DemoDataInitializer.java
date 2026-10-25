package com.example.bank.customer;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** The database is in memory, so these sample customers are recreated on every start. */
@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner demoCustomers(CustomerRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new Customer("Aarav Sharma", "aarav.sharma@example.com", LocalDate.of(1992, 4, 12), "Male", "Mumbai"),
                        new Customer("Diya Patel", "diya.patel@example.com", LocalDate.of(1995, 9, 3), "Female", "Ahmedabad"),
                        new Customer("Kavya Nair", "kavya.nair@example.com", LocalDate.of(1988, 1, 27), "Female", "Kochi")));
            }
        };
    }
}
