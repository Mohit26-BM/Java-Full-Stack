package com.company.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point - starts the embedded Tomcat on port 8080. */
@SpringBootApplication
public class GlobalExceptionHandlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlobalExceptionHandlingApplication.class, args);
    }
}
