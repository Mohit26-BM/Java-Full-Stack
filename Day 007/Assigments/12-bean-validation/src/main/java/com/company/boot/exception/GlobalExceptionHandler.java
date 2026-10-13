package com.company.boot.exception;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.company.boot.dto.ErrorResponse;
import com.company.boot.dto.ErrorResponse.Violation;

/**
 * ONE place that converts validation failures into JSON - the controllers
 * and DTOs contain no error-building code at all.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** @Valid @RequestBody failed: collect EVERY violated constraint. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex) {
        List<Violation> violations = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            violations.add(new Violation(error.getField(), error.getRejectedValue(), error.getDefaultMessage()));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            violations.add(new Violation("", null, error.getDefaultMessage()));
        }
        violations.sort(Comparator.comparing(Violation::field));
        return build(HttpStatus.BAD_REQUEST, "Validation failed: " + violations.size() + " error(s)", violations);
    }

    /** Constraints directly on @RequestParam / @PathVariable failed (Spring 6.1+ method validation). */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleParameterValidation(HandlerMethodValidationException ex) {
        List<Violation> violations = new ArrayList<>();
        ex.getParameterValidationResults().forEach(result -> {
            RequestParam requestParam = result.getMethodParameter().getParameterAnnotation(RequestParam.class);
            String name = (requestParam != null && !requestParam.name().isEmpty())
                    ? requestParam.name()
                    : result.getMethodParameter().getParameterName();
            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                violations.add(new Violation(name, result.getArgument(), error.getDefaultMessage()));
            }
        });
        return build(HttpStatus.BAD_REQUEST, "Invalid request parameter(s)", violations);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex) {
        return build(ex.getStatus(), ex.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, List<Violation> violations) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message, violations));
    }
}
