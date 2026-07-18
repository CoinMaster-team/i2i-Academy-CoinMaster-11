package com.coinmaster.ai.client;

import reactor.core.publisher.Mono;

public interface GeminiClient {

    Mono<String> generate(String prompt);
}
