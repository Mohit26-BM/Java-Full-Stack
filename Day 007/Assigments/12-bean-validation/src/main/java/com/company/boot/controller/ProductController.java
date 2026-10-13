package com.company.boot.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.dto.ProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Notice: no "if (name == null) ..." checks anywhere in this class.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * @Valid triggers every constraint on ProductRequest BEFORE this method runs.
     * If any fails, the method is never called -> MethodArgumentNotValidException -> 400.
     */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    /**
     * Constraints can also sit directly on simple parameters.
     * GET /api/products?category=ELECTRONICS&maxPrice=50000
     */
    @GetMapping
    public List<ProductResponse> search(
            @RequestParam(name = "category", required = false)
            @Size(min = 3, max = 20, message = "category must be 3-20 characters") String category,
            @RequestParam(name = "maxPrice", required = false)
            @Positive(message = "maxPrice must be greater than 0") BigDecimal maxPrice) {
        return productService.search(category, maxPrice);
    }
}
