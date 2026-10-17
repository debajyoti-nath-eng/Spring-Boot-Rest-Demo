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

@Component
public class ClientTypeHeaderInterceptor implements HandlerInterceptor {

    private static final String HEADER_NAME = "X-Client-Type";
    private static final Set<String> ALLOWED_CLIENT_TYPES = Set.of("UI", "POSTMAN");

    private final ObjectMapper objectMapper;

    public ClientTypeHeaderInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
