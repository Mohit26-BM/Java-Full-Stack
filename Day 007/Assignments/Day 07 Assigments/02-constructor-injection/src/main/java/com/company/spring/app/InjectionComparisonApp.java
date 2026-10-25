package com.company.spring.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.compare.ComparisonConfig;
import com.company.spring.compare.SetterMissingConfig;
import com.company.spring.compare.beans.SetterNotificationService;
import com.company.spring.service.NotificationService;

/*
OPTIONAL EXTENSION: constructor injection vs setter injection.

Part A - both styles work when the dependency exists.
Part B - when EmailService is missing:
         constructor injection -> fails at STARTUP (see MissingBeanStartupDemo)
         optional setter injection -> starts fine, fails LATER at run time.
*/
public class InjectionComparisonApp {

    public static void main(String[] args) {

        // ---------- Part A ----------

        System.out.println("PART A - both injection styles, EmailService available\n");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(ComparisonConfig.class)) {

            System.out.println();

            context.getBean(NotificationService.class)
                    .notifyUser("priya.sharma@example.com", "Constructor-injected service works.");

            System.out.println();

            context.getBean(SetterNotificationService.class)
                    .notifyUser("priya.sharma@example.com", "Setter-injected service works too.");
        }

        // ---------- Part B ----------

        System.out.println("\nPART B - setter injection (required = false), NO EmailService bean\n");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SetterMissingConfig.class)) {

            System.out.println("   Container started successfully - the missing dependency went unnoticed.");

            SetterNotificationService service = context.getBean(SetterNotificationService.class);

            service.notifyUser("amit.verma@example.com", "This will fail.");

        } catch (NullPointerException e) {

            System.out.println("\n   FAILED LATER, at run time:");
            System.out.println("   " + e.getClass().getName() + ": " + e.getMessage());
        }

        System.out.println("\nCompare with MissingBeanStartupDemo: constructor injection reports the");
        System.out.println("same missing bean immediately, while the application is starting.");
    }
}
