package com.hcd.jevintro.chat;

import java.util.List;

/**
 * A chat group transcript. This record is sent to Jev as the request state.
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
