package com.example.agenticdemo.agent;

import com.example.agenticdemo.ollama.OllamaClient;
import org.springframework.stereotype.Component;

@Component
public class ReviewerAgent {

    private static final String SYSTEM_PROMPT = """
            You are the Reviewer Agent in a multi-agent pipeline. \
            Given a draft article, evaluate it for clarity and accuracy, then \
            respond in exactly this format:

            FEEDBACK:
            <2-3 short bullet points of feedback>

            FINAL:
            <the final, polished version of the article, improved per your own feedback>""";

    private final OllamaClient ollamaClient;

    public ReviewerAgent(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public String run(String draft) {
        return ollamaClient.chat(SYSTEM_PROMPT, "Draft article:\n" + draft);
    }

    /** Splits the reviewer's raw output into feedback and the final article. */
    public ReviewResult parse(String reviewText) {
        if (!reviewText.contains("FINAL:")) {
            return new ReviewResult(reviewText, "");
        }
        String[] parts = reviewText.split("FINAL:", 2);
        String feedback = parts[0].replace("FEEDBACK:", "").strip();
        String finalArticle = parts[1].strip();
        return new ReviewResult(feedback, finalArticle);
    }

    public record ReviewResult(String feedback, String finalArticle) {
    }
}
