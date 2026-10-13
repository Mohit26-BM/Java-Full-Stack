package com.company.boot.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.company.boot.dto.ErrorResponse;

/**
 * Turns request problems into clear JSON error responses.
 *
 * - malformed / unreadable JSON      -> HttpMessageNotReadableException  -> 400
 * - valid JSON but missing/bad data  -> MethodArgumentNotValidException  -> 400 + fieldErrors
 * - duplicate e-mail                 -> DuplicateKeyException            -> 409
 * - body is not JSON                 -> HttpMediaTypeNotSupportedException -> 415
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        if (ex.getMessage() != null && ex.getMessage().startsWith("Required request body is missing")) {
            return build(HttpStatus.BAD_REQUEST, "Request body is missing", null);
        }
        String detail = ex.getMostSpecificCause().getMessage();
        // keep only Jackson's description, not the source-location noise after it
        for (String noise : new String[] {"\n", " (start marker", " at [Source"}) {
            int cut = detail.indexOf(noise);
            if (cut > 0) {
                detail = detail.substring(0, cut);
            }
        }
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request: " + detail, null);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateKeyException ex) {
        return build(HttpStatus.CONFLICT, "An employee with this email already exists", null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type '" + ex.getContentType() + "' is not supported; send application/json", null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, Map<String, String> fieldErrors) {
        ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message, fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
