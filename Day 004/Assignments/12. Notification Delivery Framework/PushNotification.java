package notificationDeliveryFramework;

public class PushNotification implements Notification {

    private String deviceId;
    private String message;

    PushNotification(String deviceId, String message)
            throws NotificationException {

        if (deviceId == null
                || deviceId.trim().isEmpty()) {

            throw new NotificationException(
                    "Device ID cannot be empty");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new NotificationException(
                    "Push notification message cannot be empty");
        }

        this.deviceId = deviceId;
        this.message = message;
    }

    @Override
    public void sendNotification() {

        System.out.println(
                "Push notification sent to device "
                + deviceId);
    }

    @Override
    public void displayDetails() {

        System.out.println("Channel: Push");
        System.out.println("Device ID: " + deviceId);
        System.out.println("Message: " + message);
    }
}