package notificationDeliveryFramework;


public class EmailNotification implements Notification {

    private String recipient;
    private String message;

    EmailNotification(String recipient, String message)
            throws NotificationException {

        if (recipient == null || recipient.trim().isEmpty()) {
            throw new NotificationException(
                    "Email recipient cannot be empty");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new NotificationException(
                    "Email message cannot be empty");
        }

        this.recipient = recipient;
        this.message = message;
    }

    @Override
    public void sendNotification() {

        System.out.println(
                "Email sent to " + recipient);
    }

    @Override
    public void displayDetails() {

        System.out.println("Channel: Email");
        System.out.println("Recipient: " + recipient);
        System.out.println("Message: " + message);
    }
}