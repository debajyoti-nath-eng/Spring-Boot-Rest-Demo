/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.item;

/**
 * Indicates that an item with the requested identifier does not exist.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
public class ItemNotFoundException extends RuntimeException {

    /**
     * Creates an exception for a missing item.
     *
     * @param id identifier of the item that was not found
     */
    public ItemNotFoundException(long id) {
        super("Item with ID " + id + " was not found");
    }
}
