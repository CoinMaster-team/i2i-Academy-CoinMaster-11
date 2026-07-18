package com.coinmaster.auth.session;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
public class RedisSessionStore implements SessionStore {

    private final StringRedisTemplate redisTemplate;
    private final String prefix;

    public RedisSessionStore(
            StringRedisTemplate redisTemplate,
            @Value("${coinmaster.market.redis-session-prefix}") String prefix
    ) {
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
    }

    @Override
    public String create(UUID userId, String username) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(prefix + token, userId.toString(), Duration.ofHours(24));
        return token;
    }

    @Override
    public Optional<UUID> resolve(String token) {
        String value = redisTemplate.opsForValue().get(prefix + token);
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(UUID.fromString(value));
    }

    @Override
    public void invalidate(String token) {
        redisTemplate.delete(prefix + token);
    }
}
