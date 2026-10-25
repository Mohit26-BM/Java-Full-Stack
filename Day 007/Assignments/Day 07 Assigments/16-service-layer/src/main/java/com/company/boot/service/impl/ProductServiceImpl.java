package com.company.boot.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.company.boot.dto.CreateProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.entity.Product;
import com.company.boot.exception.BusinessRuleException;
import com.company.boot.exception.DuplicateResourceException;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.repository.ProductRepository;
import com.company.boot.service.ProductService;

/**
 * SERVICE LAYER - all product business rules live here:
 *   R1 name is required, at most 100 characters
 *   R2 name must be unique (case-insensitive)
 *   R3 price is required and must be > 0
 *   R4 price may have at most 2 decimal places
 *   R5 stock is required and must be >= 0
 *   R6 restock quantity must be > 0
 *   R7 stock may never exceed the warehouse capacity (10 000)
 */
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    public static final int MAX_STOCK = 10_000;

    private static final Logger log = LoggerFactory.getLogger("SERVICE");

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        log.info("create product: checking rules R1-R5 and R7");
        String name = requireName(request.name());
        requireValidPrice(request.price());
        requireValidInitialStock(request.stock());

        if (productRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("A product named '" + name + "' already exists");
        }
        log.info("all rules passed -> calling repository.save()");
        Product saved = productRepository.save(new Product(name, request.price(), request.stock()));
        log.info("product saved with id {}", saved.getId());
        return ProductResponse.from(saved);
    }

    /**
     * A second entry point (bulk import) - it calls create(), so it gets the SAME rules
     * without duplicating a single check. Each item is reported separately.
     *
     * NOT_SUPPORTED: no surrounding transaction (the class default is read-only), so
     * every saved item commits on its own and one bad item does not undo the good ones.
     * (create() is called on "this", so its own @Transactional is NOT applied - the
     * repository methods use their own transactions.)
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<String> importProducts(List<CreateProductRequest> requests) {
        List<String> results = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            try {
                ProductResponse created = create(requests.get(i));
                results.add("item " + i + ": created id " + created.id());
            } catch (BusinessRuleException ex) {
                results.add("item " + i + ": REJECTED " + ex.getRule() + " - " + ex.getMessage());
            } catch (DuplicateResourceException ex) {
                results.add("item " + i + ": REJECTED NAME_MUST_BE_UNIQUE - " + ex.getMessage());
            }
        }
        return results;
    }

    @Override
    @Transactional
    public ProductResponse restock(Long productId, Integer quantity) {
        log.info("restock product {} by {}: checking rules R6, R7", productId, quantity);
        if (quantity == null || quantity <= 0) {
            throw new BusinessRuleException("RESTOCK_QUANTITY_MUST_BE_POSITIVE",
                    "Restock quantity must be greater than 0 (got " + quantity + ")");
        }
        Product product = getProduct(productId);
        int newStock = product.getStock() + quantity;
        if (newStock > MAX_STOCK) {
            throw new BusinessRuleException("STOCK_CAPACITY_EXCEEDED",
                    "Stock would become " + newStock + ", above the warehouse capacity of " + MAX_STOCK,
                    BusinessRuleException.Type.STATE_CONFLICT);
        }
        product.setStock(newStock);            // dirty checking -> UPDATE at commit
        log.info("rules passed -> stock {} -> {}", newStock - quantity, newStock);
        return ProductResponse.from(product);
    }

    @Override
    public ProductResponse findById(Long productId) {
        return ProductResponse.from(getProduct(productId));
    }

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(ProductResponse::from).toList();
    }

    // ---------------- rules ----------------

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("NAME_REQUIRED", "Product name is required");
        }
        String trimmed = name.trim();
        if (trimmed.length() > 100) {
            throw new BusinessRuleException("NAME_TOO_LONG", "Product name must be at most 100 characters");
        }
        return trimmed;
    }

    private void requireValidPrice(BigDecimal price) {
        if (price == null || price.signum() <= 0) {
            throw new BusinessRuleException("PRICE_MUST_BE_POSITIVE",
                    "Price must be greater than 0 (got " + price + ")");
        }
        if (price.stripTrailingZeros().scale() > 2) {
            throw new BusinessRuleException("PRICE_MAX_TWO_DECIMALS",
                    "Price may have at most 2 decimal places (got " + price + ")");
        }
    }

    private void requireValidInitialStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new BusinessRuleException("STOCK_CANNOT_BE_NEGATIVE",
                    "Stock must be 0 or more (got " + stock + ")");
        }
        if (stock > MAX_STOCK) {
            throw new BusinessRuleException("STOCK_CAPACITY_EXCEEDED",
                    "Stock " + stock + " is above the warehouse capacity of " + MAX_STOCK);
        }
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }
}
