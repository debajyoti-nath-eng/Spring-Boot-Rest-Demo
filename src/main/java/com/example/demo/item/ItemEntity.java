/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.item;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

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
