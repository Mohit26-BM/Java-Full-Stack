package com.company.boot.notification;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Registered only when NO profile is active.
 * Spring then uses the built-in profile named "default".
 */
@Component
@Profile("default")
public class DefaultNotificationSender implements NotificationSender {

    @Override
    public String send(String recipient, String text) {
        return "[DEFAULT] logged only, nothing sent to " + recipient + ": " + text;
    }
}
