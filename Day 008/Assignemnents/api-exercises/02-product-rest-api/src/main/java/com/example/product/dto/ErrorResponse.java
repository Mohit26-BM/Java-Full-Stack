package com.example.product.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The standard error structure: timestamp, status, message, path.
 * fieldErrors is added only when request fields are invalid.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(LocalDateTime timestamp,
                            int status,
                            String message,
                            String path,
                            Map<String, String> fieldErrors) {
}
