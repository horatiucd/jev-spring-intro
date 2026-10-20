package com.hcd.jevintro.primitives;

import com.hcd.jevintro.jev.JevClient;
import com.hcd.jevintro.jev.dto.Answer;
import com.hcd.jevintro.jev.dto.JevResponse;
import com.hcd.jevintro.jev.dto.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Playground for the three Jev primitives.
 * Each code review comment is sent as the state of one request that asks a Noul, a Choice and a Score question together.
 */
@Component
public class PrimitivesRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PrimitivesRunner.class);

    // a clear blocker, a harmless nit and a harsh comment, to compare probabilities and confidence
    private static final List<String> REVIEW_COMMENTS = List.of(
            "This builds the SQL query by concatenating `userId` from the request, which allows SQL injection. "
                    + "Please switch to a PreparedStatement before we merge.",
            "Nit: I'd rename `tmp` to `pendingOrders` so the loop reads better. Not a blocker.",
            "Did you even run this? The cache is never invalidated, so users will see stale prices."
    );

    private static final String BLOCKING_QUESTION_ID = "blocking";
    private static final String CATEGORY_QUESTION_ID = "category";
    private static final String TONE_QUESTION_ID = "tone";

    private static final Map<String, Question> QUESTIONS = Map.of(
            // Noul: the probability that a yes/no condition holds
            BLOCKING_QUESTION_ID, new Question.Noul(
                    "Does this code review comment require a change before the pull request can be merged?",
                    Map.of("true", "Points out a defect or risk that must be fixed, or explicitly asks for a change before merging",
                            "false", "An optional suggestion, nit, question or praise that does not block the merge")),

            // Choice: exactly one option, with a probability distribution over all of them
            CATEGORY_QUESTION_ID, new Question.Choice(
                    "What is the main concern of this code review comment?",
                    Map.of("bug", "Incorrect behavior, a logic error or a missing case",
                            "security", "A vulnerability such as injection, leaked secrets or missing authorization",
                            "performance", "Speed, memory or resource usage",
                            "readability", "Naming, formatting, structure or style",
                            "other", "Anything else, such as tests, documentation or a question")),

            // Score: a position on ordered levels; the value can fall between two levels
            TONE_QUESTION_ID, new Question.Score(
                    "How constructive is the tone of this code review comment towards the author?",
                    List.of("Hostile: insults or mocks the author",
                            "Curt: dismissive or sarcastic, no explanation",
                            "Neutral: states the problem without explaining or suggesting a fix",
                            "Constructive: explains the problem and suggests a fix",
                            "Very constructive: explains the problem, suggests a fix and is encouraging")));

    private final JevClient jevClient;

    public PrimitivesRunner(JevClient jevClient) {
        this.jevClient = jevClient;
    }

    @Override
    public void run(String... args) {
        StringBuilder out = new StringBuilder("\nJev primitives playground\n");
        for (String comment : REVIEW_COMMENTS) {
            JevResponse response = jevClient.ask(comment, QUESTIONS);

            out.append("\n\"").append(comment).append("\"\n");

            out.append(describe(BLOCKING_QUESTION_ID, response));
            out.append(describe(CATEGORY_QUESTION_ID, response));
            out.append(describe(TONE_QUESTION_ID, response));

            out.append("  tokens    %d in, %d out%n".formatted(
                    response.usage().inputTokens(), response.usage().outputTokens()));
        }
        log.info(out.toString());
    }

    private static String describe(String questionId, JevResponse response) {
        var answer = response.answers().get(questionId);

        return switch (answer) {
            case Answer.Noul noul ->
                    "  %-9s noul %.2f%n"
                            .formatted(questionId, noul.noul());
            case Answer.Choice choice ->
                    "  %-9s choice %s (confidence %.2f) %s%n"
                            .formatted(questionId, choice.choice(), choice.confidence(), choice.probabilities());
            case Answer.Score score ->
                    "  %-9s score %.2f / %d (confidence %.2f) %s%n"
                            .formatted(questionId, score.score(), score.legend().size() - 1, score.confidence(), score.probabilities());
        };
    }
}
