package com.company.spring.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.edgecase.EdgeCaseConfig;
import com.company.spring.edgecase.FreshReportService;
import com.company.spring.edgecase.ReportService;
import com.company.spring.edgecase.WebScopeConfig;
import com.company.spring.service.CounterService;

/*
Edge cases around bean scopes:
 1. a prototype injected into a singleton is created only once
 2. ObjectProvider gives a fresh prototype on every call
 3. request scope does not exist outside a web application
*/
public class ScopeEdgeCasesApp {

    public static void main(String[] args) {

        System.out.println("=== EDGE CASE 1 & 2: prototype inside a singleton ===");
        System.out.println("Starting container...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(EdgeCaseConfig.class)) {

            System.out.println("Container started.\n");

            ReportService reportService = context.getBean(ReportService.class);
            FreshReportService freshReportService = context.getBean(FreshReportService.class);

            System.out.println("1. Direct injection - 3 calls:");
            reportService.generateReport();
            reportService.generateReport();
            reportService.generateReport();

            System.out.println("\n2. ObjectProvider - 3 calls:");
            freshReportService.generateReport();
            freshReportService.generateReport();
            freshReportService.generateReport();
        }

        System.out.println("\n=== EDGE CASE 3: request scope without a web application ===");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(WebScopeConfig.class)) {

            System.out.println("Container started (request-scoped beans are not created at startup).");
            System.out.println("Asking for the request-scoped bean...");

            context.getBean("requestCounter", CounterService.class);

        } catch (IllegalStateException e) {

            System.out.println("FAILED as expected:");
            System.out.println("   " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
