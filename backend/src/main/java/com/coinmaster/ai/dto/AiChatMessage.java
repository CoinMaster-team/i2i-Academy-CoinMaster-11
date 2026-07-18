package com.coinmaster.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AiChatMessage(
        @NotBlank
        @Pattern(regexp = "user|assistant")
        String role,

        @NotBlank
        @Size(max = 8000)
        String content
) {
}
