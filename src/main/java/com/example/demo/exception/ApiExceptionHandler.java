/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts framework and domain exceptions into structured HTTP problem responses.
 *
 * <p>This advice centralizes error translation so the controller layer remains
 * focused on request handling instead of exception formatting.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Maps a missing item to a {@code 404 Not Found} response.
     *
     * @param exception exception describing the missing item
     * @return problem detail for the not-found response
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ProblemDetail handleItemNotFound(ItemNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Maps request validation failures to a {@code 400 Bad Request} response.
     *
     * @param exception validation failure containing field errors
     * @return problem detail including the invalid fields
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage(),
                        (first, second) -> first
                ));
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail("Request validation failed");
        problem.setProperty("errors", errors);
        return problem;
    }
}
