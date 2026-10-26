package com.example.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class ProductClient {
    private static final Logger log = LoggerFactory.getLogger(ProductClient.class);

    private final RestClient restClient;

    public ProductClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
    public Product getProduct(Long id) {
        return restClient.get()
                .uri("http://product-service/products/{id}", id)
                .retrieve()
                .body(Product.class);
    }

    public Product productFallback(Long id, Throwable ex) {
        log.warn("Product service unavailable for product {}: {}", id, ex.toString());
        return new Product(id, "Product temporarily unavailable", 0, null);
    }
}
