package com.coinmaster.common.security;

import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import com.coinmaster.auth.session.SessionStore;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalHeaderCurrentUserProvider implements CurrentUserProvider {

    public static final String HEADER = "X-User-Id";

    private final HttpServletRequest request;
    private final SessionStore sessionStore;

    public LocalHeaderCurrentUserProvider(HttpServletRequest request, SessionStore sessionStore) {
        this.request = request;
        this.sessionStore = sessionStore;
    }

    @Override
    public UUID currentUserId() {
        String bearerToken = bearerToken();
        if (bearerToken != null) {
            return sessionStore.resolve(bearerToken)
                    .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Session token is invalid"));
        }

        String value = request.getHeader(HEADER);
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Authorization bearer token or X-User-Id header is required");
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "X-User-Id must be a valid UUID");
        }
    }

    private String bearerToken() {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring("Bearer ".length()).trim();
        return token.isBlank() ? null : token;
    }
}
