package com.coinmaster.ai.market;

import com.coinmaster.market.repository.PriceHistoryRepository;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
public class DbRecentPriceTrendProvider implements RecentPriceTrendProvider {

    private final PriceHistoryRepository repository;

    public DbRecentPriceTrendProvider(PriceHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, List<PriceTrendPoint>> recentTrends(int limitPerSymbol) {
        int limit = Math.max(1, Math.min(limitPerSymbol, 100));
        return Map.of(
                "BTC", trend("BTC", limit),
                "ETH", trend("ETH", limit)
        );
    }

    private List<PriceTrendPoint> trend(String symbol, int limit) {
        return repository.findBySymbolOrderByCapturedAtDesc(symbol, PageRequest.of(0, limit))
                .stream()
                .map(price -> new PriceTrendPoint(price.getCapturedAt(), price.getPrice()))
                .toList();
    }
}
