package com.example.agenticdemo.agent;

import com.example.agenticdemo.ollama.OllamaClient;
import org.springframework.stereotype.Component;

@Component
public class WriterAgent {

    private static final String SYSTEM_PROMPT = """
            You are the Writer Agent in a multi-agent pipeline. \
            Given research notes, write a short, engaging article of about \
            150-200 words based only on those notes. Give it a one-line title \
            on the first line, then the article body.""";

    private final OllamaClient ollamaClient;

    public WriterAgent(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public String run(String topic, String researchNotes) {
        String userPrompt = "Topic: " + topic + "\n\nResearch notes:\n" + researchNotes;
        return ollamaClient.chat(SYSTEM_PROMPT, userPrompt);
    }
}
