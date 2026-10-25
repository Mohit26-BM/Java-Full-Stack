package com.example.hospital.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The same error format for every failure:
 * timestamp, status, errorCode, message, path
 * (+ validationErrors, only when request fields are invalid).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(LocalDateTime timestamp,
                            int status,
                            String errorCode,
                            String message,
                            String path,
                            Map<String, String> validationErrors) {
}
