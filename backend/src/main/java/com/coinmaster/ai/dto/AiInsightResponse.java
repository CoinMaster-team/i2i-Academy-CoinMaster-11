package com.coinmaster.ai.dto;

import java.time.Instant;

public record AiInsightResponse(
        String answer,
        Instant generatedAt,
        String format,
        String disclaimer
) {
}
