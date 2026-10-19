package com.hcd.jevintro.jev;

import com.hcd.jevintro.jev.dto.JevRequest;
import com.hcd.jevintro.jev.dto.JevResponse;
import com.hcd.jevintro.jev.dto.Question;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Minimal client for the TypeSafe System One REST API.
 */
@Component
public class JevClient {

    private final JevProperties properties;
    private final RestClient restClient;

    public JevClient(JevProperties properties,
                     RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiKey())
                .build();
    }

    /**
     * Asks all questions about the same state in a single request.
     */
    public JevResponse ask(Object state, Map<String, Question> questions) {
        return restClient.post()
                .uri("/v1/systemone")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new JevRequest(properties.model(), state, questions))
                .retrieve()
                .body(JevResponse.class);
    }
}
