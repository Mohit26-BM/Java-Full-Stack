package com.company.spring.service;

import org.springframework.stereotype.Service;

/*
@Service = "Spring, create one instance of this class and manage it."
Component scanning (AppConfig) finds the annotation and registers
the bean automatically - no @Bean method needed.
*/
@Service
public class EmailService {

    public EmailService() {

        System.out.println("   [EmailService] created by Spring (identity "
                + Integer.toHexString(System.identityHashCode(this)) + ")");
    }

    public void sendEmail(String to, String subject, String body) {

        System.out.println("   EMAIL -> To: " + to);
        System.out.println("            Subject: " + subject);
        System.out.println("            Body: " + body);
    }
}
