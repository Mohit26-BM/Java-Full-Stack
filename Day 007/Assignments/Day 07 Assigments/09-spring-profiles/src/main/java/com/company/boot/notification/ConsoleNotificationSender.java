package com.company.boot.notification;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** DEV: prints to the console instead of sending real e-mails. */
@Component
@Profile("dev")
public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public String send(String recipient, String text) {
        String line = "[DEV-CONSOLE] to " + recipient + ": " + text;
        System.out.println(line);
        return line;
    }
}
