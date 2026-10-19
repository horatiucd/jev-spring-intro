package com.hcd.jevintro.jev.dto;

import java.util.Map;

/**
 * Body of {@code POST /v1/systemone}.
 *
 * @param model     the model that handles the request
 * @param state     the content the questions are about; any JSON-serializable value
 * @param questions question IDs mapped to questions; IDs are not sent to the model
 */
public record JevRequest(String model, Object state, Map<String, Question> questions) {

    public JevRequest {
        if (model == null || model.isEmpty()) {
            throw new IllegalArgumentException("model shall not be null or empty");
        }
        if (state == null) {
            throw new IllegalArgumentException("state shall not be null");
        }
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("questions shall not be null or empty");
        }
    }
}
