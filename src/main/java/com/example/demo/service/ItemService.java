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

    List<Item> findAll();

    Item findById(long id);

    Item create(ItemRequest request);

    Item update(long id, ItemRequest request);

    void delete(long id);
}
