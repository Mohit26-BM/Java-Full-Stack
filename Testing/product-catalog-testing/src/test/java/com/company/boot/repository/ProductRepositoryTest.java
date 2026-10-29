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
    void testSaveGeneratesIdAndTimestamps() {
        Product saved = productRepository.save(new Product("Desk Lamp", "Accessories", new BigDecimal("999.00"), 25));

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void testFindById() {
        Product found = productRepository.findById(mouse.getId()).orElseThrow();

        assertEquals("Wireless Mouse", found.getName());
        assertEquals(50, found.getStock());
    }

    @Test
    void testFindByIdWhenMissing() {
        assertTrue(productRepository.findById(99999L).isEmpty());
    }

    @Test
    void testFindAll() {
        assertEquals(3, productRepository.findAll().size());
    }

    @Test
    void testDelete() {
        productRepository.deleteById(mouse.getId());

        assertFalse(productRepository.existsById(mouse.getId()));
        assertEquals(2, productRepository.count());
    }

    @Test
    void testExistsByNameIgnoreCase() {
        assertTrue(productRepository.existsByNameIgnoreCase("WIRELESS MOUSE"));
        assertFalse(productRepository.existsByNameIgnoreCase("Unknown"));
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNot() {
        assertFalse(productRepository.existsByNameIgnoreCaseAndIdNot("wireless mouse", mouse.getId()));
        assertTrue(productRepository.existsByNameIgnoreCaseAndIdNot("wireless mouse", -1L));
    }

    @Test
    void testFindByCategoryIgnoreCase() {
        Page<Product> page = productRepository.findByCategoryIgnoreCase("electronics", PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
    }

    @Test
    void testFindByCategoryPaging() {
        Page<Product> page = productRepository.findByCategoryIgnoreCase("Electronics", PageRequest.of(0, 1));

        assertEquals(1, page.getContent().size());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void testSearchByName() {
        Page<Product> page = productRepository.search("mouse", null, null, null, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Wireless Mouse", page.getContent().get(0).getName());
    }

    @Test
    void testSearchByPriceRange() {
        Page<Product> page = productRepository.search(null, null, new BigDecimal("500"), new BigDecimal("1000"),
                PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Wireless Mouse", page.getContent().get(0).getName());
    }

    @Test
    void testSearchWithNoFiltersReturnsEverything() {
        Page<Product> page = productRepository.search(null, null, null, null, PageRequest.of(0, 10));

        assertEquals(3, page.getTotalElements());
    }

    @Test
    void testSearchWithNoMatch() {
        Page<Product> page = productRepository.search("zzz", null, null, null, PageRequest.of(0, 10));

        assertTrue(page.isEmpty());
    }

}
