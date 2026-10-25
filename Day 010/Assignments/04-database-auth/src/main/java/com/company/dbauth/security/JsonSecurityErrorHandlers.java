package com.company.dbauth.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.company.dbauth.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 401 / 403 as JSON. The 401 message depends on WHY the login failed:
 *
 *   BadCredentialsException  wrong password OR unknown user (deliberately the same text)
 *   DisabledException        users.enabled = false
 *   LockedException          users.locked_until is in the future
 *   anything else            no credentials were sent
 */
@Component
public class JsonSecurityErrorHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JsonSecurityErrorHandlers(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        String message;
        if (exception instanceof BadCredentialsException) {
            message = "Invalid username or password";
        } else if (exception instanceof DisabledException) {
            message = "Account is disabled";
        } else if (exception instanceof LockedException) {
            message = "Account is temporarily locked after too many failed logins";
        } else {
            message = "Authentication is required to access this resource";
        }
        response.setHeader("WWW-Authenticate", "Basic");
        write(response, HttpStatus.UNAUTHORIZED, message, request);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        write(response, HttpStatus.FORBIDDEN, "You do not have permission to access this resource", request);
    }

    private void write(HttpServletResponse response, HttpStatus status, String message,
                       HttpServletRequest request) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message,
                        request.getRequestURI()));
    }
}
