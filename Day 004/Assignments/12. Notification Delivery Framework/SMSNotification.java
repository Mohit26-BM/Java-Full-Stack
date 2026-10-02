package notificationDeliveryFramework;

public class SMSNotification implements Notification {

    private String phoneNumber;
    private String message;

    SMSNotification(String phoneNumber, String message)
            throws NotificationException {

        if (phoneNumber == null
                || phoneNumber.trim().isEmpty()) {

            throw new NotificationException(
                    "Phone number cannot be empty");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new NotificationException(
                    "SMS message cannot be empty");
        }

        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    @Override
    public void sendNotification() {

        System.out.println(
                "SMS sent to " + phoneNumber);
    }

    @Override
    public void displayDetails() {

        System.out.println("Channel: SMS");
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Message: " + message);
    }
}
