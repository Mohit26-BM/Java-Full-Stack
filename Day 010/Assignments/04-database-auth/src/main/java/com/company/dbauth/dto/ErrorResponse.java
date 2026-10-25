package com.company.dbauth.dto;

import java.time.Instant;

/** The one JSON error body of the API: security errors (401 / 403) and controller errors (400 / 404 / 409). */
public record ErrorResponse(Instant timestamp,
                            int status,
                            String error,
                            String message,
                            String path) {
}
