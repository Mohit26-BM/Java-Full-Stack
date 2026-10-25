package com.company.methodsec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** HR service whose sensitive business methods enforce their own authorization rules. */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MethodSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MethodSecurityApplication.class, args);
    }
}
