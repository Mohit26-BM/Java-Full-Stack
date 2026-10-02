package notificationDeliveryFramework;

public class NotificationDeliveryFramework {

    public static void main(String[] args) {

        try {

            /*
            Creating notification objects.
            */

            Notification email =
                    new EmailNotification(
                            "ravi@example.com",
                            "Your payment was successful."
                    );

            Notification sms =
                    new SMSNotification(
                            "9876543210",
                            "Your OTP is 123456."
                    );

            Notification push =
                    new PushNotification(
                            "DEVICE1001",
                            "You have received a new message."
                    );

            Notification whatsapp =
                    new WhatsAppNotification(
                            "9876543210",
                            "Your order has been shipped."
                    );


            /*
            Email notification
            */

            System.out.println("EMAIL NOTIFICATION");

            email.displayDetails();
            email.sendNotification();

            System.out.println("-------------------------");


            /*
            SMS notification
            */

            System.out.println();
            System.out.println("SMS NOTIFICATION");

            sms.displayDetails();
            sms.sendNotification();

            System.out.println("-------------------------");


            /*
            Push notification
            */

            System.out.println();
            System.out.println("PUSH NOTIFICATION");

            push.displayDetails();
            push.sendNotification();

            System.out.println("-------------------------");


            /*
            WhatsApp notification
            */

            System.out.println();
            System.out.println("WHATSAPP NOTIFICATION");

            whatsapp.displayDetails();
            whatsapp.sendNotification();

            System.out.println("-------------------------");


            /*
            Invalid notification test
            */

            System.out.println();
            System.out.println("INVALID INPUT TEST");

            Notification invalidNotification =
                    new EmailNotification(
                            "",
                            "Test message"
                    );

        }
        catch (NotificationException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}