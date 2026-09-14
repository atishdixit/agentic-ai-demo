package com.example.chatapp.controller;

import com.example.chatapp.model.ChatMessageDto;
import com.example.chatapp.model.ChatRequest;
import com.example.chatapp.model.ChatResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatController(ChatClient chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    /** One chat turn. The username is the conversation id: MySQL remembers everything said under it. */
    @PostMapping("/messages")
    public ChatResponse sendMessage(@Valid @RequestBody ChatRequest request) {
        String reply = chatClient
                .prompt()
                .user(request.message())
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, request.username()))
                .call()
                .content();
        return new ChatResponse(reply);
    }

    /** Prior turns for this username, so a reloaded page can redisplay the conversation. */
    @GetMapping("/messages")
    public List<ChatMessageDto> history(@RequestParam("username") String username) {
        return chatMemory.get(username).stream()
                .filter(message -> message.getMessageType() == MessageType.USER || message.getMessageType() == MessageType.ASSISTANT)
                .map(this::toDto)
                .toList();
    }

    private ChatMessageDto toDto(Message message) {
        String role = message.getMessageType() == MessageType.USER ? "user" : "assistant";
        return new ChatMessageDto(role, message.getText());
    }
}
