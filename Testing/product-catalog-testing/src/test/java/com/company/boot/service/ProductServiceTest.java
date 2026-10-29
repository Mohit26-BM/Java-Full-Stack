package com.company.boot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.company.boot.dto.PageResponse;
import com.company.boot.dto.ProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.entity.Product;
import com.company.boot.exception.DuplicateResourceException;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService service;

    private ProductRequest request(String name, String category, String price, int stock) {
        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setCategory(category);
        request.setPrice(new BigDecimal(price));
        request.setStock(stock);
        return request;
    }

    @Test
    void testCreate() {
        when(productRepository.existsByNameIgnoreCase("Wireless Mouse")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = service.create(request("  Wireless Mouse ", " Electronics ", "799.00", 50));

        assertEquals("Wireless Mouse", response.name());
        assertEquals("Electronics", response.category());
        assertEquals(new BigDecimal("799.00"), response.price());
        assertEquals(50, response.stock());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testCreateDuplicateName() {
        when(productRepository.existsByNameIgnoreCase("Wireless Mouse")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.create(request("Wireless Mouse", "Electronics", "799.00", 50)));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testFindById() {
        Product product = new Product("Clean Code", "Books", new BigDecimal("450.00"), 30);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = service.findById(1L);

        assertEquals("Clean Code", response.name());
        assertEquals("Books", response.category());
    }

    @Test
    void testFindByIdNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));

        assertEquals("Product not found with id 99", ex.getMessage());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = new PageImpl<>(List.of(
                new Product("Clean Code", "Books", new BigDecimal("450.00"), 30),
                new Product("Effective Java", "Books", new BigDecimal("899.00"), 18)), pageable, 2);
        when(productRepository.findAll(pageable)).thenReturn(page);

        PageResponse<ProductResponse> response = service.findAll(pageable);

        assertEquals(2, response.content().size());
        assertEquals(2, response.totalElements());
        assertEquals("Effective Java", response.content().get(1).name());
    }

    @Test
    void testFindByCategory() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = new PageImpl<>(
                List.of(new Product("Clean Code", "Books", new BigDecimal("450.00"), 30)), pageable, 1);
        when(productRepository.findByCategoryIgnoreCase("books", pageable)).thenReturn(page);

        PageResponse<ProductResponse> response = service.findByCategory("books", pageable);

        assertEquals(1, response.content().size());
    }

    @Test
    void testSearchTurnsBlankFiltersIntoNull() {
        Pageable pageable = PageRequest.of(0, 5);
        when(productRepository.search(null, "Books", null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        PageResponse<ProductResponse> response = service.search("  ", " Books ", null, null, pageable);

        assertEquals(0, response.totalElements());
        verify(productRepository).search(null, "Books", null, null, pageable);
    }

    @Test
    void testUpdate() {
        Product existing = new Product("Old Name", "Books", new BigDecimal("100.00"), 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsByNameIgnoreCaseAndIdNot("New Name", 1L)).thenReturn(false);

        ProductResponse response = service.update(1L, request("New Name", "Electronics", "250.00", 9));

        assertEquals("New Name", response.name());
        assertEquals("Electronics", response.category());
        assertEquals(new BigDecimal("250.00"), response.price());
        assertEquals(9, response.stock());
        verify(productRepository).flush();
    }

    @Test
    void testUpdateNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(99L, request("New Name", "Books", "10.00", 1)));
    }

    @Test
    void testUpdateDuplicateName() {
        Product existing = new Product("Old Name", "Books", new BigDecimal("100.00"), 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsByNameIgnoreCaseAndIdNot("Taken", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.update(1L, request("Taken", "Books", "10.00", 1)));

        verify(productRepository, never()).flush();
    }

    @Test
    void testDelete() {
        Product existing = new Product("Clean Code", "Books", new BigDecimal("450.00"), 30);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(productRepository).delete(existing);
    }

    @Test
    void testDeleteNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));

        verify(productRepository, never()).delete(any(Product.class));
    }

}
