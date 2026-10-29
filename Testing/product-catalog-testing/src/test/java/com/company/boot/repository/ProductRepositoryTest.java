package com.company.boot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.company.boot.entity.Product;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product mouse;

    @BeforeEach
    void setUp() {
        mouse = productRepository.save(new Product("Wireless Mouse", "Electronics", new BigDecimal("799.00"), 50));
        productRepository.save(new Product("Mechanical Keyboard", "Electronics", new BigDecimal("3499.00"), 20));
        productRepository.save(new Product("Clean Code", "Books", new BigDecimal("450.00"), 30));
    }

    @Test
    void testSave() {
        assertNotNull(mouse.getId());
    }

    @Test
    void testFindById() {
        Product found = productRepository.findById(mouse.getId()).orElseThrow();

        assertEquals("Wireless Mouse", found.getName());
    }

    @Test
    void testFindAll() {
        assertEquals(3, productRepository.findAll().size());
    }

    @Test
    void testDelete() {
        productRepository.deleteById(mouse.getId());

        assertFalse(productRepository.existsById(mouse.getId()));
    }

    @Test
    void testSearchByName() {
        Page<Product> page = productRepository.search("mouse", null, null, null, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertTrue(page.getContent().get(0).getName().contains("Mouse"));
    }

}
