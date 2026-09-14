package com.example.chatapp.model;

/** A single stored turn, as returned by the chat history endpoint. */
public record ChatMessageDto(String role, String content) {
}
