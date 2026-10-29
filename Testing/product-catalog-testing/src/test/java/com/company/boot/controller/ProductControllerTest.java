package com.company.boot.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.company.boot.dto.ProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductResponse mouse;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        mouse = new ProductResponse(1L, "Wireless Mouse", "Electronics", new BigDecimal("799.00"), 50, now, now);
    }

    @Test
    void testCreate() throws Exception {
        when(productService.create(any(ProductRequest.class))).thenReturn(mouse);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Wireless Mouse\",\"category\":\"Electronics\",\"price\":799.00,\"stock\":50}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Wireless Mouse")));

        verify(productService).create(any(ProductRequest.class));
    }

    @Test
    void testCreateWithInvalidBody() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"category\":\"Electronics\",\"price\":799.00,\"stock\":50}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetById() throws Exception {
        when(productService.findById(1L)).thenReturn(mouse);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Wireless Mouse")));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).delete(1L);
    }

}
