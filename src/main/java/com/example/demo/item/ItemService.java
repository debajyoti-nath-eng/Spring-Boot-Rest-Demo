/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.item;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Provides transactional item operations backed by the JPA repository.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * Creates the service with its persistence dependency.
     *
     * @param itemRepository repository used to access stored items
     */
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Returns all items ordered by identifier.
     *
     * @return all stored items
     */
    public List<Item> findAll() {
        return itemRepository.findAll(Sort.by("id")).stream()
                .map(ItemEntity::toItem)
                .toList();
    }

    /**
     * Finds an item by its identifier.
     *
     * @param id item identifier
     * @return the matching item
     * @throws ItemNotFoundException if no item has the requested identifier
     */
    public Item findById(long id) {
        return getEntity(id).toItem();
    }

    /**
     * Persists a new item.
     *
     * @param request validated item data
     * @return the created item
     */
    @Transactional
    public Item create(ItemRequest request) {
        ItemEntity entity = new ItemEntity(request.name(), request.description());
        return itemRepository.save(entity).toItem();
    }

    /**
     * Replaces the data of an existing item.
     *
     * @param id identifier of the item to replace
     * @param request validated replacement data
     * @return the updated item
     * @throws ItemNotFoundException if no item has the requested identifier
     */
    @Transactional
    public Item update(long id, ItemRequest request) {
        ItemEntity entity = getEntity(id);
        entity.setName(request.name());
        entity.setDescription(request.description());
        return itemRepository.save(entity).toItem();
    }

    /**
     * Deletes an existing item.
     *
     * @param id identifier of the item to delete
     * @throws ItemNotFoundException if no item has the requested identifier
     */
    @Transactional
    public void delete(long id) {
        itemRepository.delete(getEntity(id));
    }

    private ItemEntity getEntity(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }
}
