package com.coinmaster.ai.dto;

public record AiStatusResponse(
        boolean configured,
        String model
) {
}
