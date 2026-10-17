/*
 * Copyright (c) 2026 Debajyoti Nath. All rights reserved.
 */
package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers MVC configuration for the REST API.
 *
 * @author Debajyoti Nath (Technical Architect - Java Backend)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ClientTypeHeaderInterceptor clientTypeHeaderInterceptor;

    /**
     * Creates the web configuration.
     *
     * @param clientTypeHeaderInterceptor interceptor that validates API client headers
     */
    public WebConfig(ClientTypeHeaderInterceptor clientTypeHeaderInterceptor) {
        this.clientTypeHeaderInterceptor = clientTypeHeaderInterceptor;
    }

    /**
     * Applies client type header validation to API routes.
     *
     * @param registry registry used to register MVC interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(clientTypeHeaderInterceptor)
                .addPathPatterns("/api/**");
    }
}
