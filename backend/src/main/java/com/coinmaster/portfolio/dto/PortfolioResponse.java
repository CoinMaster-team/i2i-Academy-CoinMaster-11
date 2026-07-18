package com.coinmaster.portfolio.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PortfolioResponse(
        BigDecimal cashBalance,
        List<PortfolioPositionResponse> positions,
        BigDecimal totalCryptoValue,
        BigDecimal totalPortfolioValue,
        Instant asOf
) {
}
