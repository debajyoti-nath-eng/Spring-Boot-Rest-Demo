/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.item;

import jakarta.validation.constraints.NotBlank;

/**
 * Validated request body used to create or replace an item.
 *
 * @param name required, non-blank item name
 * @param description optional item description
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
public record ItemRequest(
        @NotBlank(message = "Name must not be blank")
        String name,
        String description
) {
}
