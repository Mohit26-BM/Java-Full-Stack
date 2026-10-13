package com.company.spring.app;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.config.MissingEmailConfig;

/*
FAILURE EVIDENCE: missing-bean STARTUP error.

NotificationService is registered, EmailService is not (same as
removing @Service from EmailService). The error happens while the
container is STARTING - the line after "new AnnotationConfigApplicationContext"
is never reached and getBean() is never called.
*/
public class MissingBeanStartupDemo {

    public static void main(String[] args) {

        System.out.println("Starting the container WITHOUT an EmailService bean...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(MissingEmailConfig.class)) {

            System.out.println("Never printed - startup fails first: " + context.getId());

        } catch (BeanCreationException e) {

            System.out.println("\nSTARTUP FAILED as expected:");
            System.out.println("  Exception : " + e.getClass().getName());
            System.out.println("  Message   : " + e.getMessage());

            Throwable root = e.getMostSpecificCause();

            System.out.println("  Root cause: " + root.getClass().getName());
            System.out.println("              " + root.getMessage());
        }
    }
}
