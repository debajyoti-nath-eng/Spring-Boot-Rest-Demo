/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot REST application.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
@SpringBootApplication
public class SpringBootRestDemoApplication {

    /**
     * Starts the application.
     *
     * @param args command-line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBootRestDemoApplication.class, args);
    }
}
