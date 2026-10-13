package com.company.spring.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import com.company.spring.config.AppConfig;
import com.company.spring.customer.component.CustomerComponent;

/*
Starts the container with component scanning, shows which beans
Spring DISCOVERED by itself, then runs the customer report through
the chain  component -> service -> repository -> MySQL.
*/
public class CustomerApp {

    public static void main(String[] args) {

        System.out.println("1. Starting the container - scanning com.company.spring.customer ...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("2. Container started.\n");

            // ---------- what did Spring find? ----------

            System.out.println("3. Beans in the container and how each was registered:");
            System.out.println("   ---------------------------------------------------------------------");
            System.out.println(String.format("   %-20s %-14s %s", "Bean name", "Registered by", "Class"));
            System.out.println("   ---------------------------------------------------------------------");

            for (String name : context.getBeanDefinitionNames()) {

                if (name.startsWith("org.springframework")) {
                    continue;       // Spring's own internal beans
                }

                Class<?> type = ClassUtils.getUserClass(context.getType(name));

                System.out.println(String.format("   %-20s %-14s %s",
                        name, howRegistered(type), type.getName()));
            }

            System.out.println("   ---------------------------------------------------------------------");
            System.out.println("   Not a bean: Customer (no stereotype), LoyaltyComponent (outside the scan package)\n");

            // ---------- use the beans ----------

            System.out.println("4. Running the report: CustomerComponent -> CustomerService -> CustomerRepository\n");

            context.getBean(CustomerComponent.class).printCustomerReport();
        }

        System.out.println("\n5. Container closed.");
    }

    private static String howRegistered(Class<?> type) {

        if (type.isAnnotationPresent(Configuration.class)) {
            return "@Configuration";
        }

        if (type.isAnnotationPresent(Repository.class)) {
            return "@Repository";
        }

        if (type.isAnnotationPresent(Service.class)) {
            return "@Service";
        }

        if (type.isAnnotationPresent(Component.class)) {
            return "@Component";
        }

        return "@Bean method";
    }
}
