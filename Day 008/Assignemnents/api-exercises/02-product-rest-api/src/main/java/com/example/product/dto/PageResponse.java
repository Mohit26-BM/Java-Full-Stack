package com.example.product.dto;

import java.util.List;

/** One page of results plus pagination metadata. */
public record PageResponse<T>(List<T> content,
                              int page,
                              int size,
                              long totalElements,
                              int totalPages,
                              String sortBy,
                              String direction) {
}
