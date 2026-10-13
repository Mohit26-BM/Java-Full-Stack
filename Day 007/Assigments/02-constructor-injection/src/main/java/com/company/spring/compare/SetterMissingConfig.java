package com.company.spring.compare;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.company.spring.compare.beans.SetterNotificationService;

/*
Edge case for the comparison: SetterNotificationService WITHOUT any
EmailService bean. Because its setter is @Autowired(required = false),
the container starts fine - the problem only appears later, when
notifyUser() runs with a null emailService.
*/
@Configuration
@Import(SetterNotificationService.class)
public class SetterMissingConfig {
}
