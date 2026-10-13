package com.company.boot.notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TEST (optional extension): records messages in memory so a tester can
 * check what WOULD have been sent, without sending anything.
 */
@Component
@Profile("test")
public class MockNotificationSender implements NotificationSender {

    private final List<String> sent = new ArrayList<>();

    @Override
    public String send(String recipient, String text) {
        sent.add(recipient);
        return "[TEST-MOCK] recorded (total recorded: " + sent.size() + ") for " + recipient + ": " + text;
    }
}
