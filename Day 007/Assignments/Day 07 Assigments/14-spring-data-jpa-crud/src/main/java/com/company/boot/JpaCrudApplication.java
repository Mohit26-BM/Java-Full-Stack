package com.company.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point.
 * Spring Boot auto-configures the DataSource, Hibernate's EntityManagerFactory,
 * the transaction manager, and creates an implementation of every interface
 * that extends JpaRepository (found under this package).
 */
@SpringBootApplication
public class JpaCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaCrudApplication.class, args);
    }
}
