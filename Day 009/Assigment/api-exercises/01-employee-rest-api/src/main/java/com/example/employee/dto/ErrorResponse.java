package com.example.employee.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/** The same error format for every failure. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(LocalDateTime timestamp,
                            int status,
                            String error,
                            String message,
                            String path,
                            Map<String, String> fieldErrors) {
}
