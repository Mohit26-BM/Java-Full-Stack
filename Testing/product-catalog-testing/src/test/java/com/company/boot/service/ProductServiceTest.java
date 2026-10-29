package com.company.boot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private ProductRequest mouseRequest() {
        ProductRequest request = new ProductRequest();
        request.setName("Wireless Mouse");
        request.setCategory("Electronics");
        request.setPrice(new BigDecimal("799.00"));
        request.setStock(50);
        return request;
    }

    @Test
    void testCreate() {
        when(productRepository.existsByNameIgnoreCase("Wireless Mouse")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductResponse response = service.create(mouseRequest());

        assertEquals("Wireless Mouse", response.name());
        assertEquals(50, response.stock());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testCreateDuplicateName() {
        when(productRepository.existsByNameIgnoreCase("Wireless Mouse")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(mouseRequest()));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testFindById() {
        Product product = new Product("Clean Code", "Books", new BigDecimal("450.00"), 30);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = service.findById(1L);

        assertEquals("Clean Code", response.name());
    }

    @Test
    void testFindByIdNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void testDelete() {
        Product product = new Product("Clean Code", "Books", new BigDecimal("450.00"), 30);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);

        verify(productRepository).delete(product);
    }

}
