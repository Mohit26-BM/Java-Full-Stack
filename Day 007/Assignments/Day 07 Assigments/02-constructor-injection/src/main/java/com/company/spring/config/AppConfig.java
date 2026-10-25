package com.company.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/*
@ComponentScan tells Spring to look inside the service package for
classes annotated with @Service (or @Component, @Repository ...)
and register each one as a bean.

Spring then works out the order: NotificationService needs an
EmailService, so EmailService is created first and injected.
*/
@Configuration
@ComponentScan(basePackages = "com.company.spring.service")
public class AppConfig {
}
