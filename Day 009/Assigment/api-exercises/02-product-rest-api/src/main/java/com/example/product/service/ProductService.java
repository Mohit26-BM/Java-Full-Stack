package com.example.product.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.product.dto.PageResponse;
import com.example.product.dto.ProductRequest;
import com.example.product.dto.ProductResponse;
import com.example.product.dto.ProductUpdateRequest;
import com.example.product.exception.InvalidQueryException;
import com.example.product.exception.ProductModifiedException;
import com.example.product.exception.ProductNotFoundException;
import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;

/** Product operations: CRUD, search with pagination and sorting, inventory update. */
@Service
@Transactional(readOnly = true)
public class ProductService {

    /** The only fields a client may sort by. */
    private static final List<String> SORTABLE_FIELDS = List.of("id", "name", "category", "price", "quantity", "brand");
    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        boolean active = request.active() == null ? true : request.active();
        Product product = new Product(request.name().trim(), request.category().trim(), request.price(),
                request.quantity(), request.brand(), active);
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProduct(id));
    }

    /**
     * Filtering + pagination + sorting.
     * Checks the parameters first, then builds a PageRequest (page, size, sort).
     */
    public PageResponse<ProductResponse> search(String category, BigDecimal minPrice, BigDecimal maxPrice,
                                                String name, Boolean active,
                                                int page, int size, String sortBy, String direction) {
        if (page < 0) {
            throw new InvalidQueryException("page must be 0 or greater");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidQueryException("size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (!SORTABLE_FIELDS.contains(sortBy)) {
            throw new InvalidQueryException("sortBy must be one of " + SORTABLE_FIELDS);
        }
        Sort.Direction sortDirection;
        if (direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else if (direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new InvalidQueryException("direction must be asc or desc");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidQueryException("minPrice must not be greater than maxPrice");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Product> result = productRepository.search(blankToNull(category), minPrice, maxPrice,
                blankToNull(name), active, pageRequest);

        return new PageResponse<>(
                result.getContent().stream().map(ProductResponse::from).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                sortBy,
                sortDirection.name().toLowerCase());
    }

    /** PUT: replaces every field, but only if the client's version is still current. */
    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = getProduct(id);
        if (!product.getVersion().equals(request.version())) {
            throw new ProductModifiedException(id, request.version(), product.getVersion());
        }
        product.setName(request.name().trim());
        product.setCategory(request.category().trim());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setBrand(request.brand());
        product.setActive(request.active());
        // send the UPDATE now so the response already contains the new version number
        productRepository.saveAndFlush(product);
        return ProductResponse.from(product);
    }

    /** PATCH: changes only the inventory quantity. */
    @Transactional
    public ProductResponse updateQuantity(Long id, Integer quantity) {
        Product product = getProduct(id);
        product.setQuantity(quantity);
        productRepository.saveAndFlush(product);
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(getProduct(id));
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
