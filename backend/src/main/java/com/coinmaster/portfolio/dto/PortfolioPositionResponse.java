package com.coinmaster.portfolio.dto;

import java.math.BigDecimal;

public record PortfolioPositionResponse(
        String symbol,
        BigDecimal quantity,
        BigDecimal currentPrice,
        BigDecimal marketValue
) {
}
