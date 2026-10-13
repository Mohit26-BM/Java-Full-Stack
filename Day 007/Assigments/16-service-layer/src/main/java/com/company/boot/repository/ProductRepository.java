package com.company.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.boot.entity.Product;

/**
 * REPOSITORY LAYER - persistence only: load and store rows.
 * It knows nothing about HTTP and enforces no business rules.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByNameIgnoreCase(String name);
}
