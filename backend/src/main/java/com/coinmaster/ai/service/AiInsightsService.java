package com.coinmaster.ai.service;

import com.coinmaster.ai.client.GeminiClient;
import com.coinmaster.ai.dto.AiChatMessage;
import com.coinmaster.ai.dto.AiInsightResponse;
import com.coinmaster.ai.market.RecentPriceTrendProvider;
import com.coinmaster.ai.prompt.AiPromptBuilder;
import com.coinmaster.market.CurrentPriceProvider;
import com.coinmaster.market.SupportedSymbols;
import com.coinmaster.portfolio.dto.PortfolioResponse;
import com.coinmaster.portfolio.service.PortfolioService;
import com.coinmaster.trading.dto.TradeHistoryItem;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AiInsightsService {

    private static final String DISCLAIMER = "This content is informational and is not financial advice.";

    private final PortfolioService portfolioService;
    private final RecentPriceTrendProvider trendProvider;
    private final AiPromptBuilder promptBuilder;
    private final GeminiClient geminiClient;
    private final CurrentPriceProvider currentPriceProvider;

    public AiInsightsService(
            PortfolioService portfolioService,
            RecentPriceTrendProvider trendProvider,
            AiPromptBuilder promptBuilder,
            GeminiClient geminiClient,
            CurrentPriceProvider currentPriceProvider
    ) {
        this.portfolioService = portfolioService;
        this.trendProvider = trendProvider;
        this.promptBuilder = promptBuilder;
        this.geminiClient = geminiClient;
        this.currentPriceProvider = currentPriceProvider;
    }

    public Mono<AiInsightResponse> answer(UUID userId, String question, List<AiChatMessage> history) {
        PortfolioResponse portfolio = portfolioService.getPortfolio(userId);
        List<TradeHistoryItem> recentTrades = portfolioService.getTradeHistory(userId, 20);

        Map<String, Object> context = new LinkedHashMap<>();
        context.put("marketMode", "LIVE_BINANCE_OR_LOCAL_SIMULATION");
        Map<String, Object> currentMarketPrices = new LinkedHashMap<>();
        SupportedSymbols.ALL.forEach(symbol -> currentMarketPrices.put(symbol, currentPriceProvider.getRequiredPrice(symbol)));
        context.put("currentMarketPrices", currentMarketPrices);
        context.put("portfolio", portfolio);
        context.put("recentTrades", recentTrades);
        context.put("recentPriceTrends", trendProvider.recentTrends(20));

        List<AiChatMessage> recentHistory = history == null
                ? List.of()
                : history.stream().skip(Math.max(0, history.size() - 12L)).toList();
        String prompt = promptBuilder.build(question, context, recentHistory);
        return geminiClient.generate(prompt)
                .map(answer -> new AiInsightResponse(answer, Instant.now(), "MARKDOWN", DISCLAIMER));
    }
}
