package com.hcd.jevintro.jev;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Connection settings for the TypeSafe System One API.
 */
@ConfigurationProperties(prefix = "jev")
public record JevProperties(String baseUrl, String apiKey, String model) {
}
