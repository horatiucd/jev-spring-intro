package com.hcd.jevintro.chat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads the sample chat from the classpath.
 */
@Component
public class ChatProvider {

    private final Resource chatJson;
    private final ObjectMapper objectMapper;

    public ChatProvider(ObjectMapper objectMapper,
                        @Value("classpath:chat.json") Resource chatJson) {
        this.chatJson = chatJson;
        this.objectMapper = objectMapper;
    }

    public Chat chat() throws IOException {
        try (InputStream in = chatJson.getInputStream()) {
            return objectMapper.readValue(in, Chat.class);
        }
    }
}
