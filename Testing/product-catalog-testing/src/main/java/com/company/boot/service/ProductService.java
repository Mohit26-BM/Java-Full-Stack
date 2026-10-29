package com.company.boot.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.boot.dto.PageResponse;
import com.company.boot.dto.ProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.entity.Product;
import com.company.boot.exception.DuplicateResourceException;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.repository.ProductRepository;

/**
 * CRUD business logic. Only ProductRepository is used - no JDBC, no SQL.
 * @Transactional(readOnly = true) on the class; write methods override it.
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ---------------- CREATE ----------------

    @Transactional
    public ProductResponse create(ProductRequest request) {
        String name = request.getName().trim();
        if (productRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("A product named '" + name + "' already exists");
        }
        Product product = new Product(name, request.getCategory().trim(), request.getPrice(), request.getStock());
        Product saved = productRepository.save(product);        // -> INSERT, id filled in
        return ProductResponse.from(saved);
    }

    // ---------------- READ ----------------

    public ProductResponse findById(Long id) {
        return ProductResponse.from(getOrThrow(id));
    }

    public PageResponse<ProductResponse> findAll(Pageable pageable) {
        return PageResponse.from(productRepository.findAll(pageable), ProductResponse::from);
    }

    public PageResponse<ProductResponse> findByCategory(String category, Pageable pageable) {
        return PageResponse.from(productRepository.findByCategoryIgnoreCase(category, pageable), ProductResponse::from);
    }

    public PageResponse<ProductResponse> search(String name, String category, BigDecimal minPrice,
                                                BigDecimal maxPrice, Pageable pageable) {
        return PageResponse.from(
                productRepository.search(blankToNull(name), blankToNull(category), minPrice, maxPrice, pageable),
                ProductResponse::from);
    }

    // ---------------- UPDATE ----------------

    /**
     * No save() call is needed: the entity loaded inside this transaction is
     * "managed", so Hibernate detects the changed fields (dirty checking) and
     * issues the UPDATE automatically at commit.
     */
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getOrThrow(id);
        String name = request.getName().trim();
        if (productRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException("A product named '" + name + "' already exists");
        }
        product.setName(name);
        product.setCategory(request.getCategory().trim());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        productRepository.flush();      // send the UPDATE now so updatedAt in the response is current
        return ProductResponse.from(product);
    }

    // ---------------- DELETE ----------------

    @Transactional
    public void delete(Long id) {
        Product product = getOrThrow(id);
        productRepository.delete(product);                       // -> DELETE at commit
    }

    private Product getOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
