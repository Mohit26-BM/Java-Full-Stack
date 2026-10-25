package com.company.boot.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.boot.dto.OrderResponse;
import com.company.boot.dto.PlaceOrderRequest;
import com.company.boot.entity.Product;
import com.company.boot.entity.PurchaseOrder;
import com.company.boot.exception.BusinessRuleException;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.repository.OrderRepository;
import com.company.boot.repository.ProductRepository;

/** OPTIONAL EXTENSION - unit tests of OrderServiceImpl with mocked repositories. */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Product keyboard;

    @BeforeEach
    void setUp() {
        keyboard = new Product("Mechanical Keyboard", new BigDecimal("3499.00"), 5);
        keyboard.setId(2L);
    }

    @Test
    void placeOrderWithoutDiscountDecreasesStock() {
        when(productRepository.findById(2L)).thenReturn(Optional.of(keyboard));
        when(orderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse order = orderService.placeOrder(new PlaceOrderRequest(2L, 1));

        assertThat(order.total()).isEqualByComparingTo("3499.00");
        assertThat(order.discount()).isEqualByComparingTo("0");
        assertThat(keyboard.getStock()).isEqualTo(4);
    }

    @Test
    void placeOrderOf5000OrMoreGets10PercentDiscount() {
        when(productRepository.findById(2L)).thenReturn(Optional.of(keyboard));
        when(orderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse order = orderService.placeOrder(new PlaceOrderRequest(2L, 2));   // 6998.00

        assertThat(order.discount()).isEqualByComparingTo("699.80");
        assertThat(order.total()).isEqualByComparingTo("6298.20");
    }

    @Test
    void placeOrderRejectsQuantityOutsideOneToTen() {
        for (Integer quantity : new Integer[] {0, -3, 11, null}) {
            assertThatThrownBy(() -> orderService.placeOrder(new PlaceOrderRequest(2L, quantity)))
                    .isInstanceOf(BusinessRuleException.class)
                    .extracting("rule").isEqualTo("QUANTITY_OUT_OF_RANGE");
        }
        verify(productRepository, never()).findById(any());       // rejected before any database call
    }

    @Test
    void placeOrderRejectsInsufficientStockAndChangesNothing() {
        when(productRepository.findById(2L)).thenReturn(Optional.of(keyboard));

        assertThatThrownBy(() -> orderService.placeOrder(new PlaceOrderRequest(2L, 6)))
                .extracting("rule").isEqualTo("INSUFFICIENT_STOCK");
        assertThat(keyboard.getStock()).isEqualTo(5);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderForUnknownProductThrowsNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.placeOrder(new PlaceOrderRequest(99L, 1)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found with id 99");
    }
}
