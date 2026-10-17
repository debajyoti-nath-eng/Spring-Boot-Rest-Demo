package com.example.demo.item;

import jakarta.validation.constraints.NotBlank;

public record ItemRequest(
        @NotBlank(message = "Name must not be blank")
        String name,
        String description
) {
}
