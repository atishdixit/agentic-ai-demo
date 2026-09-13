package com.example.agenticdemo.agent;

import com.example.agenticdemo.web.RunResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/** Runs the Researcher -> Writer -> Reviewer agent pipeline in sequence. */
@Service
public class PipelineOrchestrator {

    private final ResearcherAgent researcherAgent;
    private final WriterAgent writerAgent;
    private final ReviewerAgent reviewerAgent;

    public PipelineOrchestrator(
            ResearcherAgent researcherAgent, WriterAgent writerAgent, ReviewerAgent reviewerAgent) {
        this.researcherAgent = researcherAgent;
        this.writerAgent = writerAgent;
        this.reviewerAgent = reviewerAgent;
    }

    public RunResponse run(String topic) {
        try {
            String researchNotes = researcherAgent.run(topic);
            String draft = writerAgent.run(topic, researchNotes);
            String reviewRaw = reviewerAgent.run(draft);
            ReviewerAgent.ReviewResult review = reviewerAgent.parse(reviewRaw);
            String finalArticle = review.finalArticle().isBlank() ? draft : review.finalArticle();

            return new RunResponse(topic, researchNotes, draft, review.feedback(), finalArticle);
        } catch (RestClientException | IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Agent pipeline failed (is Ollama running? `ollama serve`): "
                            + ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }
}
