/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.item;

/**
 * API representation of an item.
 *
 * @param id unique item identifier
 * @param name item name
 * @param description optional item description
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
public record Item(long id, String name, String description) {
}
