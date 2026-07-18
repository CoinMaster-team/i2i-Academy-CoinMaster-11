package com.coinmaster.common.security;

import com.coinmaster.auth.session.SessionStore;
import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
public class BearerTokenCurrentUserProvider implements CurrentUserProvider {

    private final HttpServletRequest request;
    private final SessionStore sessionStore;

    public BearerTokenCurrentUserProvider(HttpServletRequest request, SessionStore sessionStore) {
        this.request = request;
        this.sessionStore = sessionStore;
    }

    @Override
    public UUID currentUserId() {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Authorization bearer token is required");
        }
        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Authorization bearer token is required");
        }
        return sessionStore.resolve(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Session token is invalid"));
    }
}
