package com.company.boot.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.company.boot.entity.Product;

/**
 * NO implementation class is written.
 * Spring Data creates one at startup. JpaRepository<Product, Long> already provides:
 *   save(), findById(), findAll(), findAll(Pageable), existsById(), deleteById(), count() ...
 *
 * Extra queries are declared below - Spring Data builds them from the METHOD NAME
 * or from the JPQL in @Query (JPQL works on entities/fields, not tables/columns).
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Derived query: ... WHERE upper(name) = upper(?)  - used for the duplicate check. */
    boolean existsByNameIgnoreCase(String name);

    /** Derived query with an id exclusion - duplicate check on update. */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /** Derived query + pagination: ... WHERE upper(category) = upper(?) LIMIT ?, ? */
    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

    /** OPTIONAL EXTENSION - search: every filter is optional (null = ignore). */
    @Query("""
            SELECT p FROM Product p
            WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:category IS NULL OR LOWER(p.category) = LOWER(:category))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> search(@Param("name") String name,
                         @Param("category") String category,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         Pageable pageable);
}
