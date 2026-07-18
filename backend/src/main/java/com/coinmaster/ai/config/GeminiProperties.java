package com.coinmaster.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coinmaster.ai")
public record GeminiProperties(
        String apiKey,
        String model,
        String baseUrl,
        long timeoutMs
) {
}
