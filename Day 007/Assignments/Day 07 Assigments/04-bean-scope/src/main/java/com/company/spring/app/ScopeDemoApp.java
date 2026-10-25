package com.company.spring.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.config.PrototypeConfig;
import com.company.spring.config.SingletonConfig;
import com.company.spring.service.CounterService;

/*
Gets two references to CounterService and compares them with ==,
first with the default (singleton) scope, then with prototype scope.
*/
public class ScopeDemoApp {

    public static void main(String[] args) {

        // ---------- SINGLETON ----------

        System.out.println("=== SINGLETON (default scope) ===");
        System.out.println("Starting container...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SingletonConfig.class)) {

            System.out.println("Container started.\n");

            CounterService first = context.getBean(CounterService.class);
            CounterService second = context.getBean(CounterService.class);

            compare(first, second);

            System.out.println("Is singleton? " + context.isSingleton("counterService")
                    + " | Is prototype? " + context.isPrototype("counterService"));

            System.out.println("\nClosing container...");
        }

        // ---------- PROTOTYPE ----------

        System.out.println("\n=== PROTOTYPE (@Scope(\"prototype\")) ===");
        System.out.println("Starting container...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(PrototypeConfig.class)) {

            System.out.println("Container started. (no constructor call yet - prototypes are created on request)\n");

            System.out.println("getBean #1:");
            CounterService first = context.getBean(CounterService.class);

            System.out.println("getBean #2:");
            CounterService second = context.getBean(CounterService.class);

            compare(first, second);

            System.out.println("Is singleton? " + context.isSingleton("counterService")
                    + " | Is prototype? " + context.isPrototype("counterService"));

            System.out.println("\nClosing container... (watch: no destroy() for prototypes)");
        }

        System.out.println("\nTotal CounterService objects created in this run: "
                + CounterService.getInstancesCreated());
    }

    private static void compare(CounterService first, CounterService second) {

        System.out.println("first  = " + first);
        System.out.println("second = " + second);
        System.out.println("first == second           : " + (first == second));

        first.increment();
        first.increment();

        System.out.println("after first.increment() x2: first.count=" + first.getCount()
                + ", second.count=" + second.getCount());
    }
}
