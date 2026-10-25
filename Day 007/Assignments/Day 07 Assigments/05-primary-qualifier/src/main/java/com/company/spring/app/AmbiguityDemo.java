package com.company.spring.app;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.config.AmbiguousConfig;

/*
STEP 1: observe the ambiguity.
Two PaymentService beans, no @Primary, no @Qualifier -> startup fails.
*/
public class AmbiguityDemo {

    public static void main(String[] args) {

        System.out.println("STEP 1 - two PaymentService beans (cardPayment, upiPayment), "
                + "no @Primary, no @Qualifier\n");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AmbiguousConfig.class)) {

            System.out.println("Never printed - startup fails first: " + context.getId());

        } catch (BeanCreationException e) {

            System.out.println("\nSTARTUP FAILED - ambiguous bean:");
            System.out.println("   " + e.getClass().getName());
            System.out.println("   " + e.getMessage());

            Throwable root = e.getMostSpecificCause();

            System.out.println("\n   Root cause: " + root.getClass().getName());
            System.out.println("   " + root.getMessage());
        }
    }
}
