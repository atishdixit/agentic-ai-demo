package com.example.chatapp.model;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "username must not be blank") String username,
        @NotBlank(message = "message must not be blank") String message) {
}
