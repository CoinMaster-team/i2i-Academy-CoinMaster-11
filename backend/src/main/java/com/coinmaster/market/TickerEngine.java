package com.coinmaster.market;

import com.coinmaster.market.entity.PriceHistory;
import com.coinmaster.market.dto.BinancePriceResponse;
import com.coinmaster.market.repository.PriceHistoryRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("!local")
public class TickerEngine {

    private static final String DEFAULT_BINANCE_URL = "https://api.binance.com/api/v3/ticker/price";

    private final Map<String, BigDecimal> currentPrices = new ConcurrentHashMap<>();
    private final StringRedisTemplate redisTemplate;
    private final PriceHistoryRepository repository;
    private final String pricePrefix;
    private final String binanceUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public TickerEngine(
            StringRedisTemplate redisTemplate,
            PriceHistoryRepository repository,
            @Value("${coinmaster.market.redis-price-prefix}") String pricePrefix,
            @Value("${coinmaster.market.binance-url:" + DEFAULT_BINANCE_URL + "}") String binanceUrl
    ) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
        this.pricePrefix = pricePrefix;
        this.binanceUrl = binanceUrl;
        currentPrices.putAll(SupportedSymbols.INITIAL_PRICES);
    }

    @PostConstruct
    public void seedPrices() {
        currentPrices.forEach((symbol, price) -> {
            redisTemplate.opsForValue().set(pricePrefix + symbol, price.toPlainString());
            repository.save(new PriceHistory(symbol, price, Instant.now()));
        });
    }

    @Scheduled(fixedRateString = "${coinmaster.market.ticker-rate-ms}")
    public void fetchLivePrices() {
        try {
            BinancePriceResponse[] prices = restTemplate.getForObject(binanceUrl, BinancePriceResponse[].class);
            if (prices == null) {
                return;
            }

            Map<String, String> pairToSymbol = SupportedSymbols.BINANCE_USDT_PAIRS.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            for (BinancePriceResponse price : prices) {
                String symbol = pairToSymbol.get(price.getSymbol());
                if (symbol != null) {
                    updateAndSavePrice(symbol, new BigDecimal(price.getPrice()));
                }
            }
        } catch (Exception exception) {
            System.err.println("Cannot reach Binance API. Keeping last known prices. Reason: " + exception.getMessage());
        }
    }

    private void updateAndSavePrice(String symbol, BigDecimal price) {
        BigDecimal normalizedPrice = price.max(new BigDecimal("0.00000001")).setScale(8, RoundingMode.HALF_UP);
        currentPrices.put(symbol, normalizedPrice);
        redisTemplate.opsForValue().set(pricePrefix + symbol, normalizedPrice.toPlainString());
        repository.save(new PriceHistory(symbol, normalizedPrice, Instant.now()));
    }
}
