package com.company.spring.app;

import java.util.Arrays;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.config.AppConfig;
import com.company.spring.service.EmailService;
import com.company.spring.service.NotificationService;

/*
Main program: start the container, get NotificationService, call notifyUser().
There is no "new EmailService()" or "new NotificationService()" anywhere.
*/
public class NotificationApp {

    public static void main(String[] args) {

        System.out.println("1. Starting the Spring container (component scan)...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("2. Container started.\n");

            System.out.println("3. Calling notifyUser():");

            NotificationService notificationService = context.getBean(NotificationService.class);

            notificationService.notifyUser("ravi.kumar@example.com",
                    "Your account statement for October is ready.");

            // ---------- proof ----------

            EmailService beanInContainer = context.getBean(EmailService.class);
            EmailService injectedObject = notificationService.getEmailService();

            System.out.println("\n4. Proof that the constructor received a Spring-managed object:");
            System.out.println("   Beans found by component scan    : "
                    + Arrays.toString(userBeanNames(context)));
            System.out.println("   EmailService bean in container   : "
                    + Integer.toHexString(System.identityHashCode(beanInContainer)));
            System.out.println("   EmailService inside Notification : "
                    + Integer.toHexString(System.identityHashCode(injectedObject)));
            System.out.println("   Same object (==)                 : " + (beanInContainer == injectedObject));
            System.out.println("   EmailService created before NotificationService: see the order of lines in step 1");
        }

        System.out.println("\n5. Container closed.");
    }

    private static String[] userBeanNames(AnnotationConfigApplicationContext context) {

        return Arrays.stream(context.getBeanDefinitionNames())
                .filter(name -> !name.startsWith("org.springframework"))
                .toArray(String[]::new);
    }
}
