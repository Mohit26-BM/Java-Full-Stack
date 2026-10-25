package com.company.secureapi.exception;

import java.time.Instant;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import com.company.secureapi.config.CorrelationIdFilter;

public record ApiError(Instant timestamp, int status, String error, String code, String message,
                       String path, String correlationId) {
    public static ApiError of(HttpStatus status, ErrorCode code, String message, String path) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), code.name(), message, path,
                MDC.get(CorrelationIdFilter.MDC_KEY));
    }
}
