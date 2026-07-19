package com.coinmaster.market;

import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalCurrentPriceProvider implements CurrentPriceProvider {

    private final Map<String, BigDecimal> prices = new ConcurrentHashMap<>();

    public LocalCurrentPriceProvider() {
        prices.putAll(SupportedSymbols.INITIAL_PRICES);
    }

    @Override
    public BigDecimal getRequiredPrice(String symbol) {
        BigDecimal price = prices.get(symbol);
        if (price == null) {
            throw new BusinessException(ErrorCode.MARKET_PRICE_UNAVAILABLE, "Current price is unavailable for " + symbol);
        }
        return price;
    }

    @Scheduled(fixedRateString = "${coinmaster.market.ticker-rate-ms}")
    public void generatePrices() {
        SupportedSymbols.LOCAL_MAX_CHANGES.forEach(this::updatePrice);
    }

    private void updatePrice(String symbol, BigDecimal maxChange) {
        prices.computeIfPresent(symbol, (ignored, oldPrice) -> {
            BigDecimal change = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(
                    maxChange.negate().doubleValue(),
                    maxChange.doubleValue()
            ));
            return oldPrice.add(change).max(BigDecimal.ONE).setScale(8, RoundingMode.HALF_UP);
        });
    }
}
