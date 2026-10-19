package com.hcd.jevintro.jev.dto;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

class JevRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serialize() throws JSONException {
        Map<String, Question> questions =Map.of(
                "urgent", new Question.Noul("Urgent?", Map.of( "true", "yes", "false", "no")),
                "topic", new Question.Choice("Topic?", Map.of("billing", "Money")),
                "anger", new Question.Score("Angry?", List.of("calm", "furious"))
        );

        var jevRequest = new JevRequest("jev-latest", "some state", questions);

        String json = objectMapper.writeValueAsString(jevRequest);

        JSONAssert.assertEquals("""
            {
                "model": "jev-latest",
                "state": "some state",
                "questions": {
                    "urgent": {
                        "type": "noul",
                        "instructions": "Urgent?",
                        "criteria": {
                            "true": "yes",
                            "false": "no"
                        }
                    },
                    "topic": {
                        "type": "choice",
                        "instructions": "Topic?",
                        "criteria": {
                            "billing": "Money"
                        }
                    },
                    "anger": {
                        "type": "score",
                        "instructions": "Angry?",
                        "criteria": ["calm", "furious"]
                    }
                }
            }
            """, json, JSONCompareMode.STRICT);
    }
}
