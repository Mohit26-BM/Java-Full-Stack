package com.company.boot.service;

import java.util.List;

import com.company.boot.dto.CreateProductRequest;
import com.company.boot.dto.ProductResponse;

/**
 * SERVICE LAYER contract (optional extension: service interface).
 * Controllers depend on this interface, not on the implementation class,
 * so the implementation can be replaced or mocked in tests.
 */
public interface ProductService {

    ProductResponse create(CreateProductRequest request);

    /** Bulk import - reuses exactly the same rules as create(). */
    List<String> importProducts(List<CreateProductRequest> requests);

    ProductResponse restock(Long productId, Integer quantity);

    ProductResponse findById(Long productId);

    List<ProductResponse> findAll();
}
