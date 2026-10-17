package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ClientTypeHeaderInterceptor clientTypeHeaderInterceptor;

    public WebConfig(ClientTypeHeaderInterceptor clientTypeHeaderInterceptor) {
        this.clientTypeHeaderInterceptor = clientTypeHeaderInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(clientTypeHeaderInterceptor)
                .addPathPatterns("/api/**");
    }
}
