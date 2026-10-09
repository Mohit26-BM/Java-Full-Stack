package com.example.flightcrud.exception;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(FlightNotFoundException.class)
	ResponseEntity<Map<String, Object>> notFound(FlightNotFoundException e) {
		return response(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
		Map<String, Object> b = new LinkedHashMap<>();
		b.put("timestamp", LocalDateTime.now());
		b.put("status", 400);
		b.put("error", "Validation Failed");
		
		Map<String, String> errors = new LinkedHashMap<>();
		
		e.getBindingResult()
		 .getFieldErrors()
		 .forEach(x -> errors.put(x.getField(), x.getDefaultMessage()));
		b.put("errors", errors);
		return ResponseEntity.badRequest().body(b);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<Map<String, Object>> constraint(ConstraintViolationException e) {
		return response(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<Map<String, Object>> general(Exception e) {
		return response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	private ResponseEntity<Map<String, Object>> response(HttpStatus s, String m) {
		Map<String, Object> b = new LinkedHashMap<>();
		b.put("timestamp", LocalDateTime.now());
		b.put("status", s.value());
		b.put("error", s.getReasonPhrase());
		b.put("message", m);
		return ResponseEntity.status(s).body(b);
	}
}
