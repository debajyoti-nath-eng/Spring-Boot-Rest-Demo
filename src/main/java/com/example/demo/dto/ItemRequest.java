/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Validated request body used to create or replace an item.
 *
 * <p>Both fields are limited to 255 characters, the length of their database columns,
 * so oversized input is rejected with {@code 400 Bad Request} instead of failing in the database.
 *
 * @param name required, non-blank item name of at most 255 characters
 * @param description optional item description of at most 255 characters
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
public record ItemRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,
        @Size(max = 255, message = "Description must be at most 255 characters")
        String description
) {
}
