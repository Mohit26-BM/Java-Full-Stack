package com.company.boot.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.company.boot.dto.CreateProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.entity.Product;
import com.company.boot.exception.BusinessRuleException;
import com.company.boot.exception.DuplicateResourceException;
import com.company.boot.repository.ProductRepository;

/**
 * OPTIONAL EXTENSION - unit tests of the service layer ALONE.
 * No Spring context, no database, no HTTP: the repository is a Mockito mock.
 * This is possible only because the rules live in the service.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createSavesProductWhenAllRulesPass() {
        when(productRepository.existsByNameIgnoreCase("Webcam")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(42L);
            return product;
        });

        ProductResponse response = productService.create(new CreateProductRequest("  Webcam ", new BigDecimal("2499.00"), 10));

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.name()).isEqualTo("Webcam");        // trimmed by the service
    }

    @Test
    void createRejectsZeroOrNegativePrice() {
        for (String price : new String[] {"0", "-10"}) {
            assertThatThrownBy(() -> productService.create(new CreateProductRequest("Webcam", new BigDecimal(price), 10)))
                    .isInstanceOf(BusinessRuleException.class)
                    .extracting("rule").isEqualTo("PRICE_MUST_BE_POSITIVE");
        }
        verify(productRepository, never()).save(any());          // nothing reached the repository
    }

    @Test
    void createRejectsPriceWithThreeDecimals() {
        assertThatThrownBy(() -> productService.create(new CreateProductRequest("Webcam", new BigDecimal("9.999"), 10)))
                .extracting("rule").isEqualTo("PRICE_MAX_TWO_DECIMALS");
    }

    @Test
    void createRejectsNegativeStock() {
        assertThatThrownBy(() -> productService.create(new CreateProductRequest("Webcam", BigDecimal.TEN, -1)))
                .extracting("rule").isEqualTo("STOCK_CANNOT_BE_NEGATIVE");
    }

    @Test
    void createRejectsBlankName() {
        assertThatThrownBy(() -> productService.create(new CreateProductRequest("   ", BigDecimal.TEN, 1)))
                .extracting("rule").isEqualTo("NAME_REQUIRED");
    }

    @Test
    void createRejectsDuplicateName() {
        when(productRepository.existsByNameIgnoreCase("Wireless Mouse")).thenReturn(true);

        assertThatThrownBy(() -> productService.create(new CreateProductRequest("Wireless Mouse", BigDecimal.TEN, 1)))
                .isInstanceOf(DuplicateResourceException.class);
        verify(productRepository, never()).save(any());
    }

    @Test
    void restockRejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> productService.restock(1L, 0))
                .extracting("rule").isEqualTo("RESTOCK_QUANTITY_MUST_BE_POSITIVE");
    }

    @Test
    void restockRejectsGoingAboveCapacity() {
        Product product = new Product("Mouse", BigDecimal.TEN, 9_995);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.restock(1L, 10))
                .extracting("rule").isEqualTo("STOCK_CAPACITY_EXCEEDED");
        assertThat(product.getStock()).isEqualTo(9_995);         // unchanged
    }

    @Test
    void importAppliesTheSameRulesPerItem() {
        when(productRepository.existsByNameIgnoreCase(any())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(7L);
            return product;
        });

        List<String> results = productService.importProducts(List.of(
                new CreateProductRequest("Good", BigDecimal.ONE, 1),
                new CreateProductRequest("Bad", new BigDecimal("-5"), 1)));

        assertThat(results).containsExactly(
                "item 0: created id 7",
                "item 1: REJECTED PRICE_MUST_BE_POSITIVE - Price must be greater than 0 (got -5)");
    }
}
