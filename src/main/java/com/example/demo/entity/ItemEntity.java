/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.entity;

import com.example.demo.dto.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity persisted in the application's configured database.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
@Entity
@Table(name = "items")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    protected ItemEntity() {
    }

    /**
     * Creates an item entity for persistence.
     *
     * @param name item name
     * @param description optional item description
     */
    public ItemEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the persistence identifier assigned to this item.
     *
     * @return item identifier or {@code null} before the entity is persisted
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the item name.
     *
     * @return current item name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the item name.
     *
     * @param name new item name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the item description.
     *
     * @return optional description or {@code null}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates the item description.
     *
     * @param description new description; may be {@code null}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Converts this persistence entity to the API representation.
     *
     * @return an immutable item response
     */
    public Item toItem() {
        return new Item(id, name, description);
    }
}
