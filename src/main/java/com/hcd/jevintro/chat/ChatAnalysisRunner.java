package com.hcd.jevintro.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Analyzes the sample chat on startup and prints the profile and decisions for each member.
 */
@Component
public class ChatAnalysisRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ChatAnalysisRunner.class);

    private final ChatProvider chatProvider;
    private final ChatAnalyzer analyzer;
    private final ModerationPolicy policy;

    public ChatAnalysisRunner(ChatProvider chatProvider,
                              ChatAnalyzer analyzer,
                              ModerationPolicy policy) {
        this.chatProvider = chatProvider;
        this.analyzer = analyzer;
        this.policy = policy;
    }

    @Override
    public void run(String... args) throws IOException {
        Chat chat = chatProvider.chat();

        List<MemberProfile> profiles = analyzer.analyze(chat);

        StringBuilder out = new StringBuilder("\nChat group: " + chat.group() + "\n");
        for (MemberProfile profile : profiles) {
            out.append("""

                    %s
                      role        %-10s (confidence %.2f)  %s
                      toxic       %.2f
                      unanswered  %.2f
                      helpfulness %.2f / 3  (confidence %.2f)
                      decisions   %s
                    """.formatted(
                            profile.member(),
                    profile.role().choice(), profile.role().confidence(), profile.role().probabilities(),
                    profile.toxic().noul(),
                    profile.unanswered().noul(),
                    profile.helpfulness().score(), profile.helpfulness().confidence(),
                    policy.decide(profile).stream()
                            .map(d -> d.action() + " - " + d.reason())
                            .collect(Collectors.joining("\n                  "))
                    )
            );
        }
        log.info(out.toString());
    }
}
