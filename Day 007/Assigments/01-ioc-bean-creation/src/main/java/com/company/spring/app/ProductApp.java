package com.company.spring.app;

import java.util.Arrays;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.config.AppConfig;
import com.company.spring.service.DiscountService;
import com.company.spring.service.ProductService;

/*
Starts the IoC container, retrieves ProductService from it and
calls displayProduct(). There is no "new ProductService()" here:
the object is supplied by Spring.
*/
public class ProductApp {

    public static void main(String[] args) {

        System.out.println("1. Starting the Spring container (AnnotationConfigApplicationContext)...");

        /*
        Creating the context reads AppConfig, calls every @Bean method
        and stores the results. try-with-resources closes the container
        (and destroys its beans) at the end.
        */
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("2. Container started.\n");

            // ---------- retrieve the bean and use it ----------

            System.out.println("3. Asking the container for ProductService...");

            ProductService productService = context.getBean(ProductService.class);

            productService.displayProduct();

            // ---------- proof that Spring supplied the object ----------

            System.out.println("\n4. Proof that the object comes from Spring:");

            System.out.println("   Beans registered by AppConfig : "
                    + Arrays.toString(userBeanNames(context)));

            System.out.println("   containsBean(\"productService\") : "
                    + context.containsBean("productService"));

            ProductService byName = context.getBean("productService", ProductService.class);
            ProductService again = context.getBean(ProductService.class);

            System.out.println("   Same object by type and by name : " + (productService == byName));
            System.out.println("   Same object on a second request : " + (productService == again)
                    + "  (singleton scope - created once, shared)");
            System.out.println("   Constructor ran BEFORE step 3   : see the [ProductService] line above");

            // ---------- optional extension: second independent bean ----------

            System.out.println("\n5. Optional extension - a second, independent bean:");

            DiscountService discountService = context.getBean(DiscountService.class);

            discountService.displayOffer();

            System.out.println("   Wireless Mouse after discount : Rs. "
                    + discountService.applyDiscount(799.00));
        }

        System.out.println("\n6. Container closed.");
    }

    /*
    Bean names minus Spring's own internal beans (they start with
    "org.springframework"), plus the config class itself.
    */
    private static String[] userBeanNames(AnnotationConfigApplicationContext context) {

        return Arrays.stream(context.getBeanDefinitionNames())
                .filter(name -> !name.startsWith("org.springframework"))
                .toArray(String[]::new);
    }
}
