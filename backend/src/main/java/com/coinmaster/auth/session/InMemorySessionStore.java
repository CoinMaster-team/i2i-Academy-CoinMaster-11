package com.coinmaster.auth.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class InMemorySessionStore implements SessionStore {

    private final Map<String, UUID> sessions = new ConcurrentHashMap<>();

    @Override
    public String create(UUID userId, String username) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, userId);
        return token;
    }

    @Override
    public Optional<UUID> resolve(String token) {
        return Optional.ofNullable(sessions.get(token));
    }

    @Override
    public void invalidate(String token) {
        sessions.remove(token);
    }
}
