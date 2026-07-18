package com.coinmaster.market;

import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
public class RedisCurrentPriceProvider implements CurrentPriceProvider {

    private final StringRedisTemplate redisTemplate;
    private final String prefix;

    public RedisCurrentPriceProvider(
            StringRedisTemplate redisTemplate,
            @Value("${coinmaster.market.redis-price-prefix}") String prefix
    ) {
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
    }

    @Override
    public BigDecimal getRequiredPrice(String symbol) {
        String value = redisTemplate.opsForValue().get(prefix + symbol);
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.MARKET_PRICE_UNAVAILABLE, "Current price is unavailable for " + symbol);
        }
        return new BigDecimal(value);
    }
}
