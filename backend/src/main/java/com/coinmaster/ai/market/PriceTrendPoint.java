package com.coinmaster.ai.market;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceTrendPoint(Instant capturedAt, BigDecimal price) {
}
