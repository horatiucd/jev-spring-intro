package com.hcd.jevintro.chat;

import com.hcd.jevintro.jev.JevClient;
import com.hcd.jevintro.jev.dto.JevResponse;
import com.hcd.jevintro.jev.dto.Question;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds the questions for every chat member and asks them all to Jev in one request.
 */
@Service
public class ChatAnalyzer {

    private final JevClient jevClient;

    public ChatAnalyzer(JevClient jevClient) {
        this.jevClient = jevClient;
    }

    public List<MemberProfile> analyze(Chat chat) {
        Map<String, Question> questions = chat.members().stream()
                .flatMap(member -> questionsFor(member).entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        JevResponse response = jevClient.ask(chat, questions);

        return chat.members().stream()
                .map(member -> new MemberProfile(member,
                        response.choice(id(member, "role")),
                        response.noul(id(member, "toxic")),
                        response.noul(id(member, "unanswered")),
                        response.score(id(member, "helpfulness"))))
                .toList();
    }

    static Map<String, Question> questionsFor(String member) {
        var scope = "Consider the messages in `messages` whose `author` is \"" + member + "\", using the other messages only as context. ";

        return Map.of(
                id(member, "role"),
                new Question.Choice(scope + "What role does " + member + " mainly play in this chat group?",
                        Map.of(
                                "helper", "Answers other members' questions, shares knowledge or solves problems",
                                "asker", "Mostly asks for help or information",
                                "socializer", "Mostly greetings, jokes or casual small talk",
                                "promoter", "Mostly advertises products, services, links or themselves"
                        )
                ),
                id(member, "toxic"),
                new Question.Noul(
                        scope + "Was " + member + " insulting, harassing or hostile towards other members?",
                        Map.of(
                                "true", "Insults, mocks, threatens or belittles another member",
                                "false", "Respectful, or only disagrees politely"
                        )
                ),
                id(member, "unanswered"),
                new Question.Noul(
                        scope + "Did " + member + " ask a question that nobody in the chat has answered yet?",
                        Map.of(
                                "true", "A question from this member is still open; no one gave a useful answer",
                                "false", "This member asked nothing, or their questions were answered")
                ),
                id(member, "helpfulness"),
                new Question.Score(
                        scope + "How much did " + member + " help other members?",
                        List.of(
                                "Gave no help to anyone",
                                "Gave a little help, such as a short hint or a link without explanation",
                                "Gave useful answers that moved someone's problem forward",
                                "Gave detailed, correct answers that solved other members' problems"
                        )
                )
        );
    }

    static String id(String member, String question) {
        return member + "." + question;
    }
}
