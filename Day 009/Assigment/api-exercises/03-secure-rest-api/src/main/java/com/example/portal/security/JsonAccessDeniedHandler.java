package com.example.portal.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.example.portal.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 403 Forbidden: "I know who you are, but you are NOT ALLOWED to do this".
 * The token was valid, but the user's role is not permitted for this endpoint.
 * Denied attempts are also written to the audit log.
 */
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger audit = LoggerFactory.getLogger("AUDIT");

    private final ObjectMapper objectMapper;

    public JsonAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = authentication == null ? "unknown" : authentication.getName();
        String roles = authentication == null ? "[]" : authentication.getAuthorities().toString();
        audit.warn("user={} roles={} action=\"{} {}\" result=DENIED", user, roles,
                request.getMethod(), request.getRequestURI());

        ErrorResponse body = new ErrorResponse(LocalDateTime.now().withNano(0), HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "User '" + user + "' with roles " + roles + " is not allowed to " + request.getMethod() + " "
                        + request.getRequestURI(),
                request.getRequestURI(), null);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
