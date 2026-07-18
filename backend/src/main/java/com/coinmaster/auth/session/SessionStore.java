package com.coinmaster.auth.session;

import java.util.Optional;
import java.util.UUID;

public interface SessionStore {

    String create(UUID userId, String username);

    Optional<UUID> resolve(String token);

    void invalidate(String token);
}
