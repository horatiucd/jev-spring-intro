package com.hcd.jevintro.jev;

import com.hcd.jevintro.jev.dto.JevResponse;
import com.hcd.jevintro.jev.dto.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

@RestClientTest(value = JevClient.class, properties = {
        "jev.base-url=https://api.typesafe.ai",
        "jev.api-key=test-key",
        "jev.model=jev-latest"
})
@EnableConfigurationProperties(JevProperties.class)
class JevClientTest {

    @Autowired
    private JevClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void sendsAllPrimitivesAndReadsTypedAnswers() {
        server.expect(requestTo("https://api.typesafe.ai/v1/systemone"))
                .andExpect(method(POST))
                .andExpect(header("Authorization", "Bearer test-key"))
                .andExpect(content().json("""
                        {
                          "model": "jev-latest",
                          "state": "Help! My payouts have been failing for 3 days.",
                          "questions": {
                            "urgent": {
                              "type": "noul",
                              "instructions": "Is it urgent?",
                              "criteria": {
                                "true": "yes",
                                "false": "no"
                              }
                            },
                            "topic": {
                              "type": "choice",
                              "instructions": "Topic?",
                              "criteria": {
                                "billing": "Money",
                                "other": "Anything else"
                              }
                            },
                            "anger": {
                              "type": "score",
                              "instructions": "How angry?",
                              "criteria": ["calm", "annoyed", "furious"]
                            }
                          }
                        }
                        """, true))
                .andRespond(withSuccess("""
                {
                    "model": "jev-1.13.0",
                    "answers": {
                        "urgent": {
                            "type": "noul",
                            "noul": 0.95
                        },
                        "topic": {
                            "type": "choice",
                            "choice": "billing",
                            "probabilities": {
                                "billing": 0.9,
                                "other": 0.1
                            },
                            "confidence": 0.8
                        },
                        "anger": {
                            "type": "score",
                            "score": 1.2,
                            "legend": {
                                "0": "calm",
                                "1": "annoyed",
                                "2": "furious"
                            },
                            "probabilities": {
                                "0": 0.1,
                                "1": 0.6,
                                "2": 0.3
                            },
                            "confidence": 0.4
                        }
                    },
                    "usage": {
                        "input_tokens": 300,
                        "output_tokens": 20
                    }
                }
                """, MediaType.APPLICATION_JSON));

        Map<String, Question> questions = new LinkedHashMap<>();
        questions.put("urgent", new Question.Noul("Is it urgent?", Map.of("true", "yes", "false", "no")));
        questions.put("topic", new Question.Choice("Topic?", Map.of("billing", "Money", "other", "Anything else")));
        questions.put("anger", new Question.Score("How angry?", List.of("calm", "annoyed", "furious")));

        JevResponse response = client.ask("Help! My payouts have been failing for 3 days.", questions);

        assertThat(response.noul("urgent").noul()).isEqualTo(0.95);
        assertThat(response.choice("topic").choice()).isEqualTo("billing");
        assertThat(response.choice("topic").probabilities()).containsEntry("other", 0.1);
        assertThat(response.score("anger").score()).isEqualTo(1.2);
        assertThat(response.score("anger").legend()).containsEntry("2", "furious");

        assertThat(response.usage().inputTokens()).isEqualTo(300);
        assertThat(response.usage().outputTokens()).isEqualTo(20);

        server.verify();
    }
}
