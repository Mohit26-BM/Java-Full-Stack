package com.company.spring.app;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.config.AppConfig;
import com.company.spring.config.MissingRepositoryConfig;
import com.company.spring.outside.LoyaltyComponent;

/*
OPTIONAL EXTENSION / EDGE CASES: classes outside the scan package.

Case 1: LoyaltyComponent lives in com.company.spring.outside.
        It has @Component, but it is never discovered.

Case 2: CustomerRepository "moved out" of the scan (excluded).
        CustomerService still needs it -> the container cannot start.
*/
public class OutsideScanDemo {

    public static void main(String[] args) {

        // ---------- Case 1 ----------

        System.out.println("CASE 1 - @Component class outside the scanned package\n");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("\n   Notice: no [LoyaltyComponent] created line above.");
            System.out.println("   containsBean(\"loyaltyComponent\") : "
                    + context.containsBean("loyaltyComponent"));

            try {

                context.getBean(LoyaltyComponent.class);

            } catch (NoSuchBeanDefinitionException e) {

                System.out.println("   getBean(LoyaltyComponent.class) failed:");
                System.out.println("   " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        // ---------- Case 2 ----------

        System.out.println("\nCASE 2 - CustomerRepository outside the scan, but CustomerService needs it\n");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(MissingRepositoryConfig.class)) {

            System.out.println("   Never printed - startup fails first: " + context.getId());

        } catch (BeanCreationException e) {

            System.out.println("   STARTUP FAILED:");
            System.out.println("   " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
