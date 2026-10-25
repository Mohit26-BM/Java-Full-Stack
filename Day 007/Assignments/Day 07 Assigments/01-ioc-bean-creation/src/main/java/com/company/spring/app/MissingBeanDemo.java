package com.company.spring.app;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.config.NoBeanConfig;
import com.company.spring.service.ProductService;

/*
FAILURE EVIDENCE: what happens when @Bean is removed.

NoBeanConfig has the same productService() method as AppConfig,
but without @Bean. The container therefore never creates a
ProductService, and asking for one fails.
*/
public class MissingBeanDemo {

    public static void main(String[] args) {

        System.out.println("Starting the container with NoBeanConfig (no @Bean)...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(NoBeanConfig.class)) {

            System.out.println("Container started. Notice: no [ProductService] constructor line.\n");

            System.out.println("containsBean(\"productService\") : "
                    + context.containsBean("productService"));

            System.out.println("Asking the container for ProductService...");

            ProductService productService = context.getBean(ProductService.class);

            productService.displayProduct();

        } catch (NoSuchBeanDefinitionException e) {

            System.out.println("\nFAILED as expected:");
            System.out.println("  " + e.getClass().getName());
            System.out.println("  " + e.getMessage());
        }
    }
}
