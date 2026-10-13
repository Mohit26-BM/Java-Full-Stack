package com.company.boot.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.company.boot.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

/** Turns exceptions from any layer into HTTP responses (part of the web layer). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger("WEB-ERROR");

    /** The WEB layer decides the HTTP status - the service only says what kind of rule failed. */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleRule(BusinessRuleException ex, HttpServletRequest request) {
        HttpStatus status = ex.getType() == BusinessRuleException.Type.STATE_CONFLICT
                ? HttpStatus.UNPROCESSABLE_ENTITY
                : HttpStatus.BAD_REQUEST;
        log.info("rule {} broken -> {}", ex.getRule(), status.value());
        return build(status, ex.getRule(), ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, null, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "NAME_MUST_BE_UNIQUE", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, null,
                "Parameter '" + ex.getName() + "' has an invalid value '" + ex.getValue() + "'", request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, null, "Request body is missing or is not valid JSON", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String rule, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(LocalDateTime.now().withNano(0), status.value(),
                status.getReasonPhrase(), rule, message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
