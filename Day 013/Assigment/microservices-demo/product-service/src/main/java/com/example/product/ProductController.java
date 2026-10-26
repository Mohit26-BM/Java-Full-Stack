package com.example.product;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private static final Map<Long, String> NAMES = Map.of(1L, "Laptop", 2L, "Mouse", 3L, "Keyboard");
    private static final Map<Long, Integer> PRICES = Map.of(1L, 75000, 2L, 800, 3L, 1500);

    @Value("${app.instance}")
    private String instance;

    @GetMapping("/{id}")
    public Map<String, Object> getProduct(@PathVariable Long id) {
        log.info("Product {} served by instance {}", id, instance);
        return Map.of(
                "id", id,
                "name", NAMES.getOrDefault(id, "Product " + id),
                "price", PRICES.getOrDefault(id, 1000),
                "instance", instance);
    }
}
