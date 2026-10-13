package com.company.boot.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Uniform error body. "rule" names the business rule that was broken (when there is one). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(LocalDateTime timestamp,
                            int status,
                            String error,
                            String rule,
                            String message,
                            String path) {
}
