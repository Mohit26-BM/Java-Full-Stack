package com.company.spring.service;

import com.company.spring.model.Product;

/*
A PLAIN Java class: no Spring annotations, no "new" anywhere in the
application code. Spring creates it because AppConfig declares it
with @Bean.

The constructor prints a message so the console shows WHEN the
object is created: during container startup, before main() asks
for it.
*/
public class ProductService {

    private final Product product;

    public ProductService(Product product) {

        this.product = product;

        System.out.println("   [ProductService] constructor called - object created "
                + "(identity " + Integer.toHexString(System.identityHashCode(this)) + ")");
    }

    public void displayProduct() {

        System.out.println("-------------------------");
        System.out.println("Product ID   : " + product.getProductId());
        System.out.println("Product Name : " + product.getProductName());
        System.out.println("Category     : " + product.getCategory());
        System.out.println("Price        : Rs. " + product.getPrice());
        System.out.println("-------------------------");
    }
}
