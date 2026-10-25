package com.example.portal.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/** The same error format for every failure, including 401 and 403. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(LocalDateTime timestamp,
                            int status,
                            String error,
                            String message,
                            String path,
                            Map<String, String> fieldErrors) {
}
