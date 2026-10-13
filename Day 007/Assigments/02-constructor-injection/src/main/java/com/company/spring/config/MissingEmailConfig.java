package com.company.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.company.spring.service.NotificationService;

/*
FAILURE EVIDENCE: registers NotificationService but NOT EmailService
(no component scan). This is exactly what happens if you remove
@Service from EmailService.

NotificationService's constructor needs an EmailService, none exists,
so the container FAILS TO START.
*/
@Configuration
@Import(NotificationService.class)
public class MissingEmailConfig {
}
