package com.hcd.jevintro.chat;

import com.hcd.jevintro.chat.ModerationPolicy.Action;
import com.hcd.jevintro.chat.ModerationPolicy.Decision;
import com.hcd.jevintro.jev.dto.Answer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ModerationPolicyTest {

    private final ModerationPolicy policy = new ModerationPolicy();

    @Test
    void mutesHostileMemberAndDoesNotRewardThem() {
        MemberProfile profile = profile("helper", 0.9, 0.95, 0.1, 2.8);

        assertThat(policy.decide(profile)).extracting(Decision::action).containsExactly(Action.MUTE);
    }

    @Test
    void invitesRespectfulTopHelper() {
        var profile = profile("helper", 0.9, 0.05, 0.1, 2.8);

        assertThat(policy.decide(profile)).extracting(Decision::action)
                .containsExactly(Action.INVITE_AS_MODERATOR);
    }

    @Test
    void flagsConfidentPromoter() {
        var profile = profile("promoter", 0.8, 0.1, 0.1, 0.2);

        assertThat(policy.decide(profile)).extracting(Decision::action)
                .containsExactly(Action.REVIEW_PROMOTION);
    }

    @Test
    void followsUpOpenQuestionAndSendsUnclearRoleToHuman() {
        var profile = profile("asker", 0.3, 0.1, 0.85, 0.0);

        assertThat(policy.decide(profile)).extracting(Decision::action)
                .containsExactly(Action.FOLLOW_UP_QUESTION, Action.HUMAN_REVIEW);
    }

    private static MemberProfile profile(String role, double roleConfidence, double toxic,
                                         double unanswered, double helpfulness) {
        return new MemberProfile("member",
                new Answer.Choice(role, Map.of(role, 1.0), roleConfidence),
                new Answer.Noul(toxic),
                new Answer.Noul(unanswered),
                new Answer.Score(helpfulness, Map.of(), Map.of(), 0.9));
    }
}
