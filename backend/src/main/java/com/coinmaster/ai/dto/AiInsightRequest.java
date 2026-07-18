package com.coinmaster.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AiInsightRequest(
        @NotBlank
        @Size(max = 500)
        @Schema(example = "Summarize my portfolio and recent transactions.")
        String question,

        @Size(max = 12)
        List<@Valid AiChatMessage> history
) {
}
