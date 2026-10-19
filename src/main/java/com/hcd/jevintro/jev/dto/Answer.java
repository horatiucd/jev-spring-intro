package com.hcd.jevintro.jev.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

/**
 * A typed answer returned by Jev, discriminated by its {@code type} property.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Answer.Noul.class, name = "noul"),
        @JsonSubTypes.Type(value = Answer.Choice.class, name = "choice"),
        @JsonSubTypes.Type(value = Answer.Score.class, name = "score")
})
public sealed interface Answer {

    /**
     * @param noul probability that the answer is yes, from 0 to 1
     */
    record Noul(double noul) implements Answer {}

    /**
     * @param choice        the most probable option
     * @param probabilities every option mapped to its probability
     * @param confidence    how concentrated the distribution is
     */
    record Choice(String choice, Map<String, Double> probabilities, double confidence) implements Answer {
        public Choice {
            if (choice == null || choice.isEmpty()) {
                throw new IllegalArgumentException("choice shall not be null or empty");
            }
            if (probabilities == null || probabilities.isEmpty()) {
                throw new IllegalArgumentException("probabilities shall not be null or empty");
            }
        }
    }

    /**
     * @param score         probability-weighted level; may fall between levels
     * @param legend        level index mapped to its description
     * @param probabilities level index mapped to its probability
     * @param confidence    how concentrated the distribution is
     */
    record Score(double score, Map<String, String> legend, Map<String, Double> probabilities, double confidence) implements Answer {
        public Score {
            if (legend == null || legend.isEmpty()) {
                throw new IllegalArgumentException("legend shall not be null or empty");
            }
            if (probabilities == null || probabilities.isEmpty()) {
                throw new IllegalArgumentException("probabilities shall not be null or empty");
            }
        }
    }
}
