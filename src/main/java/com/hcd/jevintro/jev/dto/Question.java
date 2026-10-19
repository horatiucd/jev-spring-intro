package com.hcd.jevintro.jev.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A question sent to Jev.
 * The {@code type} property is written by Jackson.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(Question.Noul.class),
        @JsonSubTypes.Type(Question.Choice.class),
        @JsonSubTypes.Type(Question.Score.class)
})
public sealed interface Question {

    /**
     * Yes/no question; the answer is the probability of yes.
     *
     * @param criteria keys {@code "true"} and {@code "false"} describing each outcome
     */
    @JsonTypeName("noul")
    record Noul(String instructions, Map<String, String> criteria) implements Question {
        public Noul {
            if (instructions == null || instructions.isEmpty()) {
                throw new IllegalArgumentException("instructions shall not be null or empty");
            }

            if (criteria == null || criteria.size() != 2 ||
                    !criteria.keySet().containsAll(Set.of("true", "false"))) {
                throw new IllegalArgumentException("criteria keys must be \"true\" and \"false\"");
            }
        }
    }

    /**
     * Pick one option from a defined set.
     *
     * @param criteria option name mapped to its description
     */
    @JsonTypeName("choice")
    record Choice(String instructions, Map<String, String> criteria) implements Question {
        public Choice {
            if (instructions == null || instructions.isEmpty()) {
                throw new IllegalArgumentException("instructions shall not be null or empty");
            }
            if (criteria == null || criteria.isEmpty()) {
                throw new IllegalArgumentException("criteria shall not be null or empty");
            }
        }
    }

    /**
     * Rate against ordered levels; level {@code i} has score {@code i}.
     *
     * @param criteria level descriptions, lowest first
     */
    @JsonTypeName("score")
    record Score(String instructions, List<String> criteria) implements Question {
        public Score {
            if (instructions == null || instructions.isEmpty()) {
                throw new IllegalArgumentException("instructions shall not be null or empty");
            }
            if (criteria == null || criteria.isEmpty()) {
                throw new IllegalArgumentException("criteria shall not be null or empty");
            }
        }
    }
}
