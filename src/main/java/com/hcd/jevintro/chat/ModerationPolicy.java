package com.hcd.jevintro.chat;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic decisions taken in code from Jev's judgments.
 * The thresholds are starting points to tune on real chats.
 */
@Component
public class ModerationPolicy {

    public enum Action {
        MUTE,
        WARN,
        REVIEW_PROMOTION,
        FOLLOW_UP_QUESTION,
        INVITE_AS_MODERATOR,
        THANK_FOR_HELP,
        HUMAN_REVIEW
    }

    public record Decision(Action action, String reason) {}

    public List<Decision> decide(MemberProfile profile) {
        List<Decision> decisions = new ArrayList<>();
        double toxic = profile.toxic().noul();
        double help = profile.helpfulness().score();

        if (toxic >= 0.85) {
            decisions.add(new Decision(Action.MUTE, "very likely hostile (%.2f)".formatted(toxic)));
        } else if (toxic >= 0.5) {
            decisions.add(new Decision(Action.WARN, "possibly hostile (%.2f)".formatted(toxic)));
        }

        if ("promoter".equals(profile.role().choice()) &&
                profile.role().confidence() >= 0.6) {
            decisions.add(new Decision(Action.REVIEW_PROMOTION,
                    "mainly self-promotion (confidence %.2f)".formatted(profile.role().confidence())));
        }

        if (profile.unanswered().noul() >= 0.7) {
            decisions.add(new Decision(Action.FOLLOW_UP_QUESTION,
                    "has an open question (%.2f)".formatted(profile.unanswered().noul())));
        }

        // reward helpers only when they are also respectful
        if (toxic < 0.2) {
            if (help >= 2.5 && profile.helpfulness().confidence() >= 0.5) {
                decisions.add(new Decision(Action.INVITE_AS_MODERATOR,
                        "consistently helpful (%.2f / 3)".formatted(help)));
            } else if (help >= 1.5) {
                decisions.add(new Decision(Action.THANK_FOR_HELP, "helpful (%.2f / 3)".formatted(help)));
            }
        }

        // an uncertain role is not acted on automatically
        if (profile.role().confidence() < 0.4) {
            decisions.add(new Decision(Action.HUMAN_REVIEW,
                    "unclear role (confidence %.2f)".formatted(profile.role().confidence())));
        }
        return decisions;
    }
}
