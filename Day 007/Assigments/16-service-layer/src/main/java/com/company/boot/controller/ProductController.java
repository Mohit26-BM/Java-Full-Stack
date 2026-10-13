package com.company.boot.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.dto.CreateProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.dto.RestockRequest;
import com.company.boot.service.ProductService;

/**
 * CONTROLLER LAYER - HTTP only:
 *   map URL + method to a service call, read the body, choose the status code.
 * There is no "if" in this class: it contains NO business rules.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger("CONTROLLER");

    private final ProductService productService;       // the INTERFACE, not ProductServiceImpl

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody CreateProductRequest request) {
        log.info("POST /api/products received -> delegating to ProductService.create()");
        ProductResponse created = productService.create(request);
        log.info("returning 201 Created");
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @PostMapping("/import")
    public List<String> importProducts(@RequestBody List<CreateProductRequest> requests) {
        log.info("POST /api/products/import received ({} items) -> ProductService.importProducts()", requests.size());
        return productService.importProducts(requests);
    }

    @PostMapping("/{id}/restock")
    public ProductResponse restock(@PathVariable("id") Long id, @RequestBody RestockRequest request) {
        log.info("POST /api/products/{}/restock received -> ProductService.restock()", id);
        return productService.restock(id, request.quantity());
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable("id") Long id) {
        return productService.findById(id);
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }
}
