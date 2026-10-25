package com.company.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Employee portal: public status endpoint, protected employee data,
 * HTTP Basic for the REST API and a form-login admin page for browsers.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class BasicSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicSecurityApplication.class, args);
    }
}
