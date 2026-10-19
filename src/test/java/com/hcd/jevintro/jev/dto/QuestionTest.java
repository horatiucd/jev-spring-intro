package com.hcd.jevintro.jev.dto;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

class QuestionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serialize_noul() throws JSONException {
        var noul = new Question.Noul("Is it urgent?",
                Map.of("true", "Time-sensitive", "false", "No urgency"));

        assertJson(noul, """
            {
                "type": "noul",
                "instructions": "Is it urgent?",
                "criteria": {
                    "true": "Time-sensitive",
                    "false": "No urgency"
                }
            }
            """);
    }

    @Test
    void serialize_choice() throws JSONException {
        var choice = new Question.Choice("Role?",
                Map.of("helper", "Answers questions",
                        "asker", "Asks questions",
                        "socializer", "Small talk",
                        "promoter", "Advertises"));

        assertJson(choice, """
            {
                "type": "choice",
                "instructions": "Role?",
                "criteria": {
                    "helper": "Answers questions",
                    "asker": "Asks questions",
                    "socializer": "Small talk",
                    "promoter": "Advertises"
                }
            }
            """);
    }

    @Test
    void serialize_score() throws JSONException {
        var score = new Question.Score("How helpful?",
                List.of("none", "some", "a lot"));

        assertJson(score, """
            {
                "type": "score",
                "instructions": "How helpful?",
                "criteria": ["none", "some", "a lot"]
            }
            """);
    }

    private void assertJson(Question question, String expected) throws JSONException {
        JSONAssert.assertEquals(expected, objectMapper.writeValueAsString(question), JSONCompareMode.STRICT);
    }
}
