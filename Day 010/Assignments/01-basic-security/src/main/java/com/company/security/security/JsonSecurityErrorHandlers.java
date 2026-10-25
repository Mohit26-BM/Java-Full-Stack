package com.company.security.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.company.security.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom authentication-failure response for the REST API.
 *
 * Without this, a failed HTTP Basic login returns 401 with an EMPTY body and a
 * WWW-Authenticate header, which makes a browser show its own login pop-up.
 * API clients get a JSON body instead.
 *
 *   commence() - AuthenticationEntryPoint: not logged in, or wrong credentials -> 401
 *   handle()   - AccessDeniedHandler: logged in but not allowed           -> 403
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
        // BadCredentialsException = a username/password was sent but is wrong (wrong password
        // OR unknown user - deliberately the same message). Otherwise nothing was sent at all.
        String message = exception instanceof BadCredentialsException
                ? "Invalid username or password"
                : "Authentication is required to access this resource";
        // "Basic" tells the client which scheme to use. Without realm="..." most browsers do
        // not open their pop-up, which suits an API consumed by JavaScript or Postman.
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
