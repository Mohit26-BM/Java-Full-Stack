package com.company.boot.controller;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.dto.PageResponse;
import com.company.boot.dto.ProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.service.ProductService;

import jakarta.validation.Valid;

/**
 * CRUD endpoints:
 *   POST   /api/products          create  -> 201
 *   GET    /api/products/{id}     read    -> 200 / 404
 *   GET    /api/products          list    (?page=0&size=5&sort=price,desc)
 *   PUT    /api/products/{id}     update  -> 200 / 404
 *   DELETE /api/products/{id}     delete  -> 204 / 404
 *   GET    /api/products/search   search  (?name=&category=&minPrice=&maxPrice= + paging)
 *
 * Pageable is filled automatically by Spring Data from page / size / sort parameters.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable("id") Long id) {
        return productService.findById(id);
    }

    @GetMapping
    public PageResponse<ProductResponse> findAll(
            @RequestParam(name = "category", required = false) String category,
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        if (category != null && !category.isBlank()) {
            return productService.findByCategory(category, pageable);
        }
        return productService.findAll(pageable);
    }

    /** OPTIONAL EXTENSION: search + pagination. */
    @GetMapping("/search")
    public PageResponse<ProductResponse> search(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return productService.search(name, category, minPrice, maxPrice, pageable);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable("id") Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
