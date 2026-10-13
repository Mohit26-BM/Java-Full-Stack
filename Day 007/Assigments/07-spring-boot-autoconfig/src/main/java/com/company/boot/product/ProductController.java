package com.company.boot.product;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
The ONE REST controller.

@RestController = @Controller + @ResponseBody: every method's return
value is written to the HTTP response as JSON (by the auto-configured
Jackson converter).

Endpoints:
  GET /api/hello            -> greeting (no database)
  GET /api/products         -> all rows of a07_products
  GET /api/products/{id}    -> one product, or 404 Not Found
*/
@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
                "message", "Hello from Spring Boot!",
                "server", "embedded Tomcat, started by auto-configuration");
    }

    @GetMapping("/products")
    public List<Product> allProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> productById(@PathVariable int id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
