/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Validates the client identity header used by API callers.
 *
 * <p>This interceptor enforces a simple contract for all API routes by requiring
 * the {@code X-Client-Type} header to be present and limited to a known allow-list.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
@Component
public class ClientTypeHeaderInterceptor implements HandlerInterceptor {

    private static final String HEADER_NAME = "X-Client-Type";
    private static final Set<String> ALLOWED_CLIENT_TYPES = Set.of("UI", "POSTMAN");

    private final ObjectMapper objectMapper;

    /**
     * Creates the interceptor with the JSON serializer used for error responses.
     *
     * @param objectMapper serializer for problem detail responses
     */
    public ClientTypeHeaderInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Allows requests with a supported client type and rejects all others.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler selected MVC handler
     * @return {@code true} when the client type is supported; {@code false} otherwise
     * @throws IOException if writing the error response fails
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        String clientType = request.getHeader(HEADER_NAME);
        if (clientType != null && ALLOWED_CLIENT_TYPES.contains(clientType.trim().toUpperCase(Locale.ROOT))) {
            return true;
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Missing or invalid X-Client-Type header. Allowed values are UI and POSTMAN."
        );
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
        return false;
    }
}
