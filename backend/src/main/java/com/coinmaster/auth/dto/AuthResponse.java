package com.coinmaster.auth.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String username,
        String token,
        BigDecimal startingBalance
) {
}
