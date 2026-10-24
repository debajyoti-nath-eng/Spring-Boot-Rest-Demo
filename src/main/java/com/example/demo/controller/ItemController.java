/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.controller;

import com.example.demo.dto.Item;
import com.example.demo.dto.ItemRequest;
import com.example.demo.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST endpoints for creating, retrieving, updating, and deleting items.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    /**
     * Creates the controller with its item service.
     *
     * @param itemService service handling item operations
     */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Lists all items.
     *
     * @return all stored items
     */
    @GetMapping
    public List<Item> findAll() {
        return itemService.findAll();
    }

    /**
     * Retrieves an item by identifier.
     *
     * @param id item identifier
     * @return the matching item
     */
    @GetMapping("/{id}")
    public Item findById(@PathVariable long id) {
        return itemService.findById(id);
    }

    /**
     * Creates an item and returns its resource location.
     *
     * @param request validated item data
     * @return created item with a {@code Location} header
     */
    @PostMapping
    public ResponseEntity<Item> create(@Valid @RequestBody ItemRequest request) {
        Item item = itemService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(item.id())
                .toUri();
        return ResponseEntity.created(location).body(item);
    }

    /**
     * Replaces an existing item.
     *
     * @param id identifier of the item to replace
     * @param request validated replacement data
     * @return updated item
     */
    @PutMapping("/{id}")
    public Item update(@PathVariable long id, @Valid @RequestBody ItemRequest request) {
        return itemService.update(id, request);
    }

    /**
     * Deletes an existing item.
     *
     * @param id identifier of the item to delete
     * @return an empty {@code 204 No Content} response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
