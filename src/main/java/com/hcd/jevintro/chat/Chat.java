package com.hcd.jevintro.chat;

import java.util.List;

/**
 * A chat group transcript.
 */
public record Chat(String group, List<Message> messages) {

    public record Message(String author, String text) {}

    public List<String> members() {
        return messages.stream()
                .map(Message::author)
                .distinct()
                .toList();
    }
}
