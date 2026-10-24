/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.service;

import com.example.demo.dto.Item;
import com.example.demo.dto.ItemRequest;

import java.util.List;

/**
 * Defines item operations provided by the service layer.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
public interface ItemService {

    /**
     * Retrieves every item in the system.
     *
     * @return a list containing all persisted items
     */
    List<Item> findAll();

    /**
     * Finds a single item by its identifier.
     *
     * @param id unique item identifier
     * @return the matching item
     */
    Item findById(long id);

    /**
     * Creates a new item from the provided request body.
     *
     * @param request validated item data
     * @return the newly created item
     */
    Item create(ItemRequest request);

    /**
     * Replaces the content of an existing item.
     *
     * @param id identifier of the item to update
     * @param request validated replacement data
     * @return the updated item
     */
    Item update(long id, ItemRequest request);

    /**
     * Removes an item identified by its unique ID.
     *
     * @param id identifier of the item to remove
     */
    void delete(long id);
}
