package com.company.dbauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** Users, roles and permissions stored in MySQL; login through a database-backed UserDetailsService. */
@SpringBootApplication
@ConfigurationPropertiesScan
public class DatabaseAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseAuthApplication.class, args);
    }
}
