package notificationDeliveryFramework;

public class WhatsAppNotification implements Notification {

    private String phoneNumber;
    private String message;

    WhatsAppNotification(String phoneNumber, String message)
            throws NotificationException {

        if (phoneNumber == null
                || phoneNumber.trim().isEmpty()) {

            throw new NotificationException(
                    "WhatsApp number cannot be empty");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new NotificationException(
                    "WhatsApp message cannot be empty");
        }

        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    @Override
    public void sendNotification() {

        System.out.println(
                "WhatsApp message sent to "
                + phoneNumber);
    }

    @Override
    public void displayDetails() {

        System.out.println("Channel: WhatsApp");
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Message: " + message);
    }
}
