package com.company.secureapi.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.company.secureapi.exception.ApiError;
import com.company.secureapi.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JsonSecurityErrorHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {
    private static final Logger securityLog = LoggerFactory.getLogger("security.audit");

    private final ObjectMapper objectMapper;

    public JsonSecurityErrorHandlers(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        boolean badToken = exception instanceof InvalidTokenException;
        ErrorCode code = badToken ? ErrorCode.AUTH_INVALID_TOKEN : ErrorCode.AUTH_REQUIRED;
        String message = badToken ? exception.getMessage() : "Authentication is required to access this resource";

        securityLog.warn("security_event={} method={} path={} reason=\"{}\"",
                code, request.getMethod(), request.getRequestURI(), message);
        response.setHeader("WWW-Authenticate", badToken
                ? "Bearer error=\"invalid_token\", error_description=\"" + message + "\""
                : "Bearer");
        write(response, HttpStatus.UNAUTHORIZED, code, message, request);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        securityLog.warn("security_event={} user={} authorities={} method={} path={}",
                ErrorCode.ACCESS_DENIED, auth == null ? "-" : auth.getName(),
                auth == null ? "[]" : auth.getAuthorities(), request.getMethod(), request.getRequestURI());
        write(response, HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED,
                "You do not have permission to access this resource", request);
    }

    private void write(HttpServletResponse response, HttpStatus status, ErrorCode code, String message,
                       HttpServletRequest request) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiError.of(status, code, message, request.getRequestURI()));
    }
}
