package com.company.boot.notification;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** PROD: would send a real e-mail (simulated here). */
@Component
@Profile("prod")
public class EmailNotificationSender implements NotificationSender {

    @Override
    public String send(String recipient, String text) {
        return "[PROD-EMAIL] e-mail queued for " + recipient + ": " + text;
    }
}
