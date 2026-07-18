package com.coinmaster.market;

import com.coinmaster.market.entity.PriceHistory;
import com.coinmaster.market.repository.PriceHistoryRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("!local")
public class TickerEngine {

    private final Map<String, BigDecimal> currentPrices = new ConcurrentHashMap<>();
    private final StringRedisTemplate redisTemplate;
    private final PriceHistoryRepository repository;
    private final String pricePrefix;

    public TickerEngine(
            StringRedisTemplate redisTemplate,
            PriceHistoryRepository repository,
            @Value("${coinmaster.market.redis-price-prefix}") String pricePrefix
    ) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
        this.pricePrefix = pricePrefix;
        currentPrices.put("BTC", new BigDecimal("65000.00000000"));
        currentPrices.put("ETH", new BigDecimal("3500.00000000"));
    }

    @PostConstruct
    public void seedPrices() {
        currentPrices.forEach((symbol, price) -> {
            redisTemplate.opsForValue().set(pricePrefix + symbol, price.toPlainString());
            repository.save(new PriceHistory(symbol, price, Instant.now()));
        });
    }

    @Scheduled(fixedRateString = "${coinmaster.market.ticker-rate-ms}")
    public void generatePrices() {
        updateAndSavePrice("BTC", new BigDecimal("500.00"));
        updateAndSavePrice("ETH", new BigDecimal("50.00"));
    }

    private void updateAndSavePrice(String symbol, BigDecimal maxChange) {
        BigDecimal oldPrice = currentPrices.get(symbol);
        BigDecimal change = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(
                maxChange.negate().doubleValue(),
                maxChange.doubleValue()
        ));
        BigDecimal newPrice = oldPrice.add(change).max(BigDecimal.ONE).setScale(8, RoundingMode.HALF_UP);
        currentPrices.put(symbol, newPrice);
        redisTemplate.opsForValue().set(pricePrefix + symbol, newPrice.toPlainString());
        repository.save(new PriceHistory(symbol, newPrice, Instant.now()));
    }
}
