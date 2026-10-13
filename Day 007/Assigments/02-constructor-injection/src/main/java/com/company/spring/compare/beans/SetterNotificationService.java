package com.company.spring.compare.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.spring.service.EmailService;

/*
OPTIONAL EXTENSION: the same service using SETTER INJECTION.

Spring first creates the object with the no-argument constructor,
THEN calls the @Autowired setter to inject EmailService.

required = false means "inject it if a bean exists, otherwise leave
it null". That makes the dependency optional - and shows the risk:
the object can exist without what it needs.
*/
@Service
public class SetterNotificationService {

    private EmailService emailService;     // not final: set after construction

    public SetterNotificationService() {

        System.out.println("   [SetterNotificationService] created (emailService is still "
                + emailService + " at this point)");
    }

    @Autowired(required = false)
    public void setEmailService(EmailService emailService) {

        this.emailService = emailService;

        System.out.println("   [SetterNotificationService] setter called by Spring with EmailService "
                + "(identity " + Integer.toHexString(System.identityHashCode(emailService)) + ")");
    }

    public void notifyUser(String email, String message) {

        System.out.println("   Notifying " + email + " (setter-injected service)...");

        emailService.sendEmail(email, "Notification from CSB Bank", message);
    }
}
