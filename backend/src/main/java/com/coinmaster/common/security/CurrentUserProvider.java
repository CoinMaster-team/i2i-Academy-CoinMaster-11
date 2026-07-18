package com.coinmaster.common.security;

import java.util.UUID;

/**
 * Infrastructure/auth module implements this contract by resolving the Redis-backed
 * session token. The local profile supplies a header-based adapter for development.
 */
public interface CurrentUserProvider {

    UUID currentUserId();
}
