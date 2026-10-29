package com.company.boot.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.company.boot.dto.PageResponse;
import com.company.boot.dto.ProductRequest;
import com.company.boot.dto.ProductResponse;
import com.company.boot.exception.DuplicateResourceException;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    private static final String VALID_BODY =
            "{\"name\":\"Wireless Mouse\",\"category\":\"Electronics\",\"price\":799.00,\"stock\":50}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductResponse mouse;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        mouse = new ProductResponse(1L, "Wireless Mouse", "Electronics", new BigDecimal("799.00"), 50, now, now);
    }

    private PageResponse<ProductResponse> pageOf(ProductResponse... products) {
        return new PageResponse<>(List.of(products), 0, 5, products.length, 1, true, true, "id: ASC");
    }

    @Test
    void testCreate() throws Exception {
        when(productService.create(any(ProductRequest.class))).thenReturn(mouse);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Wireless Mouse")))
                .andExpect(jsonPath("$.price", is(799.00)));

        verify(productService).create(any(ProductRequest.class));
    }

    @Test
    void testCreateWithInvalidBody() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"category\":\"Electronics\",\"price\":-5,\"stock\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.validationErrors.name", is("name is required")))
                .andExpect(jsonPath("$.validationErrors.price", is("price must be greater than 0")))
                .andExpect(jsonPath("$.validationErrors.stock", is("stock cannot be negative")));
    }

    @Test
    void testCreateWithMalformedJson() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Request body is missing or is not valid JSON")));
    }

    @Test
    void testCreateDuplicate() throws Exception {
        when(productService.create(any(ProductRequest.class)))
                .thenThrow(new DuplicateResourceException("A product named 'Wireless Mouse' already exists"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", is("A product named 'Wireless Mouse' already exists")));
    }

    @Test
    void testGetById() throws Exception {
        when(productService.findById(1L)).thenReturn(mouse);

        mockMvc.perform(get("/api/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Wireless Mouse")))
                .andExpect(jsonPath("$.category", is("Electronics")))
                .andExpect(jsonPath("$.stock", is(50)));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(get("/api/products/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Product not found with id 99")))
                .andExpect(jsonPath("$.path", is("/api/products/99")));
    }

    @Test
    void testGetByIdWithNonNumericId() throws Exception {
        mockMvc.perform(get("/api/products/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Parameter 'id' has an invalid value 'abc'")));
    }

    @Test
    void testGetAll() throws Exception {
        when(productService.findAll(any(Pageable.class))).thenReturn(pageOf(mouse));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Wireless Mouse")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.first", is(true)));
    }

    @Test
    void testGetAllWhenEmpty() throws Exception {
        when(productService.findAll(any(Pageable.class))).thenReturn(pageOf());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void testGetAllByCategory() throws Exception {
        when(productService.findByCategory(eq("Electronics"), any(Pageable.class))).thenReturn(pageOf(mouse));

        mockMvc.perform(get("/api/products").param("category", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].category", is("Electronics")));

        verify(productService).findByCategory(eq("Electronics"), any(Pageable.class));
    }

    @Test
    void testSearch() throws Exception {
        when(productService.search(eq("mouse"), any(), any(), any(), any(Pageable.class))).thenReturn(pageOf(mouse));

        mockMvc.perform(get("/api/products/search").param("name", "mouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("Wireless Mouse")));
    }

    @Test
    void testUpdate() throws Exception {
        when(productService.update(eq(1L), any(ProductRequest.class))).thenReturn(mouse);

        mockMvc.perform(put("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Wireless Mouse")));

        verify(productService).update(eq(1L), any(ProductRequest.class));
    }

    @Test
    void testUpdateNotFound() throws Exception {
        when(productService.update(eq(99L), any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(put("/api/products/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productService).delete(1L);
    }

    @Test
    void testDeleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Product", 99L)).when(productService).delete(99L);

        mockMvc.perform(delete("/api/products/{id}", 99L))
                .andExpect(status().isNotFound());
    }

}
