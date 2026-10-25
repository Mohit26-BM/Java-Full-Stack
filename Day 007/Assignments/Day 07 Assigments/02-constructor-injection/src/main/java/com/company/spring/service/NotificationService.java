package com.company.spring.service;

import org.springframework.stereotype.Service;

/*
CONSTRUCTOR INJECTION.

NotificationService NEEDS an EmailService, but never creates one
(no "new EmailService()"). It simply asks for it as a constructor
parameter. When Spring creates NotificationService, it looks in the
container for an EmailService bean and passes it in.

- The field is final: once injected it can never change or be null.
- With only ONE constructor, @Autowired is optional (Spring 4.3+).
*/
@Service
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {

        this.emailService = emailService;

        System.out.println("   [NotificationService] created by Spring; constructor received EmailService "
                + "(identity " + Integer.toHexString(System.identityHashCode(emailService)) + ")");
    }

    public void notifyUser(String email, String message) {

        System.out.println("   Notifying " + email + "...");

        emailService.sendEmail(email, "Notification from CSB Bank", message);
    }

    /*
    For the demonstration only: lets main() compare this object
    with the EmailService bean held by the container.
    */
    public EmailService getEmailService() {
        return emailService;
    }
}
