package com.company.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.spring.model.Product;
import com.company.spring.service.DiscountService;
import com.company.spring.service.ProductService;

/*
@Configuration marks this class as a source of bean definitions.

Each @Bean method is a RECIPE: "this is how to build this object".
The application never calls these methods. The Spring container calls
them once at startup, keeps the returned objects (beans), and hands
them out when asked.

The bean NAME is the method name: "productService", "discountService".
*/
@Configuration
public class AppConfig {

    @Bean
    public ProductService productService() {

        Product product = new Product(101, "Wireless Mouse", "Electronics", 799.00);

        return new ProductService(product);
    }

    /*
    Optional extension: a second independent bean.
    */
    @Bean
    public DiscountService discountService() {

        return new DiscountService(10);
    }
}
