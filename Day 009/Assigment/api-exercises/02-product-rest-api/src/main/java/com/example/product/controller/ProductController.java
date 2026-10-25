package com.example.product.controller;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.product.dto.PageResponse;
import com.example.product.dto.ProductRequest;
import com.example.product.dto.ProductResponse;
import com.example.product.dto.ProductUpdateRequest;
import com.example.product.dto.QuantityUpdateRequest;
import com.example.product.service.ProductService;

import jakarta.validation.Valid;

/**
 * /api/products
 *
 *   POST   /api/products                    201 create
 *   GET    /api/products/{id}               200 | 404
 *   GET    /api/products?category=&minPrice=&maxPrice=&name=&active=&page=&size=&sortBy=&direction=
 *   PUT    /api/products/{id}               200 | 404 | 409 (old version)
 *   PATCH  /api/products/{id}/quantity      200 | 404
 *   DELETE /api/products/{id}               204 | 404
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

    /** All filters are optional; page/size/sortBy/direction have defaults. */
    @GetMapping
    public PageResponse<ProductResponse> search(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "active", required = false) Boolean active,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        return productService.search(category, minPrice, maxPrice, name, active, page, size, sortBy, direction);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable("id") Long id, @Valid @RequestBody ProductUpdateRequest request) {
        return productService.update(id, request);
    }

    /** Challenge extension: change only the inventory quantity. */
    @PatchMapping("/{id}/quantity")
    public ProductResponse updateQuantity(@PathVariable("id") Long id,
                                          @Valid @RequestBody QuantityUpdateRequest request) {
        return productService.updateQuantity(id, request.quantity());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
