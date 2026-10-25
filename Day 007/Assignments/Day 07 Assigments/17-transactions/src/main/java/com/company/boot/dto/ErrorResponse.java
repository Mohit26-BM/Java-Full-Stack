package com.company.boot.dto;

import java.time.LocalDateTime;

/** Uniform error body. exceptionKind = "checked" / "unchecked" for the experiments. */
public record ErrorResponse(LocalDateTime timestamp,
                            int status,
                            String error,
                            String exceptionKind,
                            String message) {
}
