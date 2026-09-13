package com.example.agenticdemo.ollama;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Thin client around Ollama's local /api/chat endpoint. Every agent in the
 * pipeline is really just this same local model, called with a different
 * system prompt.
 */
@Component
public class OllamaClient {

    private final RestClient restClient;
    private final String model;

    public OllamaClient(
            @Value("${ollama.base-url}") String baseUrl,
            @Value("${ollama.model}") String model) {
        this.model = model;

        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(10))
                        // Local CPU inference can be slow, so give a generous read timeout.
                        .withReadTimeout(Duration.ofSeconds(300)));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public String chat(String systemPrompt, String userPrompt) {
        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)),
                "stream", false,
                // Keep responses short so the demo stays fast on CPU-only inference.
                "options", Map.of("num_predict", 220));

        ChatResponse response = restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(ChatResponse.class);

        if (response == null || response.message() == null) {
            throw new IllegalStateException("Ollama returned an empty response");
        }
        return response.message().content().trim();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ChatResponse(Message message) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        private record Message(String role, String content) {
        }
    }
}
