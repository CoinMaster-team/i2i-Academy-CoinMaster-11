package com.coinmaster.ai.market;

import com.coinmaster.market.SupportedSymbols;
import com.coinmaster.market.CurrentPriceProvider;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalRecentPriceTrendProvider implements RecentPriceTrendProvider {

    private static final List<String> SYMBOLS = SupportedSymbols.ALL;
    private static final int MAX_SNAPSHOTS = 100;

    private final CurrentPriceProvider priceProvider;
    private final Map<String, Deque<PriceTrendPoint>> history = new LinkedHashMap<>();

    public LocalRecentPriceTrendProvider(CurrentPriceProvider priceProvider) {
        this.priceProvider = priceProvider;
        SYMBOLS.forEach(symbol -> history.put(symbol, new ArrayDeque<>()));
    }

    @PostConstruct
    public void seedHistory() {
        capturePrices();
    }

    @Scheduled(fixedRateString = "${coinmaster.market.ticker-rate-ms}")
    public void capturePrices() {
        Instant capturedAt = Instant.now();
        SYMBOLS.forEach(symbol -> {
            Deque<PriceTrendPoint> snapshots = history.get(symbol);
            synchronized (snapshots) {
                snapshots.addFirst(new PriceTrendPoint(capturedAt, priceProvider.getRequiredPrice(symbol)));
                while (snapshots.size() > MAX_SNAPSHOTS) {
                    snapshots.removeLast();
                }
            }
        });
    }

    @Override
    public Map<String, List<PriceTrendPoint>> recentTrends(int limitPerSymbol) {
        int limit = Math.max(1, Math.min(limitPerSymbol, MAX_SNAPSHOTS));
        Map<String, List<PriceTrendPoint>> result = new LinkedHashMap<>();
        history.forEach((symbol, snapshots) -> {
            synchronized (snapshots) {
                result.put(symbol, new ArrayList<>(snapshots).stream().limit(limit).toList());
            }
        });
        return result;
    }
}
