package com.company.spring.config;

import org.springframework.context.annotation.Configuration;

import com.company.spring.model.Product;
import com.company.spring.service.ProductService;

/*
FAILURE EVIDENCE: the same recipe as AppConfig, but WITHOUT @Bean.

Without @Bean, productService() is just an ordinary Java method.
Spring never calls it, so the container holds no ProductService,
and getBean(ProductService.class) fails with
NoSuchBeanDefinitionException.

Used by MissingBeanDemo.
*/
@Configuration
public class NoBeanConfig {

    // @Bean   <-- removed on purpose
    public ProductService productService() {

        return new ProductService(new Product(101, "Wireless Mouse", "Electronics", 799.00));
    }
}
