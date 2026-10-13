package com.company.boot.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.company.boot.dto.ErrorResponse;

/** Error responses. The simulated failures are reported as 500 with their kind. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SimulatedFailureException.class)
    public ResponseEntity<ErrorResponse> handleSimulated(SimulatedFailureException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "unchecked", ex.getMessage());
    }

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ErrorResponse> handleChecked(TransferException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "checked", ex.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(AccountNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "unchecked", ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleFunds(InsufficientFundsException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "unchecked", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "unchecked", ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, null, "Request body is missing or invalid (check 'mode' spelling)");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String kind, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(LocalDateTime.now().withNano(0),
                status.value(), status.getReasonPhrase(), kind, message));
    }
}
