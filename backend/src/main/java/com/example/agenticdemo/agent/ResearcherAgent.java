package com.example.agenticdemo.agent;

import com.example.agenticdemo.ollama.OllamaClient;
import org.springframework.stereotype.Component;

@Component
public class ResearcherAgent {

    private static final String SYSTEM_PROMPT = """
            You are the Research Agent in a multi-agent pipeline. \
            Given a topic, produce 4-6 concise, factual bullet points covering the \
            most important things a reader should know. No introduction, no \
            conclusion, just the bullet points.""";

    private final OllamaClient ollamaClient;

    public ResearcherAgent(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public String run(String topic) {
        return ollamaClient.chat(SYSTEM_PROMPT, "Topic: " + topic);
    }
}
