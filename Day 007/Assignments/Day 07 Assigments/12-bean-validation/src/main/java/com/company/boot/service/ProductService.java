package com.company.boot.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.company.boot.dto.ProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.exception.BusinessRuleException;
import com.company.boot.repository.ProductRepository;

/**
 * BUSINESS RULES live here, not in annotations.
 * By the time create() runs, @Valid has already guaranteed the request is
 * well-formed. These rules need the DATABASE to decide:
 *   1. the category must exist                      -> 422
 *   2. price must not exceed the category's ceiling -> 422
 *   3. the SKU must not already be used             -> 409
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductRequest request) {
        String category = request.getCategory().trim().toUpperCase();

        BigDecimal maxPrice = productRepository.findCategoryMaxPrice(category)
                .orElseThrow(() -> new BusinessRuleException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unknown category '" + category + "'"));

        if (request.getPrice().compareTo(maxPrice) > 0) {
            throw new BusinessRuleException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Price " + request.getPrice() + " exceeds the " + category
                    + " category limit of " + maxPrice);
        }

        if (productRepository.skuExists(request.getSku())) {
            throw new BusinessRuleException(HttpStatus.CONFLICT,
                    "SKU " + request.getSku() + " is already registered");
        }

        int id = productRepository.insert(request, category);
        return productRepository.findById(id).orElseThrow();
    }

    public List<ProductResponse> search(String category, BigDecimal maxPrice) {
        return productRepository.search(category == null ? null : category.toUpperCase(), maxPrice);
    }
}
