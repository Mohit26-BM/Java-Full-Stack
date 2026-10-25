package com.example.product.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.product.model.Product;

/**
 * JpaRepository provides save, findById, findAll, delete ...
 * search() adds filtering. Every filter is OPTIONAL: when a parameter is null,
 * its condition is always true and therefore has no effect.
 * The Pageable adds ORDER BY and LIMIT/OFFSET, and Spring Data also runs a
 * COUNT query for the total number of matches.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            WHERE (:category IS NULL OR LOWER(p.category) = LOWER(:category))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:active IS NULL OR p.active = :active)
            """)
    Page<Product> search(@Param("category") String category,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         @Param("name") String name,
                         @Param("active") Boolean active,
                         Pageable pageable);
}
