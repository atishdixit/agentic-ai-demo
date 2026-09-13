package com.example.agenticdemo.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RunResponse(
        String topic,
        String researcher,
        String writer,
        @JsonProperty("reviewer_feedback") String reviewerFeedback,
        @JsonProperty("final_article") String finalArticle) {
}
