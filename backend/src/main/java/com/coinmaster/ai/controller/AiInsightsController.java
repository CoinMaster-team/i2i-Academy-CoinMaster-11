package com.coinmaster.ai.controller;

import com.coinmaster.ai.config.GeminiProperties;
import com.coinmaster.ai.dto.AiInsightRequest;
import com.coinmaster.ai.dto.AiInsightResponse;
import com.coinmaster.ai.dto.AiStatusResponse;
import com.coinmaster.ai.service.AiInsightsService;
import com.coinmaster.common.error.ApiErrorResponse;
import com.coinmaster.common.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Insights")
@SecurityRequirement(name = "bearerAuth")
public class AiInsightsController {

    private final AiInsightsService insightsService;
    private final CurrentUserProvider currentUserProvider;
    private final GeminiProperties geminiProperties;

    public AiInsightsController(
            AiInsightsService insightsService,
            CurrentUserProvider currentUserProvider,
            GeminiProperties geminiProperties
    ) {
        this.insightsService = insightsService;
        this.currentUserProvider = currentUserProvider;
        this.geminiProperties = geminiProperties;
    }

    @GetMapping("/status")
    @Operation(summary = "Check whether Gemini is configured")
    public AiStatusResponse status() {
        boolean configured = geminiProperties.apiKey() != null && !geminiProperties.apiKey().isBlank();
        return new AiStatusResponse(configured, geminiProperties.model());
    }

    @PostMapping("/insights")
    @Operation(summary = "Ask Gemini about the current user's portfolio and recent market context")
    @ApiResponse(responseCode = "200", description = "Insight generated")
    @ApiResponse(responseCode = "503", description = "AI service unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public Mono<AiInsightResponse> insights(@Valid @RequestBody AiInsightRequest request) {
        return insightsService.answer(currentUserProvider.currentUserId(), request.question(), request.history());
    }
}
