/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.repository;

import com.example.demo.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence operations for {@link ItemEntity} records.
 *
 * <p>This repository exposes the standard CRUD behavior required by the item API
 * while leaving the data access implementation to Spring Data JPA.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
}
