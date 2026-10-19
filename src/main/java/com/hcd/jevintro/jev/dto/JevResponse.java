package com.hcd.jevintro.jev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Response of {@code POST /v1/systemone}, with answers keyed by the question IDs of the request.
 */
public record JevResponse(String model, Map<String, Answer> answers, Usage usage) {

    public JevResponse {
        if (model == null || model.isEmpty()) {
            throw new IllegalArgumentException("model shall not be null or empty");
        }
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("answers shall not be null or empty");
        }
        if (usage == null) {
            throw new IllegalArgumentException("usage shall not be null");
        }
    }

    public record Usage(@JsonProperty("input_tokens") int inputTokens,
                        @JsonProperty("output_tokens") int outputTokens) {
    }

    public Answer.Noul noul(String questionId) {
        return (Answer.Noul) answers.get(questionId);
    }

    public Answer.Choice choice(String questionId) {
        return (Answer.Choice) answers.get(questionId);
    }

    public Answer.Score score(String questionId) {
        return (Answer.Score) answers.get(questionId);
    }
}
