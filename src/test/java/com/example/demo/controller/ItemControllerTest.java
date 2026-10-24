/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for item endpoints and Actuator health.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /** Verifies the health endpoint is available without the client type header. */
    @Test
    void exposesActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    /** Verifies API requests reject missing or unsupported client types. */
    @Test
    void requiresSupportedClientTypeHeaderForApiRequests() throws Exception {
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Missing or invalid X-Client-Type header. Allowed values are UI and POSTMAN."
                ));

        mockMvc.perform(get("/api/items").header("X-Client-Type", "browser"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/items").header("X-Client-Type", "postman"))
                .andExpect(status().isOk());
    }

    /** Verifies item CRUD behavior and request validation. */
    @Test
    void supportsItemCrudAndReportsInvalidRequests() throws Exception {
        mockMvc.perform(get("/api/items").header("X-Client-Type", "UI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(post("/api/items")
                        .header("X-Client-Type", "UI")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Notebook","description":"Lined"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/items/1")))
                .andExpect(jsonPath("$.name").value("Notebook"));

        mockMvc.perform(get("/api/items/1").header("X-Client-Type", "UI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Lined"));

        mockMvc.perform(put("/api/items/1")
                        .header("X-Client-Type", "UI")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Journal","description":"Updated"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Journal"));

        mockMvc.perform(post("/api/items")
                        .header("X-Client-Type", "UI")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name must not be blank"));

        mockMvc.perform(delete("/api/items/1").header("X-Client-Type", "UI"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/items/1").header("X-Client-Type", "UI"))
                .andExpect(status().isNotFound());
    }
}
