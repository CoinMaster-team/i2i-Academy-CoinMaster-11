package com.coinmaster.ai.client;

import com.coinmaster.ai.config.GeminiProperties;
import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class GeminiWebClient implements GeminiClient {

    private final WebClient webClient;
    private final GeminiProperties properties;

    public GeminiWebClient(WebClient.Builder builder, GeminiProperties properties) {
        this.properties = properties;
        this.webClient = builder.baseUrl(properties.baseUrl()).build();
    }

    @Override
    public Mono<String> generate(String prompt) {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            return Mono.error(new BusinessException(ErrorCode.AI_NOT_CONFIGURED, "Gemini API key is not configured"));
        }

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "maxOutputTokens", 4096
                )
        );

        return webClient.post()
                .uri("/models/{model}:generateContent", properties.model())
                .header("x-goog-api-key", properties.apiKey())
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.value() == 429, response ->
                        Mono.error(new BusinessException(ErrorCode.AI_RATE_LIMITED, "AI request limit has been reached")))
                .onStatus(status -> status.value() == 401 || status.value() == 403, response ->
                        Mono.error(new BusinessException(
                                ErrorCode.AI_NOT_CONFIGURED,
                                "Gemini API key is invalid or does not have permission"
                        )))
                .onStatus(status -> status.value() == 404, response ->
                        Mono.error(new BusinessException(
                                ErrorCode.AI_SERVICE_UNAVAILABLE,
                                "The configured Gemini model is unavailable"
                        )))
                .onStatus(HttpStatusCode::isError, response ->
                        Mono.error(new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI service is temporarily unavailable")))
                .bodyToMono(GeminiResponse.class)
                .timeout(Duration.ofMillis(properties.timeoutMs()))
                .map(GeminiResponse::requiredText)
                .onErrorMap(java.util.concurrent.TimeoutException.class, exception ->
                        new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI service timed out"))
                .onErrorMap(org.springframework.web.reactive.function.client.WebClientRequestException.class, exception ->
                        new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI service could not be reached"))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofSeconds(5))
                        .filter(this::isRetryable)
                        .onRetryExhaustedThrow((retrySpec, signal) -> signal.failure()));
    }

    private boolean isRetryable(Throwable throwable) {
        return throwable instanceof BusinessException exception
                && exception.getErrorCode() == ErrorCode.AI_SERVICE_UNAVAILABLE
                && !exception.getMessage().contains("configured Gemini model");
    }

    record GeminiResponse(List<Candidate> candidates) {
        String requiredText() {
            if (candidates == null || candidates.isEmpty()
                    || candidates.get(0).content() == null
                    || candidates.get(0).content().parts() == null
                    || candidates.get(0).content().parts().isEmpty()) {
                throw new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI service returned an empty response");
            }
            String answer = String.join("\n", candidates.get(0).content().parts().stream()
                    .map(Part::text)
                    .filter(text -> text != null && !text.isBlank())
                    .toList());
            if (answer.isBlank()) {
                throw new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI service returned an empty response");
            }
            return answer;
        }
    }

    record Candidate(Content content) {
    }

    record Content(List<Part> parts) {
    }

    record Part(String text) {
    }
}
