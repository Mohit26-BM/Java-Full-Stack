package com.company.security.dto;

import java.time.Instant;

/** JSON body for security errors of the REST API (401 / 403). */
public record ErrorResponse(Instant timestamp,
                            int status,
                            String error,
                            String message,
                            String path) {
}
