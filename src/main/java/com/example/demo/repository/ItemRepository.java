/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.repository;

import com.example.demo.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence operations for item entities.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
}
