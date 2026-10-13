package com.company.spring.app;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.config.AppConfig;
import com.company.spring.config.BrokenDbConfig;
import com.company.spring.lifecycle.LifecycleLog;

/*
Edge cases:
 1. The context is never closed      -> @PreDestroy never runs (connection leaked)
 2. @PostConstruct throws            -> the container fails to start
 3. registerShutdownHook()           -> @PreDestroy runs when the JVM exits
*/
public class LifecycleEdgeCasesApp {

    public static void main(String[] args) {

        // ---------- 1 ----------

        System.out.println("CASE 1 - context created but NEVER closed\n");

        AnnotationConfigApplicationContext neverClosed =
                new AnnotationConfigApplicationContext(AppConfig.class);

        System.out.println("   ...main moves on without calling close(). No @PreDestroy line appears");
        System.out.println("   for this context - its connection stays open until the JVM dies.");
        System.out.println("   (context still active: " + neverClosed.isActive() + ")\n");

        // ---------- 2 ----------

        System.out.println("CASE 2 - @PostConstruct throws (database does not exist)\n");
        LifecycleLog.reset();

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(BrokenDbConfig.class)) {

            System.out.println("   Never printed - startup fails first: " + context.getId());

        } catch (BeanCreationException e) {

            System.out.println("\n   STARTUP FAILED in the init phase:");
            System.out.println("   " + e.getClass().getSimpleName() + ": " + firstLine(e.getMessage()));
            System.out.println("   Root cause: " + e.getMostSpecificCause().getClass().getSimpleName()
                    + ": " + e.getMostSpecificCause().getMessage());
            System.out.println("   Constructor and injection ran (STEP 1-2); the bean never became usable.\n");
        }

        // ---------- 3 ----------

        System.out.println("CASE 3 - registerShutdownHook(): no close() call, but cleanup still happens\n");
        LifecycleLog.reset();

        AnnotationConfigApplicationContext withHook =
                new AnnotationConfigApplicationContext(AppConfig.class);

        withHook.registerShutdownHook();

        System.out.println("   main() is ending now. Watch for @PreDestroy AFTER this line:");
    }

    private static String firstLine(String message) {
        int cut = message.indexOf(':', message.indexOf("defined in"));
        return cut > 0 ? message.substring(0, cut) : message;
    }
}
