package com.company.spring.compare;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/*
Scans both packages: the constructor-injected NotificationService
and the setter-injected SetterNotificationService share ONE EmailService.
*/
@Configuration
@ComponentScan(basePackages = {
        "com.company.spring.service",
        "com.company.spring.compare.beans"
})
public class ComparisonConfig {
}
