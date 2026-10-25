package com.company.boot.notification;

/**
 * One interface, one implementation PER PROFILE (@Profile).
 * Exactly one implementation is registered for any single active profile.
 */
public interface NotificationSender {

    String send(String recipient, String text);
}
