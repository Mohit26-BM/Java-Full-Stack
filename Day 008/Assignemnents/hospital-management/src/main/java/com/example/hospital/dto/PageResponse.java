package com.example.hospital.dto;

import java.util.List;

/**
 * One page of results plus the pagination metadata.
 * (Spring's Page object is converted to this simple, stable JSON shape.)
 */
public record PageResponse<T>(List<T> content,
                              int page,
                              int size,
                              long totalElements,
                              int totalPages,
                              boolean first,
                              boolean last,
                              String sort) {
}
